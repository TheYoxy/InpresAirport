package Tools;

import java.net.*;

public class Procedural {

    public static String StringIp(InetAddress i) {
        StringBuilder retour = new StringBuilder();
        byte[] tabl = i.getAddress();
        if (i instanceof Inet4Address) {
            for (byte b : tabl) {
                retour.append(String.valueOf(b & 0xFF)).append(".");
            }
            retour.deleteCharAt(retour.length() - 1);
        } else if (i instanceof Inet6Address) {
            for (byte b : tabl) {
                retour.append(String.valueOf(b & 0xFF)).append(":");
            }
            retour.deleteCharAt(retour.length() - 1);
        }
        return retour.toString();
    }

    public static String StringIp(Socket s) {
        return StringIp(s.getInetAddress());
    }

    public static String StringIp(ServerSocket s) {
        return StringIp(s.getInetAddress());
    }

    public static String IpPort(Socket s) {
        return Procedural.StringIp(s) + ":" + s.getPort();
    }
}
