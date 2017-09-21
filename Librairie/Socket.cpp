#include <unistd.h>
#include <arpa/inet.h>
#include <cstring>
#include "Socket.h"
#include "Fonctions.h"

Socket::Socket() {
    if ((this->descripteur = socket(AF_INET, SOCK_DGRAM, 0)) == -1) {
        throw Exception(getLieu() + "Impossible de créer la socket");
    }
}

Socket::Socket(const ipv4 &addr, unsigned short port) : Socket() {
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

void Socket::SendTo(const char *message, size_t size, const ipv4 &addr, unsigned short port) {
    struct sockaddr_in *remote = CreationSockStruct(addr, port);
    std::cout << getLieu() << "(Send)Message :" << (char *) message << std::endl;
    int sizeSend = static_cast<int>(sendto(this->descripteur, message, size, 0, (struct sockaddr *) remote,
                                           sizeof(struct sockaddr_in)));
    if (sizeSend == -1) throw Exception(getLieu() + "Impossible d'envoyer le message: " + strerror(errno));
    std::cout << getLieu() << "(Send)Envoi réussi de " << sizeSend << " bytes." << std::endl;
}

void Socket::Recv(char *message, size_t size) {
    struct sockaddr_in *s = (struct sockaddr_in *) malloc(sizeof(struct sockaddr_in));
    memset(s, 0, sizeof(struct sockaddr_in));
    memset(message, 0, size);
    unsigned int sizesock = sizeof(struct sockaddr_in);
    int sizeRcv = static_cast<int>(recvfrom(this->descripteur, message, size, 0, (struct sockaddr *) s, &sizesock));
    if (sizeRcv == -1)throw Exception(getLieu() + "Impossible de recevoir le message: " + strerror(errno));
    std::cout << getLieu() << "(Recv)Réception réussie de " << sizeRcv << " bytes." << std::endl;
    std::cout << getLieu() << "(Recv)Message envoyé par " << inet_ntoa(s->sin_addr) << ":" << s->sin_port << std::endl;
    std::cout << getLieu() << "(Recv)Message : " << (char *) message << std::endl;
}

void Socket::Bind(const ipv4 &addr, unsigned short port) {
    this->socketOut = CreationSockStruct(addr, port);
    if (bind(this->descripteur, (struct sockaddr *) this->socketOut, sizeof(struct sockaddr_in)) == -1) {
        close(this->descripteur);
        std::string message =
                std::string(getLieu()) + "Impossible de bind " + addr.toString() + ":" + std::to_string(port) + "\n" +
                strerror(errno);
        throw Exception(message);
    }
    std::cout << getLieu() << "Bind de " + addr.toString() + ":" + std::to_string(port) + " réussi\n" << std::endl;
}

std::string Socket::getLieu() const {
    return "Socket: ";
}