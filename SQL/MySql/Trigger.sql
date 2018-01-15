DELIMITER //
DROP TRIGGER IF EXISTS UpdateCarte;
CREATE TRIGGER UpdateCarte
  BEFORE UPDATE
  ON Carte
  FOR EACH ROW
  BEGIN
    IF (OLD.solde != NEW.solde)
    THEN
      INSERT INTO Transactions VALUES (current_timestamp, OLD.solde - NEW.solde, OLD.numeroCarte);
    END IF;
  END;
//
DELIMITER ;