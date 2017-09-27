#ifndef SERVEUR_FONCTIONSERVEUR_H
#define SERVEUR_FONCTIONSERVEUR_H

#include "../Librairie/SocketClient.h"
#include "../Librairie/SocketServeur.h"
#include <fstream>
#include <signal.h>

#define ErrorLock(couleur, message) pthread_mutex_lock(&mutexEcran);\
 Error(couleur,message);\
 pthread_mutex_unlock(&mutexEcran);

extern int maxSocketNbr;
extern int clients;
extern std::ofstream log;
extern SParametres Parametres;

//Mutex externe bloquant le nombre de clients
extern pthread_mutex_t mutexClient;
/*Variables utilisées par les thread pour le pool de thread*/
extern Socket *connexion;
extern pthread_cond_t condConnexion;
extern pthread_mutex_t mutexConnexion;
/* Variables utilisées pour le logging */
extern pthread_mutex_t mutexLog;
extern pthread_key_t keyNumThread;
/* Mutex pour le fichier des utilisateurs */
extern pthread_mutex_t mutexUserDB;
/* Mutex pour le fichier des tickets */
extern pthread_mutex_t mutexTicketDB;
/* Mutex qui lock l'écriture à l'écran */
extern pthread_mutex_t mutexEcran;
/* Variables globales pour couper le serveur */
extern pthread_t pthread[nbThread];
// Création du socket
// Any = 0.0.0.0 => Ecoute toute les addresse
// Parametres.PortRange[0] => Premier port disponible pour le serveur
extern SocketServeur *socketPrincipal;

void HandlerSignal(int sig);

void InitialisationLog();

void EcrireMessageErrThread(const std::string &message);

void EcrireMessageErrThread(int couleur, const std::string &message);

void EcrireMessageOutThread(const std::string &message);

void EcrireMessageErr(const std::string &message);

void EcrireMessageErr(int couleur, const std::string &message);

void EcrireMessageOut(const std::string &message);

std::string getThread();

bool userExist(const std::string &user, const std::string &password);

bool ticketExist(const std::vector<std::string> &ticket);

void traitementConnexion(int *num);

#endif //SERVEUR_FONCTIONSERVEUR_H
