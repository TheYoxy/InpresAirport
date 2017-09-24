//
// Created by floryan on 20/09/17.
//
#include <stdlib.h>
#include <iostream>
#include <netdb.h>
#include "../Librairie/SocketClient.h"
#include "../Librairie/ConnexionException.h"

extern SParametres Parametres;
using namespace std;


int main(int argc, char *argv[]) {
    lectureFichierParams("../config.conf");
    bool fail = 1;
    char login[20], password[20], numBillet[40];
    int numVol;
    struct hostent *host = gethostbyname(/*"floryan-virtual-machine"*/"floryan-msi-portable");
    if (host == nullptr) {
        cout << "Impossible de résoudre le nom d'hôte" << endl;
        return -1;
    }
    std::string ip;
    for (int i = 0; i < host->h_length; i++)
        ip += std::to_string((int) host->h_addr[i]) + ".";
    ip.pop_back();
    cout << "Ip de l'host: " << ip << endl;
    try {
        SocketClient SoCl(ipv4().Any);
        SoCl.Connect(ipv4(ip.c_str()), 26010);
        cout << "Client connecté" << endl;
        cout << "Login: " << endl;
        cin >> login;
        SoCl.Disconnect();
    }
    catch (ConnexionException ce) {
        cout << ce.getMessage() << endl;
    }
    catch (Exception e) {
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



/*void login(char *login, char *mdp)
{
	//Envoie chaine de caractère au serv avec log + pass et requete LOGIN_OFFICER
    string message = login + "-" + password;
    cout << "Message avant l'envoi: " << message << endl << "Taille: " << message.length() << endl;
    //SoCl.send();
    //On attend la réponse
    try {
        SocketServeur sv;
        char *rcv = new char[50];
        memset(rcv, 0, 50);
        size_t taille = 50;
        sv.Recv(rcv, taille);
        printf("Message : %s", rcv);
    }
    catch (Exception e) {
        cerr << e.getMessage() << endl;
    }
}

void logout(char *login)
{
	//Envoie chaine de caractère au serv avec log et requete LOGOUT_OFFICER
}

void check_ticket()
{
	//envoie chaine de caractère au serv avec requete CHECK_TICKET
	//Attend reponse pour encoder bagages
}
*/