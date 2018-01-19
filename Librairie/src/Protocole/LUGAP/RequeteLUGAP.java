package Protocole.LUGAP;

import java.io.Serializable;
import java.util.Arrays;

import ServeurClientLog.Objects.Requete;

public class RequeteLUGAP extends Requete {
    protected TypeRequeteLUGAP type = null;

    public RequeteLUGAP(TypeRequeteLUGAP type, Serializable... param) {
        super(param);
        this.type = type;
    }

    @Override
    public TypeRequeteLUGAP getType() {
        return type;
    }

    @Override
    public String toString() {
        return "RequeteLUGAP{" +
                "type=" + type + ',' +
                "params=" + Arrays.toString(params) +
                '}';
    }
}
