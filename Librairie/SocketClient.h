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
    SocketClient();

    SocketClient(ipv4 &addr, unsigned short port);

    ~SocketClient();

protected:

private:
};


#endif //SERVEUR_SOCKETCLIENT_H
