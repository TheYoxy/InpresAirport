#include <signal.h>
#include "../Librairie/SocketClient.h"
#include "../Librairie/SocketServeur.h"

#define CLEAN "\x1B[2J\x1B[H"
#define ErrorLock(couleur, message) pthread_mutex_lock(&mutexEcran);\
 Error(couleur,message);\
 pthread_mutex_unlock(&mutexEcran);
//FLUX ERREUR: FICHIER LOG
//FLUX OUT: CONSOLE
//POOL DE THREAD A IMPLEMENTER
//netstat -tulpn
using namespace std;
extern SParametres Parametres;
extern int maxSocketNbr;
extern int clients;
//Mutex externe bloquant le nombre de clients
extern pthread_mutex_t mutexClient;

void traitementConnexion(int *num);

void EcrireMessageErrThread(const string &message);

void EcrireMessageErrThread(int couleur, const string &message);

void EcrireMessageOutThread(const string &message);

void EcrireMessageErr(const string &message);

void EcrireMessageErr(int couleur, const string &message);

void EcrireMessageOut(const string &message);

string getThread();

bool userExist(const string &user, const string &password);

bool ticketExist(const vector<string> &ticket);

/*Variables utilisées par les thread pour le pool de thread*/
Socket *connexion;
pthread_cond_t condConnexion;
pthread_mutex_t mutexConnexion;
/* Variables utilisées pour le logging */
pthread_mutex_t mutexLog;
ofstream log("err.log", ios::app);
pthread_key_t keyNumThread;
/* Mutex pour le fichier des utilisateurs */
pthread_mutex_t mutexUserDB;
/* Mutex pour le fichier des tickets */
pthread_mutex_t mutexTicketDB;
/* Mutex qui lock l'écriture à l'écran */
pthread_mutex_t mutexEcran;
/* Variables globales pour couper le serveur */
pthread_t pthread[nbThread];
// Création du socket
// Any = 0.0.0.0 => Ecoute toute les addresse
// Parametres.PortRange[0] => Premier port disponible pour le serveur
SocketServeur *socketPrincipal = nullptr;

void HandlerSignal(int sig);

void InitialisationLog();

int main(int argc, char **args) {
    cout << CLEAN;
    InitialisationLog();
    if (argc == 1) {
        Error(RED, "Trop peu d'arguments à l'execution");
        log << "cerr> Trop peu d'arguments à l'execution" << endl;
        Error(RED, "./Serveur [FichierDeConfiguration]");
        log << "cerr> ./Serveur [FichierDeConfiguration]" << endl;
        return -1;
    }
    lectureFichierParams(args[1]);
    try {
        socketPrincipal = new SocketServeur(ipv4().Any, Parametres.PortRange[0]);
        cout << "(Socket principal)> " << socketPrincipal->getIp() << ":" << socketPrincipal->getPort() << endl;
        log << "cout> (Socket principal)> " << socketPrincipal->getIp() << ":" << socketPrincipal->getPort() << endl;
        if (pthread_cond_init(&condConnexion, nullptr) == -1) {
            Error(RED, string("Impossible d'initialiser la condition condConnexion: ") + strerror(errno));
            log << "cerr> Impossible d'initialiser la condition condConnexion: " << strerror(errno) << endl;
            return -2;
        }
        if (pthread_mutex_init(&mutexConnexion, nullptr) == -1) {
            Error(RED, string("Impossible d'initialiser le mutex mutexConnexion: ") + strerror(errno));
            log << "cerr> Impossible d'initialiser le mutex mutexConnexion: " << strerror(errno) << endl;
            return -3;
        }
        if (pthread_mutex_init(&mutexLog, nullptr) == -1) {
            Error(RED, string("Impossible d'initialiser le mutex mutexLog: ") + strerror(errno));
            log << "cerr> Impossible d'initialiser le mutex mutexLog: " << strerror(errno) << endl;
            return -4;
        }
        if (pthread_mutex_init(&mutexUserDB, nullptr) == -1) {
            Error(RED, string("Impossible d'initialiser le mutex mutexUserDB: ") + strerror(errno));
            log << "cerr> Impossible d'initialiser le mutex mutexLog: " << strerror(errno) << endl;
            return -5;
        }
        if (pthread_mutex_init(&mutexTicketDB, nullptr) == -1) {
            Error(RED, string("Impossible d'initialiser le mutex mutexLog: ") + strerror(errno));
            log << "cerr> Impossible d'initialiser le mutex mutexLog: " << strerror(errno) << endl;
            return -6;
        }
        if (pthread_mutex_init(&mutexClient, nullptr) == -1) {
            Error(RED, string("Impossible d'initialiser le mutex mutexClient: ") + strerror(errno));
            log << "cerr> Impossible d'initialiser le mutex mutexLog: " << strerror(errno) << endl;
            return -7;
        }
        if (pthread_mutex_init(&mutexEcran, nullptr) == -1) {
            Error(RED, string("Impossible d'initialiser le mutex mutexEcran: ") + strerror(errno));
            log << "cerr> Impossible d'initialiser le mutex mutexEcran: " << strerror(errno) << endl;
            return -8;
        }
        if (pthread_key_create(&keyNumThread, nullptr) == -1) {
            Error(RED, string("Impossible d'initialiser la clé keyNumThread: ") + strerror(errno));
            log << "cerr> Impossible d'initialiser la clé keyNumThread: " << strerror(errno) << endl;
            return -9;
        }
        struct sigaction sig;
        sigemptyset(&sig.sa_mask);
        sig.sa_handler = HandlerSignal;
        if (sigaction(SIGINT, &sig, nullptr) == -1) {
            Error(RED, string("Impossible d'armer le signal SIGKILL: ") + strerror(errno));
            log << "cerr> Impossible d'armer le signal SIGKILL: " << strerror(errno) << endl;
            return -10;
        }
        for (int i = 0; i < nbThread; i++) {
            //Passage d'un pointeur pour que la valeur ne soit pas modifiée
            int *param = new int;
            *param = i + 1;
            pthread_create(&pthread[i], nullptr, reinterpret_cast<void *(*)(void *)>(traitementConnexion), param);
            pthread_detach(pthread[i]);
        }
        bool bp = true;
        while (bp) {
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
                else {
                    EcrireMessageOut("Fin de l'execution du serveur");
                    bp = false;
                }
            }
        }
        for (int i = 0; i < nbThread; i++) {
            pthread_join(pthread[i], nullptr);
        }
    }
    catch (Exception e) {
        EcrireMessageErr(e.getMessage());
    }
    delete socketPrincipal;
    log.close();
    return 0;
}


