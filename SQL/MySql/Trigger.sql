DELIMITER //
DROP TRIGGER IF EXISTS UpdateCarte;
CREATE TRIGGER UpdateCarte
  BEFORE UPDATE
  ON Carte
  FOR EACH ROW
  BEGIN
    IF (OLD.solde != NEW.solde)
    THEN
      INSERT INTO Transactions (instant, somme, numeroCarte)
      VALUES (current_timestamp, NEW.solde - OLD.solde, OLD.numeroCarte);
    END IF;
  END;
//

DROP TRIGGER IF EXISTS GenIdTransaction;
CREATE TRIGGER GenIdTransaction
  BEFORE INSERT
  ON Transactions
  FOR EACH ROW
  BEGIN
    SET new.idFacture = sha1(new.instant + new.numeroCarte + new.somme);
  END;
//
DELIMITER ;