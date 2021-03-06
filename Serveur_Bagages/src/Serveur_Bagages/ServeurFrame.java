package Serveur_Bagages;

import java.io.PrintStream;

import Protocole.LUGAP.LUGAPThreadRequest;
import ServeurClientLog.Threads.ThreadServeur;
import Tools.Affichage.TextAreaOutputStream;
import Tools.PropertiesReader;

public class ServeurFrame extends javax.swing.JFrame {

    private ThreadServeur Ts = null;

    /**
     * Creates new form ServeurFrame
     */
    public ServeurFrame() {
        initComponents();
        System.setOut(new PrintStream(new TextAreaOutputStream(ConsoleTA)));
        try {
            Class.forName("Tools.Bd.Bd");
        } catch (ClassNotFoundException e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
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
                if (info.getName().equals("GTK+")) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ServeurFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ServeurFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ServeurFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ServeurFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new ServeurFrame().setVisible(true));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ConsoleSPTA = new javax.swing.JScrollPane();
        ConsoleTA = new javax.swing.JTextArea();
        StateRB = new javax.swing.JRadioButton();
        StartB = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Serveur_Bagages");
        setFont(new java.awt.Font("Liberation Mono", 0, 14)); // NOI18N

        ConsoleTA.setEditable(false);
        ConsoleTA.setColumns(20);
        ConsoleTA.setFont(ConsoleTA.getFont().deriveFont(ConsoleTA.getFont().getSize()+2f));
        ConsoleTA.setRows(5);
        ConsoleTA.setToolTipText("");
        ConsoleTA.setFocusable(false);
        ConsoleSPTA.setViewportView(ConsoleTA);

        StateRB.setText("State");
        StateRB.setToolTipText("");
        StateRB.setEnabled(false);
        StateRB.setFocusable(false);

        StartB.setText("Start");
        StartB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                StartBActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ConsoleSPTA, javax.swing.GroupLayout.DEFAULT_SIZE, 620, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(StateRB)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(StartB)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(StateRB)
                    .addComponent(StartB))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ConsoleSPTA, javax.swing.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void StartBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_StartBActionPerformed
        if (!StateRB.isSelected()) {
            try {
                Ts = new ThreadServeur(Integer.valueOf(PropertiesReader.getProperties("PORT_BAGAGES")), Integer.valueOf(PropertiesReader.getProperties("NB_THREADS")), LUGAPThreadRequest.class);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                System.exit(-1);
            }
            Ts.start();
        }
        StartB.setEnabled(false);
        StateRB.setSelected(true);
    }//GEN-LAST:event_StartBActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane ConsoleSPTA;
    private javax.swing.JTextArea ConsoleTA;
    private javax.swing.JButton StartB;
    private javax.swing.JRadioButton StateRB;
    // End of variables declaration//GEN-END:variables
}
