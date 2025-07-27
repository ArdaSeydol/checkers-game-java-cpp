# Checkers Game (Java & C++)

This is a console-based checkers (draughts) game developed using both Java and C++. The project demonstrates integration between the Java UI layer and C++ logic using native method communication through JNI (Java Native Interface).

## Overview
- Java is responsible for the user interface and game control flow.
- C++ handles the board logic, move validation, and game rule enforcement.
- The two languages communicate via JNI, enabling real-time interaction between UI and logic components.

## Features
- Two-player mode with alternating turns
- Implementation of move, jump, and king mechanics
- Console-based board rendering using Java AWT
- Native logic written in C++ for move legality and game state
- Cross-language communication between Java and C++

## Tech Stack
- Java 21
- C++
- JNI (Java Native Interface)
- IntelliJ IDEA (Java)
- CLion or g++ (C++)

## Project Structure
CheckersUTP/
├── src/
│ ├── Main.java # Java game UI and control
│ ├── Main.h / Main.cpp # C++ logic files
│ └── Main_CheckersJNI.h # Auto-generated JNI header
├── .gitignore
├── CheckersUTP.iml
├── cmake-build-debug/ # Ignored
├── out/ # IgnoredS

## How to Build and Run

### 1. Compile C++ Logic (Shared Library)
If you're on Windows:

```bash
g++ -c -I"%JAVA_HOME%\include" -I"%JAVA_HOME%\include\win32" Main.cpp -o Main.o
g++ -shared -o checkers.dll Main.o

g++ -c -fPIC -I"$JAVA_HOME/include" -I"$JAVA_HOME/include/linux" Main.cpp -o Main.o
g++ -shared -o libcheckers.so Main.o
