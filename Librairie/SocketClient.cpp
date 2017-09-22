#include <cstring>
#include "SocketClient.h"

SocketClient::SocketClient(const ipv4 &addr, unsigned short port) : Socket(addr, port) {
}

SocketClient::~SocketClient() {
}

void SocketClient::Connect(const ipv4 &addr, unsigned short port) {
    struct sockaddr_in *ip = CreationSockStruct(addr, port);
    if (connect(descripteur, (struct sockaddr *) ip, sizeof(struct sockaddr_in)) == -1)
        throw Exception(getLieu() + "Erreur de connect: " + strerror(errno));
}

std::string SocketClient::getLieu() {
    return "SocketClient: ";
}
