/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import entity.PenjualanEntity;
import entity.Sessiongs;
import helper.helper;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

/**
 *
 * @author Minami
 */
public class PenjualanController implements Initializable {

    @FXML
    Button bsimpan;

    @FXML
    Button bbayar, bhapus, bclear;
    @FXML
    TableView<PenjualanEntity> tabel;
    @FXML
    TableColumn<PenjualanEntity, String> id, tanggal, kode_barang, nama_barang, satuan, jenis_penjualan, harga_barang, jumlah, total;
    @FXML
    Label ltotal, lkembalian;
    @FXML
    TextField tkodebarang, tbayar;
    @FXML
    private FlowPane fpakunuang;
    @FXML
    private TableColumn<PenjualanEntity, String> diskon;
    Filecontrol fc = new Filecontrol();
    @FXML
    private ComboBox<?> calldiskon;

    @FXML
    private void keypress(KeyEvent e) {
        if (e.isControlDown() && e.getCode() == KeyCode.S) {
            rawsimpan();
            rawclear();
        } else if (e.isControlDown() && e.getCode() == KeyCode.R) {
            loaddata();
            rawclear();
        } else if (e.isControlDown() && e.getCode() == KeyCode.D) {
            rawhapus();
            rawclear();
        } else if (e.isControlDown() && e.getCode() == KeyCode.B) {
            tbayar.requestFocus();
        } else if (e.getCode() == KeyCode.DELETE) {
            rawhapus();
            rawclear();
        }
    }

