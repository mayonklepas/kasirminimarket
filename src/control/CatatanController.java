/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import entity.CatatanEntity;
import helper.helper;
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
import javafx.scene.control.DatePicker;
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
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Minami
 */
public class CatatanController implements Initializable {

    @FXML
    private TextField tnama;
    @FXML
    private Button bsimpan;
    @FXML
    private Button bclear;
    @FXML
    private Button bhapus;
    @FXML
    private Button brefresh;
    @FXML
    private TextField tcari;
    @FXML
    private TextField tlimit;
    @FXML
    private Button bprev;
    @FXML
    private Button bberanda;
    @FXML
    private Button bnext;
    @FXML
    private Label ldata;
    helper h = new helper();
    ObservableList<CatatanEntity> ols = FXCollections.observableArrayList();
    int limit, offset;
    String kodes;
    NumberFormat nf = NumberFormat.getInstance();
    @FXML
    private DatePicker dtanggal;
    @FXML
    private ComboBox<String> ckodecatatan;
    @FXML
    private ComboBox<String> ckodekeuangan;
    @FXML
    private Label tnamacatatan;
    @FXML
    private TextField tjumlah;
    @FXML
    private TextArea tketerangan;
    @FXML
    private TableColumn<CatatanEntity, String> id;
    @FXML
    private TableColumn<CatatanEntity, String> tanggal;
    @FXML
    private TableColumn<CatatanEntity, String> akun_catatan;
    @FXML
    private TableColumn<CatatanEntity, String> akun_keuangan;
    @FXML
    private TableColumn<CatatanEntity, String> nama_catatan;
    @FXML
    private TableColumn<CatatanEntity, String> jumlah;
    @FXML
    private TableColumn<CatatanEntity, String> keterangan;
    @FXML
    private TableView<CatatanEntity> tabel;

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

    ObservableList olsperkiraan = FXCollections.observableArrayList();
    ObservableList olskeuangan = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        makeup();
        loadcombo();
        loaddata();
        pagging();
        cari();
        refresh();
        select();
        simpan();
        hapus();
        clearfield();
        dtanggal.setValue(LocalDate.now());
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

