package Application_Bagages;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.JOptionPane;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableModel;

import NetworkObject.Bean.Table;
import Protocole.LUGAP.ReponseLUGAP;
import Protocole.LUGAP.RequeteLUGAP;
import Protocole.LUGAP.TypeReponseLUGAP;
import Protocole.LUGAP.TypeRequeteLUGAP;
import Tools.PropertiesReader;

public class Application_Bagage extends javax.swing.JFrame {

    private Login Log = null;
    private ListeBagages ListeBag = null;
    private Socket Serveur = null;
    private ObjectInputStream Ois = null;
    private ObjectOutputStream Oos = null;

    public Application_Bagage() {
        initComponents();
    }

    public Application_Bagage(boolean ouverture) {
        initComponents();

        this.setEnabled(false);
        try {
            Serveur = new Socket(InetAddress.getByName(PropertiesReader.getProperties("BILLETS_NAME")), Integer.valueOf(PropertiesReader.getProperties("PORT_BILLETS")));
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }
        this.setEnabled(true);
        Log = new Login(this, true, Serveur);

        Connection(ouverture);
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
        } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Application_Bagage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new Application_Bagage().setVisible(true));
    }

    private void DisconnectMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DisconnectMIActionPerformed
        try {
            Oos.writeObject(new RequeteLUGAP(TypeRequeteLUGAP.Logout));
            ReponseLUGAP rep = (ReponseLUGAP) Ois.readObject();
            if (rep.getCode() != TypeReponseLUGAP.OK)
                //TODO Gestion d'exception
                return;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Log.ResetChamps();
        clearChamps();
        Connection(true);
    }//GEN-LAST:event_DisconnectMIActionPerformed

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        BagagisteLabel = new javax.swing.JLabel();
        BagagisteNomPrenomLabel = new javax.swing.JLabel();
        ScrollPane = new javax.swing.JScrollPane();
        ResultatJTable = new javax.swing.JTable();
        VolsLabel = new javax.swing.JLabel();
        MenuBar = new javax.swing.JMenuBar();
        OptionMenu = new javax.swing.JMenu();
        DisconnectMI = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Application Bagages");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        BagagisteLabel.setText("Bagagiste :");

        BagagisteNomPrenomLabel.setFont(new java.awt.Font("Calibri", 2, 12)); // NOI18N

        ResultatJTable.setModel(new javax.swing.table.DefaultTableModel(new Object[][]{

        }, new String[]{

        }));
        ResultatJTable.setCellSelectionEnabled(true);
        ResultatJTable.setEnabled(false);
        ResultatJTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ResultatJTableMouseClicked(evt);
            }
        });
        ScrollPane.setViewportView(ResultatJTable);

        VolsLabel.setText("Vols prévus ce jour :");

        OptionMenu.setText("Options");

        DisconnectMI.setText("Disconnect");
        DisconnectMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DisconnectMIActionPerformed(evt);
            }
        });
        OptionMenu.add(DisconnectMI);

        MenuBar.add(OptionMenu);

        setJMenuBar(MenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(ScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(BagagisteLabel).addGap(18, 18, 18).addComponent(BagagisteNomPrenomLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)).addComponent(VolsLabel)).addGap(0, 636, Short.MAX_VALUE))).addContainerGap()));
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(BagagisteLabel).addComponent(BagagisteNomPrenomLabel)).addGap(24, 24, 24).addComponent(VolsLabel).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(ScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 353, Short.MAX_VALUE).addContainerGap()));

        getAccessibleContext().setAccessibleDescription("");

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void ResultatJTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ResultatJTableMouseClicked
        if (evt.getClickCount() == 2) {
            int row = ResultatJTable.rowAtPoint(evt.getPoint());
            if (row >= 0) {
                String vol = ResultatJTable.getValueAt(row, 0).toString();
                //Si il y a un lock, ListeBag renvoie une exception non gèrée, et fini la méthode
                ListeBag = new ListeBagages(this, true, vol, Oos, Ois, Serveur);
                ListeBag.setVisible(true);
                DisconnectMIActionPerformed(null);
            }
        }
    }//GEN-LAST:event_ResultatJTableMouseClicked

    private void clearChamps() {
        ResultatJTable.setModel(new DefaultTableModel());
        BagagisteNomPrenomLabel.setText("");
    }

    public void Connection(boolean ouvertureFenetre) {
        this.setVisible(ouvertureFenetre);
        Log.setVisible(true);
        if (!Log.isConnecter()) {
            formWindowClosing(null);
            dispose();
            return;
        }
        if (!ouvertureFenetre) {
            this.setVisible(true);
        }
        if (Ois == null) {
            Ois = Log.getOis();
        }
        if (Oos == null) {
            Oos = Log.getOos();
        }
        BagagisteNomPrenomLabel.setText(Log.getNomPrenomUser());

        ReponseLUGAP rep = null;
        try {
            Oos.writeObject(new RequeteLUGAP(TypeRequeteLUGAP.Request_Vols));
            rep = (ReponseLUGAP) Ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }
        if (rep.getCode() != TypeReponseLUGAP.OK) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la réception du tableau", "Exception", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }
        Table t = (Table) rep.getParam();
        ResultatJTable.setModel(new DefaultTableModel(t.getChamps(), t.getTete()));
    }

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        try {
            Oos.writeObject(new RequeteLUGAP(TypeRequeteLUGAP.Disconnect));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_formWindowClosing

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel BagagisteLabel;
    private javax.swing.JLabel BagagisteNomPrenomLabel;
    private javax.swing.JMenuItem DisconnectMI;
    private javax.swing.JMenuBar MenuBar;
    private javax.swing.JMenu OptionMenu;
    private javax.swing.JTable ResultatJTable;
    private javax.swing.JScrollPane ScrollPane;
    private javax.swing.JLabel VolsLabel;
    // End of variables declaration//GEN-END:variables
}
