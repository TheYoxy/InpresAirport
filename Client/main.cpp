#include <stdlib.h>
#include <iostream>
#include <netdb.h>
#include "../Librairie/SocketClient.h"
#include "../Librairie/ConnexionException.h"

#define CLEAN "\x1B[2J\x1B[H"

extern SParametres Parametres;
using namespace std;

SocketClient *SoCl;

bool Login(string login, string mdp);

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
    string login, password, numBillet, ip;
    int numVol;

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
    /*
     * do
    {
        cout << "Application CHECK IN" << endl << "--------------------" << endl;
        cout << "Numero de vol :" << endl;
        cin >> numVol;
        cout << "Numero du billet :" << endl;
        cin >> numBillet;
        cout << "Check......." << endl;
        cout << "\n\n\n\n\n" << endl;

    }while()
    */
}


bool Login(string login, string mdp) {
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

void check_ticket() {
    //envoie chaine de caractère au serv avec requete CHECK_TICKET
    //Attend reponse pour encoder bagages
}
