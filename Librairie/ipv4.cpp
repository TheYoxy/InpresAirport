#include "ipv4.h"

ipv4::ipv4(const char *addr) {
    try {
        verification(addr);
    }
    catch (Exception e) {
        throw e;
    }
    sscanf(addr, "%d.%d.%d.%d", (int *) &b1, (int *) &b2, (int *) &b3, (int *) &b4);
}

ipv4::ipv4(unsigned char a1, unsigned char a2, unsigned char a3, unsigned char a4) {
    unsigned char **tabl = new unsigned char *[4]{&a1, &a2, &a3, &a4};
    unsigned char **local = new unsigned char *[4]{&b1, &b2, &b3, &b4};
    for (int i = 0; i < 4; i++) {
        if (*tabl[i] < 0 || *tabl[i] > 255) {
            if (*tabl[i] < 0)
                throw Exception(
                        ExceptionHeader + " A la position " + std::to_string(i + 1) + " le nombre est inférieur à 0.");
            else
                throw Exception(ExceptionHeader + " A la position " + std::to_string(i + 1) +
                                " le nombre est supérieur à 255.");
        }
        *local[i] = *tabl[i];
    }
}

void ipv4::verification(const char *addr) {
    std::string s = addr;
    if (s.length() > 15)
        throw Exception(ExceptionHeader + "La longueur de l'ip est incorrecte.");
    int pt = 0;
    for (int i = 0; i < s.length(); i++) {
        if (s[i] != '.') {
            if (!isdigit(s[i]))
                throw Exception(ExceptionHeader +
                                std::string("Le caractère") + s[i] + std::string(" à la postion ") + std::to_string(i) +
                                std::string("n'est pas un chiffre."));
        } else pt++;
    }
    if (pt != 3) throw Exception(ExceptionHeader + "Il y a trop de points.");
}

std::string ipv4::toString() const {
    char *c = new char[15];
    sprintf(c, "%d.%d.%d.%d", this->b1, this->b2, this->b3, this->b4);
    std::string retour = c;
    return retour;
}

const ipv4 ipv4::LocalHost("127.0.0.1");
const std::string ipv4::ExceptionHeader("Mauvaise adresse ip: ");

int ipv4::to32bits() const {
    return (int) inet_addr(this->toString().c_str());
}

in_addr ipv4::toAddr() const {
    in_addr retour;
    inet_aton(toString().c_str(), &retour);
    return retour;
}