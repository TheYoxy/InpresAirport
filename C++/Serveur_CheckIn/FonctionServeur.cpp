#include "FonctionServeur.h"

using namespace std;

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
    ErrorLock(couleur, message);
    pthread_mutex_lock(&mutexLog);
    logStream << "cerr> " << message << endl;
    pthread_mutex_unlock(&mutexLog);
}

void EcrireMessageErr(const string &message) {
    EcrireMessageErr(RED, message);
}

void EcrireMessageOut(const string &message) {
    pthread_mutex_lock(&mutexEcran);
    cout << message << endl;
    pthread_mutex_unlock(&mutexEcran);
    pthread_mutex_lock(&mutexLog);
    logStream << "cout> " << message << endl;
    pthread_mutex_unlock(&mutexLog);
}

void HandlerSignal(int sig) {
#ifdef TRACE
    int couleur = CYAN;
    EcrireMessageErr(couleur, "\rDébut du handler de supression");
#endif
    for (int i = 0; i < nbThread; i++) {
        if (pthread_cancel(pthread[i]) != 0)
            EcrireMessageErr(RED, "Impossible de cancel le thread numero[" + to_string(i) + "]");
#ifdef TRACE
        else
            EcrireMessageErr(couleur, "pthread_cancel(pthread[" + to_string(i) + "]) réussie");
#endif
    }
#ifdef TRACE
    EcrireMessageErr(couleur, "Fin du handler de supression");
#endif
}

void supressionThread(void *parms) {
    int couleur = YELLOW;
    int *i = static_cast<int *>(pthread_getspecific(keyNumThread));
#ifdef TRACE
    int num = *i;
    ErrorLock(couleur, string("Supression Thread") + to_string(num));
#endif
    pthread_setspecific(keyNumThread, nullptr);
    Socket *s = static_cast<Socket *>(pthread_getspecific(keySocketThread));
    if (s != nullptr) {
        ErrorLock(couleur, string("Supression socket: ") + s->toString());
        delete s;
    }
    delete i;
#ifdef TRACE
    ErrorLock(couleur, string("Fin supression Thread") + to_string(num));
#endif
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
    if (pthread_setcanceltype(PTHREAD_CANCEL_ASYNCHRONOUS, nullptr) != 0) {
        EcrireMessageErrThread("Impossible de mettre en place le canceltype");
        EcrireMessageErrThread("Fin de l'execution de ce thread");
        return;
    }
    // Fin initialisation du thread
    pthread_cleanup_push(supressionThread, nullptr);
        EcrireMessageErrThread(YELLOW, std::string("Lancement du thread ") + std::to_string(*num));
        while (1) {
            pthread_mutex_lock(&mutexConnexion);
            while (s == nullptr) {
                struct timespec temps;
                temps.tv_nsec = 0;
                temps.tv_sec = 1000;
                int ret;
                if ((ret = pthread_cond_timedwait(&condConnexion, &mutexConnexion, &temps)) != 0) {
                    if (ret == ETIMEDOUT) {
                        //EcrireMessageErrThread(GREEN, "TIMEOUT");
                        pthread_testcancel();
                    } else
                        throw Exception(EXCEPTION() + string("pthread_cond_timedwait: ") + strerror(ret));
                } else {
                    s = connexion;
#ifdef TRACE
                    ErrorLock(MAGENTA, s);
#endif
                    pthread_setspecific(keySocketThread, s);
                }
            }
            pthread_mutex_unlock(&mutexConnexion);
            EcrireMessageOutThread(s->toString() + " est connecté.");
            bool stop = false;
            bool log = false;
            vector<string> ticket;
            string bagages;
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
                        case LOGIN: {
                            vsplit = split(sMessage.message, Parametres.TramesSeparator);
                            bool ue = userExist(vsplit[0], vsplit[1]);
                            s->Send(getMessage(ue ? ACCEPT : REFUSE, string("")));
                            if (ue) {
                                log = true;
                                EcrireMessageOutThread(vsplit[0] + " a ouvert sa session.");
                            }
                        }
                            break;
                        case LOGOUT:
                            log = false;
                            ticket.clear();
                            bagages.clear();
                            EcrireMessageOutThread(sMessage.message + " a terminé sa session.");
                            break;
                        case CHECK_TICKET:
                            if (log) {
                                vsplit = split(sMessage.message, Parametres.TramesSeparator);
                                Type reponse = ticketExist(vsplit) ? ACCEPT : REFUSE;
                                s->Send(getMessage(reponse, string("")));
                                if (reponse == ACCEPT)
                                    ticket = vsplit;
                            }
                            break;
                        case ADD_LUGGAGE:
                            //Si un ticket est en utilisation
                            if (log && ticket.size() > 0) {
                                double poidsTot, poidsExces;
                                addLuggage(ticket, sMessage, &poidsTot, &poidsExces);

                                EcrireMessageOutThread("Poids total  : " + to_string(round(poidsTot * 100) / 100));
                                EcrireMessageOutThread("Poids en trop: " + to_string(round(poidsExces * 100) / 100));
                                EcrireMessageOutThread("Supplément à payer: " + to_string(
                                        round(poidsExces * Parametres.PayementExces * 100) / 100));

                                s->Send(getMessage(ADD_LUGGAGE, to_string(poidsTot) + Parametres.TramesSeparator +
                                                                to_string(poidsExces) + Parametres.TramesSeparator +
                                                                to_string(poidsExces * Parametres.PayementExces)));
                                bagages = sMessage.message;
                            } else if (ticket.size() == 0) {
                                s->Send(getMessage(NO_SELECTED_TICKET, ""));
                                bagages.clear();
                                ticket.clear();
                            }
                            break;
                        case PAYEMENT_DONE:
                            //Si un ticket est en utilisation
                            if (log && ticket.size() > 0) {
                                payement(ticket, bagages);
                                ticket.clear();
                                bagages.clear();
                            }
                            break;
                            //Unique point de sortie d'un socket passif du serveur
                        case DISCONNECT:
                            EcrireMessageOutThread(s->toString() + " s'est déconnecté.");
                            delete s;
                            s = nullptr;
                            pthread_setspecific(keySocketThread, nullptr);
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
#ifdef Debug
                ErrorLock(RED, "\tTicket utilisé: ");
                if (ticket.size() >= 2) {
                    ErrorLock(RED, "\t\tVol: " + ticket[0] + "\n\t\tNuméro de billet: " + ticket[1]);
                } else {
                    ErrorLock(RED, "\t\tAucun vol sélectionné");
                }
#endif
            }
        }
    pthread_cleanup_pop(0);
    ErrorLock(YELLOW, "Fin execution thread[" + to_string(*num) + "]");
}

