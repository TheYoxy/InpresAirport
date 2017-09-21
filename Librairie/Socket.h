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

    Socket(const ipv4 &addr, unsigned short port);

    virtual ~Socket();

    virtual void SendTo(const char *message, size_t size, const ipv4 &addr, unsigned short port);

    virtual void Recv(char *message, size_t size);

protected:
    virtual void Bind(const ipv4 &, unsigned short port);

    std::string getLieu() const;

    int descripteur;
    struct sockaddr_in *socketOut;
};

#endif //SERVERU_SOCKET_H
