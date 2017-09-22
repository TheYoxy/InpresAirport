#ifndef SERVEUR_SOCKETCLIENT_H
#define SERVEUR_SOCKETCLIENT_H

#include "Socket.h"

class SocketClient : public Socket {
public:
    SocketClient(const ipv4 &addr = ipv4().LocalHost, unsigned short port = 26100);

    ~SocketClient();

    void Connect(const ipv4 &addr, unsigned short port);

protected:
    static std::string getLieu();

private:
};


#endif //SERVEUR_SOCKETCLIENT_H
