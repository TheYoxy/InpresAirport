//
// Created by floryan on 22/09/17.
//

#ifndef SERVEUR_CONNEXIONEXCEPTION_H
#define SERVEUR_CONNEXIONEXCEPTION_H


#include "Exception.h"

class ConnexionException : public Exception {
public :
    ConnexionException(std::string message);

    std::string getMessage();

private:
};


#endif //SERVEUR_CONNEXIONEXCEPTION_H