    helper h = new helper();
    ObservableList ols = FXCollections.observableArrayList();
    String kode, kodebarang;
    double total_belanja, kembalian;
    NumberFormat nf = NumberFormat.getInstance();
    Sessiongs ses = new Sessiongs();
    Setnumber number = new Setnumber();
    ObservableList<Entitykeuangan> datakeuangan = FXCollections.observableArrayList();
    ObservableList<Button> btlist = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates
        loaddata();
        simpan();
        select();
        hapus();
        clear();
        bayar();
        autoinsert();
        makeup();
        updatejenispenjualan();
        loadakunkeuangan();
        loadalldiscount();

    }

    private void makeup() {
        bhapus.disableProperty().setValue(Boolean.TRUE);
        bsimpan.setGraphic(new ImageView(getClass().getResource("/image/document-save-5.png").toString()));
        bsimpan.setTooltip(new Tooltip("Save Data"));
        bhapus.setGraphic(new ImageView(getClass().getResource("/image/edit-delete-9.png").toString()));
        bhapus.setTooltip(new Tooltip("Delete Data"));
        bclear.setGraphic(new ImageView(getClass().getResource("/image/edit-clear-2.png").toString()));
        bclear.setTooltip(new Tooltip("Clear Field and Refresh"));
        bbayar.setGraphic(new ImageView(getClass().getResource("/image/money.png").toString()));
        bbayar.setTooltip(new Tooltip("Payment"));

    }

    private void loaddata() {
        try {
            tabel.getItems().clear();
            tabel.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            h.connect();
            ResultSet res = h.read("SELECT id_penjualan,tanggal_penjualan,penjualan.id_barang,nama_barang,satuan_barang,"
                    + "jenis_penjualan,diskon,harga_jual,penjualan.jumlah,"
                    + "(harga_jual*penjualan.jumlah)-(diskon / 100 *(harga_jual*penjualan.jumlah)) AS total"
                    + " FROM penjualan INNER JOIN barang ON penjualan.id_barang=barang.id_barang WHERE penjualan.status=0"
                    + " ORDER BY id_penjualan ASC").executeQuery();
            while (res.next()) {
                ols.add(new PenjualanEntity(res.getString("id_penjualan"),
                        res.getString("tanggal_penjualan"),
                        res.getString("id_barang"),
                        res.getString("nama_barang"),
                        res.getString("satuan_barang"),
                        res.getString("jenis_penjualan"),
                        nf.format(res.getDouble("harga_jual")),
                        res.getString("jumlah"),
                        nf.format(res.getDouble("total")),
                        res.getString("diskon")));
            }

            h.disconnect();
            id.setCellValueFactory(new PropertyValueFactory<>("id"));
            tanggal.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
            kode_barang.setCellValueFactory(new PropertyValueFactory<>("kode_barang"));
            nama_barang.setCellValueFactory(new PropertyValueFactory<>("nama_barang"));
            satuan.setCellValueFactory(new PropertyValueFactory<>("satuan"));
            jenis_penjualan.setCellValueFactory(new PropertyValueFactory<>("jenis_penjualan"));
            harga_barang.setCellValueFactory(new PropertyValueFactory<>("harga_barang"));
            jumlah.setCellValueFactory(new PropertyValueFactory<>("jumlah"));
            diskon.setCellValueFactory(new PropertyValueFactory<>("diskon"));
            total.setCellValueFactory(new PropertyValueFactory<>("total"));
            tabel.setItems(ols);

            total_belanja = 0;
            for (int i = 0; i < ols.size(); i++) {
                total_belanja = total_belanja + Double.parseDouble(String.valueOf(total.getCellData(i)).replaceAll("[,.]", ""));
            }
            Double disc = 0.0;
            if (calldiskon.getEditor().getText().isEmpty()) {
                disc = 0.0;
            } else {
                disc = Double.parseDouble(calldiskon.getEditor().getText());
            }
            total_belanja = total_belanja - (disc / 100 * (total_belanja));
            ltotal.setText("Rp. " + nf.format(total_belanja));

        } catch (SQLException ex) {
            Logger.getLogger(PenjualanController.class.getName()).log(Level.SEVERE, null, ex);
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

    public void loadalldiscount() {
        ObservableList ols = FXCollections.observableArrayList();
        String isicombo = fc.diskon();
        for (int i = 0; i < isicombo.split(";").length; i++) {
            ols.add(isicombo.split(";")[i]);
        }
        calldiskon.setItems(ols);

        calldiskon.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
                loaddata();
            }
        });
    }

    private void rawsimpan() {
        try {
            h.connect();
            Object[] o = new Object[7];
            o[0] = number.nourut("PJ", "id_penjualan", "penjualan");
            o[1] = tkodebarang.getText();
            o[2] = 1;
            o[3] = "Normal";
            o[4] = tkodebarang.getText();
            o[5] = tkodebarang.getText();
            o[6] = ses.getId();

            h.insert("INSERT INTO penjualan(id_penjualan,id_barang,jumlah,jenis_penjualan,diskon,harga_jual,harga_beli,id_kasir)"
                    + " VALUES(?,?,?,?,0,"
                    + "(SELECT harga_jual_ecer_barang FROM barang WHERE id_barang=?),"
                    + "(SELECT harga_beli_barang FROM barang WHERE id_barang=?),?)", 7, o);
            h.disconnect();
            loaddata();
        } catch (SQLException | NumberFormatException | NullPointerException ex) {
            Logger.getLogger(BarangController.class.getName()).log(Level.SEVERE, null, ex);
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

    public void rawhapus() {
        Alert alertcon = new Alert(Alert.AlertType.CONFIRMATION);
        alertcon.setHeaderText("Are you sure to delete this data?");
        alertcon.setContentText("You can't undo this process");
        ButtonType ya = new ButtonType("Yes");
        ButtonType tidak = new ButtonType("No");
        alertcon.getButtonTypes().setAll(ya, tidak);
        Optional<ButtonType> opt = alertcon.showAndWait();
        if (opt.get() == ya) {
            try {
                ObservableList<PenjualanEntity> ols = tabel.getSelectionModel().getSelectedItems();
                h.connect();
                for (int i = 0; i < ols.size(); i++) {
                    h.delete("DELETE FROM penjualan WHERE id_penjualan=?", id.getCellData(ols.get(i)));
                }
                h.disconnect();
                loaddata();
            } catch (SQLException | NumberFormatException | NullPointerException ex) {
                Logger.getLogger(BarangController.class.getName()).log(Level.SEVERE, null, ex);
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

    private void rawclear() {
        kode = null;
        tkodebarang.clear();
        tkodebarang.requestFocus();
        bhapus.disableProperty().setValue(Boolean.TRUE);
        calldiskon.getEditor().clear();
    }

    private void simpan() {
        bsimpan.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                rawsimpan();
                rawclear();
            }
        });
    }

    private void select() {

        tabel.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                int i = newValue.intValue();
                kode = String.valueOf(id.getCellData(i));
                kodebarang = String.valueOf(kode_barang.getCellData(i));
                tkodebarang.setText(String.valueOf(kode_barang.getCellData(i)));
                bhapus.disableProperty().setValue(Boolean.FALSE);
            }
        });
    }

    private void hapus() {
        bhapus.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                rawhapus();
                rawclear();
            }
        });
    }

    private void clear() {
        bclear.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                rawclear();
            }
        });
    }

    private void bayar() {

        tbayar.setOnKeyReleased(
                new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event
            ) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                try {
                    double jumlahuang = 0;
                    if (tbayar.getText().isEmpty()) {
                        jumlahuang = 0;
                    } else {
                        jumlahuang = Double.parseDouble(h.digitinputreplacer(tbayar.getText()));
                    }
                    Double disc = 0.0;
                    if (calldiskon.getEditor().getText().isEmpty()) {
                        disc = 0.0;
                    } else {
                        disc = Double.parseDouble(calldiskon.getEditor().getText());
                    }
                    kembalian = jumlahuang - total_belanja;
                    lkembalian.setText("Rp. " + nf.format(kembalian));
                } catch (Exception ex) {
                    Logger.getLogger(BarangController.class.getName()).log(Level.SEVERE, null, ex);
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
        );
    }

    public void autoinsert() {
        tkodebarang.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                //new interuptinsert().start();
                if (event.getCode() == KeyCode.ENTER) {
                    try {
                        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                        h.connect();
                        Object[] o = new Object[1];
                        o[0] = tkodebarang.getText();
                        ResultSet res = h.readdetail("SELECT id_barang,nama_barang FROM barang WHERE id_barang=?", 1, o).executeQuery();
                        Object[] oo = new Object[1];
                        oo[0] = tkodebarang.getText();
                        ResultSet res2 = h.readdetail("SELECT id_barang FROM penjualan WHERE id_barang=? AND status=0", 1, oo).executeQuery();
                        while (res.next()) {
                            String kode = null;
                            while (res2.next()) {
                                kode = res2.getString("id_barang");
                            }
                            if (tkodebarang.getText().equals(res.getString("id_barang"))) {

                                if (kode != null && tkodebarang.getText().equals(kode)) {
                                    Object[] ooo = new Object[2];
                                    ooo[0] = 1;
                                    ooo[1] = tkodebarang.getText();
                                    h.update("UPDATE penjualan SET jumlah=jumlah+? WHERE id_barang=? AND status=0", 2, ooo);
                                    loaddata();
                                    rawclear();
                                } else {

                                    rawsimpan();
                                    rawclear();
                                }

                            }

                        }

                        h.disconnect();
                        this.finalize();
                    } catch (SQLException ex) {
                        Logger.getLogger(PenjualanController.class.getName()).log(Level.SEVERE, null, ex);
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
                    } catch (Throwable ex) {
                        Logger.getLogger(PenjualanController.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            }
        });
    }

    public void updatejenispenjualan() {

        jumlah.setCellFactory(TextFieldTableCell.forTableColumn());
        jumlah.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<PenjualanEntity, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<PenjualanEntity, String> event) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                try {
                    Object[] o1 = new Object[2];
                    o1[0] = Integer.parseInt(event.getNewValue());
                    o1[1] = kode;
                    h.connect();
                    h.update("UPDATE penjualan SET jumlah=? WHERE id_penjualan=?", 2, o1);
                    h.disconnect();
                    loaddata();
                    rawclear();
                } catch (Exception ex) {
                    Logger.getLogger(PenjualanController.class.getName()).log(Level.SEVERE, null, ex);
                    Alert al = new Alert(Alert.AlertType.ERROR);
                    al.setTitle("Error");
                    al.setHeaderText("Application Error");
                    al.setContentText(ex.getMessage());
                    al.show();

                }
            }
        });

        ObservableList olsjp = FXCollections.observableArrayList("Normal", "Wholesaler");
        jenis_penjualan.setCellFactory(ComboBoxTableCell.forTableColumn(olsjp));
        jenis_penjualan.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<PenjualanEntity, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<PenjualanEntity, String> event) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                if (event.getNewValue().equals("Normal")) {
                    try {
                        Object[] o2 = new Object[3];
                        o2[0] = "Normal";
                        o2[1] = kodebarang;
                        o2[2] = kode;
                        h.connect();
                        h.update("UPDATE penjualan SET jenis_penjualan=?,"
                                + "harga_jual=(SELECT harga_jual_ecer_barang FROM barang WHERE id_barang=?) WHERE id_penjualan=?", 3, o2);
                        h.disconnect();
                        loaddata();
                        rawclear();
                    } catch (SQLException ex) {
                        Logger.getLogger(PenjualanController.class.getName()).log(Level.SEVERE, null, ex);

                    }
                } else {
                    try {
                        Object[] o2 = new Object[3];
                        o2[0] = "Wholesaler";
                        o2[1] = kodebarang;
                        o2[2] = kode;
                        h.connect();
                        h.update("UPDATE penjualan SET jenis_penjualan=?,"
                                + "harga_jual=(SELECT harga_jual_grosir_barang FROM barang WHERE id_barang=?) WHERE id_penjualan=?", 3, o2);
                        h.disconnect();
                        loaddata();
                        rawclear();
                    } catch (SQLException ex) {
                        Logger.getLogger(PenjualanController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });

        diskon.setCellFactory(TextFieldTableCell.forTableColumn());
        diskon.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<PenjualanEntity, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<PenjualanEntity, String> event) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                try {
                    Object[] o1 = new Object[2];
                    o1[0] = Integer.parseInt(event.getNewValue());
                    o1[1] = kode;
                    h.connect();
                    h.update("UPDATE penjualan SET diskon=? WHERE id_penjualan=?", 2, o1);
                    h.disconnect();
                    loaddata();
                    rawclear();
                } catch (Exception ex) {
                    Logger.getLogger(PenjualanController.class.getName()).log(Level.SEVERE, null, ex);
                    Alert al = new Alert(Alert.AlertType.ERROR);
                    al.setTitle("Error");
                    al.setHeaderText("Application Error");
                    al.setContentText(ex.getMessage());
                    al.show();

                }
            }
        });
    }

    public String setterkodetransaksi() {
        String kode_transaksi = "";
        try {
            h.connect();
            String sqlgetno = "SELECT kode_transaksi FROM penjualan WHERE status=1 ORDER BY kode_transaksi DESC LIMIT 1";
            ResultSet resno = h.read(sqlgetno).executeQuery();
            String tglhariini = new SimpleDateFormat("yyMMdd").format(new Date());
            while (resno.next()) {
                kode_transaksi = resno.getString("kode_transaksi");
            }
            if (kode_transaksi == null || kode_transaksi.equals("")) {
                kode_transaksi = tglhariini + "0001";
            } else {
                if (kode_transaksi.substring(0, 6).equals(tglhariini)) {
                    kode_transaksi = String.valueOf(Integer.parseInt(kode_transaksi) + 1);
                } else {
                    kode_transaksi = tglhariini + "0001";
                }
            }
            resno.close();
        } catch (SQLException ex) {
            Logger.getLogger(PenjualanController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return kode_transaksi;
    }

    private EventHandler actionbayar(int y) {
        EventHandler evt = new EventHandler() {
            @Override
            public void handle(Event event) {
                try {
                    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    double total_uang = 0;
                    double total_diskon = 0;
                    if (calldiskon.getEditor().getText().equals("")) {
                        total_diskon = 0;
                    } else {
                        total_diskon = Double.parseDouble(calldiskon.getEditor().getText().replaceAll("[.]", ""));
                    }

                    if (tbayar.getText().isEmpty()) {
                        total_uang = 0;
                    } else {
                        total_uang = Double.parseDouble(h.digitinputreplacer(tbayar.getText()));
                    }

                    if (total_belanja > total_uang) {
                        Alert al = new Alert(Alert.AlertType.INFORMATION);
                        al.setTitle("Information");
                        al.setHeaderText("Missed Calculation");
                        al.setContentText("Cash not enough for payment");
                        al.show();
                    } else {

                        String kode_transaksi = setterkodetransaksi();
                        Sessiongs ses = new Sessiongs();
                        Filecontrol fc = new Filecontrol();
                        HashMap has = new HashMap(8);
                        has.put("nama", fc.namaperusahaan());
                        has.put("alamat", fc.alamat());
                        has.put("nohp", fc.nohp());
                        has.put("kasir", ses.getNama());
                        has.put("jumlahuang", total_uang);
                        has.put("kembalian", kembalian);
                        has.put("kode_transaksi", kode_transaksi);
                        has.put("tdiskon", total_diskon);
                        JasperReport jr = (JasperReport) JRLoader.loadObject(new File("Laporan/Struk2inc.jasper"));
                        JasperPrint jp = JasperFillManager.fillReport(jr, has, h.conn);
                        JasperPrintManager.printReport(jp, false);
                        ResultSet res = h.read("SELECT jumlah,id_barang FROM penjualan WHERE status=0").executeQuery();
                        while (res.next()) {
                            Object[] ooo = new Object[2];
                            ooo[0] = res.getInt("jumlah");
                            ooo[1] = res.getString("id_barang");
                            h.update("UPDATE barang SET jumlah_barang=jumlah_barang-? WHERE id_barang=? ", 2, ooo);
                        }
                        h.exec("UPDATE penjualan SET kode_transaksi='" + kode_transaksi + "',"
                                + "kode_akun_keuangan='" + datakeuangan.get(y).kode_akun_keuangan + "',"
                                + "diskon_transaksi=" + total_diskon + " "
                                + "WHERE status=0; UPDATE penjualan SET status=1 WHERE status=0");
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setHeaderText("Transaction Success");
                        alert.setContentText("Transaction Success");
                        alert.showAndWait();
                        rawclear();
                        tbayar.clear();
                        lkembalian.setText("Rp. 0");
                        loaddata();
                    }

                } catch (SQLException ex) {
                    Logger.getLogger(PenjualanController.class.getName()).log(Level.SEVERE, null, ex);

                } catch (JRException ex) {
                    Logger.getLogger(PenjualanController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        };

        return evt;

    }

    private void loadakunkeuangan() {
        try {
            datakeuangan.clear();
            btlist.clear();
            fpakunuang.getChildren().clear();
            h.connect();
            ResultSet res = h.read("SELECT kode_akun_keuangan,nama_akun_keuangan FROM akun_keuangan").executeQuery();
            while (res.next()) {
                String kode = res.getString("kode_akun_keuangan");
                String nama = res.getString("nama_akun_keuangan");
                datakeuangan.add(new Entitykeuangan(kode, nama));
            }
            for (int i = 0; i < datakeuangan.size(); i++) {
                final int y = i;

                Button bt = new Button(datakeuangan.get(i).nama_akun_keuangan);
                bt.setPrefWidth(80);
                bt.setPrefHeight(50);
                bt.setWrapText(true);
                bt.setAlignment(Pos.CENTER);
                bt.setContentDisplay(ContentDisplay.TOP);
                btlist.add(bt);
                btlist.get(i).setPadding(new Insets(3));
                btlist.get(i).setOnAction(actionbayar(y));
            }
            fpakunuang.getChildren().addAll(btlist);
            h.disconnect();
        } catch (Exception ex) {
            Logger.getLogger(PenjualanController.class.getName()).log(Level.SEVERE, null, ex);
            Logger.getLogger(PenjualanController.class.getName()).log(Level.SEVERE, null, ex);
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

    public class Entitykeuangan {

        String kode_akun_keuangan, nama_akun_keuangan;

        public Entitykeuangan(String kode_akun_keuangan, String nama_akun_keuangan) {
            this.kode_akun_keuangan = kode_akun_keuangan;
            this.nama_akun_keuangan = nama_akun_keuangan;
        }

        public String getKode_akun_keuangan() {
            return kode_akun_keuangan;
        }

        public void setKode_akun_keuangan(String kode_akun_keuangan) {
            this.kode_akun_keuangan = kode_akun_keuangan;
        }

        public String getNama_akun_keuangan() {
            return nama_akun_keuangan;
        }

        public void setNama_akun_keuangan(String nama_akun_keuangan) {
            this.nama_akun_keuangan = nama_akun_keuangan;
        }

    }
}
