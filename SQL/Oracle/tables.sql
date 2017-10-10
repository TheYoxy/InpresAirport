DROP TABLE activites;
CREATE TABLE activites (
  id          NUMBER(6),
  cours       VARCHAR2(50),
  type        VARCHAR2(30),
  datep       DATE,
  description VARCHAR2(50),
  reference   VARCHAR2(25),
  CONSTRAINT act$pk PRIMARY KEY (id)
);
DROP TABLE intervenant CASCADE CONSTRAINTS;
CREATE TABLE intervenant (
  id     NUMBER(6),
  nom    VARCHAR2(20),
  prenom VARCHAR2(20),
  statut VARCHAR2(30),
  CONSTRAINT inter$pk PRIMARY KEY (id)
);