void HandlerSignal(int sig) {
    int couleur = CYAN;
    EcrireMessageErr(couleur, "\rDébut du handler de supression");
    for (int i = 0; i < nbThread; i++) {
        if (pthread_cancel(pthread[i]) != 0)
            EcrireMessageErr(RED, "Impossible de cancel le thread numero[" + to_string(i) + "]");
        else
            EcrireMessageErr(couleur, "pthread_cancel(pthread[" + to_string(i) + "]) réussie");
    }
}

string getThread() {
    int num = *((int *) pthread_getspecific(keyNumThread));
    string retour;
    retour += "th_" + to_string(num) + "> ";
    return retour;
}

void EcrireMessageErrThread(int couleur, const string &message) {
    EcrireMessageErr(couleur, getThread() + message);
}

void EcrireMessageErrThread(const string &message) {
    EcrireMessageErr(getThread() + message);
}

void EcrireMessageOutThread(const string &message) {
    EcrireMessageOut(getThread() + message);
}

void EcrireMessageErr(int couleur, const string &message) {
    pthread_mutex_lock(&mutexLog);
    ErrorLock(couleur, message);
    log << "cerr> " << message << endl;
    pthread_mutex_unlock(&mutexLog);
}

void EcrireMessageErr(const string &message) {
    EcrireMessageErr(RED, message);
}

void EcrireMessageOut(const string &message) {
    pthread_mutex_lock(&mutexLog);
    cout << message << endl;
    log << "cout> " << message << endl;
    pthread_mutex_unlock(&mutexLog);
}

bool userExist(const string &user, const string &password) {
    string message;
    pthread_mutex_lock(&mutexUserDB);
    ifstream userFile(Parametres.userDB);
    do {
        message = readLine(userFile);
        vector<string> splits;
        splits = split(message, Parametres.CSVSeparator);
        if (splits[0] == user && splits[1] == password) {
            userFile.close();
            pthread_mutex_unlock(&mutexUserDB);
            return true;
        }
    } while (!userFile.eof());
    userFile.close();
    pthread_mutex_unlock(&mutexUserDB);
    return false;
}

bool ticketExist(const vector<string> &ticket) {
    string message;
    pthread_mutex_lock(&mutexTicketDB);
    ifstream ticketFile(Parametres.ticketDB);
    vector<string> retour;
    do {
        message = readLine(ticketFile);
        retour = split(message, Parametres.CSVSeparator);
        if (retour[0] == ticket[0] && retour[1] == ticket[1]) {
            ticketFile.close();
            pthread_mutex_unlock(&mutexTicketDB);
            return true;
        }
    } while (!ticketFile.eof());
    ticketFile.close();
    pthread_mutex_unlock(&mutexTicketDB);
    retour.clear();
    return false;
}

void supressionThread(void *parms) {
    int couleur = YELLOW;
    ErrorLock(couleur, "Supression Thread");
    int *i = static_cast<int *>(pthread_getspecific(keyNumThread));
    ErrorLock(couleur, "getSpecific");
    pthread_setspecific(keyNumThread, nullptr);
    ErrorLock(couleur, "setSpecific");
    delete i;
    ErrorLock(couleur, "delete");
    pthread_exit(0);
}

