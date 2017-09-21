#include <unistd.h>
#include <arpa/inet.h>
#include "Socket.h"

Socket::Socket() {
    if ((this->descripteur = socket(AF_INET, SOCK_STREAM, 0)) == -1) {
        throw Exception("Impossible de crée la socket");
    }
}

Socket::Socket(ipv4 &addr, unsigned short port) {
    try {
        Bind(addr, port);
    }
    catch (Exception e) {
        throw e;
    }
}

Socket::~Socket() {
    close(this->descripteur);
}

void Socket::Send(void *message, size_t size) {
    int sizeSend = static_cast<int>(send(this->descripteur, message, size, 0));
    if (sizeSend == -1) throw Exception("Impossible d'envoyer le message.");
    else fprintf(stderr, "Envoi réussi de %d bytes.", sizeSend);
}

void Socket::Recv(void *message, size_t size) {
    int sizeRcv = static_cast<int>(recv(this->descripteur, message, size, 0));
    if (sizeRcv == -1) throw Exception("Erreur lors de la réception de message.");
    else fprintf(stderr, "Réception réussie de %d bytes.", sizeRcv);
}

void Socket::Bind(ipv4 &addr, unsigned short port) {
    this->socketOut = new struct sockaddr_in;
    memset(&this->socketOut, sizeof(struct sockaddr_in));
    this->socketOut->sin_family = AF_INET;
    this->socketOut->sin_addr.s_addr = inet_addr(addr.toString().c_str());
    this->socketOut->sin_port = port;
    if (bind(this->descripteur, (struct sockaddr *) this->socketOut, sizeof(struct sockaddr_in *)) == -1) {
        close(this->descripteur);
        throw Exception("Impossible de bind cette addresse");
    }
    printf("Bind réussi");

}
