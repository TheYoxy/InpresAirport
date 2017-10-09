#include <cstdlib>
#include <iostream>
#include <netdb.h>
#include <limits>
#include <tgmath.h>
#include "../Librairie/SocketClient.h"
#include "../Librairie/ConnexionException.h"

#define CLEAN "\x1B[2J\x1B[H"

extern SParametres Parametres;
using namespace std;

SocketClient *SoCl;

bool Login(const string &login, const string &mdp);

void Check_ticket();

void Encodage_Bagages();

void Logout(string login);

int main(int argc, char *argv[]) {
    cout << CLEAN;
    if (argc < 2) {
        Error(RED, "Il manque des paramètre pour l'execution du programme");
        Error(RED, "./Client [nom-de-la-machine-serveur]");
        return -1;
    }
#ifdef Debug
    lectureFichierParams("../config.conf");
#endif

#ifndef Debug
    lectureFichierParams("config.conf");
#endif

    string login, password, ip;

    struct hostent *host = gethostbyname(argv[1]);
    SMessage message;

    /************************* LIAISON AVEC LE SERVEUR ****************************************/
    if (host == nullptr) {
        cout << "Impossible de résoudre le nom d'hôte" << endl;
        return -1;
    }

    for (int i = 0; i < host->h_length; i++)
        ip += to_string((unsigned char) host->h_addr[i]) + ".";
    ip.pop_back();
    cout << "Ip de l'host: " << ip << endl;
    try {
        SoCl = new SocketClient(ipv4().Any);
        SoCl->Connect(ipv4(ip.c_str()), Parametres.PortRange[0]);
        cout << "Client connecté" << endl;
        /************************************ FIN LIAISON ******************************************/
        bool boucle;
        do {
#ifndef Debug
            cout << CLEAN;
#endif
            int fail = 3;
            do {
                cout << "INPRESAIRPORT : veuillez vous identifier." << endl;
                cout << "Login: ";
                cin >> login;
                cout << "Password: ";
                cin >> password;
                boucle = Login(login, password);
                if (!boucle)
                    fail--;
                if (!fail)
                    cout << "3 tentatives échouées. Fin de l'application." << endl;
            } while (fail && !boucle);
            bool menu = boucle;
            while (menu) {
                int choix;
                cout << "1. Vérification billet" << endl;
                cout << "2. Ajouter des bagages" << endl;
                cout << "0. Quitter" << endl;
                cin >> choix;
                cin.ignore(numeric_limits<streamsize>::max(), '\n');
                switch (choix) {
                    case 0:
#ifndef Debug
                        cout << CLEAN;
#endif
                        cout << "1. Se déconnecter de l'application" << endl;
                        cout << "0. Quitter l'application" << endl;
                        cin >> choix;
                        cin.ignore(numeric_limits<streamsize>::max(), '\n');
                        if (choix == 0)
                            boucle = false;
                        menu = false;
                        Logout(login);
                        break;
                    case 1:
                        Check_ticket();
                        break;
                    case 2:
                        Encodage_Bagages();
                        break;
                    default:
                        break;
                }
            }
        } while (boucle);
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
    //Envoie chaine de caractère au serv avec logStream + pass et requete LOGIN_OFFICER
    bool retour = false;
    string message = getMessage(LOGIN, login + Parametres.TramesSeparator + mdp);
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
    //Envoie chaine de caractère au serv avec logStream et requete LOGOUT_OFFICER
    try {
        SoCl->Send(getMessage(LOGOUT, login));
        cout << "Logout reussi" << endl;
    }
    catch (Exception e) {
        cerr << e.getMessage() << endl;
    }
}

void Check_ticket() {
    string numBillet, numVol, nbPersonnes, valise, poids, paye;
    bool retour = false;
    Type flag = CHECK_TICKET;
    string message;

    //envoie chaine de caractère au serv avec requete CHECK_TICKET
    do {
        cout << "Application CHECK IN" << endl << "--------------------" << endl;
        cout << "Numero de vol: ";
        cin >> numVol;
        cout << "Numero du billet: ";
        cin >> numBillet;
        cout << "Nombre de personnes: ";
        cin >> nbPersonnes;
#ifndef Debug
        cout << CLEAN << endl;
#endif
        SoCl->Send(getMessage(flag, numBillet +
                                    Parametres.TramesSeparator +
                                    numVol +
                                    Parametres.TramesSeparator +
                                    nbPersonnes));
        message.clear();
        try {
            SoCl->Recv(message);
            switch (getStructMessageFromString(message).type) {
                case ACCEPT:
                    cout << "Enrigstrement du billet effectué avec succès !" << endl;
                    retour = true;
                    break;
                case REFUSE:
                    cout << "Numero de billet invalide" << endl;
                    break;
                default:
                    break;
            }
        }
        catch (Exception e) {
            cerr << e.getMessage() << endl;
        }
    } while (!retour);
    //Attend reponse pour encoder bagages
}

SMessage Check_Ticket_Return() {
    string numBillet, numVol, nbPersonnes, valise, poids, paye;
    Type flag = CHECK_TICKET;
    string message;

    //envoie chaine de caractère au serv avec requete CHECK_TICKET
    cout << "Application CHECK IN" << endl << "--------------------" << endl;
    cout << "Numero de vol: ";
    cin >> numVol;
    cout << "Numero du billet: ";
    cin >> numBillet;
    cout << "Nombre de personnes: ";
    cin >> nbPersonnes;
#ifndef Debug
    cout << CLEAN << endl;
#endif
    SoCl->Send(getMessage(flag, numBillet +
                                Parametres.TramesSeparator +
                                numVol +
                                Parametres.TramesSeparator +
                                nbPersonnes));
    message.clear();
    try {
        SoCl->Recv(message);
        return getStructMessageFromString(message);
    }
    catch (Exception e) {
        cerr << e.getMessage() << endl;
    }
    return getStructMessageFromString(getMessage(REFUSE, ""));
}

void Encodage_Bagages() {
    string message = "", numVol, numBillet;
    bool fin = false;
    do {
        double poids;
        string valise = "";
        //BOUCLE DE BLINDAGE
        do {
            cout << "Valise(V) ou Bagage à main(B) ?"
                 << (message == "" ? "" : "\t Enter pour valider: ");
            getline(cin, valise);
            //Condition pour éviter de n'encoder aucune valise
            if (valise == "" && message != "") {
                fin = true;
            } else if (valise != "v" && valise != "b" && valise != "V" && valise != "B") {
#ifndef Debug
                cout << CLEAN;
#endif
                cout << endl << "Entrée incorrecte, veuillez entrer une entrée correcte" << endl;
            }
        } while (valise != "v" && valise != "b" && valise != "V" && valise != "B" && !fin);

        if (!fin) {
            cout << (valise == "v" || valise == "V" ? "Poids de la valise: " : "Poids du sac: ");
            cin >> poids;
            cin.ignore(numeric_limits<streamsize>::max(), '\n');
            message += valise + Parametres.TramesSeparator + to_string(poids) +
                       Parametres.TramesSeparator;
        }
    } while (!fin);
    message.pop_back();
    try {
        SMessage sMessage;
        bool boucle;
        do {
            boucle = false;
            SoCl->Send(getMessage(ADD_LUGGAGE, message));
            string temp;
            SoCl->Recv(temp);
            sMessage = getStructMessageFromString(temp);
            if (sMessage.type == NO_SELECTED_TICKET) {
                SMessage retour;
                do {
                    retour = Check_Ticket_Return();
                } while (retour.type == REFUSE);
                boucle = true;
            }
        } while (boucle);
        vector<string> splits;
        splits = split(sMessage.message, Parametres.TramesSeparator);
        //Serveur renvoie poids total + excédent poids + plus supplément payé
        char pay;
        cout << "Poids total        : " << round(stod(splits[0]) * 100) / 100 << " kg" << endl;
        cout << "Excédent poids     : " << round(stod(splits[1]) * 100) / 100 << " kg" << endl;
        cout << "Supplément à payer : " << round(stod(splits[2]) * 100) / 100 << " €" << endl;
        do {
            cout << "Paiement effectué (O/N) : ";
            cin >> pay;
            if (pay == 'O') {
                sMessage.type = PAYEMENT_DONE;
                SoCl->Send(getStringFromStructMessage(sMessage));
            } else if (pay == 'N')
                cout << "Veuillez enregistrer le payement" << endl;
            else
                cout << "Entrée incorrecte" << endl;
        } while (pay != 'O');
    }
    catch (Exception e) {
        cerr << e.getMessage() << endl;
    }
}