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
package Application_Bagages;

import LUGAP.NetworkObject.Bagage;
import LUGAP.NetworkObject.Table;
import LUGAP.ReponseLUGAP;
import LUGAP.RequeteLUGAP;
import LUGAP.TypeReponseLUGAP;
import LUGAP.TypeRequeteLUGAP;
import Tools.Procedural;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Vector;

public class ListeBagages extends javax.swing.JDialog {
    private Table BagagesTable = null;
    private ObjectOutputStream Oos = null;
    private ObjectInputStream Ois = null;
    private Socket Serveur;
    //serialUID
    /**
     * Creates new form ListeBagages
     */
    public ListeBagages(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    public ListeBagages(java.awt.Frame parent, boolean modal, String vol, ObjectOutputStream oos, ObjectInputStream ois, Socket serveur) {
        this(parent, modal);
        setTitle("Vol " + vol);
        VolLabel2.setText(vol);
        Oos = oos;
        Ois = ois;
        Serveur = serveur;
        Affichage();
    }

    private void RecuperationVol()
    {
        ReponseLUGAP rep;
        try {
            Oos.writeObject(new RequeteLUGAP(TypeRequeteLUGAP.Request_Bagages_Vol, VolLabel2.getText(), Procedural.IpPort(Serveur)));
            rep = (ReponseLUGAP) Ois.readObject();
            if (rep.getCode() == TypeReponseLUGAP.SQL_LOCK) JOptionPane.showMessageDialog(this, "Les bagages de ce vol sont déjà en cours de modification.", "Erreur", JOptionPane.ERROR_MESSAGE);
            else if (rep.getCode() != TypeReponseLUGAP.OK) JOptionPane.showMessageDialog(this, "Erreur au niveau du serveur", "Erreur", JOptionPane.ERROR_MESSAGE);
            else BagagesTable = (Table) rep.getParam();
        } catch (IOException e) {
            //Todo Affichage d'une messagebox disant qu'il y a une erreur
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void UpdateJTable() {
        //Si jamais il est bloquant, il lance une exception qui arrête la fonction
        ResultatJTable.setModel(new DefaultTableModel(BagagesTable.getChamps(), BagagesTable.getTete()));
    }

    private void Affichage() {
        RecuperationVol();
        UpdateJTable();
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
            java.util.logging.Logger.getLogger(ListeBagages.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ListeBagages.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ListeBagages.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ListeBagages.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ListeBagages dialog = new ListeBagages(new javax.swing.JFrame(), true);
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        VolLabel = new javax.swing.JLabel();
        BagagesLabel = new javax.swing.JLabel();
        VolLabel2 = new javax.swing.JLabel();
        ScrollPane1 = new javax.swing.JScrollPane();
        ResultatJTable = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        VolLabel.setText("Vol :");

        BagagesLabel.setText("Bagages pour ce vol :");

        VolLabel2.setText("Nom du vol");

        ResultatJTable.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{
                        {null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null}
                },
                new String[]{
                        "Identifiant", "Poids", "Type", "Réceptioné(O/N)", "Chargé en soute (O/N)", "Vérifié par la douane (O/N)", "Remarques"
                }
        ));
        ResultatJTable.setCellSelectionEnabled(true);
        ResultatJTable.setEnabled(false);
        ResultatJTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ResultatJTableMouseClicked(evt);
            }
        });
        ScrollPane1.setViewportView(ResultatJTable);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(ScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 683, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(VolLabel)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(VolLabel2))
                                                        .addComponent(BagagesLabel))
                                                .addGap(0, 0, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(VolLabel)
                                        .addComponent(VolLabel2))
                                .addGap(25, 25, 25)
                                .addComponent(BagagesLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(ScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 371, Short.MAX_VALUE)
                                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private static boolean Comparaison(Vector<String> v1, Vector<String> v2) {
        if(v1.size() != v2.size()) return false;
        for (int i = 0; i < v1.size(); i++) if (v1.get(i) != v2.get(i)) return false;
        return true;
    }

    private void ResultatJTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ResultatJTableMouseClicked
        if (evt.getClickCount() == 1) {
            int pos = ResultatJTable.rowAtPoint(evt.getPoint());
            Vector<String> champModifier = BagagesTable.getChamps().get(pos);
            DetailsBagage db = new DetailsBagage((Frame) this.getParent(), true, Bagage.FromVector(champModifier));
            db.setVisible(true);
            Vector<String> retour = db.getBagage().toVector();
            //Une modification a eu lieu sur un des champs
            if (!Comparaison(champModifier,retour)) {
                LinkedList<Object> l = new LinkedList<>();
                l.add(retour.get(0));
                for (int i = 0; i < retour.size(); i++) if (!retour.get(i).contentEquals(champModifier.get(i))) {
                    l.add(retour.get(i));
                    l.add(i);
                }
                try {
                    //Envoi de l'objet au serveur
                    Oos.writeObject(new RequeteLUGAP(TypeRequeteLUGAP.Update_Bagage_Vol,l,Procedural.IpPort(Serveur)));
                    ReponseLUGAP rep = (ReponseLUGAP) Ois.readObject();
                    if (rep.getCode() != TypeReponseLUGAP.OK) JOptionPane.showMessageDialog(this, "Erreur au niveau du serveur", "Erreur", JOptionPane.ERROR_MESSAGE);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                Affichage();
            }
        }
    }//GEN-LAST:event_ResultatJTableMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel BagagesLabel;
    private javax.swing.JTable ResultatJTable;
    private javax.swing.JScrollPane ScrollPane1;
    private javax.swing.JLabel VolLabel;
    private javax.swing.JLabel VolLabel2;
    // End of variables declaration//GEN-END:variables
}
