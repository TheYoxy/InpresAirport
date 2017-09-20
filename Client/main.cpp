//
// Created by floryan on 20/09/17.
//
#include <stdlib.h>
#include <iostream>

using namespace std;



int main(int argc, char * argv[])
{
	u_long IpSocket;
    u_short PortSocket;

    bool fail = 1;
    char login[20];
    char password[20];

    do
    {
    	cout << "Application CHECK IN" << endl << "--------------------" << endl;
    	cout << "Login :" << endl;
    	cin >> login;
		cout << "Password :" << endl;
    	cin >> password;

    }while(fail != 0)


}



void login(char *login, char *mdp)
{
	//Envoie chaine de caractère au serv avec log + pass et requete LOGIN_OFFICER
	//Attend réponse
	//Attend réponse
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
