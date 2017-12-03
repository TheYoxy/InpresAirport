package IACOP;

import java.security.InvalidParameterException;

public enum TypeSpecialRequest {
    DISCONNECT(0x1000), NEW_CONNECTED(0x2000);
    private int value;
    TypeSpecialRequest(int value){ this.value = value;}
    public int getValue() { return value; }
    public static TypeSpecialRequest fromInt(int value)
    {
        switch (value)
        {
            default:
                throw new InvalidParameterException("La valeur (" + value + ") ne fait pas partie de l'énumération");
            case 0x1000:
                return DISCONNECT;
            case 0x2000:
                return NEW_CONNECTED;
        }
    }
}
