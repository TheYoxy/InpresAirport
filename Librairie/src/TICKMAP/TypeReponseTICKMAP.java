package TICKMAP;

import ServeurClientLog.Interfaces.TypeReponse;

public enum TypeReponseTICKMAP implements TypeReponse {
    UNKNOWN_LOGIN, BAD_PASSWORD, OK, NOT_OK, FULL, NOTLOGGED;

    public static TypeReponse getError() {
        return NOT_OK;
    }
}
