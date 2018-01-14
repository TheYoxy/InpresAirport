package Protocole.IACOP;

import java.security.InvalidParameterException;

import ServeurClientLog.Interfaces.TypeRequete;

public enum TypeRequeteIACOP implements TypeRequete {
    LOGIN_GROUP(0x1000), POST_QUESTION(0x10), ANSWER_QUESTION(0x20), POST_EVENT(0x30);
    private int value;

    TypeRequeteIACOP(int value) {
        this.value = value;
    }

    public static TypeRequeteIACOP fromInt(int value) {
        switch (value) {
            case 0x1000:
                return LOGIN_GROUP;
            case 0x10:
                return POST_QUESTION;
            case 0x20:
                return ANSWER_QUESTION;
            case 0x30:
                return POST_EVENT;
            default:
                throw new InvalidParameterException("La valeur (" + value + ") ne fait pas partie de l'énumération");
        }
    }

    public int getValue() {
        return value;
    }
}
