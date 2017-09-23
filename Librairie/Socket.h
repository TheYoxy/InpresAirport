#ifndef SERVEUR_SOCKET_H
#define SERVEUR_SOCKET_H

#include <unistd.h>
#include <cstring>
#include <cstdio>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <memory.h>
#include <errno.h>
#include "Exception.h"
#include "ipv4.h"
#include "Structs.h"
#include "Fonctions.h"

//Lecture selon le nombre de bytes à lire
//Lecture de bytes en fonction d'une fin de lecture
class Socket {
public:
    Socket();

    Socket(struct sockaddr_in *socket);

    Socket(int descripteur, struct sockaddr_in *socket);

    Socket(const ipv4 &addr, unsigned short port);

    Socket &operator=(const Socket &socket);

    virtual ~Socket();

    //UDP
    virtual void SendTo(const char *message, size_t size, const ipv4 &addr, unsigned short port);

    virtual void RecvFrom(char *message, size_t size);

    //TCP
    virtual void Send(const char *message);

    virtual void Send(std::string message);

    virtual int Recv(char *message, int size);

    virtual int Recv(std::string &message, int size);

    virtual int Recv(std::string &message);

    virtual void Close();

    std::string toString();

    std::string getIp();

    unsigned short getPort();

    int getDescripteur();

protected:
    virtual void Bind(const ipv4 &, unsigned short port);

    static std::string getLieu();

    //VAR
    int descripteur;
    struct sockaddr_in *socketOut;
};

#endif //SERVERU_SOCKET_H
