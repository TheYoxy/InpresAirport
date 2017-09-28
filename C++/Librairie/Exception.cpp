//
// Created by floryan on 20/09/17.
//

#include "Exception.h"

Exception::Exception(std::string message) {
    this->message = message;
}

const std::string Exception::getMessage() const {
    return this->message;
}
