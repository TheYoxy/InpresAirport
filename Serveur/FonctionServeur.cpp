#include "FonctionServeur.h"

using namespace std;

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