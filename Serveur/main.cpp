#include "../Librairie/SocketClient.h"
#include "../Librairie/SocketServeur.h"
//FLUX ERREUR: CONSOLE
//FLUX OUT: FICHIER LOG
using namespace std;

void Reception();

void Envoi();

int main() {
    pthread_t pthread, pthread1;
    pthread_create(&pthread, nullptr, reinterpret_cast<void *(*)(void *)>(Reception), nullptr);
    pthread_create(&pthread1, nullptr, reinterpret_cast<void *(*)(void *)>(Envoi), nullptr);
    pthread_join(pthread1, nullptr);
    pthread_join(pthread, nullptr);
    return 0;
}

void Reception() {
    try {
        SocketServeur sv;
        char *rcv = new char[50];
        memset(rcv, 0, 50);
        size_t taille = 50;
        sv.Recv(rcv, taille);
        printf("Message : %s", rcv);
    }
    catch (Exception e) {
        cerr << e.getMessage() << endl;
    }
}

void Envoi() {
    try {
        SocketClient cl;
        string message = "123456789";
        sleep(1);
        cout << "Message avant l'envoi: " << message << endl << "Taille: " << message.length() << endl;
        cl.SendTo(message.c_str(), message.length(), ipv4().LocalHost, 26101);
    }
    catch (Exception e) {
        cerr << e.getMessage() << endl;
    }
}