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
	@$(CC) -o $@ $^ -lpthread -D_REENTRANT

Application_Check:	$(OBJ) $(CLID)/main.o
	@$(CC) -o $@ $^

$(EXEC):	$(OBJ)
	@echo "Compilation Application"
	@$(CC) -o $@ $^
	@echo "Réussie"

$(SERVD)/main.o:	$(SERVD)/main.cpp
	$(CC) -o $@ -c $(SERVD)/main.cpp $(CFLAG) -lpthread

$(SERVD)/FonctionServeur.o:	$(SERVD)/FonctionServeur.cpp $(SERVD)/FonctionServeur.h
	$(CC) -o $@ -c $(SERVD)/FonctionServeur.cpp $(SERVD)/FonctionServeur.h $(CFLAG)

$(CLID)/main.o:	$(CLID)/main.cpp
	$(CC) -o $@ -c $(CLID)/main.cpp $(CFLAG)

$(OBJDIR)/%.o: %.cpp %.h
	$(CC) -o $@ -c %.cpp %.h $(CFLAG)

clean:
	@echo ".o Effacés"
	@rm -rf $(OBJDIR)/*.o

mrproper:	clean
	@echo "Supression de l'executable"
	@rm -rf Serveur_Check Application_Check
