/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import helper.helper;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JRViewer;

/**
 * FXML Controller class
 *
 * @author minami
 */
public class LaporanController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private Button blihat;
    @FXML
    private SwingNode swingnode;
    @FXML
    private ComboBox ckategori;
    @FXML
    private DatePicker ddari, dsampai;

    helper m = new helper();
    Filecontrol fc = new Filecontrol();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        loadlist();
        lihatlaporan();
        gantipilihan();
        ddari.setDisable(true);
        dsampai.setDisable(true);
        ddari.setValue(LocalDate.now());
        dsampai.setValue(LocalDate.now());

        blihat.setGraphic(new ImageView(getClass().getResource("/image/printer-6.png").toString()));
        blihat.setTooltip(new Tooltip("Generate Report"));
    }

    private void loadlist() {
        ObservableList ols = FXCollections.observableArrayList();
        ols.add("Stock Item Reporting");
        ols.add("Sale Reporting");
        ols.add("Purchase Reporting");
        ols.add("Daily Sale Reporting");
        ols.add("Barcode Generator");
        ckategori.setItems(ols);

    }

    public Connection conn() throws SQLException {
        m.conn = DriverManager.getConnection(m.host, m.user, m.password);
        return m.conn;
    }

    private void gantipilihan() {
        ckategori.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                if (newValue.intValue() == 0 || newValue.intValue() == 4) {
                    ddari.setDisable(true);
                    dsampai.setDisable(true);
                } else if (newValue.intValue() == 3) {
                    ddari.setDisable(false);
                    dsampai.setDisable(true);
                } else {
                    ddari.setDisable(false);
                    dsampai.setDisable(false);
                }
            }
        });
    }

    private void lihatlaporan() {
        blihat.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    Stage st = new Stage();
                    st.setTitle("Processing Data");
                    st.resizableProperty().setValue(Boolean.FALSE);
                    st.initModality(Modality.APPLICATION_MODAL);
                    VBox vb = new VBox();
                    vb.setPrefSize(300, 0);
                    vb.setSpacing(5);
                    vb.setPadding(new Insets(5, 5, 5, 5));
                    ProgressBar pb = new ProgressBar();
                    pb.setMaxWidth(Double.MAX_VALUE);
                    vb.getChildren().addAll(pb);
                    Scene sc = new Scene(vb);
                    st.setScene(sc);
                    HashMap hash = null;
                    String path = "";

                    int pilkat = ckategori.getSelectionModel().getSelectedIndex();
                    if (pilkat == 0) {
                        hash = new HashMap(2);
                        hash.put("header", fc.namaperusahaan());
                        hash.put("subheader", ckategori.getSelectionModel().getSelectedItem().toString());
                        path = "laporan/Laporanstock.jasper";
                    } else if (pilkat == 1) {
                        Instant dari = Instant.from(ddari.getValue().atStartOfDay(ZoneId.of("GMT")));
                        Instant sampai = Instant.from(dsampai.getValue().atStartOfDay(ZoneId.of("GMT")));
                        hash = new HashMap(5);
                        hash.put("header", fc.namaperusahaan());
                        hash.put("subheader", ckategori.getSelectionModel().getSelectedItem().toString());
                        hash.put("tanggal_dari", new Date().from(dari));
                        hash.put("tanggal_hingga", new Date().from(sampai));
                        hash.put("SUBREPORT_DIR","laporan/");
                        path = "laporan/Laporanpenjualanperiodik.jasper";
                    } else if (pilkat == 2) {
                        Instant dari = Instant.from(ddari.getValue().atStartOfDay(ZoneId.of("GMT")));
                        Instant sampai = Instant.from(dsampai.getValue().atStartOfDay(ZoneId.of("GMT")));
                        hash = new HashMap(4);
                        hash.put("header", fc.namaperusahaan());
                        hash.put("subheader", ckategori.getSelectionModel().getSelectedItem().toString());
                        hash.put("dari", new Date().from(dari));
                        hash.put("ke", new Date().from(sampai));
                        path = "laporan/Laporanpembelian.jasper";
                    } else if (pilkat == 3) {
                        Instant tanggal = Instant.from(ddari.getValue().atStartOfDay(ZoneId.of("GMT")));
                        hash = new HashMap(4);
                        hash.put("header", fc.namaperusahaan());
                        hash.put("subheader", ckategori.getSelectionModel().getSelectedItem().toString());
                        hash.put("tanggal", new Date().from(tanggal));
                        hash.put("SUBREPORT_DIR","laporan/");
                        path = "laporan/Laporanpenjualanharian.jasper";
                    } else if (pilkat == 4) {
                        List ls = new ArrayList();
                        m.connect();
                        ResultSet res = m.read("SELECT satuan_barang FROM barang GROUP BY satuan_barang").executeQuery();
                        while (res.next()) {
                            ls.add(res.getString("satuan_barang"));

                        }
                        m.disconnect();
                        ChoiceDialog cod = new ChoiceDialog<>("Select Unit", ls);
                        cod.setTitle("Confirmation");
                        cod.setHeaderText("Select Unit ");
                        cod.setContentText("Select Unit");
                        Optional<String> opt = cod.showAndWait();
                        if (opt.isPresent()) {
                            hash = new HashMap(4);
                            hash.put("header", fc.namaperusahaan());
                            hash.put("subheader", ckategori.getSelectionModel().getSelectedItem().toString());
                            hash.put("satuan", opt.get());
                            path = "laporan/barcode.jasper";
                        } else {
                            hash = new HashMap(4);
                            hash.put("header", fc.namaperusahaan());
                            hash.put("subheader", ckategori.getSelectionModel().getSelectedItem().toString());
                            hash.put("satuan", "PCS");
                            path = "laporan/barcode.jasper";
                        }

                    }

                    loadreport lr = new loadreport(path, hash);
                    lr.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                        @Override
                        public void handle(WorkerStateEvent event) {
                            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                            st.close();
                        }
                    });
                    lr.setOnFailed(new EventHandler<WorkerStateEvent>() {
                        @Override
                        public void handle(WorkerStateEvent event) {
                            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                            st.close();
                        }
                    });
                    pb.progressProperty().bind(lr.progressProperty());
                    Thread th = new Thread(lr);
                    th.setDaemon(true);
                    th.start();
                    st.showAndWait();
                } catch (Exception ex) {
                    Logger.getLogger(LaporanController.class.getName()).log(Level.SEVERE, null, ex);
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

    public class loadreport extends Task<Void> {

        String path;
        HashMap hash;

        public loadreport(String path, HashMap hash) {
            this.path = path;
            this.hash = hash;
        }

        @Override
        protected Void call() throws Exception {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            try {
                long start = System.currentTimeMillis();
                JasperReport jr = (JasperReport) JRLoader.loadObject(new File(path));
                JasperPrint jp = JasperFillManager.fillReport(jr, hash, conn());
                long stop = System.currentTimeMillis();
                updateProgress(start, stop);
                updateMessage("Proses selesai");
                swingnode.setContent(new JRViewer(jp));
            } catch (JRException | SQLException ex) {
                Logger.getLogger(LaporanController.class.getName()).log(Level.SEVERE, null, ex);
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
            return null;
        }
    }

}
