package Protocole.PAYP;

import java.io.Serializable;
import java.util.Arrays;

import ServeurClientLog.Objects.Reponse;

public class ReponsePAYP extends Reponse {
    protected TypeReponsePAYP reponse = null;

    public ReponsePAYP(TypeReponsePAYP reponse, Serializable... param) {
        super(param);
        this.reponse = reponse;
    }

    @Override
    public TypeReponsePAYP getCode() {
        return reponse;
    }

    @Override
    public String toString() {
        return "ReponsePAYP{" +
                "reponse=" + reponse +
                ", params=" + Arrays.toString(params) +
                '}';
    }
}
