#include "Fonctions.h"

using namespace std;
SParametres Parametres;
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
    retour += message + Parametres.FinTramesSeparator;
    return retour;
}

const string getMessage(Type t, std::string &message) {
    std::string retour = " ";
    retour[0] = t;
    retour += message + Parametres.FinTramesSeparator;
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

//Aucun delete sur buffer, car buffer est managé par le système
void lectureFichierParams(const char *nomFichier) {
    ifstream lecture(nomFichier, ifstream::in);
    char *param;
    do {
        char *buffer = new char[1024];
        lecture.getline(buffer, 1024);
        param = strsep(&buffer, "=");
        if (!strcmp(param, "ServeurPortDebut")) {
            int debut = atoi(buffer);
            buffer = new char[1024];
            lecture.getline(buffer, 1024);
            param = strsep(&buffer, "=");
            int fin = atoi(buffer);
            Parametres.nbPortRange = static_cast<short>(fin - debut + 1);
            unsigned short *portRange = new unsigned short[fin - debut];
            for (int i = 0; i <= fin - debut; i++)
                portRange[i] = static_cast<unsigned short>(debut + i);
            Parametres.PortRange = portRange;
        } else if (!strcmp(param, "Admin")) {
            Parametres.PortAdmin = atoi(buffer);
        } else if (!strcmp(param, "Fin-Trames")) {
            Parametres.FinTramesSeparator = buffer;
        } else if (!strcmp(param, "Sep-csv")) {
            Parametres.CSVSeparator = buffer;
        } else if (!strcmp(param, "Sep-Trames")) {
            Parametres.TramesSeparator = buffer;
        } else
            cout << param << " inconnu" << endl;
    } while (!lecture.eof());
    delete param;
}
