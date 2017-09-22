#include "SocketServeur.h"

using namespace std;
int maxSocketNbr = 1;

SocketServeur::SocketServeur(const ipv4 &addr, unsigned short port) : Socket(addr, port) {
}

SocketServeur::~SocketServeur() {
}

void SocketServeur::Listen() {
    if (listen(descripteur, SOMAXCONN) == -1)
        throw Exception(getLieu() + "Erreur listen: " + strerror(errno));
}

Socket &SocketServeur::Accept(struct sockaddr_in *ip) {
    //int nbMaxClients = 0;
    unsigned int size;
    int so;
    if ((so = accept(descripteur, (struct sockaddr *) ip, &size)) == -1)
        throw Exception(getLieu() + "Erreur connexion: " + strerror(errno));
    Socket *s = new Socket(so, ip);
    return *s;
    /* try {
        if (nbMaxClients >= maxSocketNbr) Send(so, getMessage(TOO_MUCH_CONNECTIONS, "Connection refusée"));
        else Send(so, getMessage(LOGIN, "Connection acceptée"));
    }
    catch (Exception e) {
        throw e;
    }
    nbMaxClients++;
     */
}

std::string SocketServeur::getLieu() {
    return "SocketServeur: ";
}
