SELECT Bagages.*
FROM Bagages
  NATURAL JOIN Billets
  NATURAL JOIN Vols
WHERE NumeroVol = '';
SELECT *
FROM Vols
WHERE heureDepart = CURRENT_DATE;
SELECT
  nom,
  prenom
FROM Agents
  NATURAL JOIN Login
WHERE username = '';