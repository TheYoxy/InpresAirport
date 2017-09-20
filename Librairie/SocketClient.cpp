#include "SocketClient.h"

SocketClient::SocketClient(ipv4 &addr, unsigned short port) : Socket() {
    try {
        Bind(addr, port);
    }
    catch (Exception e) {
        throw e;
    }

}

void SocketClient::Bind(ipv4 &addr, unsigned short port) {
    this->socket = new struct sockaddr_in;
    this->socket->sin_family = AF_INET;
    this->socket->sin_addr.s_addr = inet_addr(addr.toString().c_str());
    this->socket->sin_port = port;
    if (bind(this->descripteur, (struct sockaddr *) this->socket, sizeof(struct sockaddr_in *)) == -1) {
        close(this->descripteur);
        throw Exception("Impossible de bind cette addresse");
    }
    printf("Bind réussi");
}