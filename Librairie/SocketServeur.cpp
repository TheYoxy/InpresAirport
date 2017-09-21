//
// Created by floryan on 21/09/17.
//

#include <cstring>
#include "SocketServeur.h"

SocketServeur::SocketServeur() : SocketServeur((ipv4 &) new ipv4(), 65001) {

}

SocketServeur::SocketServeur(ipv4 &addr, unsigned short port) {
    try {
        Bind(addr, port);
    }
    catch (Exception e) {
        throw e;
    }
}


SocketServeur::~SocketServeur() : ~Socket() {

}
