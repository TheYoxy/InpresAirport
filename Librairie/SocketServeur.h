#ifndef SERVEUR_SOCKETSERVEUR_H
#define SERVEUR_SOCKETSERVEUR_H

#include "Socket.h"

class SocketServeur : public Socket {
public:
    SocketServeur(const ipv4 &addr = ipv4().LocalHost, unsigned short port = 26010);

    ~SocketServeur();

    void Listen();

    Socket &Accept(struct sockaddr_in *ip);

protected:
    static std::string getLieu();

private:
};


#endif //SERVEUR_SOCKETSERVEUR_H
