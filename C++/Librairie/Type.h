#ifndef SERVEUR_TYPE_H
#define SERVEUR_TYPE_H


enum Type {
    ACK,
    DISCONNECT,
    ACCEPT,
    REFUSE,
    LOGIN,
    LOGOUT,
    CHECK_TICKET,
    ADD_LUGGAGE,
    PAYEMENT_DONE,
    TOO_MUCH_CONNECTIONS,
    NO_SELECTED_TICKET
};

#endif //SERVEUR_TYPE_H
