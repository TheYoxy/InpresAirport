#include <unistd.h>
#include "Socket.h"

Socket::Socket() {
    if ((this->descripteur = socket(AF_INET, SOCK_STREAM, 0)) == -1) {
        throw Exception("Impossible de crée la socket");
    }
}

Socket::~Socket() {
    close(this->descripteur);
}