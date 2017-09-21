#ifndef SERVEUR_SOCKETSERVEUR_H
#define SERVEUR_SOCKETSERVEUR_H

#include "Socket.h"
#include "SocketClient.h"

class SocketServeur : public Socket {
public:
    SocketServeur(const ipv4 &addr = ipv4().LocalHost, unsigned short port = 26101);

    ~SocketServeur();

protected:
    std::string getLieu() const;

private:
};


#endif //SERVEUR_SOCKETSERVEUR_H
