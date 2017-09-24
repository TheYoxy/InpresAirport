#include <netdb.h>
#include <signal.h>
#include "../Librairie/SocketClient.h"
#include "../Librairie/SocketServeur.h"
//FLUX ERREUR: FICHIER LOG
//FLUX OUT: CONSOLE
//POOL DE THREAD A IMPLEMENTER
using namespace std;
extern SParametres Parametres;
extern int maxSocketNbr;
extern int clients;

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
/* Variables utilisées pour le logging */
pthread_mutex_t mutexLog;
ofstream log("err.log");
pthread_key_t keyNumThread;
pthread_key_t keySocket;
/* Variables globales pour couper le serveur */
pthread_t pthread[nbThread];
pthread_mutex_t mutexTMain;
pthread_t tmain;
// Création du socket
// Any = 0.0.0.0 => Ecoute toute les addresse
// Parametres.PortRange[0] => Premier port disponible pour le serveur
SocketServeur *socketPrincipal = nullptr;

void HandlerSignal(int sig);

void cleanupThread(void *sig);

int main(int argc, char **args) {
    cout << "\x1B[2J\x1B[H";
    if (argc == 1) {
        cerr << "Trop peu d'arguments à l'execution" << endl;
        log << "cerr> Trop peu d'arguments à l'execution" << endl;
        return -1;
    }
    lectureFichierParams(args[1]);
    try {
        socketPrincipal = new SocketServeur(ipv4().Any, Parametres.PortRange[0]);
        cout << "(Socket principal)> " << socketPrincipal->getIp() << ":" << socketPrincipal->getPort() << endl;
        log << "cout> (Socket principal)> " << socketPrincipal->getIp() << ":" << socketPrincipal->getPort() << endl;
        if (pthread_cond_init(&condConnexion, nullptr) == -1) {
            cerr << "Impossible d'initialiser le condition condConnexion: " << strerror(errno) << endl;
            log << "cerr> Impossible d'initialiser la condition condConnexion: " << strerror(errno) << endl;
            return -2;
        }
        if (pthread_mutex_init(&mutexConnexion, nullptr) == -1) {
            cerr << "Impossible d'initialiser le mutex mutexConnexion: " << strerror(errno) << endl;
            log << "cerr> Impossible d'initialiser le mutex mutexConnexion: " << strerror(errno) << endl;
            return -3;
        }
        if (pthread_mutex_init(&mutexLog, nullptr) == -1) {
            cerr << "Impossible d'initialiser le mutex mutexLog: " << strerror(errno) << endl;
            log << "cerr> Impossible d'initialiser le mutex mutexLog: " << strerror(errno) << endl;
            return -4;
        }
        if (pthread_mutex_init(&mutexTMain, nullptr) == -1) {
            cerr << "Impossible d'initialiser le mutex mutexTMain: " << strerror(errno) << endl;
            log << "cerr> Impossible d'initialiser le mutex mutexTMain: " << strerror(errno) << endl;
            return -5;
        }
        if (pthread_key_create(&keyNumThread, nullptr) == -1) {
            cerr << "Impossible d'initialiser le mutex mutexLog: " << strerror(errno) << endl;
            log << "cerr> Impossible d'initialiser le mutex mutexLog: " << strerror(errno) << endl;
            return -6;
        }
        if (pthread_key_create(&keySocket, nullptr) == -1) {
            cerr << "Impossible d'initialiser le mutex mutexLog: " << strerror(errno) << endl;
            log << "cerr> Impossible d'initialiser le mutex mutexLog: " << strerror(errno) << endl;
            return -7;
        }
        struct sigaction sig;
        sigemptyset(&sig.sa_mask);
        sig.sa_handler = HandlerSignal;
        if (sigaction(SIGINT, &sig, nullptr) == -1) {
            cerr << "Impossible d'armer le signal SIGKILL: " << strerror(errno) << endl;
            log << "cerr> Impossible d'armer le signal SIGKILL: " << strerror(errno) << endl;
            return -8;
        }
        tmain = pthread_self();
        for (int i = 0; i < nbThread; i++) {
            //Passage d'un pointeur pour que la valeur ne soit pas modifiée
            int *param = new int;
            *param = i + 1;
            pthread_create(&pthread[i], nullptr, reinterpret_cast<void *(*)(void *)>(traitementConnexion), param);
            pthread_detach(pthread[i]);
        }

        int i = 00;
        while (socketPrincipal != nullptr) {
            try {
                Socket *s = socketPrincipal->Accept();
                pthread_mutex_lock(&mutexConnexion);
                connexion = s;
                pthread_mutex_unlock(&mutexConnexion);
                pthread_cond_signal(&condConnexion);
            }
            catch (Exception e) {
                if (errno != EINTR)
                    EcrireMessageErr(e.getMessage());
                else
                    EcrireMessageErr("Fin de l'execution du serveur");
            }
        }
    }
    catch (Exception e) {
        EcrireMessageErr(e.getMessage());
    }
    log.close();
    return 0;
}

