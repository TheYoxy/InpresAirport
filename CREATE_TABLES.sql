-- SHOW TABLES;
-- DESCRIBE table; 

create table Billets (
	numBillet VARCHAR(40) NOT NULL,
	numVol VARCHAR(10) NOT NULL,
	PRIMARY KEY (numBillet)
)

create table Vols (
	destination VARCHAR(30)
	heureArrivee datetime,
	heureDepart datetime NOT NULL,
	heureArriveeDest datetime NOT NULL,
	modeleAvion VARCHAR(20)
)

create table Bagages (
	poids float,
	valise boolean
)

create table Agents (
	nom VARCHAR(25) NOT NULL,
	prenom VARCHAR(25) NOT NULL,
	poste VARCHAR(25) NOT NULL -- agent, bagagiste, employé, aiguilleur
)