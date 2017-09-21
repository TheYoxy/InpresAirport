#ifndef SERVEUR_SOCKET_H
#define SERVEUR_SOCKET_H

#include "Structs.h"
#include "Exception.h"
#include "ipv4.h"
#include <cstdio>
#include <sys/socket.h>
//Lecture selon le nombre de bytes à lire
//Lecture de bytes en fonction d'une fin de lecture
class Socket {
public:
    Socket();

    Socket(ipv4 &addr, unsigned short port);

    virtual ~Socket();
protected:
    virtual void Send(void *message, size_t size);

    virtual void Recv(void *message, size_t size);

    virtual void Bind(ipv4 &, unsigned short port);
    int descripteur;
    struct sockaddr_in *socketOut;
};

#endif //SERVERU_SOCKET_H
