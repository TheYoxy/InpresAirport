CC=g++
CFLAG=-Wall -g -m64 -std=c++11 -Wignored-qualifiers 

EXEC=Appli

#Differents Repertoires
OBJDIR=.

SRC=$(wildcard Librairie/*.cpp)
OBJ=$(SRC:%.cpp=$(OBJDIR)/%.o)
SERVD=Serveur_CheckIn
CLID=Application_CheckIn
all:	Serveur_Check Application_Check

Serveur_Check:	$(OBJ) $(SERVD)/main.o $(SERVD)/FonctionServeur.o
	@$(CC) -o $@ $^ $(CFLAG) -lpthread -D_REENTRANT

Application_Check:	$(OBJ) $(CLID)/main.o
	@$(CC) -o $@ $^ $(CFLAG)

$(EXEC):	$(OBJ)
	@echo "Compilation Application"
	@$(CC) -o $@ $^
	@echo "Réussie"

$(SERVD)/main.o:	$(SERVD)/main.cpp
	$(CC) -o $@ -c $^ $(CFLAG) -lpthread

$(SERVD)/FonctionServeur.o:	$(SERVD)/FonctionServeur.cpp $(SERVD)/FonctionServeur.h
	$(CC) -o $@ -c $^ $(CFLAG)

$(CLID)/main.o:	$(CLID)/main.cpp
	$(CC) -o $@ -c $^ $(CFLAG)

$(OBJDIR)/%.o: %.cpp %.h
	$(CC) -o $@ -c $^ $(CFLAG)

clean:
	@echo ".o Effacés"
	@rm -rf $(OBJDIR)/Librairie/*.o

mrproper:	clean
	@echo "Supression de l'executable"
	@rm -rf Serveur_Check Application_Check
