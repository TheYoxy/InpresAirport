package Protocole.SEBATRAP;

import java.io.Serializable;

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
}
