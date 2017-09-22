//
// Created by floryan on 20/09/17.
//

#ifndef SERVEUR_EXCEPTION_H
#define SERVEUR_EXCEPTION_H

#include <iostream>

class Exception {
public:
    Exception(std::string message);

    virtual std::string getMessage();

protected:
    std::string message;
};


#endif //SERVEUR_EXCEPTION_H
