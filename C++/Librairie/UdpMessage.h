#ifndef SERVEUR_CHAT_UDPMESSAGE_H
#define SERVEUR_CHAT_UDPMESSAGE_H

#include <iostream>

class UdpMessage {
public:
    const static char 
    UdpMessage();

    UdpMessage(int type, const std::string &message);

    char getIdMessage() const;

    char getType() const;

    void setType(char type);

    const std::string &getMessage() const;

    void setMessage(const std::string &message);

    std::string toString() const;

    void fromString(std::string);

protected:
private:
    static char cmpt = 0;
    char idMessage;
    char type;
    std::string message;
};


#endif //SERVEUR_CHAT_UDPMESSAGE_H
