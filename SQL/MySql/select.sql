SELECT Bagages.*
FROM Bagages
  NATURAL JOIN Billets
  NATURAL JOIN Vols
WHERE NumeroVol = '1';

select Bagages.*
from Bagages
NATURAL JOIN Billets
NATURAL Join Vols
where HeureDepart BETWEEN CURRENT_DATE AND CURRENT_DATE + 1;

SELECT *
FROM Vols
WHERE heureDepart = CURRENT_DATE;

SELECT
  nom,
  prenom
FROM Agents
  NATURAL JOIN Login
WHERE username = '';