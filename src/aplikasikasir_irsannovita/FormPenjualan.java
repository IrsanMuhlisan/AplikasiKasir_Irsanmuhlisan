/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package aplikasikasir_irsannovita;
import static java.lang.Thread.sleep;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import net.proteanit.sql.DbUtils;
import java.util.Date;
/**
 *
 * @author User
 */
public class FormPenjualan extends javax.swing.JFrame {
        Connection konek;
        PreparedStatement pst, pst2;
        ResultSet rst;
        int inputstok, inputstok2, inputharga, inputjumlah, kurangistok, tambahstok;
        String harga, idproduk, idprodukpenjualan, iddetail, jam, tanggal, sub_total;
    /**
     * Creates new form FormPenjualan
     */
    public FormPenjualan() {
        initComponents();
        konek = Koneksi.koneksiDB();
        
        tampiljam();
        detail();
        autonumber();
        penjumlahan();
    clear();
      
          //this.setExtendedState(MAXIMIZED_BOTH);
    }

   
   private void simpan(){
        String tgl=txttanggal.getText();
        String jam=txtjam.getText();
      try {
            String sql="insert into penjualan (PenjualanID, DetailID, TanggalPenjualan, JamPenjualan,TotalHarga) value (?,?,?,?,?)";
            pst=konek.prepareStatement(sql);
            pst.setString(1, txtidproduk.getText());
            pst.setString(2, iddetail);
            pst.setString(3, tgl);
            pst.setString(4, jam);
            pst.setString(5, txttotal.getText());
            pst.execute();
            JOptionPane.showMessageDialog(null, "Data Tersimpan");
        } catch (Exception e){
            JOptionPane.showMessageDialog(null, e);
            }
    }
    
    private void total(){
    int total, bayar, kembali;
        total= Integer.parseInt(txtbayar.getText());
        bayar= Integer.parseInt(txttotal.getText());
        kembali=total-bayar;
        String ssub=String.valueOf(kembali);
        txtkembalian.setText(ssub);
    }
    
    private void clear() {
        txtjumlah.setText(""); 
        txtkodetransaksi.setEnabled(false);
        txttotal.setEnabled(false);
        txtkembalian.setEnabled(false);
        txttanggal.setEnabled(false);
        txtjam.setEnabled(false);
    }
    
    public void cari(){
    try {
        String sql="select * from produk where ProdukID LIKE '%"+txtidproduk.getText()+"%'";
        pst=konek.prepareStatement(sql);
        rst=pst.executeQuery();
        tblnamabarang.setModel(DbUtils.resultSetToTableModel(rst));
       } catch (Exception e){ JOptionPane.showMessageDialog(null, e);} 
    }
    
    public void kurangi_stok(){
        int qty;
        qty=Integer.parseInt(txtjumlah.getText());
        kurangistok=inputstok-qty;
    }
    
    private void subtotal(){
    int jumlah, sub;
         jumlah= Integer.parseInt(txtjumlah.getText());
         sub=(jumlah * inputharga);
         sub_total=String.valueOf(sub);     
    }
    
    public void tambah_stok(){
    tambahstok=inputjumlah+inputstok2;
        try {
        String update="update produk set Stok='"+tambahstok+"' where ProdukID='"+idproduk+"'";
        pst2=konek.prepareStatement(update);
        pst2.execute();
        }catch (Exception e){
            JOptionPane.showMessageDialog(null, e);}
    }
    
