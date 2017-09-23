#include <cstring>
#include "SocketClient.h"
#include "ConnexionException.h"

SocketClient::SocketClient(const ipv4 &addr, unsigned short port) try : Socket(addr, port) {
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
    Type flag = ACK;
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

void SocketClient::Disconnect() {
    bool stop = false;
    while (!stop) {
        Type flag = DISCONNECT;
        if (send(descripteur, &flag, 1, 0) == -1)
            throw Exception(getLieu() + "Erreur lors de la deconnection: " + strerror(errno));
        if (recv(descripteur, &flag, 1, 0) == -1)
            throw Exception(getLieu() + "Erreur lors de la reception de l'ACK de la deconnection: " + strerror(errno));
        if (flag == ACK)
            stop = true;
    }
}

std::string SocketClient::getLieu() {
    return "SocketClient: ";
}

