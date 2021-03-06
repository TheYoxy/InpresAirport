#ifndef SERVEUR_SOCKETSERVEUR_H
#define SERVEUR_SOCKETSERVEUR_H
#define nbThread 5
#include "Socket.h"

class SocketServeur : public Socket {
public:
    SocketServeur(const ipv4 &addr = ipv4().Any, unsigned short port = 26010);

    SocketServeur(struct sockaddr_in *socket);

    SocketServeur(int descripteur, struct sockaddr_in *socket);

    ~SocketServeur();

    Socket *Accept();

protected:

private:
    void Listen();
};


#endif //SERVEUR_SOCKETSERVEUR_H
