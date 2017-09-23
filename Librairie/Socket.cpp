#include "Socket.h"

extern SParametres Parametres;
Socket::Socket() {
    if ((this->descripteur = socket(AF_INET, SOCK_STREAM, 0)) == -1)
        throw Exception(getLieu() + "Impossible de créer la socket");
}

Socket::Socket(struct sockaddr_in *socket) : Socket() {
    socketOut = socket;
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
}

Socket &Socket::operator=(const Socket &socket) {
    this->descripteur = socket.descripteur;
    this->socketOut = new struct sockaddr_in;
    memset(this->socketOut, 0, sizeof(struct sockaddr_in));
    this->socketOut->sin_addr = socket.socketOut->sin_addr;
    this->socketOut->sin_port = socket.socketOut->sin_port;
    this->socketOut->sin_family = socket.socketOut->sin_family;
}

/**************************************************************/
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

/**************************************************************/
void Socket::Send(const char *message) {
    Send(std::string(message));
}

void Socket::Send(const std::string message) {
    bool stop = false;
    while (!stop) {
        if (send(descripteur, message.data(), message.length() + 1, 0) == -1)
            throw Exception(getLieu() + "Impossible d'envoyer le message " + strerror(errno));
        Type flag = ACK;
        if (recv(descripteur, &flag, 1, 0) == -1)
            throw Exception(getLieu() + "Impossible de recevoir l'accusé de réception du message: " + strerror(errno));
        if (flag == ACK)
            stop = true;
    }
}

int Socket::Recv(char *message, int size) {
    int taille;
    if ((taille = (int) (recv(descripteur, message, (size_t) size, 0))) == -1)
        throw Exception(getLieu() + "Impossible de recevoir le message " + strerror(errno));
    try {
        SendAck();
    }
    catch (Exception e) {
        throw e;
    }
    return taille;
}

int Socket::Recv(std::string &message, int size) {
    char *msg = new char[size];
    int retour = Recv(msg, size);
    message.clear();
    message = msg;
    delete msg;
    return retour;
}

int Socket::Recv(std::string &message) {
    char lu;
    bool stop = false;
    int taille = 0;
    message.clear();
    while (!stop) {
        if (recv(descripteur, &lu, 1, 0) == -1)
            throw Exception(getLieu() + "Impossible de recevoir le message " + strerror(errno));
        if (lu == Parametres.FinTramesSeparator) {
            stop = true;
        } else {
            message.push_back(lu);
            taille++;
        }
    }
    try {
        SendAck();
    }
    catch (Exception e) {
        throw e;
    }
    return taille;
}

/**************************************************************/
void Socket::Close() {
    if (close(descripteur) == -1)
        throw Exception(getLieu() + " Erreur de close: " + strerror(errno));
}

unsigned short Socket::getPort() {
    if (this->socketOut == nullptr)
        throw Exception("SocketOut is null");
    return this->socketOut->sin_port;
}

std::string Socket::toString() {
    return getIp() + ":" + std::to_string(getPort());
}

std::string Socket::getIp() {
    if (this->socketOut == nullptr)
        throw Exception("SocketOut is null");
    std::string retour;
    retour = inet_ntoa(this->socketOut->sin_addr);
    return retour;
}

int Socket::getDescripteur() {
    return descripteur;
}

std::string Socket::getLieu() {
    return std::to_string(__LINE__) + ">Socket: ";
}

void Socket::SendAck() {
    Type flag = ACK;
    if (send(descripteur, &flag, 1, 0) == -1)
        throw Exception(getLieu() + "Impossible d'envoyer l'accusé de réception" + strerror(errno));
}
