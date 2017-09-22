//
// Created by floryan on 22/09/17.
//

#include "ConnexionException.h"
#include "Fonctions.h"

ConnexionException::ConnexionException(std::string message) : Exception(message) {

}

std::string ConnexionException::getMessage() {
    return "Connexion impossible\nCause: " + Exception::getMessage();
}
