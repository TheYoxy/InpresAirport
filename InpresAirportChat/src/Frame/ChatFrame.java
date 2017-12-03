package Frame;

import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;

import IACOP.TypeRequeteIACOP;
import Tools.Procedural;
import Tools.PropertiesReader;
import Tools.TextAreaOutputStream;

public class ChatFrame extends javax.swing.JFrame {

    private LoginFram lf;
    private Thread Chat;
    private MulticastSocket ms;
    private SocketAddress server;
    private String username;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList<String> ListQuestion;
    private javax.swing.JList<String> connectList;
    private javax.swing.JButton envoyerB;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextArea msgTa;
    private javax.swing.JTextField msgTf;
    public ChatFrame() {
        initComponents();
        lf = new LoginFram(this, true);
        Login();
        System.setOut(new PrintStream(new TextAreaOutputStream(msgTa)));
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
            java.util.logging.Logger.getLogger(ChatFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ChatFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ChatFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ChatFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ChatFrame().setVisible(true);
            }
        });
    }

    public void Login() {
        lf.setVisible(true);
        if (lf.isConnected()) {
            username = lf.getUsername();
            Chat = new Thread(() -> {
                try {
                    ms = new MulticastSocket(lf.getPort() + 1);
//                    ms.setLoopbackMode(true);
//                    ms.setReuseAddress(true);
                    /*Pour le débug sur une seule machine*/
//                    ms.setInterface(InetAddress.getByName(PropertiesReader.getProperties("Servername")));
                    ms.joinGroup(lf.getInetAddressMulticast());
                    while (!Chat.isInterrupted()) {
                        byte[] b = Procedural.ReadUdp(ms);
                        List l = Procedural.DivParametersUdp(b);
                        /* Traitement uniquement des packets ne provenant pas du client lui même */
                        for(int i = 0; i < l.size(); i++) {
                            System.err.println(i);
                            System.err.println("Classe: " + l.get(i).getClass());
                            System.err.println("toString(): " + l.get(i).toString());
                            System.err.println();
                        }

                        if (!l.get(0).toString().startsWith(username)) {
                            System.out.println(l.get(0) + ": " + l.get(2));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            Chat.setName("Thread de gestion des message venant du serveur");
            Chat.start();

            try {
                server = new InetSocketAddress(InetAddress.getByName(PropertiesReader.getProperties("SERVER_NAME")), lf.getPort());
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Connexion au serveur impossible", "Erreur", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }
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
        msgTa = new javax.swing.JTextArea();
        msgTf = new javax.swing.JTextField();
        envoyerB = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        connectList = new javax.swing.JList<>();
        jScrollPane3 = new javax.swing.JScrollPane();
        ListQuestion = new javax.swing.JList<>();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1280, 720));

        msgTa.setEditable(false);
        msgTa.setColumns(20);
        msgTa.setRows(5);
        jScrollPane1.setViewportView(msgTa);

        msgTf.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                msgTfKeyTyped(evt);
            }
        });

        envoyerB.setText("Envoyer");
        envoyerB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                envoyerBActionPerformed(evt);
            }
        });

        connectList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                connectListMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(connectList);

        ListQuestion.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ListQuestionMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(ListQuestion);

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(msgTf)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 576, Short.MAX_VALUE)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addGap(0, 0, Short.MAX_VALUE)
                                                .addComponent(envoyerB)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 493, Short.MAX_VALUE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(msgTf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(envoyerB))
                                        .addComponent(jScrollPane2)
                                        .addComponent(jScrollPane3))
                                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void envoyerBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_envoyerBActionPerformed
        // TODO add your handling code here:
        try {
            sendMessage(TypeRequeteIACOP.POST_QUESTION, msgTf.getText());
            msgTf.setText("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_envoyerBActionPerformed

    private void msgTfKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_msgTfKeyTyped
        // TODO add your handling code here:
        if (evt.getKeyChar() == '\n') {
            try {
                sendMessage(TypeRequeteIACOP.POST_QUESTION, msgTf.getText());
                msgTf.setText("");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_msgTfKeyTyped

    private void ListQuestionMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ListQuestionMouseClicked
        if (evt.getClickCount() == 2) {

        }
    }//GEN-LAST:event_ListQuestionMouseClicked

    private void connectListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_connectListMouseClicked
        if (evt.getClickCount() == 2) {

        }
    }//GEN-LAST:event_connectListMouseClicked

    private void sendMessage(TypeRequeteIACOP type, String message) throws IOException {
        List l = new LinkedList();
        l.add(username);
        l.add(type.getValue());
        l.add(message);
        byte[] b = Procedural.ListObjectToBytes(l);

        DatagramPacket dp = new DatagramPacket(b, b.length, server);
        ms.send(dp);
        System.out.println("La question à bien été posée");
    }
    // End of variables declaration//GEN-END:variables
}