void addLuggage(const vector<string> &ticket, const SMessage &sMessage, double *poidsTot, double *poidsExces) {
    if (poidsTot == nullptr) throw Exception(EXCEPTION() + "poidsTot is null");
    if (poidsExces == nullptr) throw Exception(EXCEPTION() + "poidsExces is null");
    //Le split doit être utilisé sur un index en base 2
    vector<string> vsplit = split(sMessage.message, Parametres.TramesSeparator);
    for (int i = 0; i < (int) vsplit.size(); i += 2) {
//        Error(RED, string("Valeurs: ") + vsplit[i] + "|" + vsplit[i + 1]);
        double max = (vsplit[i][0] == 'V' || vsplit[i][0] == 'v' ? Parametres.poidsValise
                                                                 : Parametres.poidsMain);
        double poids = stod(vsplit[i + 1]);
        *poidsTot += poids;
        *poidsExces += (poids - max);
        if (*poidsExces < 0)
            *poidsExces = 0;
    }
}

void payement(const vector<string> &ticket, const string &message) {
    ErrorLock(RED, "payement");
    time_t t = time(NULL);
    struct tm *debut = localtime(&t);
    string filename = "";
    string varname = "";
    varname += ticket[0] + "_" + ticket[1] + "_";
    varname += to_string(debut->tm_mday) + to_string(1 + debut->tm_mon) + to_string(1970 + debut->tm_year);
    filename += varname + ".csv";
    varname += "_";
    ofstream bagages(filename, ios::trunc);
    vector<string> vsplit = split(message, Parametres.TramesSeparator);
    int i, j;
    for (i = 0, j = 0; i < (int) vsplit.size(); i += 2, j++) {
//        Error(RED, string("Valeurs: ") + vsplit[i] + "|" + vsplit[i + 1]);
        string numero = varname + (j < 100 ? ((j < 10) ? "00" + to_string(j) : "0" + to_string(j)) : to_string(j));
        bagages << numero << Parametres.CSVSeparator
                << (vsplit[i][0] == 'V' || vsplit[i][0] == 'v' ? "Valise" : "Bagage à main")
                << Parametres.CSVSeparator << vsplit[i + 1] << endl;
    }
    bagages.close();
    EcrireMessageOutThread(std::string("Fichier ") + filename + " crée");
}

void InitialisationLog() {
    time_t t = time(NULL);
    struct tm *debut = localtime(&t);
    for (int i = 0; i < 100; i++)
        logStream << "=";
    logStream << endl;
    logStream << "Date de compilation: " << __DATE__ << endl;
    logStream << put_time(debut, "Date d'execution: %d-%m-%Y %H-%M-%S") << endl;
    for (int i = 0; i < 100; i++)
        logStream << "-";
    logStream << endl;
}

bool userExist(const string &user, const string &password) {
    string message;
    pthread_mutex_lock(&mutexUserDB);
    ifstream userFile(Parametres.userDB);
    if (!userFile.is_open()) throw Exception(EXCEPTION() + "Fichier " + Parametres.userDB + " inexistant");
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
    if (!ticketFile.is_open()) throw Exception(EXCEPTION() + "Fichier " + Parametres.ticketDB + " inexistant");
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