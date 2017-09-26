//
// Created by floryan on 20/09/17.
//
#ifndef SERVEUR_FONCTIONS_H
#define SERVEUR_FONCTIONS_H
#define EXCEPTION() (std::string(__FILE__) + ":" + std::to_string(__LINE__) + " ")
#include <vector>
#include <iostream>
#include "Structs.h"
#include "ipv4.h"
#include "Type.h"
#include <cstring>
#include <fstream>

struct sockaddr_in *CreationSockStruct(const ipv4 &addr, unsigned short port);

std::string getMessage(const Type &t, const char *message);

std::string getMessage(const Type &t, const std::string &message);

SMessage getStructMessageFromString(const std::string &message);

std::string getStringFromStructMessage(SMessage m);

void lectureFichierParams(const char *nomFichier);

std::vector<std::string> split(std::string message, char delimiter);

std::string readLine(std::istream &stream);

std::string typeName(const Type &t);
#endif //SERVEUR_FONCTIONS_H
