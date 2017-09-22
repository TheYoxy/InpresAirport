//
// Created by floryan on 20/09/17.
//
#ifndef SERVEUR_FONCTIONS_H
#define SERVEUR_FONCTIONS_H

#include <iostream>
#include "Structs.h"
#include "ipv4.h"
#include "Type.h"
#include <cstring>

struct sockaddr_in *CreationSockStruct(const ipv4 &addr, unsigned short port);

std::string getMessage(Type t, const char *message);

std::string getMessage(Type t, std::string message);
#endif //SERVEUR_FONCTIONS_H
