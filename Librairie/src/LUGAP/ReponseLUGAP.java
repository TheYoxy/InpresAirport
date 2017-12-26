package LUGAP;

import java.io.Serializable;

import ServeurClientLog.Interfaces.Reponse;
import ServeurClientLog.Interfaces.TypeReponse;

public class ReponseLUGAP implements Reponse {
    private static final long serialVersionUID = 124L;
    private TypeReponseLUGAP Reponse = null;
    private Serializable Param = null;

    public ReponseLUGAP(TypeReponseLUGAP reponse) {
        Reponse = reponse;
    }

    public ReponseLUGAP(TypeReponseLUGAP reponse, Serializable param) {
        this.Reponse = reponse;
        this.Param = param;
    }

    @Override
    public String toString() {
        return "ReponseLUGAP{" +
                "Reponse=" + Reponse +
                ", Param=" + Param +
                '}';
    }

    public Serializable getParam() {
        return Param;
    }

    @Override
    public TypeReponse getCode() {
        return this.Reponse;
    }
}
