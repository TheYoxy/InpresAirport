package Tools;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

public class Procedural {
    public static final String SEPARATION = ";;;";
    public static final String INTIDENTIFIER = "WZK";

    public static List<Object> DivParametersUdp(byte[] array) {
        if (array == null) throw new IllegalArgumentException();
        LinkedList<Object> l = new LinkedList<>();
        byte[] b = new byte[array.length - Integer.BYTES];
        System.arraycopy(array, Integer.BYTES, b, 0, b.length);
//        System.out.println("Array: " + Arrays.toString(array));
//        System.out.println("b:     " + Arrays.toString(b));
        /* Division des éléments envoyés */

        String s = new String(b);
        String[] ss = s.split(String.valueOf(SEPARATION), ByteBuffer.wrap(new String(array).substring(0, Integer.BYTES).getBytes()).asIntBuffer().get() + 1);

        for (int i = 0; i < ss.length - 1; i++) {
            Object add;
            if (ss[i].length() == Integer.BYTES + INTIDENTIFIER.length() && ss[i].startsWith(INTIDENTIFIER))
                l.add(add = ByteBuffer.wrap(ss[i].substring(INTIDENTIFIER.length()).getBytes()).asIntBuffer().get());
            else l.add(add = ss[i]);
//            System.out.println("Add: " + add);
        }
        /*Digest*/
        byte[] d = new byte[(b.length - s.indexOf(ss[ss.length - 1]))];
//        System.out.println("s.indexOf(ss[ss.length - 1]): " + s.indexOf(ss[ss.length - 1]));
        System.arraycopy(b, s.indexOf(ss[ss.length - 1]), d, 0, d.length);
//        System.out.println("d: " + Arrays.toString(d) + " (" + d.length + ")");
        l.add(d);
        return l;
    }

    public static byte[] ListObjectToBytes(List l) throws IOException {
        int i = 0;
        for (Object o : l) {
            if (o instanceof Integer)
                i += Integer.BYTES + INTIDENTIFIER.length();
            else if (o instanceof byte[])
                i += ((byte[]) o).length;
            else
                i += o.toString().length();
            i++;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(l.size());
        for (Object o : l) {
            if (o instanceof Integer) {
                dos.write(INTIDENTIFIER.getBytes());
                dos.writeInt((Integer) o);
            }
//            else if (o instanceof String) dos.writeUTF((String) o);
            else if (o instanceof byte[]) dos.write((byte[]) o);
            else dos.write(o.toString().getBytes());
            dos.write(SEPARATION.getBytes());
        }
        byte[] dig = DigestCalculator.digestMessage(l);
        dos.write(dig);
        return baos.toByteArray();
    }

    public static void main(String[] args) throws IOException {
        List l = new LinkedList();
        l.add(new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 1024));
        l.add(42);
        l.add("Coucou");
        byte[] b = ListObjectToBytes(l);
        List ll = DivParametersUdp(b);
    }

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

    public static byte[] ReadUdp(DatagramSocket ds) throws IOException {
        DatagramPacket dp = new DatagramPacket(new byte[1024], 1024);
        ds.receive(dp);
        byte[] b = new byte[dp.getLength()];
        System.arraycopy(dp.getData(), 0, b, 0, dp.getLength());
        return b;
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

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
