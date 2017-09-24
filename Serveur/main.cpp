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

void traitementConnexion(int *num);

void EcrireMessageErrThread(std::string message);

void EcrireMessageOutThread(std::string message);

void EcrireMessageErr(std::string message);

void EcrireMessageOut(std::string message);

std::string getThread();

/*Variables utilisées par les thread pour le pool de thread*/
Socket *connexion;
pthread_cond_t condConnexion;
pthread_mutex_t mutexConnexion;
pthread_mutex_t mutexLog;
ofstream log("err.log");
pthread_key_t keyNumThread;

int main(int argc, char **args) {
    if (argc == 1) {
        cerr << "Trop peu d'arguments à l'execution" << endl;
        log << "cerr> Trop peu d'arguments à l'execution" << endl;
        return -1;
    }
    lectureFichierParams(args[1]);
    try {
        if (pthread_cond_init(&condConnexion, nullptr) == -1) {
            cerr << "Impossible d'initialiser le condition condConnexion: " << strerror(errno) << endl;
            log << "cerr> Impossible d'initialiser la condition condConnexion: " << strerror(errno) << endl;
            return -1;
        }
        if (pthread_mutex_init(&mutexConnexion, nullptr) == -1) {
            cerr << "Impossible d'initialiser le mutex mutexConnexion: " << strerror(errno) << endl;
            log << "cerr> Impossible d'initialiser le mutex mutexConnexion: " << strerror(errno) << endl;
            return -2;
        }
        if (pthread_mutex_init(&mutexLog, nullptr) == -1) {
            cerr << "Impossible d'initialiser le mutex mutexLog: " << strerror(errno) << endl;
            log << "cerr> Impossible d'initialiser le mutex mutexLog: " << strerror(errno) << endl;
            return -3;
        }
        if (pthread_key_create(&keyNumThread, nullptr) == -1) {
            cerr << "Impossible d'initialiser le mutex mutexLog: " << strerror(errno) << endl;
            log << "cerr> Impossible d'initialiser le mutex mutexLog: " << strerror(errno) << endl;
            return -4;
        }
        // Création du socket
        // Any = 0.0.0.0 => Ecoute toute les addresse
        // Parametres.PortRange[0] => Premier port disponible pour le serveur
        SocketServeur socket(ipv4().Any, Parametres.PortRange[0]);
        cout << "(Socket principal)> " << socket.getIp() << ":" << socket.getPort() << endl;
        log << "cout> (Socket principal)> " << socket.getIp() << ":" << socket.getPort() << endl;

        pthread_t pthread[nbThread];
        for (int i = 0; i < nbThread; i++) {
            //Passage d'un pointeur pour que la valeur ne soit pas modifiée
            int *param = new int;
            *param = i + 1;
            pthread_create(&pthread[i], nullptr, reinterpret_cast<void *(*)(void *)>(traitementConnexion), param);
            pthread_detach(pthread[i]);
        }

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

std::string getThread() {
    int num = *((int *) pthread_getspecific(keyNumThread));
    std::string retour;
    retour += "th_" + std::to_string(num) + "> ";
    return retour;
}

void EcrireMessageErrThread(std::string message) {
    cerr << getThread() << message << endl;
    pthread_mutex_lock(&mutexLog);
    log << "cerr> " << message << endl;
    pthread_mutex_unlock(&mutexLog);
}

void EcrireMessageOutThread(std::string message) {
    cerr << getThread() << message << endl;
    pthread_mutex_lock(&mutexLog);
    log << "cout> " << message << endl;
    pthread_mutex_unlock(&mutexLog);
}

void EcrireMessageErr(std::string message) {
    cerr << message << endl;
    pthread_mutex_lock(&mutexLog);
    log << "cerr> " << message << endl;
    pthread_mutex_unlock(&mutexLog);
}

void EcrireMessageOut(std::string message) {
    cout << message << endl;
    pthread_mutex_lock(&mutexLog);
    log << "cout> " << message << endl;
    pthread_mutex_unlock(&mutexLog);
}

void traitementConnexion(int *num) {
    if (pthread_setspecific(keyNumThread, num) != 0) {
        EcrireMessageErr(
                std::string("th_") + std::to_string(*num) + "> Impossible de mettre num(" + std::to_string(*num) +
                ") dans la variable spécifique au thread.");
        EcrireMessageErr(std::string("th_") + std::to_string(*num) + "> Fin de l'execution de ce thread.");
        return;
    }

    Socket *s = nullptr;

    while (1) {
        pthread_mutex_lock(&mutexConnexion);
        while (s == nullptr) {
            pthread_cond_wait(&condConnexion, &mutexConnexion);
            s = connexion;
        }
        pthread_mutex_unlock(&mutexConnexion);
        EcrireMessageOutThread(s->toString() + " est connecté.");
        bool stop = false;
        while (!stop) {
            try {
                std::string message;
                s->Recv(message);
                s->SendAck();
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
                        EcrireMessageOutThread(s->toString() + " s'est déconnecté.");
                        delete s;
                        s = nullptr;
                        clients--;
                        stop = true;
                    }
                        break;
                    default:
                        EcrireMessageOutThread(std::string("Message <")
                                               + sMessage.message
                                               + "> de type <" +
                                               std::to_string(sMessage.type)
                                               + "> reçu de "
                                               + s->toString());
                        break;
                }
            } catch (Exception e) {
                EcrireMessageErrThread(e.getMessage());
            }
        }
    }
}