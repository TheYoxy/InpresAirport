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
#include <fstream>
struct sockaddr_in *CreationSockStruct(const ipv4 &addr, unsigned short port);

const std::string getMessage(Type t, const char *message);

const std::string getMessage(Type t, std::string &message);

SMessage getStructMessageFromString(std::string message);

std::string getStringFromStructMessage(SMessage m);

void lectureFichierParams(const char *nomFichier);
#endif //SERVEUR_FONCTIONS_H
