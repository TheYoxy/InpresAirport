-- Delete les valeurs existante et remet des valeurs 'bidon'
DELETE FROM Login;
DELETE FROM Agents;
DELETE FROM Vols;
DELETE FROM Billets;
DELETE FROM Bagages;
INSERT INTO Login VALUES ('floryan', '1234'), ('nico', '1234');
INSERT INTO Agents VALUES ('Bonemme', 'Nicolas', 'Bagagiste', 'nico'), ('Simar', 'Floryan', 'Bagagiste', 'floryan');
INSERT INTO Vols VALUES ('1', 'Paris', current_date, current_date, current_date + 1, 42);
INSERT INTO Billets VALUES ('1', '1');
INSERT INTO Bagages VALUES ('1', 40, TRUE, '1'), ('2', 20, FALSE, '1'), ('3', 10, TRUE, '1'), ('4', 3, TRUE, '1');
COMMIT;