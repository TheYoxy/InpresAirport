SELECT Bagages.*
FROM Bagages
  NATURAL JOIN Billets
  NATURAL JOIN Vols
WHERE numVol = '';