package ServeurClientLog.Objects;

import java.io.Serializable;

import ServeurClientLog.Interfaces.TypeRequete;

public abstract class Requete implements Serializable {
    protected TypeRequete type = null;
    protected Serializable param = null;

    public Requete(TypeRequete type, Serializable param) {
        this(type);
        this.param = param;
    }

    public Requete(TypeRequete type) {
        this.type = type;
    }

    public Serializable getParam() {
        return param;
    }

    public TypeRequete getType() {
        return type;
    }
}
