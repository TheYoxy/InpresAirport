package Librairie.LUGAP;

import Librairie.ServeurClientLog.Interfaces.Reponse;
import Librairie.ServeurClientLog.Interfaces.TypeReponse;

import java.io.Serializable;

public class ReponseLUGAP implements Reponse, Serializable {
    private TypeReponseLUGAP Reponse;
    private String ChargeUtile;

    public ReponseLUGAP(TypeReponseLUGAP reponse, String chargeUtile) {
        this.Reponse = reponse;
        this.ChargeUtile = chargeUtile;
    }

    public String getChargeUtile() {
        return this.ChargeUtile;
    }

    @Override
    public TypeReponse getCode() {
        return this.Reponse;
    }
}
