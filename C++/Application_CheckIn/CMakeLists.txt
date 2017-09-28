cmake_minimum_required(VERSION 3.8)
project(Application_CheckIn)

set(CMAKE_CXX_STANDARD 11)

set(SOURCE_FILES
        main.cpp)

include_directories(.)

add_executable(Application_CheckIn ${SOURCE_FILES})
