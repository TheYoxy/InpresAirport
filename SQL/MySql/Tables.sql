SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS Avion;
CREATE TABLE Avion (
  IdAvion INT AUTO_INCREMENT PRIMARY KEY,
  Modele  VARCHAR(100),
  Vol     BOOLEAN
)
  ENGINE = INNODB;

DROP TABLE IF EXISTS Vol;
CREATE TABLE Vol (
  NumeroVol        VARCHAR(15) PRIMARY KEY,
  Lieu             VARCHAR(100),
  HeureDepart      DATETIME NOT NULL,
  HeureArrivee     DATETIME NOT NULL,
  Prix             DECIMAL(15, 2),
  Description      VARCHAR(1000),
  PlacesDisponible INTEGER CHECK (PlacesDisponible >= 0),
  IdAvion          INT,
  FOREIGN KEY (IdAvion) REFERENCES Avion (IdAvion)
)
  ENGINE = INNODB;

DROP TABLE IF EXISTS Billets;
CREATE TABLE Billets (
  NumeroBillet VARCHAR(40) PRIMARY KEY,
  NumeroVol    VARCHAR(15) NOT NULL,
  NumeroPlace  INTEGER     NOT NULL,
  idFacture    INTEGER     NOT NULL,
  idVoyageur   INTEGER     NOT NULL,
  FOREIGN KEY (NumeroVol) REFERENCES Vol (NumeroVol),
  FOREIGN KEY (idFacture) REFERENCES Facture (idFacture),
  FOREIGN KEY (idVoyageur) REFERENCES Voyageur (idVoyageur)
)
  ENGINE = INNODB;

DROP TABLE IF EXISTS Voyageur;
CREATE TABLE Voyageur (
  idVoyageur INTEGER AUTO_INCREMENT PRIMARY KEY,
  nom        VARCHAR(50) NOT NULL,
  prenom     VARCHAR(50) NOT NULL,
  naissance  DATE        NOT NULL
)
  ENGINE = INNODB;

DROP TABLE IF EXISTS Bagages;
CREATE TABLE Bagages (
  NumeroBagage VARCHAR(15) PRIMARY KEY,
  Poids        FLOAT,
  Valise       BOOLEAN,
  NumeroBillet VARCHAR(40),
  Reception    BOOLEAN       DEFAULT FALSE,
  Charger      CHAR(1)       DEFAULT 'N',
  Verifier     BOOLEAN       DEFAULT FALSE,
  Remarque     VARCHAR(1000) DEFAULT '',
  CHECK (Charger IN ('O', 'N', 'R')),
  FOREIGN KEY (NumeroBagage) REFERENCES Billets (NumeroBillet)
)
  ENGINE = INNODB;

DROP TABLE IF EXISTS Login;
CREATE TABLE Login (
  Username VARCHAR(25) PRIMARY KEY,
  Password VARCHAR(25)
)
  ENGINE = INNODB;

DROP TABLE IF EXISTS Agents;
CREATE TABLE Agents (
  Username VARCHAR(25) PRIMARY KEY,
  Nom      VARCHAR(30) NOT NULL,
  Prenom   VARCHAR(30) NOT NULL,
  Poste    VARCHAR(25) NOT NULL,
  FOREIGN KEY (Username) REFERENCES Login (Username)
)
  ENGINE = INNODB;

/* WEB */

DROP TABLE IF EXISTS WebUsers;
CREATE TABLE WebUsers (
  Username VARCHAR(25) PRIMARY KEY,
  Nom      VARCHAR(30),
  Prenom   VARCHAR(30),
  Mail     VARCHAR(100) NOT NULL UNIQUE,
  FOREIGN KEY (Username) REFERENCES Login (Username)
)
  ENGINE = INNODB;

DROP TABLE IF EXISTS Reservation;
CREATE TABLE Reservation (
  Username        VARCHAR(20),
  NumeroVol       VARCHAR(15),
  nbPlaces        INTEGER                               NOT NULL CHECK (nbPlaces > 0),
  timeReservation TIMESTAMP DEFAULT current_timestamp() NOT NULL,
  PRIMARY KEY (Username, NumeroVol, timeReservation),
  FOREIGN KEY (Username) REFERENCES Login (Username),
  FOREIGN KEY (NumeroVol) REFERENCES Vol (NumeroVol)
)
  ENGINE = INNODB;

DROP TABLE IF EXISTS Carte;
CREATE TABLE Carte (
  numeroCarte CHAR(17) PRIMARY KEY,
  solde       DECIMAL(15, 2) NOT NULL,
  username    VARCHAR(20),
  FOREIGN KEY (username) REFERENCES Login (Username)
)
  ENGINE = INNODB;

DROP TABLE IF EXISTS Facture;
CREATE TABLE Facture (
  idFacture   INTEGER PRIMARY KEY AUTO_INCREMENT,
  instant     TIMESTAMP           DEFAULT current_timestamp(),
  Username    VARCHAR(20) NOT NULL,
  NumeroVol   VARCHAR(15) NOT NULL,
  nbPlaces    INTEGER     NOT NULL,
  prix        DOUBLE      NOT NULL,
  numeroCarte CHAR(17)    NOT NULL,
  FOREIGN KEY (Username) REFERENCES Login (Username),
  FOREIGN KEY (NumeroVol) REFERENCES Vol (NumeroVol),
  FOREIGN KEY (numeroCarte) REFERENCES Carte (numeroCarte)
)
  ENGINE = INNODB;

DROP TABLE IF EXISTS Transactions;
CREATE TABLE Transactions
(
  instant     TIMESTAMP,
  somme       DOUBLE,
  numeroCarte CHAR(17),
  PRIMARY KEY (instant, somme, numeroCarte),
  FOREIGN KEY (numeroCarte) REFERENCES Carte (numeroCarte)
)
  ENGINE = INNODB;

SET FOREIGN_KEY_CHECKS = 1;