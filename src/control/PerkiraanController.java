/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import entity.PerkiraanEntity;
import helper.helper;
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
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Minami
 */
public class PerkiraanController implements Initializable {

    @FXML
    private TextField tkode;
    @FXML
    private ComboBox<String> ckategori;
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
    @FXML
    private TableView<PerkiraanEntity> tabel;
    @FXML
    private TableColumn<PerkiraanEntity, String> kode;
    @FXML
    private TableColumn<PerkiraanEntity, String> kategori;
    @FXML
    private TableColumn<PerkiraanEntity, String> nama;

    helper h = new helper();
    ObservableList ols = FXCollections.observableArrayList();
    int limit, offset;
    String kodes;
    NumberFormat nf = NumberFormat.getInstance();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        makeup();
        loadtipe();
        loaddata();
        pagging();
        cari();
        refresh();
        select();
        simpan();
        hapus();
        clearfield();
    }

    @FXML
    private void keypress(KeyEvent event) {
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

    private void loadtipe() {
        ObservableList ols = FXCollections.observableArrayList("Pendapatan", "Pengeluaran");
        ckategori.setItems(ols);
        //ctipe.getEditor().
    }

    private void loaddata() {
        try {
            tabel.getItems().clear();
            tabel.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            offset = 0;
            limit = Integer.parseInt(tlimit.getText());
            h.connect();
            ResultSet res = h.read("SELECT kode,nama,tipe"
                    + " FROM perkiraan LIMIT " + limit + " OFFSET " + offset + " ").executeQuery();
            while (res.next()) {
                ols.add(new PerkiraanEntity(res.getString("kode"),
                        res.getString("nama"),
                        res.getString("tipe")));
            }

            ResultSet resjumahdata = h.read("SELECT COUNT(kode) AS total FROM perkiraan").executeQuery();
            resjumahdata.next();
            ldata.setText(ols.size() + "/" + resjumahdata.getString("total") + " Data");

            h.disconnect();
            kode.setCellValueFactory(new PropertyValueFactory<>("kode"));
            nama.setCellValueFactory(new PropertyValueFactory<>("nama"));
            kategori.setCellValueFactory(new PropertyValueFactory<>("tipe"));
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
                    ResultSet res = h.read("SELECT kode,nama,tipe"
                            + " FROM perkiraan LIMIT " + limit + " OFFSET " + offset + " ").executeQuery();
                    while (res.next()) {
                        ols.add(new PerkiraanEntity(res.getString("kode"),
                                res.getString("nama"),
                                res.getString("tipe")));
                    }

                    ResultSet resjumahdata = h.read("SELECT COUNT(kode) AS total FROM perkiraan").executeQuery();
                    resjumahdata.next();
                    ldata.setText(ols.size() + "/" + resjumahdata.getString("total") + " Data");

                    h.disconnect();
                    kode.setCellValueFactory(new PropertyValueFactory<>("kode"));
                    nama.setCellValueFactory(new PropertyValueFactory<>("nama"));
                    kategori.setCellValueFactory(new PropertyValueFactory<>("tipe"));
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
                    ResultSet res = h.read("SELECT kode,nama,tipe"
                            + " FROM perkiraan LIMIT " + limit + " OFFSET " + offset + " ").executeQuery();
                    while (res.next()) {
                        ols.add(new PerkiraanEntity(res.getString("kode"),
                                res.getString("nama"),
                                res.getString("tipe")));
                    }

                    ResultSet resjumahdata = h.read("SELECT COUNT(kode) AS total FROM perkiraan").executeQuery();
                    resjumahdata.next();
                    ldata.setText(ols.size() + "/" + resjumahdata.getString("total") + " Data");

                    h.disconnect();
                    kode.setCellValueFactory(new PropertyValueFactory<>("kode"));
                    nama.setCellValueFactory(new PropertyValueFactory<>("nama"));
                    kategori.setCellValueFactory(new PropertyValueFactory<>("tipe"));
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
                    Object[] o = new Object[3];
                    o[0] = "%" + tcari.getText() + "%";
                    o[1] = "%" + tcari.getText() + "%";
                    o[2] = "%" + tcari.getText() + "%";
                    ResultSet res = h.readdetail("SELECT kode,nama,tipe"
                            + " FROM perkiraan WHERE kode ILIKE ? OR nama ILIKE ? OR tipe ILIKE ? ",3,o).executeQuery();
                    while (res.next()) {
                        ols.add(new PerkiraanEntity(res.getString("kode"),
                                res.getString("nama"),
                                res.getString("tipe")));
                    }

                    ResultSet resjumahdata = h.read("SELECT COUNT(kode) AS total FROM perkiraan").executeQuery();
                    resjumahdata.next();
                    ldata.setText(ols.size() + "/" + resjumahdata.getString("total") + " Data");

                    h.disconnect();
                    kode.setCellValueFactory(new PropertyValueFactory<>("kode"));
                    nama.setCellValueFactory(new PropertyValueFactory<>("nama"));
                    kategori.setCellValueFactory(new PropertyValueFactory<>("tipe"));
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
        tabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

            }
        });

        tabel.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                int i = newValue.intValue();
                kodes = String.valueOf(kode.getCellData(i));
                tnama.setText(String.valueOf(nama.getCellData(i)));
                tkode.setText(String.valueOf(kode.getCellData(i)));
                ckategori.getEditor().setText(String.valueOf(kategori.getCellData(i)));
                bhapus.disableProperty().set(Boolean.FALSE);
            }
        });
    }

    private void rawsimpan() {
        try {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            h.connect();
            if (kodes == null || kodes.equals("")) {
                Object[] o = new Object[3];
                o[0] = tkode.getText();
                o[1] = tnama.getText();
                o[2] = ckategori.getEditor().getText();
                h.insert("INSERT INTO perkiraan(kode,nama,tipe) VALUES(?,?,?)", 3, o);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Data has been added");
                alert.setContentText("Refresh if new data not appeared");
                alert.showAndWait();
            } else {
                Object[] o = new Object[4];
                o[0] = tkode.getText();
                o[1] = tnama.getText();
                o[2] = ckategori.getEditor().getText();
                o[3] = kodes;
                Alert alertcon = new Alert(Alert.AlertType.CONFIRMATION);
                alertcon.setHeaderText("Are you sure to update this data?");
                alertcon.setContentText("You can't undo this process");
                ButtonType ya = new ButtonType("Yes");
                ButtonType tidak = new ButtonType("NO");
                alertcon.getButtonTypes().setAll(ya, tidak);
                Optional<ButtonType> opt = alertcon.showAndWait();
                if (opt.get() == ya) {
                    h.update("UPDATE perkiraan SET kode=?,nama=?,tipe=? WHERE kode=? ", 4, o);
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
                ObservableList<PerkiraanEntity> ols = tabel.getSelectionModel().getSelectedItems();
                h.connect();
                for (int i = 0; i < ols.size(); i++) {
                    h.delete("DELETE FROM perkiraan WHERE kode=?", kode.getCellData(ols.get(i)));
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
        tkode.clear();
        ckategori.getEditor().clear();
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
