DROP TABLE IF EXISTS Avion;
CREATE TABLE Avion (
  id     INT AUTO_INCREMENT PRIMARY KEY,
  modele VARCHAR(100),
  vol    BOOLEAN
)
  ENGINE = INNODB;
DROP TABLE IF EXISTS Vols;
CREATE TABLE Vols (
  numVol           VARCHAR(15) PRIMARY KEY,
  destination      VARCHAR(30),
  heureDepart      DATETIME NOT NULL,
  heureArrivee     DATETIME NOT NULL,
  heureArriveeDest DATETIME NOT NULL,
  idAvion          INT REFERENCES Avion (id)
)
  ENGINE = INNODB;
DROP TABLE IF EXISTS Billets;
CREATE TABLE Billets (
  numBillet VARCHAR(40) PRIMARY KEY,
  numVol    VARCHAR(15) NOT NULL REFERENCES Vols (numVol)
)
  ENGINE = INNODB;
DROP TABLE IF EXISTS Bagages;
CREATE TABLE Bagages (
  numBagage VARCHAR(15) PRIMARY KEY,
  poids     FLOAT,
  valise    BOOLEAN,
  numBillet VARCHAR(40) REFERENCES Billets (numBillet)
)
  ENGINE = INNODB;
DROP TABLE IF EXISTS Login;
CREATE TABLE Login (
  username VARCHAR(100) PRIMARY KEY,
  password VARCHAR(100)
)
  ENGINE = INNODB;
DROP TABLE IF EXISTS Agents;
CREATE TABLE Agents (
  nom    VARCHAR(25) NOT NULL,
  prenom VARCHAR(25) NOT NULL,
  poste  VARCHAR(25) NOT NULL,
  username VARCHAR(100) not null REFERENCES Login(username),
  PRIMARY KEY (nom, prenom)
)
  ENGINE = INNODB;
