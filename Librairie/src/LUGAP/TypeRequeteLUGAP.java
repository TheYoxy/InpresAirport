package LUGAP;

import ServeurClientLog.Interfaces.TypeRequete;

public enum TypeRequeteLUGAP implements TypeRequete {
    Login,
    Logout,
    Disconnect,
    Request_Vols,
    Request_Bagages_Vol
}
