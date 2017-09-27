#include <stdlib.h>
#include <iostream>
#include <netdb.h>
#include "../Librairie/SocketClient.h"
#include "../Librairie/ConnexionException.h"

#define CLEAN "\x1B[2J\x1B[H"

extern SParametres Parametres;
using namespace std;

SocketClient *SoCl;

bool Login(const string &login, const string &mdp);

void logout(char *login);

void check_ticket();

void Logout(string login);

int main(int argc, char *argv[]) {
    cout << CLEAN;
    if (argc < 2) {
        cout << "Il manque des paramètre pour l'execution du programme" << endl;
        cout << "./Client [nom-de-la-machine-hôte]" << endl;
        return -1;
    }
    lectureFichierParams("../config.conf");
    string login, password, ip;

    struct hostent *host = gethostbyname(argv[1]);
    SMessage message;

    /************************* LIAISON AVEC LE SERVEUR ****************************************/
    if (host == nullptr) {
        cout << "Impossible de résoudre le nom d'hôte" << endl;
        return -1;
    }

    for (int i = 0; i < host->h_length; i++)
        ip += std::to_string((int) host->h_addr[i]) + ".";
    ip.pop_back();
    cout << "Ip de l'host: " << ip << endl;
    try {
        SoCl = new SocketClient(ipv4().Any);
        SoCl->Connect(ipv4(ip.c_str()), Parametres.PortRange[0]);
        cout << "Client connecté" << endl;
        /************************************ FIN LIAISON ******************************************/
        bool boucle = true;
        while (boucle) {
#ifndef DEBUG
            cout << CLEAN;
#endif
            cout << "INPRESAIRPORT : veuillez vous identifier." << endl;
            cout << "Login: ";
            cin >> login;
            cout << "Password: ";
            cin >> password;
            boucle = Login(login, password);
            bool menu = boucle;
            while (menu) {
                int choix;
#ifndef DEBUG
                cout << CLEAN;
#endif
                cout << "1. Encoder billet" << endl;
                cout << "2. Encoder bagage" << endl;
                cout << "0. Quitter" << endl;
                cin >> choix;
                switch (choix) {
                    case 0:
#ifndef DEBUG
                        cout << CLEAN;
#endif
                        cout << "1. Se déconnecter de l'application" << endl;
                        cout << "0. Quitter l'application" << endl;
                        cin >> choix;
                        if (choix == 0)
                            boucle = false;
                        menu = false;
                        Logout(login);
                        break;
                    case 1:
                        check_ticket();
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    default:
                        break;
                }
            }
        }
        SoCl->Disconnect();
    }
    catch (ConnexionException ce) {
        cout << ce.getMessage() << endl;
    }
    catch (Exception e) {
        cout << e.getMessage() << endl;
    }
    /************************************ FIN LIAISON ******************************************/

    /******************************* CENTRE DE L APPLICATION ***********************************/
    return 0;
}


bool Login(const string &login, const string &mdp) {
    //Envoie chaine de caractère au serv avec log + pass et requete LOGIN_OFFICER
    bool retour = false;
    string message = getMessage(LOGIN_OFFICER, login + Parametres.TramesSeparator + mdp);
    cout << "Type: " << (Type) message[0] << "(" << typeName((Type) message[0]) << ")" << endl;
    SoCl->Send(message);
    message.clear();
    //On attend la réponse
    try {
        SoCl->Recv(message);
        SMessage sMessage = getStructMessageFromString(message);
        if (sMessage.type == ACCEPT) {
            cout << "Login reussi" << endl;
            retour = true;
        } else if (sMessage.type == REFUSE)
            cout << "Erreur de combinaison login/mot de passe" << endl;
        else
            cout << "Message inconnu" << endl;
    }
    catch (Exception e) {
        cerr << e.getMessage() << endl;
    }
    return retour;
}

void Logout(string login) {
    //Envoie chaine de caractère au serv avec log et requete LOGOUT_OFFICER
    try {
        SoCl->Send(getMessage(LOGOUT_OFFICER, login));
        cout << "Logout reussi" << endl;
    }
    catch (Exception e) {
        cerr << e.getMessage() << endl;
    }
}

void Check_ticket() {
    string numBillet, numVol, nbAccompagants, valise, poids, paye;
    int nbBagages, i;
    bool retour = false;
    Type flag = CHECK_TICKET;
    string message;

    //envoie chaine de caractère au serv avec requete CHECK_TICKET
    do {
        cout << "Application CHECK IN" << endl << "--------------------" << endl;
        cout << "Numero de vol :" << endl;
        cin >> numVol;
        cout << "Numero du billet :" << endl;
        cin >> numBillet;
        cout << "Nombre d'accompagants :" << endl;
        cin >> nbAccompagants;
        cout << "Check......." << endl;
#ifndef DEBUG
        cout << CLEAN << endl;
#endif
        message = getMessage(flag, numBillet + Parametres.TramesSeparator + numVol + Parametres.TramesSeparator +
                                   nbAccompagants);
        SoCl->Send(message);
        message.clear();
        try {
            SoCl->Recv(message);
            SMessage sMessage = getStructMessageFromString(message);
            if (sMessage.type == ACCEPT) {
                cout << "Enrigstrement du billet effectué avec succès !" << endl;
                retour = true;
            } else if (sMessage.type == REFUSE)
                cout << "Numero de billet invalide" << endl;
        }
        catch (Exception e) {
            cerr << e.getMessage() << endl;
        }
    } while (!retour);

    cout << "ENREGISTREMENT BAGAGES" << endl;
    cout << "Nombre de bagages : " << endl;
    cin >> nbBagages;
    message.clear();
    flag = CHECK_LUGGAGE;

    for (i = 0; i < nbBagages; i++) {
        cout << "Poids du bagage numero " << i + 1 << " :" << endl;
        cin >> poids;
        cout << "Valise ?";
        cin >> valise;
        message += poids + Parametres.TramesSeparator + valise +
                   Parametres.TramesSeparator; //poids de la valise + separateur + valise O/N
        //ATTENTION pour dernier bagage remove le dernier séparateur avant séparateur de fin /!
    }
    message = getMessage(flag, message);
    SoCl->Send(message);
    message.clear();
    try {
        SoCl->Recv(message);
        SMessage sMessage = getStructMessageFromString(message);
        vector<string> splits;
        splits = split(message,
                       Parametres.TramesSeparator); //Serveur renvoie poids total + excédent poids + plus supplément payé
        cout << "Poids total : " << splits[0] << "kg" << endl;
        cout << "Excédent poids : " << splits[1] << "kg" << endl;
        cout << "Supplément à payer : " << splits[2] << "Euro" << endl;
        cout << "Paiement effectué (O/N) : ";
        cin >> paye;
        flag = PAYMENT_DONE;
        message.clear();
        message = getMessage(flag, message);//Envoi du flag uniquement message vide
        SoCl->Send(message);
    }
    catch (Exception e) {
        cerr << e.getMessage() << endl;
    }

    //Attend reponse pour encoder bagages
}
