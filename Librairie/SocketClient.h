#ifndef SERVEUR_SOCKETCLIENT_H
#define SERVEUR_SOCKETCLIENT_H

#include "Socket.h"
#include "Fonctions.h"
#include "ipv4.h"
#include <netinet/in.h>
#include <unistd.h>
#include <arpa/inet.h>

class SocketClient : public Socket {
public:
    SocketClient(const ipv4 &addr = ipv4().LocalHost, unsigned short port = 26100);

    ~SocketClient();

protected:
    std::string getLieu() const;

private:
};


#endif //SERVEUR_SOCKETCLIENT_H
