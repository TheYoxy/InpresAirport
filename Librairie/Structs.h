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
#endif //SERVEUR_STRUCTS_H
