#include "../Librairie/SocketClient.h"
#include "../Librairie/SocketServeur.h"
//FLUX ERREUR: FICHIER LOG
//FLUX OUT: CONSOLE
using namespace std;

extern int maxSocketNbr;
extern int clients;

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