package Frame;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

import javax.swing.JOptionPane;

import NetworkObject.Bean.Login;
import Protocole.IACOP.TypeRequeteIACOP;
import Tools.Crypto.Digest.DigestCalculator;
import Tools.PropertiesReader;

public class LoginFram extends javax.swing.JDialog {
    private static final int User = 0x100;
    private static final int Billet = 0x200;
    private Integer port = -1;
    private InetAddress inetAddressMulticast = null;
    private String username = null;
    private boolean connected = false;
    private List<String> ListConnectes = null;

    public List<String> getListConnectes() {
        return ListConnectes;
    }
    public String getUsername() {
        return username;
    }
    public boolean isConnected() {
        return connected;
    }
    public int getPort() {
        return port;
    }
    public InetAddress getInetAddressMulticast() {
        return inetAddressMulticast;
    }

    public LoginFram(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        UsernameTF = new javax.swing.JTextField();
        connectButton1 = new javax.swing.JButton();
        passwordPF = new javax.swing.JPasswordField();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        BilletTF = new javax.swing.JTextField();
        connectButton2 = new javax.swing.JButton();

        setTitle("Login");
        setResizable(false);

        jLabel1.setText("Username:");

        jLabel2.setText("Password: ");

        connectButton1.setText("Connect");
        connectButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(connectButton1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(passwordPF, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                            .addComponent(UsernameTF))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(UsernameTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(passwordPF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addComponent(connectButton1)
                .addContainerGap())
        );

        jTabbedPane1.addTab("User", jPanel1);

        jLabel3.setText("Billet: ");

        connectButton2.setText("Connect");
        connectButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(BilletTF, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(43, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(connectButton2)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(BilletTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 66, Short.MAX_VALUE)
                .addComponent(connectButton2)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Billet", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private boolean connect(int type)
    {
        Socket s = null;
        try {
            s = new Socket(InetAddress.getByName(PropertiesReader.getProperties("Servername")),Integer.parseInt(PropertiesReader.getProperties("PORT_CHAT")));
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            oos.writeObject(TypeRequeteIACOP.LOGIN_GROUP);
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
            Integer seed = (Integer) ois.readObject();
            byte[] b = new byte[seed];
            int i = s.getInputStream().read(b);
            switch (type)
            {
                case User:
                    oos.writeObject(new Login(UsernameTF.getText(), DigestCalculator.hashPassword(new String(passwordPF.getPassword()), b)));
                    break;
                case Billet:
                    oos.writeObject(BilletTF.getText());
                    break;
            }
            port = (Integer) ois.readObject();
            if (port != -1) {
                inetAddressMulticast = (InetAddress) ois.readObject();
                username = (String) ois.readObject();
                ListConnectes = (List<String>) ois.readObject();
                s.close();
                return true;
            } else
                JOptionPane.showMessageDialog(this,"La combinaison login/password est incorrecte","Erreur",JOptionPane.ERROR_MESSAGE);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }finally {
            if (s != null) {
                try {
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    private void connectButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectButton1ActionPerformed
        if (connected = connect(User))
            setVisible(false);
    }//GEN-LAST:event_connectButton1ActionPerformed

    private void connectButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectButton2ActionPerformed
        if (connected = connect(Billet))
            setVisible(false);
    }//GEN-LAST:event_connectButton2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced inetAddressMulticast Java SE 6) is not available, stay with the default look and feel.
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
            java.util.logging.Logger.getLogger(LoginFram.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LoginFram.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LoginFram.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LoginFram.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                LoginFram dialog = new LoginFram(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField BilletTF;
    private javax.swing.JTextField UsernameTF;
    private javax.swing.JButton connectButton1;
    private javax.swing.JButton connectButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPasswordField passwordPF;
    // End of variables declaration//GEN-END:variables
}