    public void ambil_stok(){
        try {
            String sql="select * from produk where ProdukID='"+idproduk+"'";
            pst=konek.prepareStatement(sql);
            rst=pst.executeQuery();
            if (rst.next()) {    
            String stok=rst.getString(("Stok"));
            inputstok2= Integer.parseInt(stok);
            }
            }catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);}
    }
    
    public void penjumlahan(){
        int totalBiaya = 0;
        int subtotal;
        DefaultTableModel dataModel = (DefaultTableModel) tbldatatransaksi.getModel();
        int jumlah = tbldatatransaksi.getRowCount();
        for (int i=0; i<jumlah; i++){
        subtotal = Integer.parseInt(dataModel.getValueAt(i, 4).toString());
        totalBiaya += subtotal;
        }
        txttotal.setText(String.valueOf(totalBiaya));
    }
    
    public void autonumber(){
    try{
        String sql = "SELECT MAX(RIGHT(PenjualanID,3)) AS NO FROM penjualan";
        pst=konek.prepareStatement(sql);
        rst=pst.executeQuery();
        while (rst.next()) {
                if (rst.first() == false) {
                    txtkodetransaksi.setText("IDP001");
                } else {
                    rst.last();
                    int auto_id = rst.getInt(1) + 1;
                    String no = String.valueOf(auto_id);
                    int NomorJual = no.length();
                    for (int j = 0; j < 3 - NomorJual; j++) {
                        no = "0" + no;
                    }
                    txtkodetransaksi.setText("IDP" + no);
                }
            }
        rst.close();
        }catch (Exception e){
            JOptionPane.showMessageDialog(null, e);}
    }
    
    public void detail(){
        try {
            String Kode_detail=txtkodetransaksi.getText();
            String KD="D"+Kode_detail;
            String sql="select * from detailpenjualan where DetailID='"+KD+"'";
            pst=konek.prepareStatement(sql);
            rst=pst.executeQuery();
            tbldatatransaksi.setModel(DbUtils.resultSetToTableModel(rst));
           } catch (Exception e){ 
               JOptionPane.showMessageDialog(null, e);} 
    }
    
    public void tampiljam(){
        Thread clock=new Thread(){
            public void run(){
                for(;;){
                    Calendar cal=Calendar.getInstance();
                    SimpleDateFormat format=new SimpleDateFormat("HH:mm:ss");
                    SimpleDateFormat format2=new SimpleDateFormat("yyyy-MM-dd");
                    txtjam.setText(format.format(cal.getTime()));
                    txttanggal.setText(format2.format(cal.getTime()));

                try { sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(FormPenjualan.class.getName()).log(Level.SEVERE, null, ex);
                }
              }
            }
          };
        clock.start();
        }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextField7 = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtjam = new javax.swing.JTextField();
        txttanggal = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtidproduk = new javax.swing.JTextField();
        btncari = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblnamabarang = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        txtjumlah = new javax.swing.JTextField();
        btntambah = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        txtkodetransaksi = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tbldatatransaksi = new javax.swing.JTable();
        btnhapus = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        txttotal = new javax.swing.JTextField();
        btnbayar = new javax.swing.JButton();
        btnkeluar = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        txtbayar = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtkembalian = new javax.swing.JTextField();

        jTextField7.setText("jTextField7");

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(jTable2);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 153, 204));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setText("FORM TRANSAKSI PENJUALAN");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(25, 26, -1, -1));
        jPanel1.add(txtjam, new org.netbeans.lib.awtextra.AbsoluteConstraints(594, 36, 103, -1));
        jPanel1.add(txttanggal, new org.netbeans.lib.awtextra.AbsoluteConstraints(709, 36, 105, -1));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setText("Masukkan ID Produk");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(25, 90, -1, -1));
        jPanel1.add(txtidproduk, new org.netbeans.lib.awtextra.AbsoluteConstraints(25, 116, 150, 30));

        btncari.setText("CARI");
        btncari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btncariActionPerformed(evt);
            }
        });
        jPanel1.add(btncari, new org.netbeans.lib.awtextra.AbsoluteConstraints(202, 117, -1, 30));

        tblnamabarang.setModel(new javax.swing.table.DefaultTableModel(
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
        tblnamabarang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblnamabarangMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblnamabarang);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(25, 159, 512, 140));

        jLabel3.setText("Jumlah");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 200, -1, -1));
        jPanel1.add(txtjumlah, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 230, 90, -1));

        btntambah.setIcon(new javax.swing.ImageIcon("C:\\Users\\User\\Downloads\\icons8-plus-20 (1).png")); // NOI18N
        btntambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btntambahActionPerformed(evt);
            }
        });
        jPanel1.add(btntambah, new org.netbeans.lib.awtextra.AbsoluteConstraints(757, 226, -1, -1));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel5.setText("Kode Transaksi");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(25, 327, -1, -1));

        txtkodetransaksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtkodetransaksiActionPerformed(evt);
            }
        });
        jPanel1.add(txtkodetransaksi, new org.netbeans.lib.awtextra.AbsoluteConstraints(158, 327, 144, -1));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel6.setText("Data Transaksi");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(25, 376, -1, -1));

        tbldatatransaksi.setModel(new javax.swing.table.DefaultTableModel(
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
        tbldatatransaksi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbldatatransaksiMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tbldatatransaksi);

        jPanel1.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(25, 402, 512, 129));

        btnhapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/icons8-delete-20.png"))); // NOI18N
        btnhapus.setText("HAPUS");
        btnhapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnhapusActionPerformed(evt);
            }
        });
        jPanel1.add(btnhapus, new org.netbeans.lib.awtextra.AbsoluteConstraints(592, 453, -1, -1));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel7.setText("Total");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(25, 557, -1, -1));
        jPanel1.add(txttotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(86, 557, 146, -1));

        btnbayar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/icons8-pay-20.png"))); // NOI18N
        btnbayar.setText("BAYAR");
        btnbayar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnbayarActionPerformed(evt);
            }
        });
        jPanel1.add(btnbayar, new org.netbeans.lib.awtextra.AbsoluteConstraints(569, 536, -1, -1));

        btnkeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/icons8-export-20.png"))); // NOI18N
        btnkeluar.setText("KELUAR");
        btnkeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnkeluarActionPerformed(evt);
            }
        });
        jPanel1.add(btnkeluar, new org.netbeans.lib.awtextra.AbsoluteConstraints(707, 536, -1, -1));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel8.setText("Bayar");
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 610, -1, -1));
        jPanel1.add(txtbayar, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 600, 132, -1));

        jLabel9.setText("Kembalian");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 560, -1, -1));
        jPanel1.add(txtkembalian, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 560, 149, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 844, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 826, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 17, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnbayarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnbayarActionPerformed
 total();
        simpan();
        autonumber();
        detail();
        txttotal.setText("");
        txtbayar.setText("");
        txtkembalian.setText("");
        txtidproduk.setText("");
        cari();             // TODO add your handling code here:
    }//GEN-LAST:event_btnbayarActionPerformed

    private void btnkeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnkeluarActionPerformed
