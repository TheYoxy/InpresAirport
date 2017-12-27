package TICKMAP;

import ServeurClientLog.Interfaces.TypeRequete;

public enum TypeRequeteTICKMAP implements TypeRequete {
    TryConnect, Login, Handshake, Logout, Disconnect
}
