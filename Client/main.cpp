//
// Created by floryan on 20/09/17.
//
#include <stdlib.h>
#include <iostream>
#include <netdb.h>
#include "../Librairie/SocketClient.h"
#include "../Librairie/ConnexionException.h"
#include "../Librairie/Structs.h"
extern SParametres Parametres;
using namespace std;

SocketClient* SoCl;

short Login(string login, string mdp);
void logout(char *login);
void check_ticket();

int main(int argc, char *argv[]) {
    lectureFichierParams("../config.conf");
    bool state = 0;
    string login, password, numBillet, ip;
    int numVol;
    struct hostent *host = gethostbyname(/*"floryan-virtual-machine"*/"floryan-msi-portable");
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
    }
    catch (ConnexionException ce) {
        cout << ce.getMessage() << endl;
    }
    catch (Exception e) {
        cout << e.getMessage() << endl;
    }
    /************************************ FIN LIAISON ******************************************/

    /******************************* CENTRE DE L APPLICATION ***********************************/
    try{
        while(!state)
        {
            cout << "INPRESAIRPORT : veuillez vous identifier." << endl;
            cout << "Login: " << endl;
            cin >> login;
            cout << "Password :" << endl;
            cin >> password;
            state = Login(login, password);
            while(state)
            {
                cout << "1. Encoder billet" << endl;
                cout << "2. Encoder bagage" << endl;
            }
        }

        //SoCl.Disconnect();

        while(state == 1)
        {

        }
    }
    catch(Exception e)
    {
        cout << e.getMessage() << endl;
    }
    return -1;
    /*
    do
    {
    	cout << "Application CHECK IN" << endl << "--------------------" << endl;
    	cout << "Login :" << endl;
    	cin >> login;
		cout << "Password :" << endl;
    	cin >> password;
    	cout << "Connexion......." << endl;
        cout << "\n\n\n\n\n" << endl;
        login(&login, &password);
    }while(fail != 0)

    do
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



short Login(string login, string mdp)
{
	//Envoie chaine de caractère au serv avec log + pass et requete LOGIN_OFFICER
    short returnVal = 0;
    string message = login + Parametres.TramesSeparator + mdp;
    Type flag = LOGIN_OFFICER;
    SoCl->Send(message);
    message = "";
    //On attend la réponse
    try {
        SoCl->Recv(message);
        SMessage sMessage = getStructMessageFromString(message);
        switch(sMessage.type)
        {
            case ACK: cout << "Login reussi" << endl;
                    returnVal = 1;
                    break;
        }
    }
    catch (Exception e) {
        cerr << e.getMessage() << endl;
    }
    return returnVal;
}

void logout(char *login)
{
	//Envoie chaine de caractère au serv avec log et requete LOGOUT_OFFICER
    Type flag = LOGOUT_OFFICER;
    string message = flag + login;
    SoCl->Send(message);
    message.clear();
    //On attend la réponse
    try {
        SoCl->Recv(message);
        SMessage sMessage = getStructMessageFromString(message);
        switch(sMessage.type)
        {
            case ACK: cout << "Logout reussi" << endl;
                    break;
        }
    }
    catch (Exception e) {
        cerr << e.getMessage() << endl;
    }
}

void check_ticket()
{
	//envoie chaine de caractère au serv avec requete CHECK_TICKET
	//Attend reponse pour encoder bagages
}