    private void loadcombo() {
        try {
            h.connect();
            ResultSet res = h.read("SELECT kode,nama FROM perkiraan").executeQuery();
            while (res.next()) {
                olsperkiraan.add(res.getString("kode") + ":" + res.getString("nama"));
            }

            ckodecatatan.setItems(olsperkiraan);
        } catch (SQLException ex) {
            Logger.getLogger(CatatanController.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            h.connect();
            ResultSet res = h.read("SELECT kode_akun_keuangan,nama_akun_keuangan FROM akun_keuangan ").executeQuery();
            while (res.next()) {
                olskeuangan.add(res.getString("kode_akun_keuangan") + ":" + res.getString("nama_akun_keuangan"));
            }

            ckodekeuangan.setItems(olskeuangan);
        } catch (SQLException ex) {
            Logger.getLogger(CatatanController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loaddata() {
        try {
            tabel.getItems().clear();
            tabel.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            offset = 0;
            limit = Integer.parseInt(tlimit.getText());
            h.connect();
            ResultSet res = h.read("SELECT c.id,c.tanggal,c.kode_perkiraan,p.nama AS nama_perkiraan,c.kode_keuangan,"
                    + "k.nama_akun_keuangan,"
                    + "c.nama,c.jumlah,c.keterangan FROM catatan c LEFT JOIN "
                    + "perkiraan p ON c.kode_perkiraan=p.kode LEFT JOIN akun_keuangan k "
                    + "ON c.kode_keuangan = k.kode_akun_keuangan  LIMIT " + limit + " OFFSET " + offset + " ").executeQuery();
            while (res.next()) {
                String id = res.getString("id");
                String tanggal = res.getString("tanggal");
                String kode_perkiraan = res.getString("kode_perkiraan");
                String nama_perkiraan = res.getString("nama_perkiraan");
                String kode_keuangan = res.getString("kode_keuangan");
                String nama_keuangan = res.getString("nama_akun_keuangan");
                String nama = res.getString("nama");
                String jumlah = res.getString("jumlah");
                String keterangan = res.getString("keterangan");
                ols.add(new CatatanEntity(id, tanggal, kode_perkiraan, nama_perkiraan,
                        kode_keuangan, nama_keuangan, nama, jumlah, keterangan));
            }

            ResultSet resjumahdata = h.read("SELECT COUNT(id) AS total FROM catatan").executeQuery();
            resjumahdata.next();
            ldata.setText(ols.size() + "/" + resjumahdata.getString("total") + " Data");

            h.disconnect();
            id.setCellValueFactory(new PropertyValueFactory<>("id"));
            tanggal.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
            akun_catatan.setCellValueFactory(new PropertyValueFactory<>("nama_perkiraan"));
            akun_keuangan.setCellValueFactory(new PropertyValueFactory<>("nama_keuangan"));
            nama_catatan.setCellValueFactory(new PropertyValueFactory<>("nama"));
            jumlah.setCellValueFactory(new PropertyValueFactory<>("jumlah"));
            keterangan.setCellValueFactory(new PropertyValueFactory<>("keterangan"));
            tabel.setItems(ols);
        } catch (Exception ex) {
            Logger.getLogger(AkunController.class.getName()).log(Level.SEVERE, null, ex);
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
                    ResultSet res = h.read("SELECT c.id,c.tanggal,c.kode_perkiraan,p.nama AS nama_perkiraan,c.kode_keuangan,"
                            + "k.nama_akun_keuangan,"
                            + "c.nama,c.jumlah,c.keterangan FROM catatan c LEFT JOIN "
                            + "perkiraan p ON c.kode_perkiraan=p.kode LEFT JOIN akun_keuangan k "
                            + "ON c.kode_keuangan = k.kode_akun_keuangan  LIMIT " + limit + " OFFSET " + offset + " ").executeQuery();
                    while (res.next()) {
                        String id = res.getString("id");
                        String tanggal = res.getString("tanggal");
                        String kode_perkiraan = res.getString("kode_perkiraan");
                        String nama_perkiraan = res.getString("nama_perkiraan");
                        String kode_keuangan = res.getString("kode_keuangan");
                        String nama_keuangan = res.getString("nama_akun_keuangan");
                        String nama = res.getString("nama");
                        String jumlah = res.getString("jumlah");
                        String keterangan = res.getString("keterangan");
                        ols.add(new CatatanEntity(id, tanggal, kode_perkiraan, nama_perkiraan,
                                kode_keuangan, nama_keuangan, nama, jumlah, keterangan));
                    }

                    ResultSet resjumahdata = h.read("SELECT COUNT(kode) AS total FROM perkiraan").executeQuery();
                    resjumahdata.next();
                    ldata.setText(ols.size() + "/" + resjumahdata.getString("total") + " Data");

                    h.disconnect();
                    id.setCellValueFactory(new PropertyValueFactory<>("id"));
                    tanggal.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
                    akun_catatan.setCellValueFactory(new PropertyValueFactory<>("nama_perkiraan"));
                    akun_keuangan.setCellValueFactory(new PropertyValueFactory<>("nama_keuangan"));
                    nama_catatan.setCellValueFactory(new PropertyValueFactory<>("nama"));
                    jumlah.setCellValueFactory(new PropertyValueFactory<>("jumlah"));
                    keterangan.setCellValueFactory(new PropertyValueFactory<>("keterangan"));
                    tabel.setItems(ols);
                } catch (SQLException ex) {
                    Logger.getLogger(AkunController.class.getName()).log(Level.SEVERE, null, ex);
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
                    ResultSet res = h.read("SELECT c.id,c.tanggal,c.kode_perkiraan,p.nama AS nama_perkiraan,c.kode_keuangan,"
                            + "k.nama_akun_keuangan,"
                            + "c.nama,c.jumlah,c.keterangan FROM catatan c LEFT JOIN "
                            + "perkiraan p ON c.kode_perkiraan=p.kode LEFT JOIN akun_keuangan k "
                            + "ON c.kode_keuangan = k.kode_akun_keuangan  LIMIT " + limit + " OFFSET " + offset + " ").executeQuery();
                    while (res.next()) {
                        String id = res.getString("id");
                        String tanggal = res.getString("tanggal");
                        String kode_perkiraan = res.getString("kode_perkiraan");
                        String nama_perkiraan = res.getString("nama_perkiraan");
                        String kode_keuangan = res.getString("kode_keuangan");
                        String nama_keuangan = res.getString("nama_akun_keuangan");
                        String nama = res.getString("nama");
                        String jumlah = res.getString("jumlah");
                        String keterangan = res.getString("keterangan");
                        ols.add(new CatatanEntity(id, tanggal, kode_perkiraan, nama_perkiraan,
                                kode_keuangan, nama_keuangan, nama, jumlah, keterangan));
                    }

                    ResultSet resjumahdata = h.read("SELECT COUNT(kode) AS total FROM perkiraan").executeQuery();
                    resjumahdata.next();
                    ldata.setText(ols.size() + "/" + resjumahdata.getString("total") + " Data");

                    h.disconnect();
                    id.setCellValueFactory(new PropertyValueFactory<>("id"));
                    tanggal.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
                    akun_catatan.setCellValueFactory(new PropertyValueFactory<>("nama_perkiraan"));
                    akun_keuangan.setCellValueFactory(new PropertyValueFactory<>("nama_keuangan"));
                    nama_catatan.setCellValueFactory(new PropertyValueFactory<>("nama"));
                    jumlah.setCellValueFactory(new PropertyValueFactory<>("jumlah"));
                    keterangan.setCellValueFactory(new PropertyValueFactory<>("keterangan"));
                    tabel.setItems(ols);
                } catch (SQLException ex) {
                    Logger.getLogger(AkunController.class.getName()).log(Level.SEVERE, null, ex);
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
                    Object[] o = new Object[6];
                    o[0] = "%" + tcari.getText() + "%";
                    o[1] = "%" + tcari.getText() + "%";
                    o[2] = "%" + tcari.getText() + "%";
                    o[3] = "%" + tcari.getText() + "%";
                    o[4] = "%" + tcari.getText() + "%";
                    o[5] = "%" + tcari.getText() + "%";

                    ResultSet res = h.readdetail("SELECT c.id,c.tanggal,c.kode_perkiraan,p.nama AS nama_perkiraan,"
                            + "c.kode_keuangan,"
                            + "k.nama_akun_keuangan,"
                            + "c.nama,c.jumlah,c.keterangan FROM catatan c LEFT JOIN "
                            + "perkiraan p ON c.kode_perkiraan=p.kode LEFT JOIN akun_keuangan k "
                            + "ON c.kode_keuangan = k.kode_akun_keuangan WHERE "
                            + "c.tanggal::character varying ILIKE ? OR "
                            + "p.nama ILIKE ? OR "
                            + "k.nama_akun_keuangan ILIKE ? OR "
                            + "c.nama ILIKE ? OR "
                            + "c.jumlah::character varying ILIKE ? OR "
                            + "c.keterangan ILIKE ?", 6, o).executeQuery();
                    while (res.next()) {
                        String id = res.getString("id");
                        String tanggal = res.getString("tanggal");
                        String kode_perkiraan = res.getString("kode_perkiraan");
                        String nama_perkiraan = res.getString("nama_perkiraan");
                        String kode_keuangan = res.getString("kode_keuangan");
                        String nama_keuangan = res.getString("nama_akun_keuangan");
                        String nama = res.getString("nama");
                        String jumlah = res.getString("jumlah");
                        String keterangan = res.getString("keterangan");
                        ols.add(new CatatanEntity(id, tanggal, kode_perkiraan, nama_perkiraan,
                                kode_keuangan, nama_keuangan, nama, jumlah, keterangan));
                    }

                    ResultSet resjumahdata = h.read("SELECT COUNT(kode) AS total FROM perkiraan").executeQuery();
                    resjumahdata.next();
                    ldata.setText(ols.size() + "/" + resjumahdata.getString("total") + " Data");

                    h.disconnect();
                    id.setCellValueFactory(new PropertyValueFactory<>("id"));
                    tanggal.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
                    akun_catatan.setCellValueFactory(new PropertyValueFactory<>("nama_perkiraan"));
                    akun_keuangan.setCellValueFactory(new PropertyValueFactory<>("nama_keuangan"));
                    nama_catatan.setCellValueFactory(new PropertyValueFactory<>("nama"));
                    jumlah.setCellValueFactory(new PropertyValueFactory<>("jumlah"));
                    keterangan.setCellValueFactory(new PropertyValueFactory<>("keterangan"));
                    tabel.setItems(ols);
                } catch (SQLException ex) {
                    Logger.getLogger(AkunController.class.getName()).log(Level.SEVERE, null, ex);
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
                rawclear();
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
                rawclear();
                bprev.setDisable(false);
                bnext.setDisable(false);
                tcari.clear();
                bhapus.disableProperty().set(Boolean.TRUE);
            }
        });
    }

    private void select() {
        tabel.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                int i = newValue.intValue();
                if (i >= 0) {
                    kodes = String.valueOf(id.getCellData(i));
                    dtanggal.setValue(LocalDate.parse(tanggal.getCellData(i)));
                    ckodecatatan.getEditor().setText(ols.get(i).getKode_perkiraan() + ":" + ols.get(i).getNama_perkiraan());
                    ckodekeuangan.getEditor().setText(ols.get(i).getKode_keuangan() + ":" + ols.get(i).getNama_keuangan());
                    tnama.setText(String.valueOf(nama_catatan.getCellData(i)));
                    tjumlah.setText(String.valueOf(jumlah.getCellData(i)));
                    tketerangan.setText(keterangan.getCellData(i));
                    bhapus.disableProperty().set(Boolean.FALSE);
                } else {

                }
            }
        });
    }

    private void rawsimpan() {
        try {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            h.connect();
            if (kodes == null || kodes.equals("")) {
                Object[] o = new Object[6];
                o[0] = dtanggal.getValue().toString();
                o[1] = ckodecatatan.getEditor().getText().split(":")[0];
                o[2] = ckodekeuangan.getEditor().getText().split(":")[0];
                o[3] = tnama.getText();
                o[4] = Double.parseDouble(h.digitinputreplacer(tjumlah.getText()));
                o[5] = tketerangan.getText();
                h.insert("INSERT INTO catatan(tanggal,kode_perkiraan,kode_keuangan,nama,jumlah,keterangan) "
                        + "VALUES(?::date,?,?,?,?,?)", 6, o);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Data has been added");
                alert.setContentText("Refresh if new data not appeared");
                alert.showAndWait();
            } else {
                Object[] o = new Object[7];
                o[0] = dtanggal.getValue().toString();
                o[1] = ckodecatatan.getEditor().getText().split(":")[0];
                o[2] = ckodekeuangan.getEditor().getText().split(":")[0];
                o[3] = tnama.getText();
                o[4] = Double.parseDouble(h.digitinputreplacer(tjumlah.getText()));
                o[5] = tketerangan.getText();
                o[6] = kodes;
                Alert alertcon = new Alert(Alert.AlertType.CONFIRMATION);
                alertcon.setHeaderText("Are you sure to update this data?");
                alertcon.setContentText("You can't undo this process");
                ButtonType ya = new ButtonType("Yes");
                ButtonType tidak = new ButtonType("No");
                alertcon.getButtonTypes().setAll(ya, tidak);
                Optional<ButtonType> opt = alertcon.showAndWait();
                if (opt.get() == ya) {
                    h.update("UPDATE catatan SET tanggal=?::date,kode_perkiraan=?,"
                            + "kode_keuangan=?,nama=?,jumlah=?,keterangan=? WHERE id=?::integer ", 7, o);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Data has been update");
                    alert.setContentText("Refresh if data not changed");
                    alert.showAndWait();
                }

            }
            h.disconnect();
            loaddata();
        } catch (SQLException ex) {
            Logger.getLogger(AkunController.class.getName()).log(Level.SEVERE, null, ex);
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
                ObservableList<CatatanEntity> ols = tabel.getSelectionModel().getSelectedItems();
                h.connect();
                for (int i = 0; i < ols.size(); i++) {
                    h.delete("DELETE FROM catatan WHERE id=?::integer", id.getCellData(ols.get(i)));
                }
                h.disconnect();
                loaddata();
            } catch (SQLException ex) {
                Logger.getLogger(AkunController.class.getName()).log(Level.SEVERE, null, ex);
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
        loaddata();
        kodes = null;
        tnama.clear();
        tjumlah.clear();
        tketerangan.clear();
        ckodecatatan.getEditor().clear();
        ckodekeuangan.getEditor().clear();
        dtanggal.setValue(LocalDate.now());
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

}
