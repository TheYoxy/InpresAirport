//
// Created by floryan on 20/09/17.
//
#ifndef SERVEUR_FONCTIONS_H
#define SERVEUR_FONCTIONS_H

#include <iostream>
#include "Structs.h"
#include "ipv4.h"

struct sockaddr_in *CreationSockStruct(const ipv4 &addr, unsigned short port);

#endif //SERVEUR_FONCTIONS_H
