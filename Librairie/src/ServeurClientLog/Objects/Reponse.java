package ServeurClientLog.Objects;

import java.io.Serializable;

import ServeurClientLog.Interfaces.TypeReponse;

public abstract class Reponse implements ServeurClientLog.Interfaces.Reponse {
    protected Serializable[] params = null;

    protected Reponse(Serializable... params) {
        this.params = params;
    }

    @Override
    public abstract TypeReponse getCode();

    public abstract String toString();

    public Serializable[] getParams() {
        return params;
    }

    public Serializable getParam() {
        return params[0];
    }

    public Serializable getParam(int place) {
        return params[place];
    }
}
