package LUGAP;

import ServeurClientLog.Interfaces.TypeReponse;

public enum TypeReponseLUGAP implements TypeReponse {
    UNKNOWN_LOGIN, BAD_PASSWORD, LOG,
    OK, NOT_OK
}