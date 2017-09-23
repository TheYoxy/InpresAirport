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

int indexClient;

void traitementConnexion();

Socket *connexion;
pthread_cond_t condConnexion;
pthread_mutex_t mutexConnexion;

int main(int argc, char **args) {
    if (argc == 1) {
        cout << "Trop peu d'arguments à l'execution" << endl;
        return -1;
    }
    lectureFichierParams(args[1]);
    try {
        if (pthread_cond_init(&condConnexion, nullptr) == -1) {
            cerr << "Impossible d'initialiser le pthread_cond: " << strerror(errno) << endl;
            return -1;
        }
        if (pthread_mutex_init(&mutexConnexion, nullptr) == -1) {
            cerr << "Impossible d'initialiser le mutex: " << strerror(errno) << endl;
            return -2;
        }
        pthread_t pthread[nbThread];

        for (int i = 0; i < nbThread; i++) {
            pthread_create(&pthread[i], nullptr, reinterpret_cast<void *(*)(void *)>(traitementConnexion), nullptr);
            pthread_detach(pthread[i]);
        }

        SocketServeur socket(ipv4().Any, Parametres.PortRange[0]);
        cout << "(Serveur): " << socket.getIp() << ":" << socket.getPort() << endl;
        while (1) {
            try {
                socket.Listen();
                Socket *s = socket.Accept();
                pthread_mutex_lock(&mutexConnexion);
                connexion = s;
                pthread_mutex_unlock(&mutexConnexion);
                pthread_cond_signal(&condConnexion);
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

void traitementConnexion() {
    Socket *s = nullptr;
    while (1) {
        pthread_mutex_lock(&mutexConnexion);
        while (s == nullptr) {
            pthread_cond_wait(&condConnexion, &mutexConnexion);
            s = connexion;
        }
        pthread_mutex_unlock(&mutexConnexion);
        cout << s->toString() << " est connecté." << endl;
        bool stop = false;
        while (!stop) {
            try {
                std::string message;
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
                                    s->getLieu() + "Erreur lors de l'envoi de l'ACK de la déconnexion" +
                                    strerror(errno));
                        cout << s->toString() << " s'est déconnecté." << endl;
                        delete s;
                        s = nullptr;
                        clients--;
                        stop = true;
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
}