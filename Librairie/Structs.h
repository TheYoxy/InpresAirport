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
    short nbPortRange;
    unsigned short *PortRange;
    int PortAdmin;
    char FinTramesSeparator;
    char CSVSeparator;
    char TramesSeparator;
};
#endif //SERVEUR_STRUCTS_H
