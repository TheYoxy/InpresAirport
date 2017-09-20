//
// Created by floryan on 20/09/17.
//

#include "Exception.h"

std::string Exception::getMessage() {
    return this->message;
}

Exception::Exception(std::string message) {
    this->message = message;
}
