package Frame;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

import Protocole.IACOP.TypeRequeteIACOP;
import Protocole.IACOP.TypeSpecialRequest;
import Tools.Affichage.TextAreaOutputStream;
import Tools.Procedural;
import Tools.PropertiesReader;

public class ChatFrame extends javax.swing.JFrame {

    private LoginFram lf;
    private Thread Chat;
    private MulticastSocket ms;
    private SocketAddress server;
    private String username;
    private DefaultListModel<String> Users;
    private DefaultComboBoxModel<String> AnswerModel;

    public ChatFrame() {
        initComponents();
        lf = new LoginFram(this, true);
        Users = new DefaultListModel<>();
        AnswerModel = new DefaultComboBoxModel<>();
        connectList.setModel(Users);
        answerCB.setModel(this.AnswerModel);
        Login();
        System.setOut(new PrintStream(new TextAreaOutputStream(msgTa)));
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                super.windowClosing(windowEvent);
                try {
                    sendDisconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.exit(0);
            }
        });
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
        java.awt.EventQueue.invokeLater(() -> new ChatFrame().setVisible(true));
    }

    public void Login() {
        lf.setVisible(true);
        if (lf.isConnected()) {
            username = lf.getUsername();

            Chat = new Thread(() -> {
                try {
                    ms = new MulticastSocket(lf.getPort() + 1);

                    System.out.println("Properties \"Servername\": " + PropertiesReader.getProperties("Servername"));
                    System.out.println("InetAddress \"Servername \": " + InetAddress.getByName(PropertiesReader.getProperties("Servername")));
                    System.out.println("InetAddress.getByName(PropertiesReader.getProperties(\"Servername\")).isLoopbackAddress() = " +
                        InetAddress.getByName(PropertiesReader.getProperties("Servername")).isLoopbackAddress());

                    if (InetAddress.getByName(PropertiesReader.getProperties("Servername")).isLoopbackAddress()){
//                        ms.setNetworkInterface(NetworkInterface.getByInetAddress(InetAddress.getLoopbackAddress()));
                        ms.setLoopbackMode(true);
                    }

                    System.out.println("getInetAddressMulticast(): " + lf.getInetAddressMulticast());
                    ms.joinGroup(lf.getInetAddressMulticast());
                    while (!Chat.isInterrupted()) {
                        try{
                            byte[] b = Procedural.ReadUdp(ms);
                            System.err.println("Message reçu");
                            List l = Procedural.DivParametersUdp(b);
                            /* Traitement uniquement des packets ne provenant pas du client lui même */
                            TypeRequeteIACOP t = TypeRequeteIACOP.fromInt((Integer) l.get(0));
                            switch (t)
                            {
                                case POST_QUESTION:
                                    System.out.println(l.get(1) + " (" + l.get(2) + ") >" + l.get(3));
                                    AjoutQuestion((Integer) l.get(2));
                                    break;
                                case ANSWER_QUESTION:
                                    System.out.println(l.get(1) + " (Réponse à " + l.get(2) + ") >" + l.get(3));
                                    SupressionQuestion((Integer) l.get(2));
                                    break;
                                case POST_EVENT:
                                {
                                    TypeSpecialRequest tsr = TypeSpecialRequest.fromInt((Integer) l.get(1));
                                    switch (tsr){
                                        case NEW_CONNECTED:
                                            Users.addElement((String) l.get(2));
                                            break;
                                        case DISCONNECT:
                                            for (int i = 0; i < Users.size(); i++)
                                                if (l.get(2).equals(Users.get(i)))
                                                    Users.removeElementAt(i);
                                            break;
                                        case OTHERS:
                                            System.out.println(l.get(2) + "> " + l.get(3));
                                            break;
                                    }
                                }
                                break;
                            }
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            Chat.setName("Thread de gestion des message venant du serveur");
            Chat.start();

            for(String s: lf.getListConnectes())
                Users.addElement(s);

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

    private void AjoutQuestion(Integer nb) {
        this.answerCB.addItem(nb.toString());
    }

    private void SupressionQuestion(Integer nb) {
        for (int i = 0; i < AnswerModel.getSize(); i++)
            if (AnswerModel.getElementAt(i).equals(nb.toString()))
                AnswerModel.removeElementAt(i);
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
        typeCB = new javax.swing.JComboBox<>();
        answerCB = new javax.swing.JComboBox<>();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Chat");
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

        jScrollPane2.setViewportView(connectList);

        typeCB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Question", "Réponse", "Information" }));
        typeCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                typeCBActionPerformed(evt);
            }
        });

        answerCB.setAutoscrolls(true);
        answerCB.setEnabled(false);
        answerCB.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                answerCBItemStateChanged(evt);
            }
        });

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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(typeCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(answerCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(envoyerB))
                    .addComponent(msgTf)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 782, Short.MAX_VALUE))
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
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(envoyerB)
                            .addComponent(typeCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(answerCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane2))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void envoyerBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_envoyerBActionPerformed
        // TODO add your handling code here:
        envoyerMessageAction();
    }//GEN-LAST:event_envoyerBActionPerformed

    private void msgTfKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_msgTfKeyTyped
        // TODO add your handling code here:
        if (evt.getKeyChar() == '\n') {
            envoyerMessageAction();
        }
    }//GEN-LAST:event_msgTfKeyTyped

    private void typeCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_typeCBActionPerformed
        // TODO add your handling code here:
        answerCB.setEnabled(typeCB.getSelectedIndex() == 1);
    }//GEN-LAST:event_typeCBActionPerformed

    private void answerCBItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_answerCBItemStateChanged
        // TODO add your handling code here:
        System.out.println("STATE CHANGED");
    }//GEN-LAST:event_answerCBItemStateChanged

    private void envoyerMessageAction()
    {
        try {
            TypeRequeteIACOP t = null;
            String message = null;
            switch (typeCB.getSelectedIndex())
            {
                case 0:
                    sendMessage(TypeRequeteIACOP.POST_QUESTION,msgTf.getText());
                    break;
                case 1:
                    if (answerCB.getSelectedItem() == null)
                    {
                        JOptionPane.showMessageDialog(this,"Impossible d'envoyer une réponse, il n'y a aucune question qui à été sélectionnée.");
                        return;
                    }
                    sendMessage(TypeRequeteIACOP.ANSWER_QUESTION,Integer.parseInt(answerCB.getSelectedItem().toString()), msgTf.getText());
                    break;
                case 2:
                    sendMessage(TypeRequeteIACOP.POST_EVENT,TypeSpecialRequest.OTHERS.getValue(),msgTf.getText());
                    break;
            }
            msgTf.setText("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(TypeRequeteIACOP type,Object... message) throws IOException {
        List l = new LinkedList();
        l.add(type.getValue());
        l.add(username);
        for (Object o : message)
            if (o instanceof Integer)
                l.add(o);
            else
                l.add(o.toString());

        byte[] b = Procedural.ListObjectToBytes(l);
        DatagramPacket dp = new DatagramPacket(b, b.length, server);
        ms.send(dp);
        // System.out.println("La question à bien été posée");
    }

    private void sendDisconnect() throws IOException {
        List l = new LinkedList();
        l.add(TypeRequeteIACOP.POST_EVENT.getValue());
        l.add(TypeSpecialRequest.DISCONNECT.getValue());
        l.add(username);
        byte[] b = Procedural.ListObjectToBytes(l);
        DatagramPacket dp = new DatagramPacket(b, b.length,server);
        ms.send(dp);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> answerCB;
    private javax.swing.JList<String> connectList;
    private javax.swing.JButton envoyerB;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea msgTa;
    private javax.swing.JTextField msgTf;
    private javax.swing.JComboBox<String> typeCB;
    // End of variables declaration//GEN-END:variables
}
