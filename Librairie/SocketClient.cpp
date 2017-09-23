#include <cstring>
#include "SocketClient.h"
#include "ConnexionException.h"

SocketClient::SocketClient(const ipv4 &addr, unsigned short port) try : Socket(addr, port) {
}
catch (Exception e) {
    throw e;
}

SocketClient::SocketClient(int descripteur, struct sockaddr_in *socket) try : Socket(descripteur, socket) {

}
catch (Exception e) {
    throw e;
}

SocketClient::SocketClient(struct sockaddr_in *socket) try : Socket(socket) {

}
catch (Exception e) {
    throw e;
}

SocketClient::~SocketClient() {
}

void SocketClient::Connect(const ipv4 &addr, unsigned short port) {
    struct sockaddr_in *ip = CreationSockStruct(addr, port);
    if (connect(descripteur, (struct sockaddr *) ip, sizeof(struct sockaddr_in)) == -1)
        throw Exception(getLieu() + "Erreur de connect: " + strerror(errno));
    Type flag;
    if (recv(descripteur, &flag, 1, 0) == -1)
        throw Exception(getLieu() + "Impossible de recevoir le message " + strerror(errno));
    switch (flag) {
        case TOO_MUCH_CONNECTIONS:
            throw ConnexionException("Trop de personnes");
        case ACK:
            try {
                if (send(descripteur, &flag, 1, 0) == -1)
                    throw Exception(getLieu() + "Impossible d'envoyer le message " + strerror(errno));
            } catch (Exception e) {
                throw ConnexionException(e.getMessage());
            }
            break;
        default :
            throw ConnexionException("Erreur de flag");
    }
}

std::string SocketClient::getLieu() {
    return "SocketClient: ";
}

