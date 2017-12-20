#ifndef SERVEUR_CHAT_MACRO_H
#define SERVEUR_CHAT_MACRO_H

#define EXCEPTION() (std::string(__FILE__) + ":" + std::to_string(__LINE__) + " ")
//Couleurs pour le terminal
#define INIT 0
#define NOIR 30
#define RED 31
#define GREEN 32
#define YELLOW 33
#define BLUE 34
#define MAGENTA 35
#define CYAN 36

#define WHITE 37
#define Error(couleur, message) std::cerr << "\033[" << couleur << "m" << message << "\033[" << INIT << "m" << std::endl

#endif