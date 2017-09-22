//
// Created by floryan on 20/09/17.
//
#include <stdlib.h>
#include <iostream>
#include "../Librairie/SocketClient.h"
#include "../Librairie/SocketServeur.h"

using namespace std;



int main(int argc, char * argv[])
{
	ipv4 IpSocket;
    u_short PortSocket;

    bool fail = 1;
    char login[20], password[20], numBillet[40];
    int numVol;
    SocketClient SoCl;

    //Creation du socket client 
    // = new ipv4();
    //SocketClient socketC = new SocketClient(IpSocket ,PortSocket);

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


}



void login(char *login, char *mdp)
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
