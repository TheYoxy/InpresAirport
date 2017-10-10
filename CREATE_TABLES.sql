create table Billets (
	numBillet VARCHAR(40) NOT NULL,
	numVol VARCHAR(10) NOT NULL,
	PRIMARY KEY (numBillet)
);

create table Vols (
	numVol VARCHAR(15) NOT NULL,
	destination VARCHAR(30),
	heureArrivee datetime,
	heureDepart datetime NOT NULL,
	heureArriveeDest datetime NOT NULL,
	modeleAvion VARCHAR(20),
	PRIMARY KEY (numVol)
);

create table Bagages (
	numBagage VARCHAR(15) NOT NULL,
	poids float,
	valise boolean,
	PRIMARY KEY (numBagage)
);

create table Agents (
	nom VARCHAR(25) NOT NULL,
	prenom VARCHAR(25) NOT NULL,
	poste VARCHAR(25) NOT NULL,
	PRIMARY KEY (nom, prenom)
);