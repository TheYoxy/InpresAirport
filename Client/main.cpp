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

void Logout(char *login);

void Check_ticket();

int main(int argc, char *argv[]) {
    if (argc < 2) {
        cout << "Il manque des paramètre pour l'execution du programme" << endl;
        cout << "./Client [nom-de-la-machine-hôte]" << endl;
        return -1;
    }
    lectureFichierParams("../config.conf");
    string login, password,ip;

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
            cout << "Login: " << endl;
            cin >> login;
            cout << "Password :" << endl;
            cin >> password;
            boucle = Login(login, password);
            bool menu = true;
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
                        cout << "0. Quitter l'application" << endl;
                        cout << "1. Se déconnecter de l'application" << endl;
                        cin >> choix;
                        if (choix == 1)
                            boucle = false;
                        menu = false;
                        break;
                    case 1:
                        Check_ticket();
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
    return 0;
}


bool Login(const string &login, const string &mdp) {
    //Envoie chaine de caractère au serv avec log + pass et requete LOGIN_OFFICER
    bool retour = false;
    Type flag = LOGIN_OFFICER;
    string message = login + Parametres.TramesSeparator + mdp;
    message = getMessage(flag, message);
    SoCl->Send(message);
    message = "";
    //On attend la réponse
    try {
        SoCl->Recv(message);
        SMessage sMessage = getStructMessageFromString(message);
        if (sMessage.type == ACCEPT) {
            cout << "Login reussi" << endl;
            retour = true;
        } else if (sMessage.type == REFUSE)
            cout << "Erreur de combinaison login/mot de passe" << endl;
    }
    catch (Exception e) {
        cerr << e.getMessage() << endl;
    }
    return retour;
}

void Logout(char *login) {
    //Envoie chaine de caractère au serv avec log et requete LOGOUT_OFFICER
    Type flag = LOGOUT_OFFICER;
    string message = flag + login;
    SoCl->Send(message);
    message.clear();
    //On attend la réponse
    try {
        SoCl->Recv(message);
        SMessage sMessage = getStructMessageFromString(message);
        cout << "Logout reussi" << endl;
    }
    catch (Exception e) {
        cerr << e.getMessage() << endl;
    }
}

void Check_ticket() {
    string numBillet, numVol;
    bool retour;
    Type flag = LOGOUT_OFFICER;
    string message;

    //envoie chaine de caractère au serv avec requete CHECK_TICKET
    do
    {
        cout << "Application CHECK IN" << endl << "--------------------" << endl;
        cout << "Numero de vol :" << endl;
        cin >> numVol;
        cout << "Numero du billet :" << endl;
        cin >> numBillet;
        cout << "Check......." << endl;
        cout << "\n\n\n\n\n" << endl;
        message = numBillet + Parametres.TramesSeparator + numVol;
        message = getMessage(flag, message);
        SoCl->Send(message);
        message.clear();
        try {
            SoCl->Recv(message);
            SMessage sMessage = getStructMessageFromString(message);
            if (sMessage.type == ACCEPT) {
                cout << "Numero de billet accepté" << endl;
                retour = true;
            } else if (sMessage.type == REFUSE)
                cout << "Numero de billet invalide" << endl;
        }
        catch (Exception e) {
            cerr << e.getMessage() << endl;
        }
    }while(!retour);
    //Attend reponse pour encoder bagages
}
