#include <cstring>
#include "SocketClient.h"
#include "ConnexionException.h"

SocketClient::SocketClient(const ipv4 &addr, unsigned short port) : Socket(addr, port) {
}

SocketClient::~SocketClient() {
}

void SocketClient::Connect(const ipv4 &addr, unsigned short port) {
    struct sockaddr_in *ip = CreationSockStruct(addr, port);
    if (connect(descripteur, (struct sockaddr *) ip, sizeof(struct sockaddr_in)) == -1)
        throw Exception(getLieu() + "Erreur de connect: " + strerror(errno));
    Type flag;
    if (recv(descripteur, &flag, 1, 0) == -1)
        throw Exception(getLieu() + "Erreur de recv dans le connect: " + strerror(errno));
    switch (flag) {
        case TOO_MUCH_CONNECTIONS:
            throw ConnexionException("Trop de personnes");
        case ACK:
            if (send(descripteur, &flag, 1, 0) == -1)
                throw Exception(getLieu() + "Erreur de send dans le connect: " + strerror(errno));
            break;
    }
}

std::string SocketClient::getLieu() {
    return "SocketClient: ";
}

SocketClient::SocketClient(int descripteur, struct sockaddr_in *socket) : Socket(descripteur, socket) {

}

SocketClient::SocketClient(struct sockaddr_in *socket) : Socket(socket) {

}
