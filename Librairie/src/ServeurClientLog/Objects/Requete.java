package ServeurClientLog.Objects;

import java.io.Serializable;

import ServeurClientLog.Interfaces.TypeRequete;

public abstract class Requete implements Serializable {
    protected Serializable[] param = null;

    protected Requete(Serializable... param) {
        this.param = param;
    }

    public Serializable[] getParams() {
        return param;
    }

    public Serializable getParam() {
        return param[0];
    }

    public Serializable getParam(int pos) {
        return param[pos];
    }

    public abstract TypeRequete getType();
}
