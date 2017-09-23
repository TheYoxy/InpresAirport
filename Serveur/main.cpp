#include "../Librairie/SocketClient.h"
#include "../Librairie/SocketServeur.h"
//FLUX ERREUR: FICHIER LOG
//FLUX OUT: CONSOLE
//Exemple de trame: HEADER,MESSAGE
using namespace std;

extern int maxSocketNbr;
extern int clients;

#ifdef DEBUG

void Client();

void Serveur();

int main() {
    pthread_t th1, th2;
    pthread_create(&th2, nullptr, (void *(*)(void *)) (Serveur), nullptr);
//    pthread_create(&th1, nullptr, (void *(*)(void *)) (Client), nullptr);

    pthread_join(th2, nullptr);
//    pthread_join(th1, nullptr);
    return 0;
}

void Client() {
    try {
        string message = "Coucou";
        struct timespec t;
        t.tv_sec = 1;
        t.tv_nsec = 0;
//        nanosleep(&t, nullptr);

        SocketClient sc;
        cerr << "(Client): Création" << endl;
        sc.Connect(ipv4().LocalHost, 26010);
        cerr << "(Client): Connexion réussie" << endl;
        sc.Send(message);
        cerr << "(Client): Message : " << message << " envoyé " << endl;
        sc.Close();
    }
    catch (Exception e) {
        cerr << e.getMessage() << endl;
    }
}

void Serveur() {
    try {
        ipv4 ip("192.168.1.5");
        SocketServeur ss(ip);
        cerr << "(Serveur): Création" << endl;
        ss.Listen();
        cerr << "(Serveur): Ecoute" << endl;
        Socket * s = ss.Accept();
        cerr << "(Serveur): Connexion de " << s->getIp() << ":" << s->getPort() << endl;
        char *message = new char[50];
        memset(message, 0, 50);
        int size;
        s->Recv(message, &size);
        cerr << "(Serveur): Message reçu: " << message << "\tTaille : " << endl;
        ss.Close();
        cerr << "(Serveur): Fermeture" << endl;
        delete s;
    }
    catch (Exception e) {
        cerr << e.getMessage() << endl;
    }
}

#else

void fctThreadConnectee(Socket *s);

int main() {
    try {
        SocketServeur socket;
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
    cout << "Fin" << endl;
    std::string message;
    int size = 100;
    while (1) {
        s->Recv(message);
        cout << "Message <" << message << ">reçu de " << s->toString() << endl;
    }
    s->Close();
    clients--;
}

#endif