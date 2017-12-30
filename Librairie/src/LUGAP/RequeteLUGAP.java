package LUGAP;

import java.io.Serializable;

public class RequeteLUGAP implements Serializable {
    private static final long serialVersionUID = 123L;
    private TypeRequeteLUGAP Type = null;
    private Serializable Param = null;
    private String From = "";

    public RequeteLUGAP(TypeRequeteLUGAP type, Serializable Param) {
        this(type);
        this.Param = Param;
    }

    public RequeteLUGAP(TypeRequeteLUGAP type, String from) {
        this(type);
        From = from;
    }

    public RequeteLUGAP(TypeRequeteLUGAP type, Serializable param, String from) {
        this(type, from);
        Param = param;
    }

    private RequeteLUGAP(TypeRequeteLUGAP type) {
        this.Type = type;
    }

    public TypeRequeteLUGAP getType() {
        return Type;
    }

    public String getFrom() {
        return From;
    }

    public Serializable getParam() {
        return Param;
    }
}
