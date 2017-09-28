cmake_minimum_required(VERSION 3.8)
project(Librairie)

set(CMAKE_CXX_STANDARD 11)

set(SOURCE_FILES
    ConnexionException.cpp
    ConnexionException.h
    Exception.cpp
    Exception.h
    Fonctions.cpp
    Fonctions.h
    ipv4.cpp
    ipv4.h
    Socket.cpp
    Socket.h
    SocketClient.cpp
    SocketClient.h
    SocketServeur.cpp
    SocketServeur.h
    Structs.h
    Type.h)

include_directories(.)

add_executable(Librairie ${SOURCE_FILES})