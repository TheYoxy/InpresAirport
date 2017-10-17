package LUGAP;

import ServeurClientLog.Interfaces.Reponse;
import ServeurClientLog.Interfaces.TypeReponse;

import java.io.Serializable;

public class ReponseLUGAP implements Reponse, Serializable {
    private TypeReponseLUGAP Reponse;
    private String ChargeUtile;
    private Serializable Param;

    public ReponseLUGAP(TypeReponseLUGAP reponse, String chargeUtile, Serializable param) {
        this.Reponse = reponse;
        this.ChargeUtile = chargeUtile;
        this.Param = param;
    }

    public ReponseLUGAP(TypeReponseLUGAP reponse, String chargeUtile) {
        this.Reponse = reponse;
        this.ChargeUtile = chargeUtile;
        this.Param = null;
    }

    public Serializable getParam() {
        return Param;
    }

    public String getChargeUtile() {
        return this.ChargeUtile;
    }

    @Override
    public TypeReponse getCode() {
        return this.Reponse;
    }
}
