cmake_minimum_required(VERSION 3.8)
project(Client)
project(Serveur)
set(LIB Librairie/Socket.cpp
        Librairie/Socket.h
        Librairie/Exception.cpp
        Librairie/Exception.h
        Librairie/Structs.h
        Librairie/SocketClient.cpp
        Librairie/SocketClient.h
        Librairie/Fonctions.h
        Librairie/Fonctions.cpp
        Librairie/ipv4.cpp
        Librairie/ipv4.h
        Librairie/SocketServeur.cpp
        Librairie/SocketServeur.h
        Librairie/Type.h
        Librairie/ConnexionException.cpp
        Librairie/ConnexionException.h)
set(CMAKE_CXX_STANDARD 11)
set(SOURCE_FILES_SERVER
        Serveur/main.cpp)
set(SOURCE_FILES_CLIENT
        Client/main.cpp)
find_package(Threads REQUIRED)

add_executable(ClientDebug ${LIB} ${SOURCE_FILES_CLIENT})
target_link_libraries(ClientDebug ${CMAKE_THREAD_LIBS_INIT})
target_compile_definitions(ClientDebug PUBLIC DEBUG)

add_executable(ServeurDebug ${LIB} ${SOURCE_FILES_SERVER})
target_link_libraries(ServeurDebug ${CMAKE_THREAD_LIBS_INIT})
target_compile_definitions(ClientDebug PUBLIC DEBUG)

add_executable(ClientRelease ${LIB} ${SOURCE_FILES_CLIENT})
target_link_libraries(ClientRelease ${CMAKE_THREAD_LIBS_INIT})

add_executable(ServeurRelease ${LIB} ${SOURCE_FILES_SERVER})
target_link_libraries(ServeurRelease ${CMAKE_THREAD_LIBS_INIT})