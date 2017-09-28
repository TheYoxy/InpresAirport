cmake_minimum_required(VERSION 3.8)
project(Applcation_CheckIn)
project(Serveur_CheckIn)
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
set(CMAKE_RUNTIME_OUTPUT_DIRECTORY Build/)
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY Build/)
set(CMAKE_ARCHIVE_OUTPUT_DIRECTORY Build/)
set(SOURCE_FILES_SERVER
        Serveur_CheckIn/main.cpp
        Serveur_CheckIn/FonctionServeur.h
        Serveur_CheckIn/FonctionServeur.cpp)
set(SOURCE_FILES_CLIENT
        Application_CheckIn/main.cpp)

set(LIBRARY_OUTPUT_PATH Build/Lib)
find_package(Threads REQUIRED)

add_executable(Application_CheckIn ${LIB} ${SOURCE_FILES_CLIENT})
target_link_libraries(Application_CheckIn ${CMAKE_THREAD_LIBS_INIT})
target_compile_definitions(Application_CheckIn PUBLIC ${CMAKE_BUILD_TYPE})

add_executable(Serveur_CheckIn ${LIB} ${SOURCE_FILES_SERVER})
target_link_libraries(Serveur_CheckIn ${CMAKE_THREAD_LIBS_INIT})
target_compile_definitions(Serveur_CheckIn PUBLIC ${CMAKE_BUILD_TYPE})
