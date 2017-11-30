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

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Random;

import javax.swing.JOptionPane;

import IACOP.TypeRequeteIACOP;
import NetworkObject.Login;
import Tools.Bd;
import Tools.BdType;
import Tools.DigestCalculator;
import Tools.PropertiesReader;
import Tools.TextAreaOutputStream;

public class Main extends javax.swing.JFrame {
    private final static int PORT_CHAT = Integer.parseInt(PropertiesReader.getProperties("PORT_CHAT"));
    private final static int PORT_JOUR = /*new Random().nextInt(65535 - 1024) + 1024*/ PORT_CHAT;
    private InetAddress group;
    private Bd mysql;
    private Thread Ecoute;
    private Thread Chat;
    private InetAddress Multicast;

    /**
     * Creates new form Main
     */
    public Main() {
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
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Main().setVisible(true);
            }
        });
    }

    private void postInit() {
        System.setOut(new PrintStream(new TextAreaOutputStream(logsTA)));
        try {
            group = InetAddress.getByName("234.0.1.1");
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
        Chat = new Thread(() -> {
            DatagramSocket ds = null;
            try {
                ds = new DatagramSocket(PORT_JOUR);
                System.out.println("Port du jour: " + PORT_JOUR);
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Impossible de bind le multicast.\nFin de l'application", "Erreur", JOptionPane.ERROR_MESSAGE);
                System.exit(-1);
            }
            while (!Chat.isInterrupted()) {
                try {
                    DatagramPacket dp = new DatagramPacket(new byte[1024], 1024);
                    ds.receive(dp);
                    String s = new String(dp.getData());
                    System.out.println("Message de " + dp.getSocketAddress().toString().substring(1) + " : " + s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        Chat.setName("Thread d'écoute des messages envoyés");
        Chat.start();

        Ecoute = new Thread(() -> {
            ServerSocket ss = null;
            try{
                ss = new ServerSocket(PORT_CHAT);
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Impossible de bind le serveur de réception.\nFin de l'application", "Erreur", JOptionPane.ERROR_MESSAGE);
                System.exit(-1);
            }


            while (!Ecoute.isInterrupted()) {
                try
                {
                    Socket s = ss.accept();
                    System.out.println("Connexion de " + s.getLocalSocketAddress().toString().substring(1));
                    ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
                    TypeRequeteIACOP type = (TypeRequeteIACOP) ois.readObject();
                    ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
                    if (type == TypeRequeteIACOP.LOGIN_GROUP) {
                        Integer seedSize = 10;
                        byte[] nb = new SecureRandom().generateSeed(seedSize);
                        System.out.println("seedSize = " + seedSize);
                        oos.writeObject(seedSize);
                        System.out.println("Arrays.toString(nb) = " + Arrays.toString(nb));
                        s.getOutputStream().write(nb);
                        Object o = ois.readObject();
                        if (o instanceof Login) {
                            Login l = (Login) o;
                            try {
                                ResultSet rs = mysql.SelectUserPassword(l.getUser());
                                if (rs.next()) {
                                    byte[] pass = DigestCalculator.hashPassword(rs.getString(1), nb);
                                    if (MessageDigest.isEqual(l.getPassword(), pass)){
                                        oos.writeObject(PORT_JOUR);
                                        oos.writeObject(group);
                                        String user = rs.getString(2);
                                    }
                                    else
                                        oos.writeObject(-1);
                                } else {
                                    oos.writeObject(-1);
                                }
                            } catch (SQLException e) {
                                e.printStackTrace(System.out);
                                oos.writeObject(-1);
                            }
                        }
                        else if (o instanceof String) {
                            String billet = (String) o;
                            try {
                                ResultSet rs = mysql.SelectUserBillet(billet);
                                if(rs.next()) {
                                    oos.writeObject(PORT_JOUR);
                                    oos.writeObject(group);
                                    String user = rs.getString(1);
                                }
                                else
                                    oos.writeObject(-1);
                            } catch (SQLException e) {
                                e.printStackTrace(System.out);
                                oos.writeObject(-1);
                            }
                        }
                        else
                            oos.writeObject(-1);
                    } else
                        oos.writeObject(-1);
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                }
            }

        });
        Ecoute.setName("Thread d'écoute TCP");
        Ecoute.start();
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField commandTF;
    private javax.swing.JList<String> connectedList;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea logsTA;
    // End of variables declaration//GEN-END:variables
}
