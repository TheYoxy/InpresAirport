#include "Fonctions.h"

using namespace std;
std::string FileSeparator = "\r\n";

struct sockaddr_in *CreationSockStruct(const ipv4 &addr, unsigned short port) {
    struct sockaddr_in *retour = (struct sockaddr_in *) malloc(sizeof(struct sockaddr_in));
    memset(retour, 0, sizeof(struct sockaddr_in));
    retour->sin_family = AF_INET;
    retour->sin_addr = addr.toAddr();
    retour->sin_port = port;
    //cerr << "Création de " << addr.toString() << ":" << port << endl;
    return retour;
}

const string getMessage(Type t, const char *message) {
    std::string retour = " ";
    retour[0] = t;
    retour += message + FileSeparator;
    return retour;
}

const string getMessage(Type t, std::string &message) {
    std::string retour = " ";
    retour[0] = t;
    retour += message + FileSeparator;
    return retour;
}

std::string getStringFromStructMessage(SMessage m) {
    return getMessage(m.type, m.message);
}

SMessage getStructMessageFromString(std::string message) {
    SMessage retour;
    retour.type = (Type) message[0];
    retour.message = message.substr(1);
    return retour;
}
