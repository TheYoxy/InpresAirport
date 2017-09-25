//
// Created by floryan on 20/09/17.
//
#ifndef SERVEUR_STRUCTS_H
#define SERVEUR_STRUCTS_H

#include "Type.h"

typedef struct __Message SMessage;
struct __Message {
    Type type;
    std::string message;
};
typedef struct __Params SParametres;
struct __Params {
    short nbPortRange = -1;
    unsigned short *PortRange = nullptr;
    unsigned short PortAdmin = -1;
    char FinTramesSeparator = -1;
    char CSVSeparator = -1;
    char TramesSeparator = -1;
    std::string userDB = "";
};
#endif //SERVEUR_STRUCTS_H
