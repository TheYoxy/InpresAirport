package Protocole.XMLAP;

import ServeurClientLog.Objects.Requete;

import java.io.Serializable;
import java.util.Arrays;

public class RequeteXMLAP extends Requete {
    protected TypeRequeteXMLAP type = null;

    public RequeteXMLAP(TypeRequeteXMLAP type, Serializable... param) {
        super(param);
        this.type = type;
    }

    @Override
    public TypeRequeteXMLAP getType() {
        return type;
    }

    @Override
    public String toString() {
        return "RequeteXMLAP{" +
                "type=" + type + ',' +
                "params=" + Arrays.toString(params) +
                '}';
    }
}
