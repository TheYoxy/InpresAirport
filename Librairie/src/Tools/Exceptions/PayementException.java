package Tools.Exceptions;

import ServeurClientLog.Objects.Reponse;
import Tools.PayementExceptionType;

public class PayementException extends Exception {
    private Reponse rep = null;
    private PayementExceptionType type;

    public PayementException(PayementExceptionType type, Reponse rep) {
        this(type);
        this.rep = rep;
    }

    protected PayementException(PayementExceptionType type) {
        super(getMessage(type));
        this.type = type;
    }

    public static String getMessage(PayementExceptionType type) {
        String message = "Erreur lors de la procédure de payement: ";
        switch (type) {
            case FULL:
                message += "Le vol sélectionné est plein.";
                break;
            case OTHER:
                message += "Autre";
                break;
            default:
                message += "";
                break;
        }
        return message;
    }

    public Reponse getRep() {
        return rep;
    }

    public PayementExceptionType getType() {
        return type;
    }
}