void HandlerSignal(int sig) {
    //Permet de retourner au début de la ligne
    printf("\r");
    cout << "SIGINT reçu" << endl;
    cout << "Début du handler de supression" << endl;
    for (int i = 0; i < nbThread; i++) {
        cout << "Envoi du signal au thread " << i << endl;
        if (pthread_cancel(pthread[i]) != 0) //TODO Demander si le cancel fait sortir d'une fonction en attente
        {
            EcrireMessageErr("Impossible de cancel le thread numero[" + to_string(i) + "]");
        }
        pthread_yield();
        sched_yield();
//        pthread_join(pthread[i], nullptr);
    }
    delete socketPrincipal;
    socketPrincipal = nullptr;
}

std::string getThread() {
    int num = *((int *) pthread_getspecific(keyNumThread));
    std::string retour;
    retour += "th_" + std::to_string(num) + "> ";
    return retour;
}

void EcrireMessageErrThread(std::string message) {
    pthread_mutex_lock(&mutexLog);
    cerr << getThread() << message << endl;
    log << "cerr> " << message << endl;
    pthread_mutex_unlock(&mutexLog);
}

void EcrireMessageOutThread(std::string message) {
    pthread_mutex_lock(&mutexLog);
    cerr << getThread() << message << endl;
    log << "cout> " << message << endl;
    pthread_mutex_unlock(&mutexLog);
}

void EcrireMessageErr(std::string message) {
    pthread_mutex_lock(&mutexLog);
    cerr << message << endl;
    log << "cerr> " << message << endl;
    pthread_mutex_unlock(&mutexLog);
}

void EcrireMessageOut(std::string message) {
    pthread_mutex_lock(&mutexLog);
    cout << message << endl;
    log << "cout> " << message << endl;
    pthread_mutex_unlock(&mutexLog);
}

void cleanupThread(void *sig) {
    EcrireMessageErrThread("Fonction cleanup");
    Socket *s = (Socket *) pthread_getspecific(keySocket);
    if (s != nullptr) {
        s->Close();
        delete s;
    }
    pthread_setspecific(keySocket, nullptr);
    EcrireMessageErr("Socket supprimé");
    int *i = (int *) pthread_getspecific(keyNumThread);
    if (s != nullptr)
        delete i;
    pthread_setspecific(keyNumThread, nullptr);
    cout << "Fin du cleanup" << endl;
}

void traitementConnexion(int *num) {
    // Initialisation des threads
    sigset_t mask;
    Socket *s = nullptr;
    pthread_setspecific(keyNumThread, num);
    if (sigemptyset(&mask) != 0) {
        EcrireMessageErrThread("Impossible de réinitialiser le mask");
        EcrireMessageErrThread("Fin de l'execution de ce thread");
        return;
    }
    if (sigaddset(&mask, SIGINT) != 0) {
        EcrireMessageErrThread("Impossible d'ajouter SIGKILL au mask");
        EcrireMessageErrThread("Fin de l'execution de ce thread");
        return;
    }
    if (sigprocmask(SIG_SETMASK, &mask, nullptr) != 0) {
        EcrireMessageErrThread("Impossible de mettre en place le mask");
        EcrireMessageErrThread("Fin de l'execution de ce thread");
        return;
    }
    if (pthread_setcancelstate(PTHREAD_CANCEL_ENABLE, nullptr) != 0) {
        EcrireMessageErrThread("Impossible de mettre en place le cancelstate");
        EcrireMessageErrThread("Fin de l'execution de ce thread");
        return;
    }
    if (pthread_setcanceltype(PTHREAD_CANCEL_DEFERRED, nullptr) != 0) {
        EcrireMessageErrThread("Impossible de mettre en place le canceltype");
        EcrireMessageErrThread("Fin de l'execution de ce thread");
        return;
    }
    // Fin initialisation du thread
    pthread_cleanup_push(cleanupThread, 0);
        bool boucle = true;
        while (boucle) {
            pthread_mutex_lock(&mutexConnexion);
            while (s == nullptr) {
                pthread_cond_wait(&condConnexion, &mutexConnexion);
                s = connexion;
                pthread_setspecific(keySocket, s);
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
                    if (errno == EINTR)
                        boucle = false;
                    EcrireMessageErrThread(e.getMessage());
                }
            }
            pthread_setspecific(keySocket, nullptr);
        }
    pthread_cleanup_pop(1);
    return;
}