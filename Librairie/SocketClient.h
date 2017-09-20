#ifndef SERVEUR_SOCKETCLIENT_H
#define SERVEUR_SOCKETCLIENT_H

#include "Socket.h"
#include "Fonctions.h"
#include "ipv4.h"
#include <netinet/in.h>
#include <unistd.h>
#include <arpa/inet.h>

class SocketClient : Socket {
public:
    SocketClient(ipv4 &, unsigned short port);

protected:
    void Bind(ipv4 &, unsigned short port);

    struct sockaddr_in *socket;
private:
};


#endif //SERVEUR_SOCKETCLIENT_H
