#include "UdpSocket.h"
#include "Fonctions.h"

UdpSocket::UdpSocket() {
    this->open = false;
    if ((this->descripteur = socket(AF_INET, SOCK_STREAM, 0)) == -1)
        throw Exception(EXCEPTION() + "Impossible de créer la socket" + strerror(errno));
    int tru = 1;
    if (setsockopt(this->descripteur, SOL_SOCKET, SO_REUSEADDR, &tru, sizeof(int)) == -1)
        throw Exception(EXCEPTION() + "Impossible de bind le reuseaddr" + strerror(errno));
    if (setsockopt(this->descripteur, SOL_SOCKET, SO_REUSEPORT, &tru, sizeof(int)) == -1)
        throw Exception(EXCEPTION() + "Impossible de bind le reuseport" + strerror(errno));
    this->open = true;
}


void UdpSocket::Close() {
#ifdef Debug
    Error(MAGENTA, std::string("\tFermeture de ") + toString());
#endif
    if (close(descripteur) == -1)
        throw Exception(EXCEPTION() + " Erreur de close: " + strerror(errno));
    open = false;
}

unsigned short UdpSocket::getPort() {
    if (this->socketOut == nullptr)
        throw Exception(EXCEPTION() + "SocketOut is null");
    return htons(this->socketOut->sin_port);
}

std::string UdpSocket::toString() {
    return getIp() + ":" + std::to_string(getPort());
}

std::string UdpSocket::getIp() {
    if (this->socketOut == nullptr)
        throw Exception(EXCEPTION() + "SocketOut is null");
    std::string retour;
    retour = inet_ntoa(this->socketOut->sin_addr);
    return retour;
}

void UdpSocket::Bind(const ipv4 &addr, unsigned short port) {
    this->socketOut = CreationSockStruct(addr, port);
    if (bind(this->descripteur, (struct sockaddr *) this->socketOut, sizeof(struct sockaddr_in)) == -1) {
        close(this->descripteur);
        throw Exception(EXCEPTION() + "Impossible de bind " + addr.toString() + ":" + std::to_string(port) + "\n" +
                        strerror(errno));
    }
}

UdpSocket &UdpSocket::operator=(const UdpSocket &socket) {
    this->descripteur = socket.descripteur;
    this->socketOut = new struct sockaddr_in;
    memset(this->socketOut, 0, sizeof(struct sockaddr_in));
    this->socketOut->sin_addr = socket.socketOut->sin_addr;
    this->socketOut->sin_port = socket.socketOut->sin_port;
    this->socketOut->sin_family = socket.socketOut->sin_family;
    return *this;
}

/* Message: Taille, type, charge utile, */
void UdpSocket::SendTo(const char *message, size_t size, const ipv4 &addr, unsigned short port) {
    struct sockaddr_in *remote = CreationSockStruct(addr, port);
    if (sendto(this->descripteur, message, size, 0, (struct sockaddr *) remote, sizeof(struct sockaddr_in)) == -1)
        throw Exception(EXCEPTION() + "Impossible d'envoyer le message: " + strerror(errno));
    delete remote;
}

void UdpSocket::SendTo(const std::string &message, const ipv4 &addr, unsigned short port) {
    struct sockaddr_in *remote = CreationSockStruct(addr, port);
    if (sendto(this->descripteur, message.c_str(), message.size(), 0,
               (struct sockaddr *) remote, sizeof(struct sockaddr_in)) == -1)
        throw Exception(EXCEPTION() + "Impossible d'envoyer le message (Message): " + strerror(errno));
    delete remote;
}

void UdpSocket::RecvFrom(char *message, size_t size) {
    struct sockaddr_in *s = new struct sockaddr_in;
    memset(s, 0, sizeof(struct sockaddr_in));
    memset(message, 0, size);
    unsigned int sizesock = sizeof(struct sockaddr_in);
    if (recvfrom(this->descripteur, message, size, 0, (struct sockaddr *) s, &sizesock))
        throw Exception(EXCEPTION() + "Impossible de recevoir le message: " + strerror(errno));
    delete s;
}

struct sockaddr_in *UdpSocket::Recv(std::string &message) {
    struct sockaddr_in *s = new struct sockaddr_in;
    memset(s, 0, sizeof(struct sockaddr_in));
    unsigned int sizesock = sizeof(struct sockaddr_in);

}
