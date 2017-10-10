DROP TABLE activites;
CREATE TABLE activites (
  id          NUMBER(6),
  cours       CHAR(50),
  type        CHAR(30),
  datep       DATE,
  description CHAR(50),
  reference   CHAR(25),
  CONSTRAINT act$pk PRIMARY KEY (id)
);
DROP TABLE intervenant CASCADE CONSTRAINTS;
CREATE TABLE intervenant (
  id     NUMBER(6),
  nom    CHAR(20),
  prenom CHAR(20),
  statut CHAR(30),
  CONSTRAINT inter$pk PRIMARY KEY (id)
);