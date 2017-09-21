#include <cstring>
#include "SocketClient.h"

SocketClient::SocketClient(const ipv4 &addr, unsigned short port) : Socket(addr, port) {
}

SocketClient::~SocketClient() {

}

std::string SocketClient::getLieu() const {
    return "SocketClient: ";
}

