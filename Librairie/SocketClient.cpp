#include <cstring>
#include "SocketClient.h"

SocketClient::SocketClient() : SocketClient((ipv4 &) new ipv4(), 65000) {

}

SocketClient::SocketClient(ipv4 &addr, unsigned short port) : Socket() {
    try {
        Bind(addr, port);
    }
    catch (Exception e) {
        throw e;
    }
}

SocketClient::~SocketClient() : ~Socket() {

}
