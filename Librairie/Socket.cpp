#include "Socket.h"

Socket::Socket() {
    if ((this->descripteur = socket(AF_INET, SOCK_STREAM, 0)) == -1) {
        throw Exception("Impossible de crée la socket");
    }
}

/*Socket::Socket(ip &addrip, unsigned short port) : Socket() {
    try {
        this->Bind(addrip, port);
    }
    catch (Exception e) {
        std::cerr << e.getMessage() << std::endl;
        throw e;
    }

}*/

Socket::~Socket() {

}