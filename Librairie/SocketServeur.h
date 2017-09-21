//
// Created by floryan on 21/09/17.
//

#ifndef SERVEUR_SOCKETSERVEUR_H
#define SERVEUR_SOCKETSERVEUR_H

#include "Socket.h"
#include "SocketClient.h"

class SocketServeur : Socket {
public:
    SocketServeur();

    SocketServeur(ipv4 &addr, unsigned short port);

    ~SocketServeur();

protected:
private:
};


#endif //SERVEUR_SOCKETSERVEUR_H
