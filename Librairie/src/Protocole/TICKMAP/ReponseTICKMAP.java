package Protocole.TICKMAP;

import java.io.Serializable;
import java.util.Arrays;

import ServeurClientLog.Objects.Reponse;

public class ReponseTICKMAP extends Reponse {
    public static final ReponseTICKMAP BAD = new ReponseTICKMAP(TypeReponseTICKMAP.NOT_OK);
    protected TypeReponseTICKMAP reponse = null;

    public ReponseTICKMAP(TypeReponseTICKMAP reponse, Serializable... param) {
        super(param);
        this.reponse = reponse;
    }

    @Override
    public TypeReponseTICKMAP getCode() {
        return this.reponse;
    }

    @Override
    public String toString() {
        return "ReponseTICKMAP{" +
                "reponse=" + reponse +
                ", Param=" + Arrays.toString(params) +
                '}';
    }
}
