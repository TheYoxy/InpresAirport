#include <cstdlib>
#include <iostream>
#include <netdb.h>
#include "../Librairie/Socket.h"

using namespace std;

bool Send(int descripteur, std::string msg);

std::string Recv(int descripteur);

bool verif(const std::string &message);

int main(int argc, char **argv) {
    string smtpServer, ip;
    unsigned short port;
    if (argc == 3) {
        cout << argv[1] << endl << argv[2] << endl;
        smtpServer = argv[1];
        port = static_cast<unsigned short>(std::stoi(argv[2]));
    } else {
        cout << "Adresse du serveur mail: ";
        cin >> smtpServer;
        cout << "Port du serveur: ";
        cin >> port;
    }

    struct hostent *host = gethostbyname(smtpServer.c_str());

    if (host == nullptr) {
        cout << "Impossible de résoudre le nom d'hôte: " << smtpServer << endl;
        return -1;
    }

    for (int i = 0; i < host->h_length; i++)
        ip += to_string((unsigned char) host->h_addr[i]) + ".";
    ip.pop_back();
    cout << "IP: " << ip << endl;

    int descripteur;
    if ((descripteur = socket(AF_INET, SOCK_STREAM, 0)) == -1) {
        Error(RED, "Impossible de créer une socket:" << strerror(errno) << endl);
        return -1;
    }

    struct sockaddr_in *sok = CreationSockStruct(ipv4().Any, INADDR_ANY);
    if (bind(descripteur, (struct sockaddr *) sok, sizeof(struct sockaddr_in)) == -1) {
        close(descripteur);
        Error(RED, "Impossible de bind: " << strerror(errno) << endl);
        return -1;
    }

    sok = CreationSockStruct(ipv4(ip.c_str()), port);
    if (connect(descripteur, (struct sockaddr *) sok, sizeof(struct sockaddr_in)) == -1) {
        close(descripteur);
        Error(RED, "Impossible de connect: " << strerror(errno) << endl);
        return -1;
    }

    if (!Send(descripteur, "HELO " + smtpServer + "\r\n"))
        return -1;
    string message = Recv(descripteur);
    if (!verif(message))
        return -1;
    string mail;

//    cout << "Entrez votre adresse mail: ";
//    cin >> mail;
    mail = "floryan@floryan.com";
    if (!Send(descripteur, "MAIL FROM:" + mail + "\r\n"))
        return -1;
    message = Recv(descripteur);
    if (!verif(message))
        return -1;

//    cout << "Entrez l'adresse mail de destination: ";
//    cin >> mail;
    mail = "floryan@floryan.com";
    if (!Send(descripteur, "RCPT TO:" + mail + "\r\n"))
        return -1;
    message = Recv(descripteur);
    if (!verif(message))
        return -1;

//    cout << "Entrez le corps du message: (Entrer . Enter pour quitter)" << endl;

    message.clear();
//    bool boucle = true;
    string temp;
//    do {
//        cin >> temp;
//        if (temp == ".")
//            boucle = false;
//        else
//            message += temp;
//    } while (boucle);
    message = "Coucou, tu veux voir ma bite ?";
    if (!Send(descripteur, "DATA\r\n"))
        return -1;
    temp = Recv(descripteur);
    if (!verif(temp))
        return -1;

    if (!Send(descripteur, message))
        return -1;
    message = Recv(descripteur);
    if (!verif(message))
        return -1;

    if (!Send(descripteur, "\r\n.\r\n"))
        return -1;
    message = Recv(descripteur);
    if (!verif(message))
        return -1;

    if (!Send(descripteur, "QUIT\r\n"))
        return -1;
    message = Recv(descripteur);
    if (!verif(message))
        return -1;

    close(descripteur);
}

bool Send(int descripteur, std::string msg) {
    Error(BLUE, "Envoyé: " + msg);
    if (send(descripteur, msg.data(), msg.length(), 0) == -1) {
        close(descripteur);
        Error(RED, "Impossible d'envoyer un message: " << strerror(errno));
        return false;
    }
    return true;
}

std::string Recv(int descripteur) {
    std::string retour;
    char c;
    do {
        recv(descripteur, &c, 1, 0);
        retour += c;
    } while (c != '\n');
    Error(YELLOW, "Reçu: " << retour);
    return retour;
}

bool verif(const std::string &message) {
    return message.substr(0, 3) == "220" | message.substr(0, 3) == "250" || message.substr(0,3) == "354";
}