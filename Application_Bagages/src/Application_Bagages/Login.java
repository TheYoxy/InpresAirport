package Application_Bagages;

import LUGAP.ReponseLUGAP;
import LUGAP.RequeteLUGAP;
import LUGAP.TypeReponseLUGAP;
import LUGAP.TypeRequeteLUGAP;
import Tools.Procedural;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author floryan
 */
public class Login extends javax.swing.JDialog {

    private Socket Socket = null;
    private boolean Connecter = false;
    private ObjectOutputStream Oos = null;
    private ObjectInputStream Ois = null;
    private String NomPrenomUser = "";

    /**
     * Creates new form Login
     */
    public Login(java.awt.Frame parent, boolean modal, Socket serveur) {
        super(parent, modal);
        initComponents();
        Socket = serveur;
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
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(() -> {
            Login dialog = null;
            try {
                dialog = new Login(new JFrame(), true, new Socket("127.0.0.1", 26011));
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert dialog != null;
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                }
            });
            dialog.setVisible(true);
        });
    }

    public String getNomPrenomUser() {
        return NomPrenomUser;
    }

    public ObjectOutputStream getOos() {
        return Oos;
    }

    public ObjectInputStream getOis() {
        return Ois;
    }

    public boolean isConnecter() {
        return Connecter;
    }

    public void ResetChamps() {
        LoginTF.setText("");
        PasswordPF.setText("");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ConnectionButton = new javax.swing.JButton();
        LoginLabel = new javax.swing.JLabel();
        LoginTF = new javax.swing.JTextField();
        PasswordLabel = new javax.swing.JLabel();
        PasswordPF = new javax.swing.JPasswordField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Login");
        setResizable(false);
        setType(java.awt.Window.Type.UTILITY);

        ConnectionButton.setText("Connect !");
        ConnectionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ConnectionButtonActionPerformed(evt);
            }
        });

        LoginLabel.setText("Login :");

        LoginTF.setToolTipText("");

        PasswordLabel.setText("Password :");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(LoginTF)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(LoginLabel)
                                                        .addComponent(PasswordLabel)
                                                        .addComponent(ConnectionButton))
                                                .addGap(0, 91, Short.MAX_VALUE))
                                        .addComponent(PasswordPF))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(LoginLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(LoginTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(PasswordLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(PasswordPF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(20, 20, 20)
                                .addComponent(ConnectionButton)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void ConnectionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ConnectionButtonActionPerformed
        int challenge = 0;
        ReponseLUGAP rep = null;
        //Demande du digest
        try {
            if (Oos == null) {
                Oos = new ObjectOutputStream(Socket.getOutputStream());
            }
            Oos.writeObject(new RequeteLUGAP(TypeRequeteLUGAP.TryConnect));
            if (Ois == null)
                Ois = new ObjectInputStream(Socket.getInputStream());
            rep = (ReponseLUGAP) Ois.readObject();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Exception", e.getLocalizedMessage(), JOptionPane.ERROR_MESSAGE);
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Exception", e.getLocalizedMessage(), JOptionPane.ERROR_MESSAGE);
        }
        if (rep == null) return;
        if (rep.getCode() == TypeReponseLUGAP.OK)
            challenge = (int) rep.getParam();
        System.out.println("Challenge: " + challenge);
        //Login
        try {
            Oos.writeObject(new RequeteLUGAP(TypeRequeteLUGAP.Login, "", new LUGAP.NetworkObject.Login(LoginTF.getText(), RequeteLUGAP.hashPassword(new String(PasswordPF.getPassword()), challenge)), Procedural.IpPort(Socket)));
            rep = (ReponseLUGAP) Ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (rep == null) return;
        switch ((TypeReponseLUGAP) rep.getCode()) {
            case UNKNOWN_LOGIN:
                JOptionPane.showMessageDialog(this, "Le login entré est inexistant", "Retour de connection", JOptionPane.ERROR_MESSAGE);
                break;
            case BAD_PASSWORD:
                JOptionPane.showMessageDialog(this, "Le mot de passe entré est incorrect", "Retour de connection", JOptionPane.WARNING_MESSAGE);
                break;
            case LOG:
                JOptionPane.showMessageDialog(this, "Connecté", "Retour de connection", JOptionPane.INFORMATION_MESSAGE);
                this.setVisible(false);
                Connecter = true;
                NomPrenomUser = (String) rep.getParam();
                break;
        }
    }//GEN-LAST:event_ConnectionButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ConnectionButton;
    private javax.swing.JLabel LoginLabel;
    private javax.swing.JTextField LoginTF;
    private javax.swing.JLabel PasswordLabel;
    private javax.swing.JPasswordField PasswordPF;
    // End of variables declaration//GEN-END:variables
}
