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
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

public class Procedural {
    public static final char SEPARATION = 0xfffd;
    public static final String BYTEIDENTIFIER = "BYTE";
    public static final String INTIDENTIFIER = "WZK";

    public static List<Object> DivParametersUdp(byte[] array) {
        if (array == null) throw new IllegalArgumentException();
        LinkedList<Object> l = new LinkedList<>();
        byte[] b = new byte[array.length -1];
        System.arraycopy(array,1,b,0,b.length);

        String s = new String(b);
        String[] ss = s.split(String.valueOf(SEPARATION),ByteBuffer.wrap(new String(array).substring(0,Integer.BYTES).getBytes()).asIntBuffer().get() + 1);
        for (int i = 0; i < ss.length; i++) {
            l.add(i == ss.length - 1
                    ? ss[i].getBytes()
                    : ss[i].length() == Integer.BYTES + INTIDENTIFIER.length() && ss[i].startsWith(INTIDENTIFIER)
                        ? Integer.valueOf(ByteBuffer.wrap(ss[i].substring(INTIDENTIFIER.length()).getBytes()).asIntBuffer().get())
                        : ss[i]);
        }
        return l;
    }

    public static byte[] ListObjectToBytes(List l) throws IOException {
        int i = 0;
        for (Object o : l) {
            if (o instanceof Integer)
                i += Integer.BYTES + INTIDENTIFIER.length();
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
            } else {
                dos.write(o.toString().getBytes());
            }
            dos.write((byte) SEPARATION);
        }
        dos.write(DigestCalculator.digestMessage(l));
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
