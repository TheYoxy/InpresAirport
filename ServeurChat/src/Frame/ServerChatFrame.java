/*
 * Copyright 2017 floryan.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package Frame;

import com.sun.org.apache.xpath.internal.SourceTree;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

import IACOP.TypeRequeteIACOP;
import IACOP.TypeSpecialRequest;
import NetworkObject.Login;
import Tools.Bd;
import Tools.BdType;
import Tools.DigestCalculator;
import Tools.Procedural;
import Tools.PropertiesReader;
import Tools.TextAreaOutputStream;

public class ServerChatFrame extends javax.swing.JFrame {
    private final static int PORT_CHAT = Integer.parseInt(PropertiesReader.getProperties("PORT_CHAT"));
    private final static int PORT_JOUR = /*new Random().nextInt(65535 - 1024) + 1024*/ PORT_CHAT;
    private static Integer nbQuestion = 1;
    private InetAddress group;
    private SocketAddress groupAddress;
    private Bd mysql;
    private Thread Ecoute;
    private Thread Chat;
    private InetAddress Multicast;
    private DefaultListModel<String> listModelUser;
    private DatagramSocket ds;
    private List<String> Connecter;

    /**
     * Creates new form Main
     */
    public ServerChatFrame() {
        initComponents();
        postInit();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("GTK+".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ServerChatFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ServerChatFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ServerChatFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ServerChatFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ServerChatFrame().setVisible(true);
            }
        });
    }

    private void postInit() {
        System.setOut(new PrintStream(new TextAreaOutputStream(logsTA)));
        try {
            group = InetAddress.getByName("224.0.100.1");
        } catch (UnknownHostException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Impossible de retrouver le groupe pour le multicast", "Erreur", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }
        try {
            mysql = new Bd(BdType.MySql);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Impossible de se connecter à la base de données.\nFin de l'application", "Erreur", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }
        Connecter = new LinkedList<>();
        groupAddress = new InetSocketAddress(group, PORT_JOUR + 1);

        Chat = new Thread(() -> {
            try {
                ds = new DatagramSocket(PORT_JOUR);
//                ds.setReuseAddress(true);
                System.out.println("Port du jour: " + PORT_JOUR);
            } catch (IOException e) {
                e.printStackTrace(System.out);
                JOptionPane.showMessageDialog(this, "Impossible de bind le multicast.\nFin de l'application", "Erreur", JOptionPane.ERROR_MESSAGE);
                System.exit(-1);
            }
            while (!Chat.isInterrupted()) try {
                byte[] b = Procedural.ReadUdp(ds);
                System.out.println("\nReçu: " + Arrays.toString(b));
                List l = Procedural.DivParametersUdp(b);
                System.out.println("List: " + l.subList(0,l.size() - 1) + Arrays.toString((byte[])l.get(l.size() - 1)));
                byte[] digest = (byte[]) l.remove(l.size() - 1);
                System.out.println("List: " + l);
                byte[] calculated = DigestCalculator.digestMessage(l);
                System.out.println("Calculé");
                if (MessageDigest.isEqual(digest,calculated)) {
                    if (TypeRequeteIACOP.fromInt((Integer) l.get(0)) == TypeRequeteIACOP.POST_QUESTION)
                        l.add(2, nbQuestion++);
                    if (TypeRequeteIACOP.fromInt((Integer)l.get(0)) == TypeRequeteIACOP.POST_EVENT
                            && TypeSpecialRequest.fromInt((Integer)l.get(1)) == TypeSpecialRequest.DISCONNECT)
                        disconnectUser((String) l.get(2));
                    envoiMulticast(ds, Procedural.ListObjectToBytes(l));
                    System.out.println("Message envoyé");
                }
                else {
                    System.out.println("Erreur lors du contrôle d'intègrité.\n Le paquêt n'a pas été retransmis");
                    System.out.println("Calculé: " + Arrays.toString(calculated) + " (" + calculated.length + ") !=");
                    System.out.println("Reçu   : " + Arrays.toString(digest) + " (" + digest.length + ")");
                }
            } catch (IOException e) {
                e.printStackTrace(System.out);
                System.out.println("Réseau: " + group.toString().substring(1));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace(System.out);
            }
        });
        Chat.setName("Thread d'écoute des messages UDP");
        Chat.start();
        /*new Thread(() -> {
            MulticastSocket ms = null;
            try {
                ms = new MulticastSocket(PORT_JOUR + 1);
                ms.joinGroup(group);
                byte[] b = Procedural.ReadUdp(ms);
                System.out.println("Thread: message multicast reçu");
                System.out.println(new String(b));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();*/
        Ecoute = new Thread(() -> {
            ServerSocket ss = null;
            try {
                ss = new ServerSocket(PORT_CHAT);
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Impossible de bind le serveur de réception.\nFin de l'application", "Erreur", JOptionPane.ERROR_MESSAGE);
                System.exit(-1);
            }
            while (!Ecoute.isInterrupted()) try {
                Socket s = ss.accept();
                System.out.print("Tentative de connexion de " + s.getRemoteSocketAddress().toString().substring(1) + ": ");
                ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
                TypeRequeteIACOP type = (TypeRequeteIACOP) ois.readObject();
                ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
                if (type == TypeRequeteIACOP.LOGIN_GROUP) {
                    Integer seedSize = 10;
                    byte[] nb = new SecureRandom().generateSeed(seedSize);
                    oos.writeObject(seedSize);
                    s.getOutputStream().write(nb);
                    Object o = ois.readObject();

                    ResultSet rs;
                    if (o instanceof Login)
                        rs = mysql.SelectUserPassword(((Login) o).getUser());
                    else if (o instanceof String)
                        rs = mysql.SelectUserBillet((String) o);
                    else {
                        oos.writeObject(-1);
                        return;
                    }

                    if (rs.next()) {
                        byte[] pass = null;
                        if (o instanceof Login) pass = DigestCalculator.hashPassword(rs.getString(1), nb);
                        if ((o instanceof Login && MessageDigest.isEqual(((Login) o).getPassword(), pass)) ||
                                o instanceof String) {
                            String user = rs.getString(o instanceof Login ? 2 : 1);
                            if (!Connecter.contains(user)) {
                                oos.writeObject(PORT_JOUR);
                                oos.writeObject(group);
                                oos.writeObject(user);
                                ajoutConnecte(user, s.getRemoteSocketAddress());
                                oos.writeObject(Connecter);
                                System.out.println("Réussi");
                            } else {
                                System.out.println("Echouée");
                                oos.writeObject(-1);
                            }
                        } else {
                            System.out.println("Echouée");
                            oos.writeObject(-1);
                        }
                    } else {
                        System.out.println("Echouée");
                        oos.writeObject(-1);
                    }
                } else {
                    System.out.println("Echouée");
                    oos.writeObject(-1);
                }
                s.close();
            } catch (IOException e) {
                System.out.println();
                e.printStackTrace(System.out);
            } catch (SQLException e) {
                System.out.println();
                e.printStackTrace(System.out);
            } catch (ClassNotFoundException e) {
                System.out.println();
                e.printStackTrace(System.out);
            }
        });
        Ecoute.setName("Thread d'écoute TCP");
        Ecoute.start();

        listModelUser = new DefaultListModel<>();
        connectedList.setModel(listModelUser);
    }

    private void disconnectUser(String o) {
        listModelUser.removeElement(o);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        logsTA = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        connectedList = new javax.swing.JList<>();
        commandTF = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1280, 720));

        logsTA.setColumns(20);
        logsTA.setRows(5);
        jScrollPane1.setViewportView(logsTA);

        jScrollPane2.setViewportView(connectedList);

        commandTF.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                commandTFKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 415, Short.MAX_VALUE)
                                        .addComponent(commandTF))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 452, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jScrollPane1)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(commandTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void commandTFKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_commandTFKeyTyped
        // TODO add your handling code here:
        if (evt.getKeyChar() == '\n') {
            switch (commandTF.getText().toLowerCase()) {
                case "stop":
                    break;
                default:
                    System.out.println("Message incompris");
                    break;
            }
            commandTF.setText("");
        }
    }//GEN-LAST:event_commandTFKeyTyped

    /**
     * Ajout un utilisateur à la liste et le notifie à tout les clients du multicast
     *
     * @param username Nom de l'utilisateur
     * @param s Caca
     */
    private void ajoutConnecte(String username, SocketAddress s) throws IOException {
        listModelUser.addElement(username + " (" + s.toString().substring(1) + ")");
        Connecter.add(username);
        List l = new LinkedList();
        l.add(TypeRequeteIACOP.POST_EVENT.getValue());
        l.add(TypeSpecialRequest.NEW_CONNECTED.getValue());
        l.add(username);
        byte[] b = Procedural.ListObjectToBytes(l);
        DatagramPacket dp = new DatagramPacket(b,b.length,group,PORT_JOUR + 1);
        ds.send(dp);
    }

    private void envoiMulticast(DatagramSocket ds, byte[] data) throws IOException {
        ds.send(new DatagramPacket(data, data.length, group, PORT_JOUR + 1));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField commandTF;
    private javax.swing.JList<String> connectedList;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea logsTA;
    // End of variables declaration//GEN-END:variables
}
