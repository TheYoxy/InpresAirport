DELETE INTERVENANT;
INSERT INTO intervenant (id, nom, prenom, statut) VALUES ('00001', 'Bonemme', 'Nicolas', 'etudiant');
INSERT INTO intervenant (id, nom, prenom, statut) VALUES ('00002', 'Simar', 'Floryan', 'etudiant');
INSERT INTO intervenant (id, nom, prenom, statut) VALUES ('00003', 'Vilvens', 'Claude', 'professeur');
DELETE ACTIVITES;
INSERT INTO activites (id, cours, type, datep, description, reference) VALUES
  ('00001', 'technologies internet', 'labo', to_date('11/10/2017', 'DD/MM/YYYY'), 'laboratoire de java', 'Vilvens');
INSERT INTO activites (id, cours, type, datep, description, reference) VALUES
  ('00305', 'technologies internet', 'travail equipe', to_date('11/10/2017', 'DD/MM/YYYY'), 'laboratoire de java',
   'BonnemeSimar');
COMMIT;