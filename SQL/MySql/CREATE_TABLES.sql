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
  Reception    CHAR(1)       DEFAULT 'N',
  Charger      CHAR(1)       DEFAULT 'N',
  Verifier     CHAR(1)       DEFAULT 'N',
  Remarque     VARCHAR(1000) DEFAULT '',
  CHECK (Reception IN ('O', 'N')),
  CHECK (Charger IN ('O', 'N', 'R')),
  CHECK (Verifier IN ('O', 'N'))
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
