#ifndef SERVEUR_IPV4_H
#define SERVEUR_IPV4_H

#include <stdio.h>
#include <cstdio>
#include <iostream>
#include <ctype.h>
#include <arpa/inet.h>
#include "Exception.h"

class ipv4 {
public:
    ipv4(const char *addr = "127.0.0.1");

    ipv4(unsigned char a1, unsigned char a2, unsigned char a3, unsigned char a4);

    std::string toString() const;

    int to32bits() const;

    in_addr toAddr() const;

    static const ipv4 LocalHost;
    static const ipv4 Any;
private:
    void verification(const char *addr);

    static const std::string ExceptionHeader;
    unsigned char b1;
    unsigned char b2;
    unsigned char b3;
    unsigned char b4;
};


#endif //SERVEUR_IPV4_H
