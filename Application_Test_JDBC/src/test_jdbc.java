/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import database.tables.*;
import database.utilities.MySQLDB;
import database.utilities.OracleDB;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 * @author Nicolas
 */
public class test_jdbc extends javax.swing.JFrame {

    private MySQLDB mysqldConn = null;
    private OracleDB oracleConn = null;
    private ajoutAgent vueAgent = null;
    private int row;
    private int col;

    /**
     * Creates new form test_jdbc
     */
    public test_jdbc() {
        initComponents();
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
            java.util.logging.Logger.getLogger(test_jdbc.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new test_jdbc().setVisible(true));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        requeteLabel = new javax.swing.JLabel();
        requeteTextField = new javax.swing.JTextField();
        envoiRqtButton = new javax.swing.JToggleButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        resultatJTable = new javax.swing.JTable();
        afficherTableButton = new javax.swing.JButton();
        tableLabel = new javax.swing.JLabel();
        listeTableComboBox = new javax.swing.JComboBox<>();
        updateButton = new javax.swing.JButton();
        addElmntLabel = new javax.swing.JLabel();
        listeTable2ComboBox = new javax.swing.JComboBox<>();
        ajouterButton = new javax.swing.JButton();
        modifLabel = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        ConnexionJM = new javax.swing.JMenu();
        MysqlJMI = new javax.swing.JMenuItem();
        OracleJMI = new javax.swing.JMenuItem();
        AboutJM = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        requeteLabel.setText("Requete SQL :");
        requeteLabel.setToolTipText("");

        requeteTextField.setText("Entrez votre requete");

        envoiRqtButton.setText("Envoyer");
        envoiRqtButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                envoiRqtButtonActionPerformed(evt);
            }
        });

        resultatJTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        resultatJTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                resultatJTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(resultatJTable);

        afficherTableButton.setText("Afficher");
        afficherTableButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                afficherTableButtonActionPerformed(evt);
            }
        });

        tableLabel.setText("Afficher table : ");

        listeTableComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Table" }));

        updateButton.setText("Modifier");
        updateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateButtonActionPerformed(evt);
            }
        });

        addElmntLabel.setText("Ajouter nouvel élément :");

        listeTable2ComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Table" }));

        ajouterButton.setText("Ajouter");
        ajouterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ajouterButtonActionPerformed(evt);
            }
        });

        ConnexionJM.setText("Connexion");

        MysqlJMI.setText("MySQL");
        MysqlJMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MysqlJMIActionPerformed(evt);
            }
        });
        ConnexionJM.add(MysqlJMI);

        OracleJMI.setText("Oracle");
        OracleJMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OracleJMIActionPerformed(evt);
            }
        });
        ConnexionJM.add(OracleJMI);

        jMenuBar1.add(ConnexionJM);

        AboutJM.setText("About");
        jMenuBar1.add(AboutJM);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(requeteLabel)
                            .addComponent(envoiRqtButton)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(addElmntLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(tableLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(listeTableComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(afficherTableButton))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(listeTable2ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(ajouterButton))))
                            .addComponent(modifLabel))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(requeteTextField)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(updateButton))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(requeteLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(requeteTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(envoiRqtButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tableLabel)
                    .addComponent(listeTableComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(afficherTableButton))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addElmntLabel)
                    .addComponent(listeTable2ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ajouterButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(modifLabel)
                .addGap(30, 30, 30)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(updateButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void MysqlJMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MysqlJMIActionPerformed

        try {
            if(oracleConn != null) {
                oracleConn.Close();
                JOptionPane.showMessageDialog(this, "Client oracle déconnecté.", "Oracle notification", JOptionPane.INFORMATION_MESSAGE);
            }
            if(mysqldConn == null)
                mysqldConn = new MySQLDB();
            else
                mysqldConn.Connect();
            JOptionPane.showMessageDialog(this, "Client connecté.", "Mysql connection", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            mysqldConn = null;
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(this, "Impossible de se connecter :" + e, "Mysql connection error ", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "Client connecté.", "Mysql connection", JOptionPane.INFORMATION_MESSAGE);

        if (!listeTableComboBox.isEnabled())
            listeTableComboBox.setEnabled(true);
        if (!afficherTableButton.isEnabled())
            afficherTableButton.setEnabled(true);
        if (!requeteTextField.isEnabled())
            requeteTextField.setEnabled(true);
        if (!envoiRqtButton.isEnabled())
            envoiRqtButton.setEnabled(true);
        if (!resultatJTable.isEnabled())
            resultatJTable.setEnabled(true);

        requeteLabel.setText("Requete Mysql:");
        listeTableComboBox.removeAllItems();
        listeTableComboBox.addItem("Billets");
        listeTableComboBox.addItem("Vols");
        listeTableComboBox.addItem("Bagages");
        listeTableComboBox.addItem("Agents");
        listeTable2ComboBox.removeAllItems();
        listeTable2ComboBox.addItem("Billets");
        listeTable2ComboBox.addItem("Vols");
        listeTable2ComboBox.addItem("Bagages");
        listeTable2ComboBox.addItem("Agents");
    }//GEN-LAST:event_MysqlJMIActionPerformed

    private void OracleJMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OracleJMIActionPerformed
        try {
            if(mysqldConn != null) {
                mysqldConn.Close();
                JOptionPane.showMessageDialog(this, "Client mysql déconnecté.", "Mysql notification", JOptionPane.INFORMATION_MESSAGE);
            }
            if(oracleConn == null)
                oracleConn = new OracleDB();
            else
                oracleConn.Connect();
            JOptionPane.showMessageDialog(this, "Client connecté.", "Oracle connection", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            oracleConn = null;
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(this, "Impossible de se connecter :" + e.getMessage(), "Oracle connection error ", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Client connecté.", "Oracle connection", JOptionPane.INFORMATION_MESSAGE);

        if (!listeTableComboBox.isEnabled())
            listeTableComboBox.setEnabled(true);
        if (!afficherTableButton.isEnabled())
            afficherTableButton.setEnabled(true);
        if (!requeteTextField.isEnabled())
            requeteTextField.setEnabled(true);
        if (!envoiRqtButton.isEnabled())
            envoiRqtButton.setEnabled(true);
        if (!resultatJTable.isEnabled())
            resultatJTable.setEnabled(true);

        requeteLabel.setText("Requete Oracle :");
        listeTableComboBox.removeAllItems();
        listeTableComboBox.addItem("Activités");
        listeTableComboBox.addItem("Intervenants");
    }//GEN-LAST:event_OracleJMIActionPerformed

    private void envoiRqtButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_envoiRqtButtonActionPerformed


    }//GEN-LAST:event_envoiRqtButtonActionPerformed

    private void afficherTableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_afficherTableButtonActionPerformed
        final DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        if (listeTableComboBox.getSelectedItem() != null)
            switch (listeTableComboBox.getSelectedItem().toString()) {
                case "Billets":
                    LinkedList<Billets> listebillets = mysqldConn.get_Billets();
                    model.setColumnIdentifiers(new String[]{"numero billet", "numero vol"});
                    listebillets.forEach((b) -> model.addRow(new String[]{b.getNumBillet(), b.getNumVol()}));
                    break;
                case "Vols":
                    LinkedList<Vols> listevols = mysqldConn.get_Vols();
                    resultatJTable.setModel(model);
                    model.setColumnIdentifiers(new String[]{"numero vol", "destination", "heure Arrive", "heure Depart", "arrivee dest", "Avion"});
                    listevols.forEach((v) -> model.addRow(new String[]{v.getNumVol(), v.getDestination(), v.getHeureArrivee(), v.getHeureDepart(), v.getHeureArriveeDestination(), v.getAvionUtilise()}));
                    break;
                case "Bagages":
                    LinkedList<Bagages> listebagages = mysqldConn.get_Bagages();
                    model.setColumnIdentifiers(new String[]{"numero bagage", "poids", "valise"});
                    listebagages.forEach((b) -> model.addRow(new String[]{b.getNumBagage(), b.getPoids().toString(), b.isValise()}));
                    break;
                case "Agents":
                    modifLabel.setText("Pour modifier le poste double-cliquez sur la cellule et entrez la nouvelle valeur");
                    LinkedList<Agents> listeagents = mysqldConn.get_Agents();
                    model.setColumnIdentifiers(new String[]{"nom", "prenom", "poste"});
                    listeagents.forEach((a) -> model.addRow(new String[]{a.getNom(), a.getPrenom(), a.getPoste()}));
                    break;
                case "Activités":
                    LinkedList<Activites> listA = oracleConn.get_Activites();
                    model.setColumnIdentifiers(new String[]{"Cours", "Type", "Date", "Description", "Reference"});
                    listA.forEach((a) -> model.addRow(new String[]{a.getCours(), a.getType(), a.getDate(), a.getDescription(), a.getReference()}));
                    break;
                case "Intervenants":
                    LinkedList<Intervenant> listI = oracleConn.get_Intervenant();
                    model.setColumnIdentifiers(new String[]{"Nom", "Prenom", "Status"});
                    listI.forEach((a) -> model.addRow(new String[]{a.getNom(), a.getPrenom(), a.getStatus()}));
                    break;
                default:
                    System.err.println(listeTableComboBox.getSelectedItem().toString() + " inconnu");
                    break;
            }
        else
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un élément", "Selection error", JOptionPane.ERROR_MESSAGE);
        resultatJTable.setModel(model);
    }//GEN-LAST:event_afficherTableButtonActionPerformed

    private void ajouterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ajouterButtonActionPerformed
        if (listeTable2ComboBox.getSelectedItem() != null)
            if (listeTable2ComboBox.getSelectedItem().toString().equals("Agents")) {
                if (vueAgent == null)
                    vueAgent = new ajoutAgent(mysqldConn);
                else
                    vueAgent.setVisible(true);
            }
    }//GEN-LAST:event_ajouterButtonActionPerformed

    private void updateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateButtonActionPerformed
        Agents temp = new Agents();

        temp.setNom((String) resultatJTable.getValueAt(row, 0));
        temp.setPrenom((String) resultatJTable.getValueAt(row, 1));
        temp.setPoste((String) resultatJTable.getValueAt(row, 2));

        try {
            mysqldConn.update_Agent(temp);
            JOptionPane.showMessageDialog(this, "Modification effectuée!", "Mysql notificatin", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Probleme lors de la modification :" + e, "Mysql notification ", JOptionPane.ERROR_MESSAGE);
        }//GEN-LAST:event_updateButtonActionPerformed
    }

    private void resultatJTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_resultatJTableMouseClicked
        row = resultatJTable.rowAtPoint(evt.getPoint());
        col = resultatJTable.columnAtPoint(evt.getPoint());
    }//GEN-LAST:event_resultatJTableMouseClicked
    // End of variables declaration

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu AboutJM;
    private javax.swing.JMenu ConnexionJM;
    private javax.swing.JMenuItem MysqlJMI;
    private javax.swing.JMenuItem OracleJMI;
    private javax.swing.JLabel addElmntLabel;
    private javax.swing.JButton afficherTableButton;
    private javax.swing.JButton ajouterButton;
    private javax.swing.JToggleButton envoiRqtButton;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox<String> listeTable2ComboBox;
    private javax.swing.JComboBox<String> listeTableComboBox;
    private javax.swing.JLabel modifLabel;
    private javax.swing.JLabel requeteLabel;
    private javax.swing.JTextField requeteTextField;
    private javax.swing.JTable resultatJTable;
    private javax.swing.JLabel tableLabel;
    private javax.swing.JButton updateButton;
    // End of variables declaration//GEN-END:variables
}
