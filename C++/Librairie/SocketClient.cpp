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
        throw Exception(EXCEPTION() + "Erreur de connect: " + strerror(errno));
}

void SocketClient::CheckIn(const ipv4 &addr, unsigned short port) {
    Connect(addr, port);
    Type flag = ACK;
    if (recv(descripteur, &flag, 1, 0) == -1)
        throw Exception(EXCEPTION() + "Impossible de recevoir le message " + strerror(errno));
    switch (flag) {
        case TOO_MUCH_CONNECTIONS:
            throw ConnexionException("Trop de personnes");
        case ACK:
            try {
                if (send(descripteur, &flag, 1, 0) == -1)
                    throw Exception(EXCEPTION() + "Impossible d'envoyer le message " + strerror(errno));
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
        Type flag = ACK;
        SMessage message;
        message.type = DISCONNECT;
        message.message = "";
        this->Send(getStringFromStructMessage(message));
        if (recv(descripteur, &flag, 1, 0) == -1)
            throw Exception(
                    EXCEPTION() + "Erreur lors de la reception de l'ACK de la deconnection: " + strerror(errno));
        if (flag == ACK)
            stop = true;
    }
}