cmake_minimum_required(VERSION 4.0)
project(clox C)

set(CMAKE_C_STANDARD 99)

add_executable(clox main.c memory.c chunk.c debug.c value.c)
