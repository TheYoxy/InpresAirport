#include "../Librairie/SocketClient.h"
#include "../Librairie/SocketServeur.h"
#include "FonctionServeur.h"

#define CLEAN "\x1B[2J\x1B[H"
//FLUX ERREUR: FICHIER LOG
//FLUX OUT: CONSOLE
//POOL DE THREAD A IMPLEMENTER
//netstat -tulpn
using namespace std;
extern SParametres Parametres;
//Mutex externe bloquant le nombre de clients
extern pthread_mutex_t mutexClient;


/*Variables utilisées par les thread pour le pool de thread*/
Socket *connexion;
pthread_cond_t condConnexion;
pthread_mutex_t mutexConnexion;
/* Variables utilisées pour le logging */
pthread_mutex_t mutexLog;
ofstream logStream("err.logStream", ios::app);
pthread_key_t keyNumThread;
/* Mutex pour le fichier des utilisateurs */
pthread_mutex_t mutexUserDB;
/* Mutex pour le fichier des tickets */
pthread_mutex_t mutexTicketDB;
/* Mutex qui lock l'écriture à l'écran */
pthread_mutex_t mutexEcran;
/* Variables globales pour couper le serveur */
pthread_t pthread[nbThread];
/* Clef qui stoque le socket d'un thread pour le cleanup */
pthread_key_t keySocketThread;
// Création du socket
// Any = 0.0.0.0 => Ecoute toute les addresse
// Parametres.PortRange[0] => Premier port disponible pour le serveur
SocketServeur *socketPrincipal = nullptr;

int main(int argc, char **args) {
    cout << CLEAN;
    InitialisationLog();
    if (argc == 1) {
        Error(RED, "Trop peu d'arguments à l'execution");
        logStream << "cerr> Trop peu d'arguments à l'execution" << endl;
        Error(RED, "./Serveur [FichierDeConfiguration]");
        logStream << "cerr> ./Serveur [FichierDeConfiguration]" << endl;
        return -1;
    }
    lectureFichierParams(args[1]);
    try {
        socketPrincipal = new SocketServeur(ipv4().Any, Parametres.PortRange[0]);
        cout << "(Socket principal)> " << socketPrincipal->getIp() << ":" << socketPrincipal->getPort() << endl;
        logStream << "cout> (Socket principal)> " << socketPrincipal->getIp() << ":" << socketPrincipal->getPort() << endl;
        if (pthread_cond_init(&condConnexion, nullptr) == -1) {
            Error(RED, string("Impossible d'initialiser la condition condConnexion: ") + strerror(errno));
            logStream << "cerr> Impossible d'initialiser la condition condConnexion: " << strerror(errno) << endl;
            return -2;
        }
        if (pthread_mutex_init(&mutexConnexion, nullptr) == -1) {
            Error(RED, string("Impossible d'initialiser le mutex mutexConnexion: ") + strerror(errno));
            logStream << "cerr> Impossible d'initialiser le mutex mutexConnexion: " << strerror(errno) << endl;
            return -3;
        }
        if (pthread_mutex_init(&mutexLog, nullptr) == -1) {
            Error(RED, string("Impossible d'initialiser le mutex mutexLog: ") + strerror(errno));
            logStream << "cerr> Impossible d'initialiser le mutex mutexLog: " << strerror(errno) << endl;
            return -4;
        }
        if (pthread_mutex_init(&mutexUserDB, nullptr) == -1) {
            Error(RED, string("Impossible d'initialiser le mutex mutexUserDB: ") + strerror(errno));
            logStream << "cerr> Impossible d'initialiser le mutex mutexLog: " << strerror(errno) << endl;
            return -5;
        }
        if (pthread_mutex_init(&mutexTicketDB, nullptr) == -1) {
            Error(RED, string("Impossible d'initialiser le mutex mutexLog: ") + strerror(errno));
            logStream << "cerr> Impossible d'initialiser le mutex mutexLog: " << strerror(errno) << endl;
            return -6;
        }
        if (pthread_mutex_init(&mutexClient, nullptr) == -1) {
            Error(RED, string("Impossible d'initialiser le mutex mutexClient: ") + strerror(errno));
            logStream << "cerr> Impossible d'initialiser le mutex mutexLog: " << strerror(errno) << endl;
            return -7;
        }
        if (pthread_mutex_init(&mutexEcran, nullptr) == -1) {
            Error(RED, string("Impossible d'initialiser le mutex mutexEcran: ") + strerror(errno));
            logStream << "cerr> Impossible d'initialiser le mutex mutexEcran: " << strerror(errno) << endl;
            return -8;
        }
        if (pthread_key_create(&keyNumThread, nullptr) == -1) {
            Error(RED, string("Impossible d'initialiser la clé keyNumThread: ") + strerror(errno));
            logStream << "cerr> Impossible d'initialiser la clé keyNumThread: " << strerror(errno) << endl;
            return -9;
        }
        if (pthread_key_create(&keySocketThread, nullptr) == -1) {
            Error(RED, string("Impossible d'initialiser la clé keyNumThread: ") + strerror(errno));
            logStream << "cerr> Impossible d'initialiser la clé keyNumThread: " << strerror(errno) << endl;
            return -10;
        }
        struct sigaction sig;
        sigemptyset(&sig.sa_mask);
        sig.sa_handler = HandlerSignal;
        sig.sa_flags = 0;
        if (sigaction(SIGINT, &sig, nullptr) == -1) {
            Error(RED, string("Impossible d'armer le signal SIGKILL: ") + strerror(errno));
            logStream << "cerr> Impossible d'armer le signal SIGKILL: " << strerror(errno) << endl;
            return -11;
        }
        for (int i = 0; i < nbThread; i++) {
            //Passage d'un pointeur pour que la valeur ne soit pas modifiée
            int *param = new int;
            *param = i + 1;
            pthread_create(&pthread[i], nullptr, reinterpret_cast<void *(*)(void *)>(traitementConnexion), param);
            //pthread_detach(pthread[i]);
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
#ifdef TRACE
        EcrireMessageErr(CYAN, "Début join");
#endif
        for (int i = 0; i < nbThread; i++) {
            int ret;
            if ((ret = pthread_tryjoin_np(pthread[i], nullptr)) != 0)
                if (ret != EBUSY)
                    EcrireMessageErr(to_string(i) + ": " + strerror(ret));
#ifdef TRACE
            EcrireMessageErr(CYAN, "pthread[" + to_string(i + 1) + "] libéré");
#endif
        }
    }
    catch (Exception e) {
        EcrireMessageErr(e.getMessage());
    }
    delete socketPrincipal;
    logStream.close();
    return 0;
}


