#include "Socket.h"

Socket::Socket() {
    if ((this->descripteur = socket(AF_INET, SOCK_STREAM, 0)) == -1)
        throw Exception(getLieu() + "Impossible de créer la socket");
}

Socket::Socket(struct sockaddr_in *socket) : Socket() {
    if (bind(this->descripteur, (struct sockaddr *) socket, sizeof(struct sockaddr_in)) == -1) {
        close(this->descripteur);
        throw Exception(getLieu() + "Impossible de bind " + inet_ntoa(socket->sin_addr) + ":" +
                        std::to_string(socket->sin_port) + "\n" + strerror(errno));
    }
}

Socket::Socket(int descripteur, struct sockaddr_in *socket) {
    this->descripteur = descripteur;
    this->socketOut = new struct sockaddr_in;
    memset(this->socketOut, 0, sizeof(struct sockaddr_in));
    this->socketOut->sin_addr = socket->sin_addr;
    this->socketOut->sin_port = socket->sin_port;
    this->socketOut->sin_family = socket->sin_family;
}

Socket::Socket(const ipv4 &addr, unsigned short port) : Socket() {
    try {
        Bind(addr, port);
    }
    catch (Exception e) {
        throw e;
    }
}

Socket::~Socket() {}

void Socket::Bind(const ipv4 &addr, unsigned short port) {
    this->socketOut = CreationSockStruct(addr, port);
    if (bind(this->descripteur, (struct sockaddr *) this->socketOut, sizeof(struct sockaddr_in)) == -1) {
        close(this->descripteur);
        throw Exception(getLieu() + "Impossible de bind " + addr.toString() + ":" + std::to_string(port) + "\n" +
                        strerror(errno));
    }
//    std::cout << getLieu() << "Bind de " + addr.toString() + ":" + std::to_string(port) + " réussi\n" << std::endl;
}

Socket &Socket::operator=(const Socket &socket) {
    this->descripteur = socket.descripteur;
    this->socketOut = new struct sockaddr_in;
    memset(this->socketOut, 0, sizeof(struct sockaddr_in));
    this->socketOut->sin_addr = socket.socketOut->sin_addr;
    this->socketOut->sin_port = socket.socketOut->sin_port;
    this->socketOut->sin_family = socket.socketOut->sin_family;
}

void Socket::SendTo(const char *message, size_t size, const ipv4 &addr, unsigned short port) {
    struct sockaddr_in *remote = CreationSockStruct(addr, port);
    int sizeSend = (int) (sendto(this->descripteur, message, size, 0, (struct sockaddr *) remote,
                                 sizeof(struct sockaddr_in)));
    if (sizeSend == -1) throw Exception(getLieu() + "Impossible d'envoyer le message: " + strerror(errno));
}

void Socket::RecvFrom(char *message, size_t size) {
    struct sockaddr_in *s = (struct sockaddr_in *) malloc(sizeof(struct sockaddr_in));
    memset(s, 0, sizeof(struct sockaddr_in));
    memset(message, 0, size);
    unsigned int sizesock = sizeof(struct sockaddr_in);
    int sizeRcv = (int) (recvfrom(this->descripteur, message, size, 0, (struct sockaddr *) s, &sizesock));
    if (sizeRcv == -1) throw Exception(getLieu() + "Impossible de recevoir le message: " + strerror(errno));
}

std::string Socket::getLieu() {
    return "Socket: ";
}

void Socket::Send(const char *message) {
    if (send(descripteur, message, strlen(message) + 1, 0) == -1)
        throw Exception(getLieu() + "Impossible d'envoyer le message " + strerror(errno));
}

void Socket::Send(const std::string message) {
    char *m = new char[message.length() + 1];
    memset(m, 0, message.length() + 1);
    strcpy(m, message.c_str());
    if (send(descripteur, message.c_str(), message.length() + 1, 0) == -1)
        throw Exception(getLieu() + "Impossible d'envoyer le message " + strerror(errno));
}

void Socket::Recv(char *message, int *size) {
    if (recv(descripteur, message, (size_t) *size, 0) == -1)
        throw Exception(getLieu() + "Impossible de recevoir le message " + strerror(errno));

}

void Socket::Close() {
    if (close(descripteur) == -1)
        throw Exception(getLieu() + " Erreur de close: " + strerror(errno));
}

unsigned short Socket::getPort() {
    if (this->socketOut == nullptr)
        throw Exception("SocketOut is null");
    return this->socketOut->sin_port;
}

std::string Socket::getIp() {
    if (this->socketOut == nullptr)
        throw Exception("SocketOut is null");
    std::string retour;
    retour = inet_ntoa(this->socketOut->sin_addr);
    return retour;
}
