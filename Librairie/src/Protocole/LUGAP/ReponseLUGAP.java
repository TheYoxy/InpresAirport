package Protocole.LUGAP;

import java.io.Serializable;
import java.util.Arrays;

import ServeurClientLog.Objects.Reponse;

public class ReponseLUGAP extends Reponse {
    protected TypeReponseLUGAP reponse = null;

    public ReponseLUGAP(TypeReponseLUGAP reponse, Serializable... param) {
        this.reponse = reponse;
        this.params = param;
    }

    @Override
    public TypeReponseLUGAP getCode() {
        return this.reponse;
    }

    @Override
    public String toString() {
        return "ReponseLUGAP{" +
                "reponse=" + reponse +
                ", Param=" + Arrays.toString(params) +
                '}';
    }
}
