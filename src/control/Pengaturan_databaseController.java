/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;

/**
 * FXML Controller class
 *
 * @author minami
 */
public class Pengaturan_databaseController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private TextField thost, tport, tusername, tpassword, tdatabase;
    @FXML
    private Button bsimpan;
    Filecontrol fc = new Filecontrol();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        baca();
        simpan();
         bsimpan.setGraphic(new ImageView(getClass().getResource("/image/document-save-5.png").toString()));
        bsimpan.setTooltip(new Tooltip("Save Setting"));
    }

    public void simpan() {
        bsimpan.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                String host = thost.getText();
                String port = tport.getText();
                String user = tusername.getText();
                String password = tpassword.getText();
                String database = tdatabase.getText();
                fc.simpanpengaturandb(host, port, user, password, database);
                 Alert al=new Alert(Alert.AlertType.INFORMATION);
                 al.setTitle("Information");
                 al.setHeaderText("Setting has been saved");
                 al.setContentText("Restart Application to apply new setting");
                 al.showAndWait();
            }
           
            
        });

    }

    public void baca() {
        thost.setText(fc.host());
        tport.setText(fc.port());
        tusername.setText(fc.username());
        tpassword.setText(fc.password());
        tdatabase.setText(fc.database());
    }

}
