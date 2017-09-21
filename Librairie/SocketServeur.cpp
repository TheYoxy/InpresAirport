#include "SocketServeur.h"


SocketServeur::SocketServeur(const ipv4 &addr, unsigned short port) : Socket(addr, port) {
}


SocketServeur::~SocketServeur() {

}

std::string SocketServeur::getLieu() const {
    return "SocketServeur: ";
}
