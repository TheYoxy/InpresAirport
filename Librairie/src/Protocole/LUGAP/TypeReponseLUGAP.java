package Protocole.LUGAP;

import ServeurClientLog.Interfaces.TypeReponse;

public enum TypeReponseLUGAP implements TypeReponse {
    UNKNOWN_LOGIN, BAD_PASSWORD, LOG, OK, NOT_OK, SQL_LOCK, NOTLOGGED;

    public static TypeReponse getError() {
        return NOT_OK;
    }
}