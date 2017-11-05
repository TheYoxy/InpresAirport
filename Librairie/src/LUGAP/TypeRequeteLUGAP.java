package LUGAP;

import ServeurClientLog.Interfaces.TypeRequete;

public enum TypeRequeteLUGAP implements TypeRequete {
    TryConnect,
    Login,
    Logout,
    Disconnect,
    Request_Vols,
    Request_Bagages_Vol,
    Update_Bagage_Vol,
    Update_Bagages_Vols
}
