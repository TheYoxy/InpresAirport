#include "SocketServeur.h"

using namespace std;
int maxSocketNbr = nbThread;
int clients = 0;

SocketServeur::SocketServeur(const ipv4 &addr, unsigned short port) try : Socket(addr, port) {}
catch (Exception e) {
    throw e;
}

SocketServeur::SocketServeur(struct sockaddr_in *socket) try : Socket(socket) {

} catch (Exception e) {
    throw e;
}

SocketServeur::SocketServeur(int descripteur, struct sockaddr_in *socket) try : Socket(descripteur, socket) {

} catch (Exception e) {
    throw e;
}

SocketServeur::~SocketServeur() {
}

void SocketServeur::Listen() {
    if (listen(descripteur, SOMAXCONN) == -1)
        throw Exception(EXCEPTION() + "Erreur listen: " + strerror(errno));
}

Socket *SocketServeur::Accept() {
    Listen();
    unsigned int size = sizeof(struct sockaddr_in);
    int so;

    struct sockaddr_in *ip = new struct sockaddr_in;
    memset(ip, 0, sizeof(struct sockaddr_in));

    if ((so = accept(descripteur, (struct sockaddr *) ip, &size)) == -1)
        throw Exception(EXCEPTION() + "Erreur connexion: " + strerror(errno));

    Socket *s = new Socket(so, ip);
    bool stop = false;
    while (!stop) {
        Type type;
        if (clients >= maxSocketNbr) {
            type = TOO_MUCH_CONNECTIONS;
        } else {
            type = ACK;
        }
        if (send(s->getDescripteur(), &type, 1, 0) == -1)
            throw Exception(EXCEPTION() + "Impossible d'envoyer le message " + strerror(errno));
        if (clients >= maxSocketNbr) {
            delete s;
            return nullptr;
        }
        if (recv(s->getDescripteur(), &type, 1, 0) == -1)
            throw Exception(EXCEPTION() + "Impossible de recevoir le message " + strerror(errno));
        if (type == ACK) {
            stop = true;
        }
    }
    clients++;
    return s;
}
