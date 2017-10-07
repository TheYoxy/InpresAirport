package Librairie.LUGAP;

import Librairie.Interfaces.Reponse;

import java.io.Serializable;

public class ReponseLUGAP implements Reponse, Serializable {
    public static final int OK = 0;
    public static final int NOT_OK = 1;
    private int Reponse;
    private String ChargeUtile;

    public ReponseLUGAP(int reponse, String chargeUtile) {
        this.Reponse = reponse;
        this.ChargeUtile = chargeUtile;
    }

    public String getChargeUtile() {
        return this.ChargeUtile;
    }

    @Override
    public int getCode() {
        return Reponse;
    }
}
