DELETE FROM Login;
DELETE FROM Agents;
DELETE FROM Vols;
DELETE FROM Billets;
DELETE FROM Bagages;
-- Delete les valeurs existante et remet des valeurs 'bidon'

INSERT INTO Login VALUES ('floryan', '1234'), ('nico', '1234');
INSERT INTO Agents VALUES ('Bonemme', 'Nicolas', 'Bagagiste', 'nico'), ('Simar', 'Floryan', 'Bagagiste', 'floryan');
INSERT INTO Vols VALUES ('1', 'Paris', current_date, current_date, current_date + 1, 42);
INSERT INTO Vols VALUES ('2', 'Liège', current_date, current_date + 1, current_date + 2, 42);
INSERT INTO Vols VALUES ('3', 'Londres', current_date - 1, current_date, current_date, 43);
INSERT INTO Vols VALUES ('4', 'Liège', current_date, current_date + 1, current_date + 2, 42);
INSERT INTO Vols VALUES ('5', 'Liège', current_date, current_date + 1, current_date + 2, 42);
INSERT INTO Vols VALUES ('6', 'Liège', current_date, current_date + 1, current_date + 2, 42);
INSERT INTO Vols VALUES ('7', 'Liège', current_date, current_date + 1, current_date + 2, 42);
INSERT INTO Vols VALUES ('8', 'Liège', current_date, current_date + 1, current_date + 2, 42);
INSERT INTO Vols VALUES ('9', 'Liège', current_date, current_date + 1, current_date + 2, 42);
INSERT INTO Vols VALUES ('10', 'Liège', current_date, current_date + 1, current_date + 2, 42);
INSERT INTO Billets VALUES ('1', '1');
INSERT INTO Billets VALUES ('2', '1');
INSERT INTO Billets VALUES ('3', '1');
INSERT INTO Billets VALUES ('4', '1');
INSERT INTO Billets VALUES ('5', '2');
INSERT INTO Billets VALUES ('6', '2');
INSERT INTO Billets VALUES ('7', '2');
INSERT INTO Billets VALUES ('8', '2');
INSERT INTO Bagages (NumeroBagage, Poids, Valise, NumeroBillet)
VALUES ('1', 40, TRUE, '1'),
  ('2', 20, FALSE, '2'),
  ('3', 10, TRUE, '3'),
  ('4', 3, TRUE, '4'),
  ('5', 42.69, TRUE, '5'),
  ('6', 20.50, FALSE, '5'),
  ('7', 10.10, TRUE, '6'),
  ('8', 3.59, TRUE, '7'),
  ('9', 42.69, TRUE, '2'),
  ('10', 20.50, FALSE, '2'),
  ('11', 10.10, TRUE, '7'),
  ('12', 3.59, TRUE, '6');
COMMIT;
