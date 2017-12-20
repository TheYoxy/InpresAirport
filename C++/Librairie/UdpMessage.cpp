//
// Created by floryan on 29/11/17.
//

#include "UdpMessage.h"

UdpMessage::UdpMessage() {
    idMessage = cmpt++;
}

UdpMessage::UdpMessage(int type, const std::string &message) : idMessage(cmpt++),
                                                               type(type),
                                                               message(message)
{

}

char UdpMessage::getIdMessage() const {
    return idMessage;
}

char UdpMessage::getType() const {
    return type;
}

void UdpMessage::setType(char type) {
    this->type = type;
}

const std::string &UdpMessage::getMessage() const {
    return message;
}

void UdpMessage::setMessage(const std::string &message) {
    this->message = message;
}

void UdpMessage::fromString(std::string message) {

}

std::string UdpMessage::toString() const {
    return std::string();
}
