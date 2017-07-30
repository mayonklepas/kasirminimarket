/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kasirminimarket;

import entity.Sessiongs;
import helper.helper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.spi.Configurator;

/**
 *
 * @author Minami
 */
public class Kasirminimarket extends Application {
    
    helper h=new helper();
    StringBuilder data=new StringBuilder();

    @Override
    public void start(Stage stage) throws Exception {
        
        if (h.checkconnection() == 0) {
                h.createdb();
                System.out.println("Database Telah Dibuat");
                try {
                    File datasql = new File("kasirminimarketdb.txt");
                    String line = "";
                    BufferedReader br = new BufferedReader(new FileReader(datasql));
                    while ((line = br.readLine()) != null) {
                        data.append(line);
                    }
                    h.connect();
                    h.exec(data.toString());
                    h.disconnect();
                    System.out.println("Table Telah Dibuat");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        
        
        Parent root = FXMLLoader.load(getClass().getResource("/view/Main.fxml"));

        Scene scene = new Scene(root);
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();
        stage.setTitle("Kasirku Kasir V.1.0");
        stage.getIcons().add(new Image(getClass().getResource("/image/logo.png").toString()));
        stage.show();

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                System.exit(0);
            }
        });

        loginform();
    }

    public void loginform() {
        Stage stage2 = new Stage(StageStyle.UTILITY);
        stage2.setTitle("Login Form");
        VBox vb = new VBox();
        vb.setPadding(new Insets(10, 10, 10, 10));
        vb.setSpacing(5);
        vb.setPrefWidth(330);
        Label luser = new Label("Username");
        Label lpass = new Label("Password");
        TextField tuser = new TextField();
        tuser.setPromptText("Input Username");
        PasswordField tpass = new PasswordField();
        tpass.setPromptText("Input Password");
        Label lnotif = new Label("*Invalid Username or Password");
        lnotif.setVisible(false);
        Button login = new Button("Login");
        Button close = new Button("Close");
        Button pengaturan = new Button("Connection Setup");
        login.setGraphic(new ImageView(getClass().getResource("/image/document-decrypt-2.png").toString()));
        login.setTooltip(new Tooltip("Login"));
        close.setGraphic(new ImageView(getClass().getResource("/image/dialog-close-2.png").toString()));
        close.setTooltip(new Tooltip("Close App"));
        pengaturan.setGraphic(new ImageView(getClass().getResource("/image/network-connect-2.png").toString()));
        pengaturan.setTooltip(new Tooltip("Database Connection Setup"));

        login.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    helper m = new helper();
                    Object[] o = new Object[2];
                    o[0] = tuser.getText();
                    o[1] = tpass.getText();
                    m.connect();
                    ResultSet res = m.readdetail("SELECT COUNT(id_akun) as jumlah FROM akun WHERE "
                            + "username=? AND password=? ", 2, o).executeQuery();
                    while (res.next()) {
                        if (res.getInt("jumlah") == 1) {
                            ResultSet res2 = m.readdetail("SELECT id_akun,nama_akun,tipe FROM akun WHERE "
                                    + "username=? AND password=? ", 2, o).executeQuery();
                            while (res2.next()) {
                                Sessiongs ses = new Sessiongs();
                                ses.setId(res2.getString("id_akun"));
                                ses.setNama(res2.getString("nama_akun"));
                                ses.setTipe(res2.getString("tipe"));
                            }
                            stage2.close();
                        } else {
                            lnotif.setVisible(true);
                        }
                    }

                    m.disconnect();
                } catch (SQLException ex) {
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

        close.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                System.exit(0);
            }
        });

        pengaturan.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

                    Stage st = new Stage();
                    Parent root = FXMLLoader.load(getClass().getResource("/view/Pengaturan_database.fxml"));
                    Scene sc = new Scene(root);
                    st.setTitle("Setting Database");
                    st.setScene(sc);
                    st.initModality(Modality.APPLICATION_MODAL);
                    st.show();
                } catch (IOException ex) {
                    Logger.getLogger(Kasirminimarket.class.getName()).log(Level.SEVERE, null, ex);
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
                    al.show();
                }

            }
        });

        stage2.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                System.exit(0);
            }
        });
        HBox hb = new HBox();
        hb.setSpacing(5);
        hb.getChildren().addAll(login, close, pengaturan);
        HBox hb0 = new HBox();
        hb0.setSpacing(5);
        hb0.setAlignment(Pos.CENTER);
        Label header = new Label("Login terlebih dahulu");
        ImageView img = new ImageView(getClass().getResource("/image/system-lock-screen-2.png").toString());
        hb0.getChildren().addAll(img);

        vb.getChildren().addAll(hb0, luser, tuser, lpass, tpass, hb, lnotif);
        Scene sc2 = new Scene(vb);
        stage2.setScene(sc2);
        sc2.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                try {
                    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    helper m = new helper();
                    Object[] o = new Object[2];
                    o[0] = tuser.getText();
                    o[1] = tpass.getText();
                    m.connect();
                    ResultSet res = m.readdetail("SELECT COUNT(id_akun) as jumlah FROM akun WHERE "
                            + "username=? AND password=? ", 2, o).executeQuery();
                    while (res.next()) {
                        if (res.getInt("jumlah") == 1) {
                            ResultSet res2 = m.readdetail("SELECT id_akun,nama_akun,tipe FROM akun WHERE "
                                    + "username=? AND password=? ", 2, o).executeQuery();
                            while (res2.next()) {
                                Sessiongs ses = new Sessiongs();
                                ses.setId(res2.getString("id_akun"));
                                ses.setNama(res2.getString("nama_akun"));
                                ses.setTipe(res2.getString("tipe"));
                            }
                            stage2.close();
                        } else {
                            lnotif.setVisible(true);
                        }
                    }

                    m.disconnect();
                } catch (SQLException ex) {
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
        stage2.initModality(Modality.APPLICATION_MODAL);
        stage2.showAndWait();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        launch(args);
    }

}
