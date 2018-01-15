package Protocole.SEBATRAP;

import java.io.Serializable;
import java.util.Arrays;

import ServeurClientLog.Objects.Reponse;

public class ReponseST extends Reponse {
    protected TypeReponseST reponse = null;

    public ReponseST(TypeReponseST reponse, Serializable... param) {
        super(param);
        this.reponse = reponse;
    }

    @Override
    public TypeReponseST getCode() {
        return reponse;
    }

    @Override
    public String toString() {
        return "ReponseST{" +
                "reponse=" + reponse +
                ", params=" + Arrays.toString(params) +
                '}';
    }
}
