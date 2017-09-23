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
    void SendTo(const char *message, size_t size, const ipv4 &addr, unsigned short port);

    void RecvFrom(char *message, size_t size);

    //TCP
    void Send(const char *message);

    void Send(std::string message);

    int Recv(char *message, int size);

    int Recv(std::string &message, int size);

    int Recv(std::string &message);

    void SendAck();

    void Close();

    std::string toString();

    std::string getIp();

    unsigned short getPort();

    int getDescripteur();

    static std::string getLieu();

protected:
    virtual void Bind(const ipv4 &, unsigned short port);

    //VAR
    int descripteur;
    struct sockaddr_in *socketOut;
};

#endif //SERVERU_SOCKET_H
