/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import entity.AkunEntity;
import entity.BarangEntity;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Minami
 */
public class AkunController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    TableView<AkunEntity> tabel;
    @FXML
    TableColumn<AkunEntity, String> id, nama, username, password, tipe;
    @FXML
    Button bsimpan, bclear, brefresh, bnext, bprev, bberanda, bhapus;
    @FXML
    TextField tnama, tusername, tpassword, tlimit, tcari;
    @FXML
    ComboBox ctipe;
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
        loadtipe();
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
        ObservableList ols = FXCollections.observableArrayList("User", "Admin");
        ctipe.setItems(ols);
        //ctipe.getEditor().
    }

    private void loaddata() {
        try {
            tabel.getItems().clear();
            tabel.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            offset = 0;
            limit = Integer.parseInt(tlimit.getText());
            h.connect();
            ResultSet res = h.read("SELECT id_akun,nama_akun,username,password,tipe"
                    + " FROM akun LIMIT " + limit + " OFFSET " + offset + " ").executeQuery();
            while (res.next()) {
                ols.add(new AkunEntity(res.getString("id_akun"),
                        res.getString("nama_akun"),
                        res.getString("username"),
                        res.getString("password"),
                        res.getString("tipe")));
            }

            ResultSet resjumahdata = h.read("SELECT COUNT(id_akun) AS total FROM akun").executeQuery();
            resjumahdata.next();
            ldata.setText(ols.size() + "/" + resjumahdata.getString("total") + " Data");

            h.disconnect();
            id.setCellValueFactory(new PropertyValueFactory<>("id"));
            nama.setCellValueFactory(new PropertyValueFactory<>("nama"));
            username.setCellValueFactory(new PropertyValueFactory<>("username"));
            password.setCellValueFactory(new PropertyValueFactory<>("password"));
            tipe.setCellValueFactory(new PropertyValueFactory<>("tipe"));
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
                    ResultSet res = h.read("SELECT id_akun,nama_akun,username,password,tipe"
                            + " FROM akun LIMIT " + limit + " OFFSET " + offset + " ").executeQuery();
                    while (res.next()) {
                        ols.add(new AkunEntity(res.getString("id_akun"),
                                res.getString("nama_akun"),
                                res.getString("username"),
                                res.getString("password"),
                                res.getString("tipe")));
                    }

                    ResultSet resjumahdata = h.read("SELECT COUNT(id_akun) AS total FROM akun").executeQuery();
                    resjumahdata.next();
                    ldata.setText(ols.size() + "/" + resjumahdata.getString("total") + " Data");

                    h.disconnect();
                    id.setCellValueFactory(new PropertyValueFactory<>("id"));
                    nama.setCellValueFactory(new PropertyValueFactory<>("nama"));
                    username.setCellValueFactory(new PropertyValueFactory<>("username"));
                    password.setCellValueFactory(new PropertyValueFactory<>("password"));
                    tipe.setCellValueFactory(new PropertyValueFactory<>("tipe"));
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
                    ResultSet res = h.read("SELECT id_akun,nama_akun,username,password,tipe"
                            + " FROM akun LIMIT " + limit + " OFFSET " + offset + " ").executeQuery();
                    while (res.next()) {
                        ols.add(new AkunEntity(res.getString("id_akun"),
                                res.getString("nama_akun"),
                                res.getString("username"),
                                res.getString("password"),
                                res.getString("tipe")));
                    }

                    ResultSet resjumahdata = h.read("SELECT COUNT(id_akun) AS total FROM akun").executeQuery();
                    resjumahdata.next();
                    ldata.setText(ols.size() + "/" + resjumahdata.getString("total") + " Data");

                    h.disconnect();
                    id.setCellValueFactory(new PropertyValueFactory<>("id"));
                    nama.setCellValueFactory(new PropertyValueFactory<>("nama"));
                    username.setCellValueFactory(new PropertyValueFactory<>("username"));
                    password.setCellValueFactory(new PropertyValueFactory<>("password"));
                    tipe.setCellValueFactory(new PropertyValueFactory<>("tipe"));
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
                    ResultSet res = h.read("SELECT id_akun,nama_akun,username,password,tipe"
                            + " FROM akun WHERE id_akun ILIKE ? OR nama_akun ILIKE ? OR "
                            + "username ILIKE ? OR password ILIKE ? OR tipe ILIKE ? ").executeQuery();
                    while (res.next()) {
                        ols.add(new AkunEntity(res.getString("id_akun"),
                                res.getString("nama_akun"),
                                res.getString("username"),
                                res.getString("password"),
                                res.getString("tipe")));
                    }

                    ResultSet resjumahdata = h.read("SELECT COUNT(id_akun) AS total FROM akun").executeQuery();
                    resjumahdata.next();
                    ldata.setText(ols.size() + "/" + resjumahdata.getString("total") + " Data");

                    h.disconnect();
                    id.setCellValueFactory(new PropertyValueFactory<>("id"));
                    nama.setCellValueFactory(new PropertyValueFactory<>("nama"));
                    username.setCellValueFactory(new PropertyValueFactory<>("username"));
                    password.setCellValueFactory(new PropertyValueFactory<>("password"));
                    tipe.setCellValueFactory(new PropertyValueFactory<>("tipe"));
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
                kode = String.valueOf(id.getCellData(i));
                tnama.setText(String.valueOf(nama.getCellData(i)));
                tusername.setText(String.valueOf(username.getCellData(i)));
                tpassword.setText(String.valueOf(password.getCellData(i)));
                ctipe.getEditor().setText(String.valueOf(tipe.getCellData(i)));
                bhapus.disableProperty().set(Boolean.FALSE);
            }
        });
    }

    private void rawsimpan() {
        try {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            h.connect();
            Setnumber no = new Setnumber();
            if (kode == null || kode.equals("")) {
                Object[] o = new Object[5];
                o[0] = no.nourut("AK", "id_akun", "akun");
                o[1] = tnama.getText();
                o[2] = tusername.getText();
                o[3] = tpassword.getText();
                o[4] = ctipe.getEditor().getText();
                h.insert("INSERT INTO akun(id_akun,nama_akun,username,password,tipe) VALUES(?,?,?,?,?)", 5, o);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Data berhasil ditambahkan");
                alert.setContentText("Lakukan Refresh jika data belum terlihat");
                alert.showAndWait();
            } else {
                Object[] o = new Object[5];
                o[0] = tnama.getText();
                o[1] = tusername.getText();
                o[2] = tpassword.getText();
                o[3] = ctipe.getEditor().getText();
                o[4] = kode;
                Alert alertcon = new Alert(Alert.AlertType.CONFIRMATION);
                alertcon.setHeaderText("Yakin ingin memperbaharui data ini?");
                alertcon.setContentText("Data yang sudah diperbaharui tidak bisa dikembalikan lagi.");
                ButtonType ya = new ButtonType("Ya");
                ButtonType tidak = new ButtonType("Tidak");
                alertcon.getButtonTypes().setAll(ya, tidak);
                Optional<ButtonType> opt = alertcon.showAndWait();
                if (opt.get() == ya) {
                    h.update("UPDATE akun SET nama_akun=?,username=?,password=?,tipe=? WHERE id_akun=? ", 5, o);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Data berhasil diperbaharui");
                    alert.setContentText("Lakukan Refresh jika data belum terlihat");
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
        alertcon.setHeaderText("Yakin ingin menghapus data ini?");
        alertcon.setContentText("Data yang sudah dihapus tidak bisa dikembalikan lagi.");
        ButtonType ya = new ButtonType("Ya");
        ButtonType tidak = new ButtonType("Tidak");
        alertcon.getButtonTypes().setAll(ya, tidak);
        Optional<ButtonType> opt = alertcon.showAndWait();
        if (opt.get() == ya) {
            try {
                ObservableList<AkunEntity> ols = tabel.getSelectionModel().getSelectedItems();
                h.connect();
                for (int i = 0; i < ols.size(); i++) {
                    h.delete("DELETE FROM akun WHERE id_akun=?", id.getCellData(ols.get(i)));
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
        kode = null;
        tnama.clear();
        tusername.clear();
        ctipe.getEditor().clear();
        tpassword.clear();
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
