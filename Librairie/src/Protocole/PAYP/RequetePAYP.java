package Protocole.PAYP;

import java.io.Serializable;
import java.util.Arrays;

import ServeurClientLog.Objects.Requete;

public class RequetePAYP extends Requete {
    protected TypeRequetePAYP type = null;

    public RequetePAYP(TypeRequetePAYP type, Serializable... params) {
        super(params);
        this.type = type;
    }

    @Override
    public TypeRequetePAYP getType() {
        return type;
    }

    @Override
    public String toString() {
        return "RequetePAYP{" +
                "type=" + type +
                ", params=" + Arrays.toString(params) +
                '}';
    }
}
