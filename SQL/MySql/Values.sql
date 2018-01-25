DELETE FROM Agents;
DELETE FROM Login;
DELETE FROM Vol;
DELETE FROM Billets;
DELETE FROM Bagages;
DELETE FROM WebUsers;
DELETE FROM Reservation;
DELETE FROM Facture;
DELETE FROM Transactions;
DELETE FROM Carte;
-- Delete les valeurs existante et remet des valeurs 'bidon'
INSERT INTO Avion (Modele, Vol) VALUES
  ('Airbus A330', 0),
  ('Airbus A330', 0),
  ('Airbus A330', 0),
  ('Airbus A330', 0);
INSERT INTO Login VALUES ('floryan', '1234'), ('nico', '1234');
INSERT INTO Agents
VALUES ('nico', 'Bonemme', 'Nicolas', 'Bagagiste'), ('floryan', 'Simar', 'Floryan', 'Bagagiste');
INSERT INTO Vol VALUES
  ('1', 'Paris', current_date + 5, current_date + 5, 502.45, 'Vol en amoureux pour visiter Paris',
   1, 1),
  ('2', 'Liège', current_date + 5, current_date + 5, 40.52, 'Voyage direct vers le carré de Liège',
   100, 2),
  ('3', 'Londres', current_date + 5, current_date + 5, 69.42, 'Insert a French description here',
   100, 3);
INSERT INTO Carte VALUES ('11111111111111111', 1000000.00, NULL);
# INSERT INTO Billets VALUES ('1', '1', -1);
# INSERT INTO Billets VALUES ('2', '1', -1);
# INSERT INTO Billets VALUES ('3', '1', -1);
# INSERT INTO Billets VALUES ('4', '1', -1);
# INSERT INTO Billets VALUES ('5', '2', -1);
# INSERT INTO Billets VALUES ('6', '2', -1);
# INSERT INTO Billets VALUES ('7', '2', -1);
# INSERT INTO Billets VALUES ('8', '2', -1);
#
# INSERT INTO Bagages (NumeroBagage, Poids, Valise, NumeroBillet)
# VALUES ('1', 40, TRUE, '1'),
#   ('2', 20, FALSE, '2'),
#   ('3', 10, TRUE, '3'),
#   ('4', 3, TRUE, '4'),
#   ('5', 42.69, TRUE, '5'),
#   ('6', 20.50, FALSE, '5'),
#   ('7', 10.10, TRUE, '6'),
#   ('8', 3.59, TRUE, '7'),
#   ('9', 42.69, TRUE, '2'),
#   ('10', 20.50, FALSE, '2'),
#   ('11', 10.10, TRUE, '7'),
#   ('12', 3.59, TRUE, '6');
COMMIT;