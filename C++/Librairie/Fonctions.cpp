#include "Fonctions.h"

using namespace std;
SParametres Parametres;

struct sockaddr_in *CreationSockStruct(const ipv4 &addr, unsigned short port) {
    struct sockaddr_in *retour = new struct sockaddr_in;
    memset(retour, 0, sizeof(struct sockaddr_in));
    retour->sin_family = AF_INET;
    retour->sin_addr = addr.toAddr();
    retour->sin_port = htons(port);
    return retour;
}

std::string getMessage(const Type &t, const char *message) {
    return getMessage(t, std::string(message));
}

std::string getStringFromStructMessage(SMessage m) {
    return getMessage(m.type, m.message);
}

std::string getMessage(const Type &t, const std::string &message) {
    return (char) t + message + Parametres.FinTramesSeparator;
}

SMessage getStructMessageFromString(const std::string &message) {
    SMessage retour;
    retour.type = (Type) message[0];
    retour.message = message.substr(1);
    return retour;
}

//Aucun delete sur buffer, car buffer est managé par le système
void lectureFichierParams(const char *nomFichier) {
    ifstream lecture(nomFichier, ifstream::in);
    int debut = -1, fin = -1;
    do {
        std::vector<std::string> vector = split(readLine(lecture), '=');
        if (!vector[0].compare("ServeurPortDebut")) {
            debut = atoi(vector[1].c_str());
        } else if (!vector[0].compare("ServeurPortFin")) {
            fin = atoi(vector[1].c_str());
        } else if (!vector[0].compare("Admin")) {
            Parametres.PortAdmin = static_cast<unsigned short>(atoi(vector[1].c_str()));
        } else if (!vector[0].compare("Fin-Trames")) {
            Parametres.FinTramesSeparator = vector[1][0];
        } else if (!vector[0].compare("Sep-csv")) {
            Parametres.CSVSeparator = vector[1][0];
        } else if (!vector[0].compare("Sep-Trames")) {
            Parametres.TramesSeparator = vector[1][0];
        } else if (!vector[0].compare("userDB")) {
            Parametres.userDB = vector[1];
        } else if (!vector[0].compare("ticketDB")) {
            Parametres.ticketDB = vector[1];
        } else if (!vector[0].compare("bagageDB")) {
            Parametres.bagageDB = vector[1];
        } else if (!vector[0].compare("PoidsValise")) {
            Parametres.poidsValise = stod(vector[1]);
        } else if (!vector[0].compare("PoidsMain")) {
            Parametres.poidsMain = stod(vector[1]);
        } else if (!vector[0].compare("PayementExces")) {
            Parametres.PayementExces = stod(vector[1]);
        } else
            cout << "Paramètre : \"" << vector[0] << "\" inconnu" << endl;
        if (fin != -1 && debut != -1 && Parametres.PortRange == nullptr) {
            Parametres.nbPortRange = static_cast<short>(fin - debut + 1);
            unsigned short *portRange = new unsigned short[fin - debut];
            for (int i = 0; i <= fin - debut; i++)
                portRange[i] = static_cast<unsigned short>(debut + i);
            Parametres.PortRange = portRange;
        }
    } while (!lecture.eof());
//    if (Parametres.nbPortRange == -1)
//        throw Exception(EXCEPTION() + "Le champs nbPortRange n'a pas de valeur définie");
//    else if (Parametres.PortRange == nullptr)
//        throw Exception(EXCEPTION() + "Le champs PortRange n'a pas de valeur définie");
//    else if (Parametres.PortAdmin == 0)
//        throw Exception(EXCEPTION() + "Le champs PortAdmin n'a pas de valeur définie");
//    else if (Parametres.FinTramesSeparator == -1)
//        throw Exception(EXCEPTION() + "Le champs FinTramesSeparator n'a pas de valeur définie");
//    else if (Parametres.CSVSeparator == -1)
//        throw Exception(EXCEPTION() + "Le champs nbPortRange n'a pas de valeur définie");
//    else if (Parametres.TramesSeparator == -1)
//        throw Exception(EXCEPTION() + "Le champs nbPortRange n'a pas de valeur définie");
//    else if (Parametres.userDB == "") {
//        throw Exception(EXCEPTION() + "Le champs nbPortRange n'a pas de valeur définie");
//    }
}

//On passe pas message par référence car on a besoin d'une copie de celui-ci
std::vector<string> split(std::string message, char delimiter) {
    char *cmessage = new char[message.capacity() + 1];
    strcpy(cmessage, message.c_str());
    vector<string> vecteur;
    string::size_type index;
    do {
        index = message.find(delimiter);
        vecteur.push_back(message.substr(0, index));
        message = message.substr(index + 1);
    } while (index != string::npos);
    return vecteur;
}

std::string readLine(std::istream &stream) {
    char c;
    std::string message;
    do {
        c = (char) stream.get();
        message.push_back(c);
    } while (c != '\n' && c != -1);
    message.pop_back();
    return message;
}

std::string typeName(const Type &t) {
    switch (t) {
        case ACK:
            return std::string("ACK");
        case DISCONNECT:
            return std::string("DISCONNECT");
        case ACCEPT:
            return std::string("ACCEPT");
        case REFUSE:
            return std::string("REFUSE");
        case LOGIN:
            return std::string("LOGIN");
        case LOGOUT:
            return std::string("LOGOUT");
        case CHECK_TICKET:
            return std::string("CHECK_TICKET");
        case ADD_LUGGAGE:
            return std::string("ADD_LUGGAGE");
        case TOO_MUCH_CONNECTIONS:
            return std::string("TOO_MUCH_CONNECTIONS");
        case NO_SELECTED_TICKET:
            return std::string("NO_SELECTED_TICKET");
        case PAYEMENT_DONE:
            return std::string("PAYEMENT_DONE");
        default:
            return std::string("INCONNU");
    }
}

