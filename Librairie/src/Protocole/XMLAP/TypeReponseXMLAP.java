package Protocole.XMLAP;

import ServeurClientLog.Interfaces.TypeReponse;

public enum TypeReponseXMLAP implements TypeReponse {
    OK, NOT_OK, BAD_TRANSACTION, NOTLOGGED;

    public static TypeReponse getError() {
        return NOT_OK;
    }
}
