package Protocole.TICKMAP;

import java.io.Serializable;
import java.util.Arrays;

import ServeurClientLog.Objects.Requete;

public class RequeteTICKMAP extends Requete {
    protected TypeRequeteTICKMAP type = null;

    public RequeteTICKMAP(TypeRequeteTICKMAP type, Serializable... param) {
        super(param);
        this.type = type;
    }

    @Override
    public TypeRequeteTICKMAP getType() {
        return type;
    }

    @Override
    public String toString() {
        return "RequeteTICKMAP{" +
                "type=" + type + ',' +
                "params=" + Arrays.toString(params) +
                '}';
    }
}
