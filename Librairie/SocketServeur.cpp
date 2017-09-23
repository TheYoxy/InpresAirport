#include "SocketServeur.h"

using namespace std;

#ifndef _GLOBAL
#define _GLOBAL
int maxSocketNbr = 1;
int clients = 0;
#endif

SocketServeur::SocketServeur(const ipv4 &addr, unsigned short port) try : Socket(addr, port) {}
catch (Exception e) {
    throw e;
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
    unsigned int size = sizeof(struct sockaddr_in);
    int so;

    struct sockaddr_in *ip = new struct sockaddr_in;
    memset(ip, 0, sizeof(struct sockaddr_in));

    if ((so = accept(descripteur, (struct sockaddr *) ip, &size)) == -1)
        throw Exception(getLieu() + "Erreur connexion: " + strerror(errno));

    Socket *s = new Socket(so, ip);
    Type type;
    if (clients >= maxSocketNbr) {
        type = TOO_MUCH_CONNECTIONS;
    } else {
        type = ACK;
    }
    if (send(s->getDescripteur(), &type, 1, 0) == -1)
        throw Exception(getLieu() + "Impossible d'envoyer le message " + strerror(errno));
    if (clients >= maxSocketNbr) {
        delete s;
        return nullptr;
    }
    if (recv(s->getDescripteur(), &type, 1, 0) == -1)
        throw Exception(getLieu() + "Impossible de recevoir le message " + strerror(errno));
    if (type != ACK) {
        delete s;
        s = nullptr;
    }
    clients++;
    return s;
}

std::string SocketServeur::getLieu() {
    return "SocketServeur: ";
}

