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
set(LIBRARY_OUTPUT_PATH Build/Lib)
find_package(Threads REQUIRED)
#set(CMAKE_BUILD_TYPE Release)
#set(EXECUTABLE_OUTPUT_PATH ../)
#set(CMAKE_MAKE_PROGRAM )
add_executable(Client ${LIB} ${SOURCE_FILES_CLIENT})
target_link_libraries(Client ${CMAKE_THREAD_LIBS_INIT})
target_compile_definitions(Client PUBLIC ${CMAKE_BUILD_TYPE})

add_executable(Serveur ${LIB} ${SOURCE_FILES_SERVER})
target_link_libraries(Serveur ${CMAKE_THREAD_LIBS_INIT})
target_compile_definitions(Serveur PUBLIC ${CMAKE_BUILD_TYPE})
