package Protocole.XMLAP;

import ServeurClientLog.Objects.Reponse;

import java.io.Serializable;
import java.util.Arrays;

public class ReponseXMLAP extends Reponse {

    protected TypeReponseXMLAP reponse = null;

    public ReponseXMLAP(TypeReponseXMLAP reponse, Serializable... param) {
        super(param);
        this.reponse = reponse;
    }

    @Override
    public TypeReponseXMLAP getCode() {
        return this.reponse;
    }

    @Override
    public String toString() {
        return "ReponseXMLAP{" +
                "reponse=" + reponse +
                ", Param=" + Arrays.toString(params) +
                '}';
    }

}
