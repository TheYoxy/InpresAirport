package Protocole.TICKMAP;

import java.io.Serializable;

import ServeurClientLog.Interfaces.Reponse;
import ServeurClientLog.Interfaces.TypeReponse;

public class ReponseTICKMAP implements Reponse {
    public static final ReponseTICKMAP BAD = new ReponseTICKMAP(TypeReponseTICKMAP.NOT_OK);
    private static final long serialVersionUID = 111L;
    private TypeReponseTICKMAP Reponse = null;
    private Serializable Param = null;

    public ReponseTICKMAP(TypeReponseTICKMAP reponse, Serializable param) {
        this(reponse);
        this.Param = param;
    }

    public ReponseTICKMAP(TypeReponseTICKMAP reponse) {
        this.Reponse = reponse;
    }

    @Override
    public String toString() {
        return "ReponseTICKMAP{" +
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
