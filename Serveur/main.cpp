#include <netdb.h>
#include "../Librairie/SocketClient.h"
#include "../Librairie/SocketServeur.h"
//FLUX ERREUR: FICHIER LOG
//FLUX OUT: CONSOLE
//POOL DE THREAD A IMPLEMENTER
using namespace std;

extern SParametres Parametres;
extern int maxSocketNbr;
extern int clients;

void fctThreadConnectee(Socket *s);

int main(int argc, char **args) {
    if (argc == 1) {
        cout << "Trop peu d'arguments à l'execution" << endl;
        return -1;
    }
    lectureFichierParams(args[1]);
    try {
        SocketServeur socket(ipv4().Any, Parametres.PortRange[0]);
        cerr << "(Serveur): " << socket.getIp() << ":" << socket.getPort() << endl;
        pthread_t pthread;
        while (1) {
            try {
                socket.Listen();
                Socket *s = socket.Accept();
                pthread_create(&pthread, nullptr, reinterpret_cast<void *(*)(void *)>(fctThreadConnectee), s);
                pthread_detach(pthread);
            }
            catch (Exception e) {
                cerr << e.getMessage() << endl;
            }
        }
    }
    catch (Exception e) {
        cerr << e.getMessage() << endl;
    }
    return 0;
}

void fctThreadConnectee(Socket *s) {
    cout << s->toString() << " est connecté." << endl;
    std::string message;
    while (1) {
        try {
            s->Recv(message);
            SMessage sMessage = getStructMessageFromString(message);
            switch (sMessage.type) {
                case LOGIN_OFFICER:
                    break;
                case LOGOUT_OFFICER:
                    break;
                case CHECK_TICKET:
                    break;
                case CHECK_LUGGAGE:
                    break;
                case PAYMENT_DONE:
                    break;
                    //Unique point de sortie d'un socket passif du serveur
                case DISCONNECT: {
                    Type flag = ACK;
                    if (send(s->getDescripteur(), &flag, 1, 0) == -1)
                        throw Exception(
                                s->getLieu() + "Erreur lors de l'envoi de l'ACK de la déconnexion" + strerror(errno));
                    cout << s->toString() << " s'est déconnecté." << endl;
                    s->Close();
                    clients--;
                    return;
                }
                    break;
                default:
                    cout << "Message <" << sMessage.message << "> de type <" << sMessage.type << "> reçu de "
                         << s->toString() << endl;
                    break;
            }
        } catch (Exception e) {
            cout << e.getMessage() << endl;
        }
    }
}