void traitementConnexion(int *num) {
    // Initialisation des threads
    sigset_t mask;
    Socket *s = nullptr;

    if (pthread_setspecific(keyNumThread, num) != 0) {
        EcrireMessageErr(string("th_") + to_string(*num) + "> Impossible de mettre num(" + to_string(*num) +
                         ") dans la variable spécifique au thread.");
        EcrireMessageErr(string("th_") + to_string(*num) + "> Fin de l'execution de ce thread.");
        return;
    }
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
    pthread_cleanup_push(supressionThread, nullptr);
        while (1) {
            pthread_mutex_lock(&mutexConnexion);
            while (s == nullptr) {
                struct timespec temps;
                temps.tv_nsec = 0;
                temps.tv_sec = 1;
                if (pthread_cond_timedwait(&condConnexion, &mutexConnexion, &temps) == -1 && errno == ETIMEDOUT)
                    pthread_testcancel();
                else
                    s = connexion;
            }
            pthread_mutex_unlock(&mutexConnexion);
            EcrireMessageOutThread(s->toString() + " est connecté.");
            bool stop = false;
            bool log = false;
            while (!stop) {
                try {
                    string message = "";
                    message.clear();
                    s->Recv(message);
                    SMessage sMessage = getStructMessageFromString(message);
#ifdef Debug
                    ErrorLock(CYAN, "\tMessage reçu de " + s->toString());
                    ErrorLock(CYAN, "\tType : " + typeName(sMessage.type));
                    ErrorLock(CYAN, "\tMessage: " + sMessage.message);
#endif
                    vector<string> vsplit;
                    switch (sMessage.type) {
                        case LOGIN_OFFICER: {
                            vsplit = split(sMessage.message, Parametres.TramesSeparator);
                            bool ue = userExist(vsplit[0], vsplit[1]);
                            s->Send(getMessage(ue ? ACCEPT : REFUSE, string("")));
                            if (ue) {
                                log = true;
                                EcrireMessageOutThread(vsplit[0] + " a ouvert sa session.");
                            }
                        }
                            break;
                        case LOGOUT_OFFICER:
                            log = false;
                            EcrireMessageOutThread(sMessage.message + " a terminé sa session.");
                            break;
                        case CHECK_TICKET:
                            if (log) {
                                vsplit = split(sMessage.message, Parametres.TramesSeparator);
                                s->Send(getMessage(ticketExist(vsplit) ? ACCEPT : REFUSE, string("")));
                            }
                            break;
                        case CHECK_LUGGAGE:
                            if (log) {
                                double poidstot = 0.0, poidsExces = 0.0;
                                vsplit = split(sMessage.message, Parametres.TramesSeparator);
                                for (int i = 0; vsplit[i] != ""; i += 2) {
                                    poidstot += stod(vsplit[i]);
                                    if (stod(vsplit[i]) > 20.0)
                                        poidsExces += (stod(vsplit[i]) - 20.0);
                                }
                                s->Send(getMessage(CHECK_LUGGAGE, to_string(poidsExces) + Parametres.TramesSeparator +
                                                                  to_string(poidstot)));

                            }
                            break;
                        case PAYMENT_DONE:
                            if (log) {
                                s->Send(getMessage(ACCEPT, ""));
                            }
                            break;
                            //Unique point de sortie d'un socket passif du serveur
                        case DISCONNECT:
                            EcrireMessageOutThread(s->toString() + " s'est déconnecté.");
                            delete s;
                            s = nullptr;
                            pthread_mutex_lock(&mutexClient);
                            clients--;
                            pthread_mutex_unlock(&mutexClient);
                            stop = true;
                            break;
                        default:
                            EcrireMessageOutThread(string("Message non-reconnu <")
                                                   + sMessage.message
                                                   + "> de type <" +
                                                   to_string(sMessage.type)
                                                   + "> reçu de "
                                                   + s->toString());
                            break;
                    }
                } catch (Exception e) {
                    EcrireMessageErrThread(e.getMessage());
                }
            }
        }
    pthread_cleanup_pop(1);
    ErrorLock(YELLOW, "Fin execution thread[" + to_string(*num) + "]");
}

void InitialisationLog() {
    time_t t = time(NULL);
    struct tm debut = *localtime(&t);
    for (int i = 0; i < 100; i++)
        log << "=";
    log << endl;
    log << "Date de compilation: " << __DATE__ << endl;
    log << "Date d'execution: " << debut.tm_mday + 1 << "/" << debut.tm_mon << "/" << debut.tm_year << " à "
        << debut.tm_hour << ":" << debut.tm_min << ":" << debut.tm_sec << endl;
    for (int i = 0; i < 100; i++)
        log << "-";
    log << endl;
}