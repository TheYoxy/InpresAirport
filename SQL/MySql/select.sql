SELECT Bagages.*
FROM Bagages
  NATURAL JOIN Billets
  NATURAL JOIN Vols
WHERE numVol = '';
select * from Vols where heureDepart = CURRENT_DATE;
select nom,prenom from Agents NATURAL join Login where username = '';