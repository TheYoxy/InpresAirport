package Tools;

import com.sun.istack.internal.NotNull;

import java.net.*;

public class Procedural {
    @NotNull
    public static String StringIp(InetAddress i) {
        StringBuilder retour = new StringBuilder();
        byte[] tabl = i.getAddress();
        if (i instanceof Inet4Address) {
            for (byte b : tabl) {
                retour.append(String.valueOf(b)).append(".");
            }
            retour.deleteCharAt(retour.length() - 1);
        } else if (i instanceof Inet6Address) {
            for (byte b : tabl) {
                retour.append(String.valueOf(b)).append(":");
            }
            retour.deleteCharAt(retour.length() - 1);
        }
        return retour.toString();
    }

    @NotNull
    public static String StringIp(Socket s) {
        return StringIp(s.getInetAddress());
    }

    @NotNull
    public static String StringIp(ServerSocket s) {
        return StringIp(s.getInetAddress());
    }

    @NotNull
    public static String IpPort(@NotNull Socket s) {
        return Procedural.StringIp(s) + ":" + s.getPort();
    }
}
