package Protocole.TICKMAP;

import ServeurClientLog.Interfaces.TypeRequete;

public enum TypeRequeteTICKMAP implements TypeRequete {
    TryConnect, Login, Ajout_Voyageurs, Confirm_Payement, Logout, Disconnect
}
