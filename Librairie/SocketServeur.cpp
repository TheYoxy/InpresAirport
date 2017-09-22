#include "SocketServeur.h"

using namespace std;
int maxSocketNbr = 1;

SocketServeur::SocketServeur(const ipv4 &addr, unsigned short port) : Socket(addr, port) {
}

SocketServeur::SocketServeur(struct sockaddr_in *socket) : Socket(socket) {

}

SocketServeur::SocketServeur(int descripteur, struct sockaddr_in *socket) : Socket(descripteur, socket) {

}

SocketServeur::~SocketServeur() {
}

void SocketServeur::Listen() {
    if (listen(descripteur, SOMAXCONN) == -1)
        throw Exception(getLieu() + "Erreur listen: " + strerror(errno));
}

Socket *SocketServeur::Accept() {
    int nbMaxClients = 0;
    unsigned int size;
    int so;
    struct sockaddr_in ip;
    if ((so = accept(descripteur, (struct sockaddr *) &ip, &size)) == -1)
        throw Exception(getLieu() + "Erreur connexion: " + strerror(errno));

    Socket *s = new Socket(so, &ip);
    try {
        std::string message;
        if (nbMaxClients >= maxSocketNbr) {
            s->Send(getMessage(TOO_MUCH_CONNECTIONS, ""));
        } else {
            s->Send(getMessage(ACK, ""));
        }
        if (nbMaxClients >= maxSocketNbr) {
            delete s;
            return nullptr;
        }
        Type flag;
        int i = 1;
        s->Recv((char *) &flag, &i);
        if (flag != ACK)
            s->Send(getMessage(TOO_MUCH_CONNECTIONS, ""));
    }
    catch (Exception e) {
        throw e;
    }
    nbMaxClients++;

    return s;
}

std::string SocketServeur::getLieu() {
    return "SocketServeur: ";
}

