DROP TABLE IF EXISTS Avion;
CREATE TABLE Avion (
  IdAvion INT AUTO_INCREMENT PRIMARY KEY,
  Modele  VARCHAR(100),
  Vol     BOOLEAN
)
  ENGINE = INNODB;
DROP TABLE IF EXISTS Vols;
CREATE TABLE Vols (
  NumeroVol        VARCHAR(15) PRIMARY KEY,
  Destination      VARCHAR(30),
  HeureDepart      DATETIME NOT NULL,
  HeureArrivee     DATETIME NOT NULL,
  HeureArriveeDest DATETIME NOT NULL,
  IdAvion          INT REFERENCES Avion (IdAvion)
)
  ENGINE = INNODB;
DROP TABLE IF EXISTS Billets;
CREATE TABLE Billets (
  NumeroBillet VARCHAR(40) PRIMARY KEY,
  NumeroVol    VARCHAR(15) NOT NULL REFERENCES Vols (NumeroVol)
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
DROP TABLE IF EXISTS VolReservable;
CREATE TABLE VolReservable (
  NumeroVol        VARCHAR(15) PRIMARY KEY REFERENCES Vols (NumeroVol),
  Lieu             VARCHAR(100) REFERENCES Vols (Destination),
  Date             DATE,
  Prix             DECIMAL(15, 2),
  Description      VARCHAR(1000),
  PlacesDisponible INTEGER
)
  ENGINE = INNODB;
DROP TABLE IF EXISTS Users;
CREATE TABLE Users (
  Username VARCHAR(20) PRIMARY KEY,
  Password VARCHAR(20)  NOT NULL,
  Nom      VARCHAR(100),
  Prenom   VARCHAR(100),
  Mail     VARCHAR(100) NOT NULL
)
  ENGINE = INNODB;
DROP TABLE IF EXISTS Reservation;
CREATE TABLE Reservation (
  Username        VARCHAR(20) REFERENCES Users (Username),
  NumeroVol       VARCHAR(15) REFERENCES Vols (NumeroVol),
  nbPlaces        INTEGER                               NOT NULL CHECK (nbPlaces > 0),
  timeReservation TIMESTAMP DEFAULT current_timestamp() NOT NULL,
  PRIMARY KEY (Username, NumeroVol, timeReservation)
)
  ENGINE = INNODB;
DROP TABLE IF EXISTS Acheter;
CREATE TABLE Acheter (
  id        VARCHAR(20) PRIMARY KEY,
  Username  VARCHAR(20) REFERENCES Users (Username),
  NumeroVol VARCHAR(15) REFERENCES Vols (NumeroVol),
  nbPlaces  INTEGER NOT NULL
)
  ENGINE = INNODB;