package TICKMAP;

import java.io.Serializable;

public class RequeteTICKMAP implements Serializable {
    private static final long serialVersionUID = 111L;
    private TypeRequeteTICKMAP Type = null;
    private Serializable Param = null;

    public RequeteTICKMAP(TypeRequeteTICKMAP type, Serializable Param) {
        this(type);
        this.Param = Param;
    }

    public RequeteTICKMAP(TypeRequeteTICKMAP type) {
        this.Type = type;
    }

    public Serializable getParam() {
        return Param;
    }

    public TypeRequeteTICKMAP getType() {
        return Type;
    }
}
