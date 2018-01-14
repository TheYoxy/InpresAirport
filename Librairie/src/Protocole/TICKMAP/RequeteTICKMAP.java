package Protocole.TICKMAP;

import java.io.Serializable;

import ServeurClientLog.Objects.Requete;

public class RequeteTICKMAP extends Requete {
    private static final long serialVersionUID = 111L;
    protected TypeRequeteTICKMAP type = null;

    public RequeteTICKMAP(TypeRequeteTICKMAP type, Serializable param) {
        super(type, param);
    }

    public RequeteTICKMAP(TypeRequeteTICKMAP type) {
        super(type);
    }

    @Override
    public TypeRequeteTICKMAP getType() {
        return type;
    }
}
