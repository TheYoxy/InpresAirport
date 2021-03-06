#include "Socket.h"

extern SParametres Parametres;

Socket::Socket() {
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

Socket::Socket(struct sockaddr_in *socket) : Socket() {
    socketOut = socket;
    if (bind(this->descripteur, (struct sockaddr *) socket, sizeof(struct sockaddr_in)) == -1) {
        close(this->descripteur);
        throw Exception(EXCEPTION() + "Impossible de bind " + inet_ntoa(socket->sin_addr) + ":" +
                        std::to_string(socket->sin_port) + strerror(errno));
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

Socket::~Socket() {
    try {
        Close();
    }
    catch (Exception e) {

    }
}

void Socket::Bind(const ipv4 &addr, unsigned short port) {
    this->socketOut = CreationSockStruct(addr, port);
    if (bind(this->descripteur, (struct sockaddr *) this->socketOut, sizeof(struct sockaddr_in)) == -1) {
        close(this->descripteur);
        throw Exception(EXCEPTION() + "Impossible de bind " + addr.toString() + ":" + std::to_string(port) + "\n" +
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
    return *this;
}

/**************************************************************/
void Socket::SendTo(const char *message, size_t size, const ipv4 &addr, unsigned short port) {
    struct sockaddr_in *remote = CreationSockStruct(addr, port);
    int sizeSend = (int) (sendto(this->descripteur, message, size, 0, (struct sockaddr *) remote,
                                 sizeof(struct sockaddr_in)));
    if (sizeSend == -1) throw Exception(EXCEPTION() + "Impossible d'envoyer le message: " + strerror(errno));
}

void Socket::RecvFrom(char *message, size_t size) {
    struct sockaddr_in *s = new struct sockaddr_in;
    memset(s, 0, sizeof(struct sockaddr_in));
    memset(message, 0, size);
    unsigned int sizesock = sizeof(struct sockaddr_in);
    int sizeRcv = (int) (recvfrom(this->descripteur, message, size, 0, (struct sockaddr *) s, &sizesock));
    if (sizeRcv == -1) throw Exception(EXCEPTION() + "Impossible de recevoir le message: " + strerror(errno));
}

/**************************************************************/
void Socket::Send(const char *message) {
    try {
        Send(std::string(message));
    }
    catch (Exception e) {
        throw e;
    }
}

void Socket::Send(const std::string message) {
#ifdef Debug
        Error(BLUE, std::string("\tSend string: Type: ") + std::to_string((Type) message[0]) + "("
                << typeName((Type) message[0]) + ")");
        Error(BLUE, std::string("\tSend string: Message: ") + message.substr(1));
#endif
        if (send(descripteur, message.data(), message.length(), 0) == -1)
            throw Exception(EXCEPTION() + "Impossible d'envoyer le message " + strerror(errno));
}

int Socket::Recv(char *message, int size) {
    int taille;
    char *msg = new char[size];
    if ((taille = (int) (recv(descripteur, msg, (size_t) size, 0))) == -1)
        throw Exception(EXCEPTION() + "Impossible de recevoir le message " + strerror(errno));
    strcpy(message, msg);
    return taille;
}

int Socket::Recv(std::string &message, int size) {
    char *msg = new char[size];

    int retour;
    try {
        retour = Recv(msg, size);
    }
    catch (Exception e) {
        throw e;
    }
    message.clear();
    message = msg;
    delete msg;
    return retour;
}

int Socket::Recv(std::string &message) {
    char lu;
    bool stop = false;
    int taille = 0;
    message = "";
    while (!stop) {
        if (recv(descripteur, &lu, 1, 0) == -1)
            throw Exception(EXCEPTION() + "Impossible de recevoir le message " + strerror(errno));
        if (lu == Parametres.FinTramesSeparator) {
            stop = true;
        } else {
            message.push_back(lu);
            taille++;
        }
    }
#ifdef Debug
    Error(GREEN, std::string("\tRecv string: Type: ") + std::to_string((Type) message[0]) + "("
            << typeName((Type) message[0]) + ")");
    Error(GREEN, std::string("\tRecv string: Message: ") + message.substr(1));
#endif
    return taille;
}

/**************************************************************/
void Socket::Close() {
#ifdef Debug
    Error(MAGENTA, std::string("\tFermeture de ") + toString());
#endif
    if (close(descripteur) == -1)
        throw Exception(EXCEPTION() + " Erreur de close: " + strerror(errno));
    open = false;
}

unsigned short Socket::getPort() {
    if (this->socketOut == nullptr)
        throw Exception(EXCEPTION() + "SocketOut is null");
    return htons(this->socketOut->sin_port);
}

std::string Socket::toString() {
    return getIp() + ":" + std::to_string(getPort());
}

std::string Socket::getIp() {
    if (this->socketOut == nullptr)
        throw Exception(EXCEPTION() + "SocketOut is null");
    std::string retour;
    retour = inet_ntoa(this->socketOut->sin_addr);
    return retour;
}

int Socket::getDescripteur() {
    return descripteur;
}

bool Socket::isOpen() {
    return open;
}
