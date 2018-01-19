package ServeurClientLog.Objects;

import java.io.Serializable;

import ServeurClientLog.Interfaces.TypeRequete;

public abstract class Requete implements Serializable {
    protected final Serializable[] params;

    protected Requete(Serializable... params) {
        this.params = params;
    }

    public final Serializable getParam() {
        return params[0];
    }

    public final Serializable getParam(int pos) {
        return params[pos];
    }

    public final Serializable[] getParams() {
        return params;
    }

    public abstract TypeRequete getType();

    @Override
    public abstract String toString();
}
