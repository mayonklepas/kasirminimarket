/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author minami
 */
public class Pengaturan_infoController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private Button bsimpan;
    @FXML
    private TextField tnama, tpemilik, tnohp, temail;
    @FXML
    private TextArea talamat;
    Filecontrol fc = new Filecontrol();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        baca();
        simpan();
        bsimpan.setGraphic(new ImageView(getClass().getResource("/image/document-save-5.png").toString()));
        bsimpan.setTooltip(new Tooltip("Save Setting"));
    }

    private void baca() {
        tnama.setText(fc.namaperusahaan());
        tpemilik.setText(fc.namapemilik());
        tnohp.setText(fc.nohp());
        temail.setText(fc.email());
        talamat.setText(fc.alamat());
    }

    private void simpan() {
        bsimpan.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

                try {
                    fc.simpaninfo(tnama.getText(), tpemilik.getText(), tnohp.getText(), temail.getText(), talamat.getText());
                    Alert al = new Alert(Alert.AlertType.INFORMATION);
                    al.setTitle("Information");
                    al.setHeaderText("Setting has been saved");
                    al.setContentText("Restart Application to apply new setting");
                    al.showAndWait();
                } catch (Exception e) {
                    Alert al = new Alert(Alert.AlertType.ERROR);
                    al.setTitle("Error");
                    al.setHeaderText("Application Error");
                    VBox v = new VBox();
                    v.setPadding(new Insets(5, 5, 5, 5));
                    v.setSpacing(5);
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);
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

}
