#ifndef INPRESAIRPORT_SOCKET_H
#define INPRESAIRPORT_SOCKET_H

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

    //Socket(ip&, unsigned short port);
    //bool bind();
    ~Socket();

protected:

    virtual void Bind(ipv4 &, unsigned short port) = 0;

    int descripteur;
};

#endif //INPRESAIRPORT_SOCKET_H
