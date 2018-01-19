package Protocole.SEBATRAP;

import java.io.Serializable;
import java.util.Arrays;

import ServeurClientLog.Objects.Requete;

public class RequeteST extends Requete {
    protected TypeRequeteST type;

    public RequeteST(TypeRequeteST type, Serializable... param) {
        super(param);
        this.type = type;
    }

    @Override
    public TypeRequeteST getType() {
        return type;
    }

    @Override
    public String toString() {
        return "RequeteST{" +
                "type=" + type + ',' +
                "params=" + Arrays.toString(params) +
                '}';
    }
}
