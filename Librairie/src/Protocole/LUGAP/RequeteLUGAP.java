package Protocole.LUGAP;

import java.io.Serializable;

import ServeurClientLog.Objects.Requete;

public class RequeteLUGAP extends Requete {
    private static final long serialVersionUID = 123L;
    protected TypeRequeteLUGAP type = null;
    private String from = "";

    public RequeteLUGAP(TypeRequeteLUGAP type, Serializable param) {
        super(type, param);
    }

    public RequeteLUGAP(TypeRequeteLUGAP type, Serializable param, String from) {
        this(type, from);
        this.param = param;
    }

    public RequeteLUGAP(TypeRequeteLUGAP type, String from) {
        super(type);
        this.from = from;
    }

    private RequeteLUGAP(TypeRequeteLUGAP type) {
        super(type);
    }

    public String getFrom() {
        return from;
    }

    @Override
    public TypeRequeteLUGAP getType() {
        return type;
    }
}
