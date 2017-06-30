/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import entity.BarangEntity;
import helper.helper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
//import org.apache.poi.openxml4j.opc.internal.FileHelper;

/**
 * FXML Controller class
 *
 * @author Minami
 */
public class BarangController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    TableView<BarangEntity> tabel;
    @FXML
    TableColumn<BarangEntity, String> kode_barang, nama_barang, satuan, harga_beli, harga_jual_ecer, harga_jual_grosir, jumlah;
    @FXML
    Button bsimpan, bclear, brefresh, bnext, bprev, bberanda, bhapus,bcek;
    @FXML
    TextField tkodebarang, tnamabarang, thargabeli, thargajualecer, thargajualgrosir, tjumlah, tlimit, tcari;
    @FXML
    ComboBox csatuan;
    @FXML
    Label ldata;

    @FXML
    private void keypress(KeyEvent e) {
        if (e.isControlDown() && e.getCode() == KeyCode.S) {
            rawsimpan();
        } else if (e.isControlDown() && e.getCode() == KeyCode.D) {
            rawhapus();
        } else if (e.isControlDown() && e.getCode() == KeyCode.R) {
            loaddata();
        } else if (e.isControlDown() && e.getCode() == KeyCode.E) {
            loaddata();
            rawclear();
        }
    }
    helper h = new helper();
    ObservableList ols = FXCollections.observableArrayList();
    int limit, offset;
    String kode;
    NumberFormat nf = NumberFormat.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        loaddata();
        pagging();
        cari();
        refresh();
        select();
        simpan();
        hapus();
        clearfield();
        makeup();
        loadsatuan();
        cekhargabeli();
        loadsatuan();
        
    }

    private void makeup() {
        bhapus.disableProperty().set(Boolean.TRUE);
        bsimpan.setGraphic(new ImageView(getClass().getResource("/image/document-save-5.png").toString()));
        bsimpan.setTooltip(new Tooltip("Simpan Data"));
        bhapus.setGraphic(new ImageView(getClass().getResource("/image/edit-delete-9.png").toString()));
        bhapus.setTooltip(new Tooltip("Hapus Data"));
        bclear.setGraphic(new ImageView(getClass().getResource("/image/edit-clear-2.png").toString()));
        bclear.setTooltip(new Tooltip("Clear Field"));
        brefresh.setGraphic(new ImageView(getClass().getResource("/image/view-refresh.png").toString()));
        brefresh.setTooltip(new Tooltip("Refresh Data"));
        bnext.setGraphic(new ImageView(getClass().getResource("/image/go-next-3.png").toString()));
        bnext.setTooltip(new Tooltip("Data Selanjutnya"));
        bprev.setGraphic(new ImageView(getClass().getResource("/image/go-previous-3.png").toString()));
        bprev.setTooltip(new Tooltip("Data Sebelumnya"));
        bberanda.setGraphic(new ImageView(getClass().getResource("/image/go-home-4.png").toString()));
        bberanda.setTooltip(new Tooltip("Beranda"));
    }

    private void loadsatuan() {
       ObservableList ols = FXCollections.observableArrayList();
        String data="";
        int i=1;
        int j=1;
        try {
            BufferedReader br=new BufferedReader(new FileReader(new File("satuan")));
            while ((data=br.readLine())!=null) {
                if(i==j){
                    ols.add(data);
                    i++;
                    j++;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(BarangController.class.getName()).log(Level.SEVERE, null, ex);
        }
        csatuan.setItems(ols);
    }

    private void loaddata() {
        try {
            tabel.getItems().clear();
            tabel.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            offset = 0;
            limit = Integer.parseInt(tlimit.getText());
            h.connect();
            ResultSet res = h.read("SELECT id_barang,nama_barang,satuan_barang,"
                    + "harga_beli_barang,harga_jual_ecer_barang,harga_jual_grosir_barang,jumlah_barang"
                    + " FROM barang LIMIT " + limit + " OFFSET " + offset + " ").executeQuery();
            while (res.next()) {
                ols.add(new BarangEntity(res.getString("id_barang"),
                        res.getString("nama_barang"),
                        res.getString("satuan_barang"),
                        nf.format(res.getDouble("harga_beli_barang")),
                        nf.format(res.getDouble("harga_jual_ecer_barang")),
                        nf.format(res.getDouble("harga_jual_grosir_barang")),
                        res.getString("jumlah_barang")));
            }

            ResultSet resjumahdata = h.read("SELECT COUNT(id_barang) AS total FROM barang").executeQuery();
            resjumahdata.next();
            ldata.setText(ols.size() + "/" + resjumahdata.getString("total") + " Data");

            h.disconnect();
            kode_barang.setCellValueFactory(new PropertyValueFactory<>("kode_barang"));
            nama_barang.setCellValueFactory(new PropertyValueFactory<>("nama_barang"));
            satuan.setCellValueFactory(new PropertyValueFactory<>("satuan"));
            harga_beli.setCellValueFactory(new PropertyValueFactory<>("harga_beli"));
            harga_jual_ecer.setCellValueFactory(new PropertyValueFactory<>("harga_jual_ecer"));
            harga_jual_grosir.setCellValueFactory(new PropertyValueFactory<>("harga_jual_grosir"));
            jumlah.setCellValueFactory(new PropertyValueFactory<>("jumlah"));
            tabel.setItems(ols);
        } catch (SQLException | NumberFormatException | NullPointerException ex) {
            Logger.getLogger(BarangController.class.getName()).log(Level.SEVERE, null, ex);
            Alert al = new Alert(Alert.AlertType.ERROR);
            al.setTitle("Kesalahan");
            al.setHeaderText("Terjadi Kesalahan Pada Aplikasi");
            VBox v = new VBox();
            v.setPadding(new Insets(5, 5, 5, 5));
            v.setSpacing(5);
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            TextArea terror = new TextArea(sw.toString());
            terror.setMaxWidth(400);
            terror.setMaxHeight(400);
            terror.setWrapText(true);
            v.getChildren().add(new Label("Detail error yang terbaca :"));
            v.getChildren().add(terror);
            al.getDialogPane().setContent(v);
            al.showAndWait();
        }
    }

    private void pagging() {
        bnext.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                try {
                    tabel.getItems().clear();
                    tabel.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
                    offset = offset + limit;
                    limit = Integer.parseInt(tlimit.getText());
                    h.connect();
                    ResultSet res = h.read("SELECT id_barang,nama_barang,satuan_barang,"
                            + "harga_beli_barang,harga_jual_ecer_barang,harga_jual_grosir_barang,jumlah_barang"
                            + " FROM barang LIMIT " + limit + " OFFSET " + offset + " ").executeQuery();
                    while (res.next()) {
                        ols.add(new BarangEntity(res.getString("id_barang"),
                                res.getString("nama_barang"),
                                res.getString("satuan_barang"),
                                nf.format(res.getDouble("harga_beli_barang")),
                                nf.format(res.getDouble("harga_jual_ecer_barang")),
                                nf.format(res.getDouble("harga_jual_grosir_barang")),
                                res.getString("jumlah_barang")));
                    }

                    ResultSet resjumahdata = h.read("SELECT COUNT(id_barang) AS total FROM barang").executeQuery();
                    resjumahdata.next();
                    ldata.setText(ols.size() + "/" + resjumahdata.getString("total") + " Data");

                    h.disconnect();
                    kode_barang.setCellValueFactory(new PropertyValueFactory<>("kode_barang"));
                    nama_barang.setCellValueFactory(new PropertyValueFactory<>("nama_barang"));
                    satuan.setCellValueFactory(new PropertyValueFactory<>("satuan"));
                    harga_beli.setCellValueFactory(new PropertyValueFactory<>("harga_beli"));
                    harga_jual_ecer.setCellValueFactory(new PropertyValueFactory<>("harga_jual_ecer"));
                    harga_jual_grosir.setCellValueFactory(new PropertyValueFactory<>("harga_jual_grosir"));
                    jumlah.setCellValueFactory(new PropertyValueFactory<>("jumlah"));
                    tabel.setItems(ols);
                } catch (SQLException | NumberFormatException | NullPointerException ex) {
                    Logger.getLogger(BarangController.class.getName()).log(Level.SEVERE, null, ex);
                    Alert al = new Alert(Alert.AlertType.ERROR);
                    al.setTitle("Kesalahan");
                    al.setHeaderText("Terjadi Kesalahan Pada Aplikasi");
                    VBox v = new VBox();
                    v.setPadding(new Insets(5, 5, 5, 5));
                    v.setSpacing(5);
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    ex.printStackTrace(pw);
                    TextArea terror = new TextArea(sw.toString());
                    terror.setMaxWidth(400);
                    terror.setMaxHeight(400);
                    terror.setWrapText(true);
                    v.getChildren().add(new Label("Detail error yang terbaca :"));
                    v.getChildren().add(terror);
                    al.getDialogPane().setContent(v);
                    al.showAndWait();
                }
            }
        });

        bprev.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                try {
                    tabel.getItems().clear();
                    tabel.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
                    offset = offset - limit;
                    limit = Integer.parseInt(tlimit.getText());
                    h.connect();
                    ResultSet res = h.read("SELECT id_barang,nama_barang,satuan_barang,"
                            + "harga_beli_barang,harga_jual_ecer_barang,harga_jual_grosir_barang,jumlah_barang"
                            + " FROM barang LIMIT " + limit + " OFFSET " + offset + " ").executeQuery();
                    while (res.next()) {
                        ols.add(new BarangEntity(res.getString("id_barang"),
                                res.getString("nama_barang"),
                                res.getString("satuan_barang"),
                                nf.format(res.getDouble("harga_beli_barang")),
                                nf.format(res.getDouble("harga_jual_ecer_barang")),
                                nf.format(res.getDouble("harga_jual_grosir_barang")),
                                res.getString("jumlah_barang")));
                    }

                    ResultSet resjumahdata = h.read("SELECT COUNT(id_barang) AS total FROM barang").executeQuery();
                    resjumahdata.next();
                    ldata.setText(ols.size() + "/" + resjumahdata.getString("total") + " Data");

                    h.disconnect();
                    kode_barang.setCellValueFactory(new PropertyValueFactory<>("kode_barang"));
                    nama_barang.setCellValueFactory(new PropertyValueFactory<>("nama_barang"));
                    satuan.setCellValueFactory(new PropertyValueFactory<>("satuan"));
                    harga_beli.setCellValueFactory(new PropertyValueFactory<>("harga_beli"));
                    harga_jual_ecer.setCellValueFactory(new PropertyValueFactory<>("harga_jual_ecer"));
                    harga_jual_grosir.setCellValueFactory(new PropertyValueFactory<>("harga_jual_grosir"));
                    jumlah.setCellValueFactory(new PropertyValueFactory<>("jumlah"));
                    tabel.setItems(ols);
                } catch (SQLException | NumberFormatException | NullPointerException ex) {
                    Logger.getLogger(BarangController.class.getName()).log(Level.SEVERE, null, ex);
                    Alert al = new Alert(Alert.AlertType.ERROR);
                    al.setTitle("Kesalahan");
                    al.setHeaderText("Terjadi Kesalahan Pada Aplikasi");
                    VBox v = new VBox();
                    v.setPadding(new Insets(5, 5, 5, 5));
                    v.setSpacing(5);
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    ex.printStackTrace(pw);
                    TextArea terror = new TextArea(sw.toString());
                    terror.setMaxWidth(400);
                    terror.setMaxHeight(400);
                    terror.setWrapText(true);
                    v.getChildren().add(new Label("Detail error yang terbaca :"));
                    v.getChildren().add(terror);
                    al.getDialogPane().setContent(v);
                    al.showAndWait();
                }

            }
        });

    }

    private void cari() {
        tcari.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                bprev.setDisable(true);
                bnext.setDisable(true);
                try {
                    tabel.getItems().clear();
                    tabel.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
                    offset = offset - 1;
                    limit = Integer.parseInt(tlimit.getText());
                    h.connect();
                    Object[] o = new Object[7];
                    o[0] = "%" + tcari.getText() + "%";
                    o[1] = "%" + tcari.getText() + "%";
                    o[2] = "%" + tcari.getText() + "%";
                    o[3] = "%" + tcari.getText() + "%";
                    o[4] = "%" + tcari.getText() + "%";
                    o[5] = "%" + tcari.getText() + "%";
                    o[6] = "%" + tcari.getText() + "%";
                    ResultSet res = h.readdetail("SELECT id_barang,nama_barang,satuan_barang,"
                            + "harga_beli_barang,harga_jual_ecer_barang,harga_jual_grosir_barang,jumlah_barang"
                            + " FROM barang WHERE id_barang ILIKE ? OR nama_barang ILIKE ? "
                            + "OR satuan_barang ILIKE ? OR harga_beli_barang::text ILIKE ? "
                            + "OR harga_jual_ecer_barang::text ILIKE ? "
                            + "OR harga_jual_grosir_barang::text ILIKE ? OR jumlah_barang::text ILIKE ? ", 7, o).executeQuery();
                    while (res.next()) {
                        ols.add(new BarangEntity(res.getString("id_barang"),
                                res.getString("nama_barang"),
                                res.getString("satuan_barang"),
                                nf.format(res.getDouble("harga_beli_barang")),
                                nf.format(res.getDouble("harga_jual_ecer_barang")),
                                nf.format(res.getDouble("harga_jual_grosir_barang")),
                                res.getString("jumlah_barang")));
                    }

                    ResultSet resjumahdata = h.read("SELECT COUNT(id_barang) AS total FROM barang").executeQuery();
                    resjumahdata.next();
                    ldata.setText(ols.size() + "/" + resjumahdata.getString("total") + " Data");

                    h.disconnect();
                    kode_barang.setCellValueFactory(new PropertyValueFactory<>("kode_barang"));
                    nama_barang.setCellValueFactory(new PropertyValueFactory<>("nama_barang"));
                    satuan.setCellValueFactory(new PropertyValueFactory<>("satuan"));
                    harga_beli.setCellValueFactory(new PropertyValueFactory<>("harga_beli"));
                    harga_jual_ecer.setCellValueFactory(new PropertyValueFactory<>("harga_jual_ecer"));
                    harga_jual_grosir.setCellValueFactory(new PropertyValueFactory<>("harga_jual_grosir"));
                    jumlah.setCellValueFactory(new PropertyValueFactory<>("jumlah"));
                    tabel.setItems(ols);
                } catch (SQLException | NullPointerException ex) {
                    Logger.getLogger(BarangController.class.getName()).log(Level.SEVERE, null, ex);
                    Alert al = new Alert(Alert.AlertType.ERROR);
                    al.setTitle("Kesalahan");
                    al.setHeaderText("Terjadi Kesalahan Pada Aplikasi");
                    VBox v = new VBox();
                    v.setPadding(new Insets(5, 5, 5, 5));
                    v.setSpacing(5);
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    ex.printStackTrace(pw);
                    TextArea terror = new TextArea(sw.toString());
                    terror.setMaxWidth(400);
                    terror.setMaxHeight(400);
                    terror.setWrapText(true);
                    v.getChildren().add(new Label("Detail error yang terbaca :"));
                    v.getChildren().add(terror);
                    al.getDialogPane().setContent(v);
                    al.showAndWait();
                }

            }
        });
    }

    private void refresh() {
        brefresh.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                loaddata();
                bprev.setDisable(false);
                bnext.setDisable(false);
                tcari.clear();
                bhapus.disableProperty().set(Boolean.TRUE);
            }
        });

        bberanda.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                loaddata();
                bprev.setDisable(false);
                bnext.setDisable(false);
                tcari.clear();
                bhapus.disableProperty().set(Boolean.TRUE);
            }
        });
    }

    private void select() {
        tabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                int i = tabel.getSelectionModel().getSelectedIndex();
                kode = String.valueOf(kode_barang.getCellData(i));
                tkodebarang.setText(String.valueOf(kode_barang.getCellData(i)));
                tnamabarang.setText(String.valueOf(nama_barang.getCellData(i)));
                csatuan.getEditor().setText(String.valueOf(satuan.getCellData(i)));
                thargabeli.setText(String.valueOf(harga_beli.getCellData(i)));
                thargajualecer.setText(String.valueOf(harga_jual_ecer.getCellData(i)));
                thargajualgrosir.setText(String.valueOf(harga_jual_grosir.getCellData(i)));
                tjumlah.setText(String.valueOf(jumlah.getCellData(i)));
                bhapus.disableProperty().set(Boolean.FALSE);
            }
        });
    }

    private void rawsimpan() {
        try {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            h.connect();
            if (kode == null || kode.equals("")) {
                Object[] o = new Object[7];
                o[0] = tkodebarang.getText();
                o[1] = tnamabarang.getText();
                o[2] = csatuan.getEditor().getText();
                o[3] = Double.parseDouble(thargabeli.getText().replaceAll("[.,]", ""));
                o[4] = Double.parseDouble(thargajualecer.getText().replaceAll("[.,]", ""));
                o[5] = Double.parseDouble(thargajualgrosir.getText().replaceAll("[.,]", ""));
                o[6] = Integer.parseInt(tjumlah.getText());
                h.insert("INSERT INTO barang(id_barang,nama_barang,satuan_barang,"
                        + "harga_beli_barang,harga_jual_ecer_barang,harga_jual_grosir_barang,jumlah_barang) "
                        + "VALUES(?,?,?,?,?,?,?)", 7, o);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Data berhasil ditambahkan");
                alert.setContentText("Lakukan Refresh jika data belum terlihat");
                alert.showAndWait();
                tkodebarang.requestFocus();
            } else {
                Object[] o = new Object[8];
                o[0] = tkodebarang.getText();
                o[1] = tnamabarang.getText();
                o[2] = csatuan.getEditor().getText();
                o[3] = Double.parseDouble(thargabeli.getText().replaceAll("[.,]", ""));
                o[4] = Double.parseDouble(thargajualecer.getText().replaceAll("[.,]", ""));
                o[5] = Double.parseDouble(thargajualgrosir.getText().replaceAll("[.,]", ""));
                o[6] = Integer.parseInt(tjumlah.getText());
                o[7] = kode;
                Alert alertcon = new Alert(Alert.AlertType.CONFIRMATION);
                alertcon.setHeaderText("Yakin ingin memperbaharui data ini?");
                alertcon.setContentText("Data yang sudah diperbaharui tidak bisa dikembalikan lagi.");
                ButtonType ya = new ButtonType("Ya");
                ButtonType tidak = new ButtonType("Tidak");
                alertcon.getButtonTypes().setAll(ya, tidak);
                Optional<ButtonType> opt = alertcon.showAndWait();
                if (opt.get() == ya) {
                    h.update("UPDATE barang SET id_barang=?,nama_barang=?,satuan_barang=?,"
                            + "harga_beli_barang=?,harga_jual_ecer_barang=?,harga_jual_grosir_barang=?,jumlah_barang=?"
                            + "WHERE id_barang=? ", 8, o);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Data berhasil diperbaharui");
                    alert.setContentText("Lakukan Refresh jika data belum terlihat");
                    alert.showAndWait();
                }

            }
            h.disconnect();
            loaddata();
        } catch (SQLException | NumberFormatException | NullPointerException ex) {
            Logger.getLogger(BarangController.class.getName()).log(Level.SEVERE, null, ex);
            Alert al = new Alert(Alert.AlertType.ERROR);
            al.setTitle("Kesalahan");
            al.setHeaderText("Terjadi Kesalahan Pada Aplikasi");
            VBox v = new VBox();
            v.setPadding(new Insets(5, 5, 5, 5));
            v.setSpacing(5);
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            TextArea terror = new TextArea(sw.toString());
            terror.setMaxWidth(400);
            terror.setMaxHeight(400);
            terror.setWrapText(true);
            v.getChildren().add(new Label("Detail error yang terbaca :"));
            v.getChildren().add(terror);
            al.getDialogPane().setContent(v);
            al.showAndWait();

        }
    }

    private void simpan() {
        bsimpan.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                rawsimpan();

            }
        }
        );
    }

    private void rawhapus() {
        Alert alertcon = new Alert(Alert.AlertType.CONFIRMATION);
        alertcon.setHeaderText("Yakin ingin menghapus data ini?");
        alertcon.setContentText("Data yang sudah dihapus tidak bisa dikembalikan lagi.");
        ButtonType ya = new ButtonType("Ya");
        ButtonType tidak = new ButtonType("Tidak");
        alertcon.getButtonTypes().setAll(ya, tidak);
        Optional<ButtonType> opt = alertcon.showAndWait();
        if (opt.get() == ya) {
            try {
                ObservableList<BarangEntity> ols = tabel.getSelectionModel().getSelectedItems();
                h.connect();
                for (int i = 0; i < ols.size(); i++) {
                    h.delete("DELETE FROM barang WHERE id_barang=?", kode_barang.getCellData(ols.get(i)));
                }
                h.disconnect();
                loaddata();
            } catch (SQLException | NumberFormatException | NullPointerException ex) {
                Logger.getLogger(BarangController.class.getName()).log(Level.SEVERE, null, ex);
                Alert al = new Alert(Alert.AlertType.ERROR);
                al.setTitle("Kesalahan");
                al.setHeaderText("Terjadi Kesalahan Pada Aplikasi");
                VBox v = new VBox();
                v.setPadding(new Insets(5, 5, 5, 5));
                v.setSpacing(5);
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                ex.printStackTrace(pw);
                TextArea terror = new TextArea(sw.toString());
                terror.setMaxWidth(400);
                terror.setMaxHeight(400);
                terror.setWrapText(true);
                v.getChildren().add(new Label("Detail error yang terbaca :"));
                v.getChildren().add(terror);
                al.getDialogPane().setContent(v);
                al.showAndWait();
            }

        }
    }

    private void hapus() {
        bhapus.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                rawhapus();
            }
        });
    }

    private void rawclear() {
        kode = null;
        tkodebarang.clear();
        tnamabarang.clear();
        csatuan.getEditor().clear();
        thargabeli.clear();
        thargajualecer.clear();
        thargajualgrosir.clear();
        tjumlah.clear();
        bhapus.disableProperty().set(Boolean.TRUE);
    }

    private void clearfield() {
        bclear.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                rawclear();
            }
        });
    }
    
    
    private void cekhargabeli(){
        bcek.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                int hargabeli=Integer.parseInt(thargabeli.getText().replaceAll("[.,]", ""));
                int jumlahbarang=Integer.parseInt(tjumlah.getText());
                thargabeli.setText(String.valueOf(hargabeli/jumlahbarang));
            }
        });
    }

}
