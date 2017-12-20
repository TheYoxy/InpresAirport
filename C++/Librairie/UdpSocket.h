#ifndef SERVEUR_CHAT_UDPSOCKET_H
#define SERVEUR_CHAT_UDPSOCKET_H

#include "Exception.h"
#include "Macro.h"
#include "ipv4.h"
#include "UdpMessage.h"
#include <sys/socket.h>
#include <cstring>
#include <arpa/inet.h>
#include <unistd.h>

class UdpSocket {
public:

    void Close();

    std::string toString();

    unsigned short getPort();

    std::string getIp();

    UdpSocket &operator=(const UdpSocket &socket);

    void SendTo(const char *message, size_t size, const ipv4 &addr, unsigned short port);

    void SendTo(const std::string &message, const ipv4 &addr, unsigned short port);

    void RecvFrom(char *message, size_t size);

    sockaddr_in * Recv(std::string &message);

protected:
    UdpSocket();

    void Bind(const ipv4 &addr, unsigned short port);

private:
    int descripteur;
    struct sockaddr_in *socketOut;
    bool open;
};


#endif //SERVEUR_CHAT_UDPSOCKET_H
