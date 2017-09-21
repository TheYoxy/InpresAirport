#include "Fonctions.h"

using namespace std;

struct sockaddr_in *CreationSockStruct(const ipv4 &addr, unsigned short port) {
    struct sockaddr_in *retour = (struct sockaddr_in *) malloc(sizeof(struct sockaddr_in));
    memset(retour, 0, sizeof(struct sockaddr_in));
    retour->sin_family = AF_INET;
    retour->sin_addr = addr.toAddr();
    retour->sin_port = port;
    cerr << "Création de " << addr.toString() << ":" << port << endl;
    return retour;
}
