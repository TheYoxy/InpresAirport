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
  IdAvion          INT REFERENCES Avion (IdAvion)
)
  ENGINE = INNODB;

DROP TABLE IF EXISTS Billets;
CREATE TABLE Billets (
  NumeroBillet VARCHAR(40) PRIMARY KEY,
  NumeroVol    VARCHAR(15) NOT NULL REFERENCES Vol (NumeroVol),
  NumeroPlace  INTEGER     NOT NULL,
  idFacture    INTEGER     NOT NULL REFERENCES Facture (idFacture),
  idVoyageur   INTEGER     NOT NULL REFERENCES Voyageur (idVoyageur)
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
  NumeroBillet VARCHAR(40) REFERENCES Billets (NumeroBillet),
  Reception    BOOLEAN       DEFAULT FALSE,
  Charger      CHAR(1)       DEFAULT 'N',
  Verifier     BOOLEAN       DEFAULT FALSE,
  Remarque     VARCHAR(1000) DEFAULT '',
  CHECK (Charger IN ('O', 'N', 'R'))
)
  ENGINE = INNODB;

DROP TABLE IF EXISTS Login;
CREATE TABLE Login (
  Username VARCHAR(100) PRIMARY KEY,
  Password VARCHAR(100)
)
  ENGINE = INNODB;

DROP TABLE IF EXISTS Agents;
CREATE TABLE Agents (
  Nom      VARCHAR(25)  NOT NULL,
  Prenom   VARCHAR(25)  NOT NULL,
  Poste    VARCHAR(25)  NOT NULL,
  Username VARCHAR(100) NOT NULL REFERENCES Login (Username),
  PRIMARY KEY (Nom, Prenom)
)
  ENGINE = INNODB;

/* WEB */

DROP TABLE IF EXISTS WebUsers;
CREATE TABLE WebUsers (
  Username VARCHAR(20) PRIMARY KEY,
  Password VARCHAR(20)  NOT NULL,
  Nom      VARCHAR(100),
  Prenom   VARCHAR(100),
  Mail     VARCHAR(100) NOT NULL UNIQUE
)
  ENGINE = INNODB;

DROP TABLE IF EXISTS Reservation;
CREATE TABLE Reservation (
  Username        VARCHAR(20) REFERENCES WebUsers (Username),
  NumeroVol       VARCHAR(15) REFERENCES Vol (NumeroVol),
  nbPlaces        INTEGER                               NOT NULL CHECK (nbPlaces > 0),
  timeReservation TIMESTAMP DEFAULT current_timestamp() NOT NULL,
  PRIMARY KEY (Username, NumeroVol, timeReservation)
)
  ENGINE = INNODB;

DROP TABLE IF EXISTS Facture;
CREATE TABLE Facture (
  idFacture INTEGER PRIMARY KEY AUTO_INCREMENT,
  Username  VARCHAR(20) REFERENCES WebUsers (Username),
  NumeroVol VARCHAR(15) REFERENCES Vol (NumeroVol),
  nbPlaces  INTEGER NOT NULL,
  prix      DOUBLE  NOT NULL
)
  ENGINE = INNODB;
/* */
DROP TABLE IF EXISTS Transactions;
CREATE TABLE Transactions (
  instant TIME
)
  ENGINE = INNODB;