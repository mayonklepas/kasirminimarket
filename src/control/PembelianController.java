/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import entity.PembelianEntity;
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
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Minami
 */
public class PembelianController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    TableView<PembelianEntity> tabel;
    @FXML
    TableColumn<PembelianEntity, String> id_pembelian, tanggal, kode_barang, nama_barang, satuan, harga_beli, jumlah, total;
    @FXML
    Button bsimpan, bclear, brefresh, bnext, bprev, bberanda, bhapus, bcek;
    @FXML
    TextField tkodebarang, tnamabarang, thargabeli, thargajualecer, thargajualgrosir, tjumlah, tlimit, tcari;
    @FXML
    DatePicker dtanggal;
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
        dtanggal.setValue(LocalDate.now());
        loadsatuan();
        cekidbarang();
        bagiharga();

    }

    private void makeup() {
        bhapus.disableProperty().set(Boolean.TRUE);
        bsimpan.setGraphic(new ImageView(getClass().getResource("/image/document-save-5.png").toString()));
        bsimpan.setTooltip(new Tooltip("Save Data"));
        bhapus.setGraphic(new ImageView(getClass().getResource("/image/edit-delete-9.png").toString()));
        bhapus.setTooltip(new Tooltip("Delete Data"));
        bclear.setGraphic(new ImageView(getClass().getResource("/image/edit-clear-2.png").toString()));
        bclear.setTooltip(new Tooltip("Clear Field"));
        brefresh.setGraphic(new ImageView(getClass().getResource("/image/view-refresh.png").toString()));
        brefresh.setTooltip(new Tooltip("Refresh Data"));
        bnext.setGraphic(new ImageView(getClass().getResource("/image/go-next-3.png").toString()));
        bnext.setTooltip(new Tooltip("Next Data"));
        bprev.setGraphic(new ImageView(getClass().getResource("/image/go-previous-3.png").toString()));
        bprev.setTooltip(new Tooltip("Previous Data"));
        bberanda.setGraphic(new ImageView(getClass().getResource("/image/go-home-4.png").toString()));
        bberanda.setTooltip(new Tooltip("Home"));
    }

    private void loadsatuan() {
        ObservableList ols = FXCollections.observableArrayList();
        String data = "";
        int i = 1;
        int j = 1;
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File("satuan")));
            while ((data = br.readLine()) != null) {
                if (i == j) {
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
            ResultSet res = h.read("SELECT id_pembelian,tanggal_pembelian,id_barang,nama_barang,satuan_barang,"
                    + "harga_beli,jumlah,(harga_beli*jumlah) AS total"
                    + " FROM pembelian LIMIT " + limit + " OFFSET " + offset + " ").executeQuery();
            while (res.next()) {
                ols.add(new PembelianEntity(
                        res.getString("id_pembelian"),
                        res.getString("tanggal_pembelian"),
                        res.getString("id_barang"),
                        res.getString("nama_barang"),
                        res.getString("satuan_barang"),
                        res.getString("jumlah"),
                        nf.format(res.getDouble("harga_beli")),
                        nf.format(res.getDouble("total"))
                ));
            }

            ResultSet resjumahdata = h.read("SELECT COUNT(id_barang) AS total FROM pembelian").executeQuery();
            resjumahdata.next();
            ldata.setText(ols.size() + "/" + resjumahdata.getString("total") + " Data");

            h.disconnect();
            id_pembelian.setCellValueFactory(new PropertyValueFactory<>("id_pembelian"));
            tanggal.setCellValueFactory(new PropertyValueFactory<>("tanggal_pembelian"));
            kode_barang.setCellValueFactory(new PropertyValueFactory<>("id_barang"));
            nama_barang.setCellValueFactory(new PropertyValueFactory<>("nama_barang"));
            satuan.setCellValueFactory(new PropertyValueFactory<>("satuan"));
            jumlah.setCellValueFactory(new PropertyValueFactory<>("jumlah"));
            harga_beli.setCellValueFactory(new PropertyValueFactory<>("harga_beli"));
            total.setCellValueFactory(new PropertyValueFactory<>("total"));
            tabel.setItems(ols);
        } catch (SQLException ex) {
            Logger.getLogger(PembelianController.class.getName()).log(Level.SEVERE, null, ex);
            Alert al = new Alert(Alert.AlertType.ERROR);
            al.setTitle("Error");
            al.setHeaderText("Application Error");
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
            v.getChildren().add(new Label("Error Detail has been read :"));
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
                    ResultSet res = h.read("SELECT id_pembelian,tanggal_pembelian,id_barang,nama_barang,satuan_barang,"
                            + "harga_beli,jumlah,(harga_beli*jumlah) AS total"
                            + " FROM pembelian LIMIT " + limit + " OFFSET " + offset + " ").executeQuery();
                    while (res.next()) {
                        ols.add(new PembelianEntity(
                                res.getString("id_pembelian"),
                                res.getString("tanggal_pembelian"),
                                res.getString("id_barang"),
                                res.getString("nama_barang"),
                                res.getString("satuan_barang"),
                                res.getString("jumlah"),
                                nf.format(res.getDouble("harga_beli")),
                                nf.format(res.getDouble("total"))
                        ));
                    }

                    ResultSet resjumahdata = h.read("SELECT COUNT(id_barang) AS total FROM pembelian").executeQuery();
                    resjumahdata.next();
                    ldata.setText(ols.size() + "/" + resjumahdata.getString("total") + " Data");

                    h.disconnect();
                    id_pembelian.setCellValueFactory(new PropertyValueFactory<>("id_pembelian"));
                    tanggal.setCellValueFactory(new PropertyValueFactory<>("tanggal_pembelian"));
                    kode_barang.setCellValueFactory(new PropertyValueFactory<>("id_barang"));
                    nama_barang.setCellValueFactory(new PropertyValueFactory<>("nama_barang"));
                    satuan.setCellValueFactory(new PropertyValueFactory<>("satuan"));
                    jumlah.setCellValueFactory(new PropertyValueFactory<>("jumlah"));
                    harga_beli.setCellValueFactory(new PropertyValueFactory<>("harga_beli"));
                    total.setCellValueFactory(new PropertyValueFactory<>("total"));
                    tabel.setItems(ols);
                } catch (SQLException ex) {
                    Logger.getLogger(PembelianController.class.getName()).log(Level.SEVERE, null, ex);
                    Alert al = new Alert(Alert.AlertType.ERROR);
                    al.setTitle("Error");
                    al.setHeaderText("Application Error");
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
                    v.getChildren().add(new Label("Error Detail has been read :"));
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
                    ResultSet res = h.read("SELECT id_pembelian,tanggal_pembelian,id_barang,nama_barang,satuan_barang,"
                            + "harga_beli,jumlah,(harga_beli*jumlah) AS total"
                            + " FROM pembelian LIMIT " + limit + " OFFSET " + offset + " ").executeQuery();
                    while (res.next()) {
                        ols.add(new PembelianEntity(
                                res.getString("id_pembelian"),
                                res.getString("tanggal_pembelian"),
                                res.getString("id_barang"),
                                res.getString("nama_barang"),
                                res.getString("satuan_barang"),
                                res.getString("jumlah"),
                                nf.format(res.getDouble("harga_beli")),
                                nf.format(res.getDouble("total"))
                        ));
                    }

                    ResultSet resjumahdata = h.read("SELECT COUNT(id_barang) AS total FROM pembelian").executeQuery();
                    resjumahdata.next();
                    ldata.setText(ols.size() + "/" + resjumahdata.getString("total") + " Data");

                    h.disconnect();
                    id_pembelian.setCellValueFactory(new PropertyValueFactory<>("id_pembelian"));
                    tanggal.setCellValueFactory(new PropertyValueFactory<>("tanggal_pembelian"));
                    kode_barang.setCellValueFactory(new PropertyValueFactory<>("id_barang"));
                    nama_barang.setCellValueFactory(new PropertyValueFactory<>("nama_barang"));
                    satuan.setCellValueFactory(new PropertyValueFactory<>("satuan"));
                    jumlah.setCellValueFactory(new PropertyValueFactory<>("jumlah"));
                    harga_beli.setCellValueFactory(new PropertyValueFactory<>("harga_beli"));
                    total.setCellValueFactory(new PropertyValueFactory<>("total"));
                    tabel.setItems(ols);
                } catch (SQLException ex) {
                    Logger.getLogger(PembelianController.class.getName()).log(Level.SEVERE, null, ex);
                    Alert al = new Alert(Alert.AlertType.ERROR);
                    al.setTitle("Error");
                    al.setHeaderText("Application Error");
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
                    v.getChildren().add(new Label("Error Detail has been read :"));
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
                    ResultSet res = h.readdetail("SELECT id_pembelian,tanggal_pembelian,id_barang,nama_barang,satuan_barang,"
                            + "harga_beli,jumlah,(harga_beli*jumlah) AS total"
                            + " FROM pembelian WHERE tanggal_pembelian::text ILIKE ? OR id_pembelian ILIKE ? "
                            + "OR id_barang ILIKE ? OR nama_barang ILIKE ? OR satuan_barang ILIKE ? "
                            + "OR harga_beli::text ILIKE ? OR jumlah::text ILIKE ? ", 7, o).executeQuery();
                    while (res.next()) {
                        ols.add(new PembelianEntity(
                                res.getString("id_pembelian"),
                                res.getString("tanggal_pembelian"),
                                res.getString("id_barang"),
                                res.getString("nama_barang"),
                                res.getString("satuan_barang"),
                                res.getString("jumlah"),
                                nf.format(res.getDouble("harga_beli")),
                                nf.format(res.getDouble("total"))
                        ));
                    }

                    ResultSet resjumahdata = h.read("SELECT COUNT(id_barang) AS total FROM pembelian").executeQuery();
                    resjumahdata.next();
                    ldata.setText(ols.size() + "/" + resjumahdata.getString("total") + " Data");

                    h.disconnect();
                    id_pembelian.setCellValueFactory(new PropertyValueFactory<>("id_pembelian"));
                    tanggal.setCellValueFactory(new PropertyValueFactory<>("tanggal_pembelian"));
                    kode_barang.setCellValueFactory(new PropertyValueFactory<>("id_barang"));
                    nama_barang.setCellValueFactory(new PropertyValueFactory<>("nama_barang"));
                    satuan.setCellValueFactory(new PropertyValueFactory<>("satuan"));
                    jumlah.setCellValueFactory(new PropertyValueFactory<>("jumlah"));
                    harga_beli.setCellValueFactory(new PropertyValueFactory<>("harga_beli"));
                    total.setCellValueFactory(new PropertyValueFactory<>("total"));
                    tabel.setItems(ols);
                } catch (SQLException ex) {
                    Logger.getLogger(PembelianController.class.getName()).log(Level.SEVERE, null, ex);
                    Alert al = new Alert(Alert.AlertType.ERROR);
                    al.setTitle("Error");
                    al.setHeaderText("Application Error");
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
                    v.getChildren().add(new Label("Error Detail has been read :"));
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
                kode = String.valueOf(id_pembelian.getCellData(i));
                dtanggal.setValue(LocalDate.parse(tanggal.getCellData(i)));
                tkodebarang.setText(String.valueOf(kode_barang.getCellData(i)));
                tnamabarang.setText(String.valueOf(nama_barang.getCellData(i)));
                csatuan.getEditor().setText(String.valueOf(satuan.getCellData(i)));
                thargabeli.setText(String.valueOf(harga_beli.getCellData(i)));
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

                Setnumber no = new Setnumber();
                Object[] o = new Object[7];
                o[0] = no.nourut("PB", "id_pembelian", "pembelian");
                o[1] = dtanggal.getValue().toString();
                o[2] = tkodebarang.getText();
                o[3] = tnamabarang.getText();
                o[4] = csatuan.getEditor().getText();
                o[5] = Double.parseDouble(thargabeli.getText().replaceAll("[.,]", ""));
                o[6] = Integer.parseInt(tjumlah.getText());
                h.insert("INSERT INTO pembelian(id_pembelian,tanggal_pembelian,id_barang,nama_barang,satuan_barang,"
                        + "harga_beli,jumlah) VALUES(?,?::date,?,?,?,?,?)", 7, o);

                Object[] oo = new Object[1];
                oo[0] = tkodebarang.getText();
                ResultSet res = h.readdetail("SELECT COUNT(id_barang)as jumlah FROM barang WHERE id_barang = ?", 1, oo).executeQuery();
                while (res.next()) {
                    if (res.getInt("jumlah") == 1) {
                        Object[] ooo = new Object[2];
                        ooo[0] = Integer.parseInt(tjumlah.getText());
                        ooo[1] = tkodebarang.getText();
                        h.update("UPDATE barang SET jumlah_barang=jumlah_barang+? WHERE id_barang=? ", 2, ooo);
                    } else {
                        /*TextInputDialog tid = new TextInputDialog();
                        tid.setTitle("Pemberitahuan");
                        tid.setHeaderText("Data tidak ditemukan");
                        tid.setContentText("Silahkan input harga jual : ");
                        Optional<String> resharga = tid.showAndWait();
                        if (resharga.isPresent()) {*/
                        Object[] oooo = new Object[7];
                        oooo[0] = tkodebarang.getText();
                        oooo[1] = tnamabarang.getText();
                        oooo[2] = csatuan.getEditor().getText();
                        oooo[3] = Double.parseDouble(thargabeli.getText().replaceAll("[.,]", ""));
                        oooo[4] = Double.parseDouble(thargajualecer.getText().replaceAll("[.,]", ""));
                        oooo[5] = Double.parseDouble(thargajualgrosir.getText().replaceAll("[.,]", ""));
                        oooo[6] = Integer.parseInt(tjumlah.getText());
                        h.insert("INSERT INTO barang(id_barang,nama_barang,satuan_barang,"
                                + "harga_beli_barang,harga_jual_ecer_barang,"
                                + "harga_jual_grosir_barang,jumlah_barang) VALUES(?,?,?,?,?,?,?)", 7, oooo);
                        //}
                    }
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Success");
                alert.setContentText("Refresh if data not appeared");
                alert.showAndWait();
                tkodebarang.requestFocus();
            } else {
                Object[] o = new Object[7];
                o[0] = dtanggal.getValue().toString();
                o[1] = tkodebarang.getText();
                o[2] = tnamabarang.getText();
                o[3] = csatuan.getEditor().getText();
                o[4] = Double.parseDouble(thargabeli.getText().replaceAll("[.,]", ""));
                o[5] = Integer.parseInt(tjumlah.getText());
                o[6] = kode;
                Alert alertcon = new Alert(Alert.AlertType.CONFIRMATION);
                alertcon.setHeaderText("Are you sure to update this data?");
                alertcon.setContentText("You can't undo this process");
                ButtonType ya = new ButtonType("Yes");
                ButtonType tidak = new ButtonType("No");
                alertcon.getButtonTypes().setAll(ya, tidak);
                Optional<ButtonType> opt = alertcon.showAndWait();
                if (opt.get() == ya) {
                    h.update("UPDATE pembelian SET tanggal_pembelian=?::date,id_barang=?,nama_barang=?,satuan_barang=?,"
                            + "harga_beli=?,jumlah=? WHERE id_pembelian=? ", 7, o);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Success");
                    alert.setContentText("Refresh if data not appeared");
                    alert.showAndWait();
                }

            }
            h.disconnect();
            loaddata();
        } catch (SQLException | NumberFormatException | NullPointerException ex) {
            Logger.getLogger(PembelianController.class.getName()).log(Level.SEVERE, null, ex);
            Alert al = new Alert(Alert.AlertType.ERROR);
            al.setTitle("Error");
            al.setHeaderText("Application Error");
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
            v.getChildren().add(new Label("Error Detail has been read :"));
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
        alertcon.setHeaderText("Are you sure to delete this data?");
        alertcon.setContentText("You can't undo this process");
        ButtonType ya = new ButtonType("Yes");
        ButtonType tidak = new ButtonType("No");
        alertcon.getButtonTypes().setAll(ya, tidak);
        Optional<ButtonType> opt = alertcon.showAndWait();
        if (opt.get() == ya) {
            try {
                ObservableList<PembelianEntity> ols = tabel.getSelectionModel().getSelectedItems();
                h.connect();
                for (int i = 0; i < ols.size(); i++) {
                    h.delete("DELETE FROM pembelian WHERE id_pembelian=?", id_pembelian.getCellData(ols.get(i)));
                }
                h.disconnect();
                loaddata();
            } catch (SQLException | NumberFormatException | NullPointerException ex) {
                Logger.getLogger(PembelianController.class.getName()).log(Level.SEVERE, null, ex);
                Alert al = new Alert(Alert.AlertType.ERROR);
                al.setTitle("Error");
                al.setHeaderText("Application Error");
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
                v.getChildren().add(new Label("Error Detail has been read :"));
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
        dtanggal.setValue(LocalDate.now());
        tkodebarang.clear();
        tnamabarang.clear();
        csatuan.getEditor().clear();
        thargabeli.clear();
        tjumlah.clear();
        thargajualecer.clear();
        thargajualgrosir.clear();
        bhapus.disableProperty().set(Boolean.TRUE);
        thargajualecer.setDisable(false);
        thargajualgrosir.setDisable(false);
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

    private void cekidbarang() {
        tkodebarang.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                try {
                    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    h.connect();
                    Object[] o = new Object[1];
                    o[0] = tkodebarang.getText();
                    ResultSet res = h.readdetail("SELECT id_barang,nama_barang,satuan_barang,jumlah_barang,"
                            + "harga_beli_barang,harga_jual_ecer_barang,harga_jual_grosir_barang FROM barang"
                            + " WHERE id_barang=? ", 1, o).executeQuery();
                    while (res.next()) {
                        if (tkodebarang.getText().equals(res.getString("id_barang"))) {
                            tnamabarang.setText(res.getString("nama_barang"));
                            csatuan.getEditor().setText(res.getString("satuan_barang"));
                            thargabeli.setText(res.getString("harga_beli_barang"));
                            thargajualecer.setText(res.getString("harga_jual_ecer_barang"));
                            thargajualgrosir.setText(res.getString("harga_jual_grosir_barang"));
                            tjumlah.setText(res.getString("jumlah_barang"));
                        }
                    }

                    h.disconnect();
                } catch (SQLException ex) {
                    Logger.getLogger(PembelianController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    private void bagiharga() {
        bcek.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                int n1 = Integer.parseInt(thargabeli.getText());
                int n2 = Integer.parseInt(tjumlah.getText());
                thargabeli.setText(String.valueOf(n1 / n2));
            }
        });
    }

}