new MenuUtama().setVisible(true);
this.dispose();        // TODO add your handling code here:
    }//GEN-LAST:event_btnkeluarActionPerformed

    private void btnhapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnhapusActionPerformed
 try {
            String sql="delete from penjualan where ProdukID=?";
            pst=konek.prepareStatement(sql);
            pst.setString(1, idprodukpenjualan);
            pst.execute();
        }catch (Exception e){
            JOptionPane.showMessageDialog(null, e);
        }
        detail();
        penjumlahan();
        tambah_stok();
        cari();               // TODO add your handling code here:
    }//GEN-LAST:event_btnhapusActionPerformed

    private void btntambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btntambahActionPerformed
            subtotal();
            kurangi_stok();
        try {
            String Kode_detail=txtkodetransaksi.getText();
            iddetail="D"+Kode_detail;
            String sql="insert into detailpenjualan (DetailID, ProdukID, Harga, JumlahProduk, Subtotal) value (?,?,?,?,?)";
            String update="update produk set Stok='"+kurangistok+"' where ProdukID='"+idproduk+"'";
            pst=konek.prepareStatement(sql);
            pst2=konek.prepareStatement(update);
            pst.setString(1, iddetail);
            pst.setString(2, idproduk);
            pst.setString(3, harga);
            pst.setString(4, txtjumlah.getText());
            pst.setString(5, sub_total);
            pst.execute();
            pst2.execute();
        } catch (Exception e){
            JOptionPane.showMessageDialog(null, e);
            }
        detail();
        penjumlahan();
        cari();
        clear();            // TODO add your handling code here:
    }//GEN-LAST:event_btntambahActionPerformed

    private void tbldatatransaksiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbldatatransaksiMouseClicked
try {
            int row=tbldatatransaksi.getSelectedRow();
            idprodukpenjualan=(tbldatatransaksi.getModel().getValueAt(row, 1).toString());
            String sql="select * from penjualan where ProdukID='"+idprodukpenjualan+"'";
            pst=konek.prepareStatement(sql);
            rst=pst.executeQuery();
            if (rst.next()) {   
            String jumlah=rst.getString(("JumlahProduk"));
            inputjumlah= Integer.parseInt(jumlah);
            }
        }catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
        ambil_stok();          // TODO add your handling code here:
    }//GEN-LAST:event_tbldatatransaksiMouseClicked

    private void tblnamabarangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblnamabarangMouseClicked
 try {
        int row=tblnamabarang.getSelectedRow();
        String tabel_klik=(tblnamabarang.getModel().getValueAt(row, 0).toString());
        String sql="select * from produk where ProdukID='"+tabel_klik+"'";
        pst=konek.prepareStatement(sql);
        rst=pst.executeQuery();
        if (rst.next()) {
        idproduk=rst.getString(("ProdukID"));    
        String stok=rst.getString(("Stok"));
        inputstok= Integer.parseInt(stok);
        harga=rst.getString(("Harga"));
        inputharga= Integer.parseInt(harga);
        }
    }catch (Exception e) {
        JOptionPane.showMessageDialog(null, e);
    }           // TODO add your handling code here:
    }//GEN-LAST:event_tblnamabarangMouseClicked

    private void btncariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncariActionPerformed
cari();                        // TODO add your handling code here:
    }//GEN-LAST:event_btncariActionPerformed

    private void txtkodetransaksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtkodetransaksiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtkodetransaksiActionPerformed

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
            java.util.logging.Logger.getLogger(FormPenjualan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FormPenjualan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FormPenjualan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FormPenjualan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FormPenjualan().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnbayar;
    private javax.swing.JButton btncari;
    private javax.swing.JButton btnhapus;
    private javax.swing.JButton btnkeluar;
    private javax.swing.JButton btntambah;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTable tbldatatransaksi;
    private javax.swing.JTable tblnamabarang;
    private javax.swing.JTextField txtbayar;
    private javax.swing.JTextField txtidproduk;
    private javax.swing.JTextField txtjam;
    private javax.swing.JTextField txtjumlah;
    private javax.swing.JTextField txtkembalian;
    private javax.swing.JTextField txtkodetransaksi;
    private javax.swing.JTextField txttanggal;
    private javax.swing.JTextField txttotal;
    // End of variables declaration//GEN-END:variables
}
