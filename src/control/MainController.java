/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import entity.Sessiongs;
import helper.helper;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import kasirminimarket.Kasirminimarket;

/**
 *
 * @author Minami
 */
public class MainController implements Initializable {

    @FXML
    MenuItem mbarang, makun, mpenjualan, mpembelian, mdatabase, minfo, mlaporan, mtentang, mclear;
    @FXML
    TabPane tabpane;
    @FXML
    ImageView ibg;
    @FXML
    AnchorPane anchorpane;
    @FXML
    BorderPane borderpane;
    Sessiongs ses = new Sessiongs();
    File image = new File("bg.jpg");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        menubarang();
        menupenjualan();
        menupembelian();
        menuakun();
        menulaporan();
        menuaturdb();
        menuaturinfo();
        menutentang();
        cleartransaksi();

        /*try {
            ibg.setImage(new Image(new FileInputStream(image)));
            ibg.fitWidthProperty().bind(borderpane.widthProperty());
            ibg.fitHeightProperty().bind(borderpane.heightProperty());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }*/

    }

    private void menubarang() {
        mbarang.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                if (ses.getTipe().equals("User")) {
                    Alert alt = new Alert(Alert.AlertType.ERROR);
                    alt.setHeaderText("Akses tidak diterima");
                    alt.setContentText("Maaf anda tidak diizinkan untuk mengakses menu ini");
                    alt.showAndWait();
                } else {

                    try {
                        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                        Node node = (AnchorPane) FXMLLoader.load(getClass().getResource("/view/Barang.fxml"));
                        Tab page = new Tab("Item", node);
                        tabpane.getTabs().add(page);
                    } catch (IOException ex) {
                        Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
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
        });
    }

    private void menupenjualan() {
        mpenjualan.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    Node node = (AnchorPane) FXMLLoader.load(getClass().getResource("/view/Penjualan.fxml"));
                    Tab page = new Tab("Sales", node);
                    tabpane.getTabs().add(page);
                } catch (IOException ex) {
                    Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
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

    private void menupembelian() {
        mpembelian.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (ses.getTipe().equals("User")) {
                    Alert alt = new Alert(Alert.AlertType.ERROR);
                    alt.setHeaderText("Akses tidak diterima");
                    alt.setContentText("Maaf anda tidak diizinkan untuk mengakses menu ini");
                    alt.showAndWait();
                } else {
                    try {
                        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                        Node node = (AnchorPane) FXMLLoader.load(getClass().getResource("/view/Pembelian.fxml"));
                        Tab page = new Tab("Purchase", node);
                        tabpane.getTabs().add(page);
                    } catch (IOException ex) {
                        Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
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
        });
    }

    private void menuakun() {
        makun.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (ses.getTipe().equals("User")) {
                    Alert alt = new Alert(Alert.AlertType.ERROR);
                    alt.setHeaderText("Akses tidak diterima");
                    alt.setContentText("Maaf anda tidak diizinkan untuk mengakses menu ini");
                    alt.showAndWait();
                } else {
                    try {
                        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                        Node node = (AnchorPane) FXMLLoader.load(getClass().getResource("/view/Akun.fxml"));
                        Tab page = new Tab("User Account", node);
                        tabpane.getTabs().add(page);
                    } catch (IOException ex) {
                        Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
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
        });
    }

    private void menulaporan() {
        mlaporan.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (ses.getTipe().equals("User")) {
                    Alert alt = new Alert(Alert.AlertType.ERROR);
                    alt.setHeaderText("Akses tidak diterima");
                    alt.setContentText("Maaf anda tidak diizinkan untuk mengakses menu ini");
                    alt.showAndWait();
                } else {
                    try {
                        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                        Node node = (AnchorPane) FXMLLoader.load(getClass().getResource("/view/Laporan.fxml"));
                        Tab page = new Tab("Report", node);
                        tabpane.getTabs().add(page);
                    } catch (IOException ex) {
                        Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
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
        });
    }

    private void menuaturdb() {
        mdatabase.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                try {
                    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

                    Stage st = new Stage();
                    Parent root = FXMLLoader.load(getClass().getResource("/view/Pengaturan_database.fxml"));
                    Scene sc = new Scene(root);
                    st.setTitle("Database Setting");
                    st.setScene(sc);
                    st.initModality(Modality.APPLICATION_MODAL);
                    st.show();
                } catch (IOException ex) {
                    Logger.getLogger(Kasirminimarket.class.getName()).log(Level.SEVERE, null, ex);
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
                    al.show();
                }
            }
        });
    }

    private void menuaturinfo() {
        minfo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                try {
                    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

                    Stage st = new Stage();
                    Parent root = FXMLLoader.load(getClass().getResource("/view/Pengaturan_info.fxml"));
                    Scene sc = new Scene(root);
                    st.setTitle("Information Setting");
                    st.setScene(sc);
                    st.initModality(Modality.APPLICATION_MODAL);
                    st.show();
                } catch (IOException ex) {
                    Logger.getLogger(Kasirminimarket.class.getName()).log(Level.SEVERE, null, ex);
                    Alert al = new Alert(Alert.AlertType.ERROR);
                    al.setTitle("Kesalahan");
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
                    al.show();
                }
            }
        });
    }

    private void menutentang() {
        mtentang.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                try {
                    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    Parent root = FXMLLoader.load(getClass().getResource("/view/Tentangapp.fxml"));
                    Scene sc = new Scene(root);
                    Stage st = new Stage();
                    st.setTitle("About");
                    st.initModality(Modality.APPLICATION_MODAL);
                    st.resizableProperty().setValue(Boolean.FALSE);
                    st.setScene(sc);
                    st.showAndWait();
                } catch (IOException ex) {
                    Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    private void cleartransaksi() {
        mclear.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                Alert dial = new Alert(Alert.AlertType.CONFIRMATION);
                dial.setTitle("Konfirmasi");
                dial.setHeaderText("Yakin Ingin Menghapus Semua Data Transaksi??");
                dial.setContentText("Data yang sudah dihapus tidak akan bisa dikembaliakan");
                ButtonType ya = new ButtonType("Ya");
                ButtonType tidak = new ButtonType("Tidak");
                dial.getButtonTypes().setAll(ya, tidak);
                Optional<ButtonType> opt = dial.showAndWait();
                if (opt.get() == ya) {
                    try {
                        helper h = new helper();
                        h.connect();
                        h.exec("DELETE FROM penjualan");
                        h.disconnect();

                    } catch (SQLException ex) {
                        Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    Alert al = new Alert(Alert.AlertType.INFORMATION);
                    al.setTitle("Informasi");
                    al.setHeaderText("Berhasil");
                    al.setContentText("Data penjualan sudah berhasil dikosongkan");
                    al.showAndWait();
                }
            }
        });
    }

}
