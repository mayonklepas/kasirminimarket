/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import helper.helper;

/**
 *
 * @author minami
 */
public class Setnumber {

    helper m = new helper();

    public String nourut(String prefix, String field, String table) {
        String nourut = prefix + new SimpleDateFormat("yyMMdd").format(new Date()) + "-" + "001";
        try {
            m.connect();
            ResultSet res = m.read("SELECT " + field + " FROM " + table + " ORDER BY substring(replace(" + field + ",'-','') from 3)::INTEGER DESC LIMIT 1").executeQuery();
            while (res.next()) {
                String cekid = res.getString(field);
                if (!cekid.equals("") || cekid != null) {
                    String idfield = res.getString(field);
                    String[] splitidfield = idfield.split("-");
                    int tengah = Integer.parseInt(splitidfield[0].substring(2));
                    String akhir = splitidfield[1];
                    int sekarang = Integer.parseInt(new SimpleDateFormat("yyMMdd").format(new Date()));
                    if (tengah == sekarang) {
                        String rtx = String.valueOf(sekarang) + akhir;
                        int rxtx = Integer.parseInt(rtx) + 1;
                        //System.out.println(rxtx);
                        StringBuffer sb = new StringBuffer();
                        nourut = prefix + new StringBuffer(String.valueOf(rxtx)).insert(6, "-");
                        //System.out.println(nourut);
                    } else {
                        nourut = prefix + String.valueOf(sekarang) + "-" + "001";
                    }
                }

            }
        } catch (SQLException | NumberFormatException | NullPointerException ex) {
            Logger.getLogger(Setnumber.class.getName()).log(Level.SEVERE, null, ex);
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
        return nourut;
    }

    public String nourutv2(String prefix, String field, String table) {
        String nourut = prefix + new SimpleDateFormat("yyMM").format(new Date()) + "-" + "0001";
        try {
            m.connect();
            ResultSet res = m.read("SELECT " + field + " FROM " + table + " ORDER BY substring(replace(" + field + ",'-','') from 3)::INTEGER DESC LIMIT 1").executeQuery();
            while (res.next()) {
                String cekid = res.getString(field);
                if (!cekid.equals("") || cekid != null) {
                    String idfield = res.getString(field);
                    String[] splitidfield = idfield.split("-");
                    int tengah = Integer.parseInt(splitidfield[0].substring(2));
                    String akhir = splitidfield[1];
                    int sekarang = Integer.parseInt(new SimpleDateFormat("yyMM").format(new Date()));
                    if (tengah == sekarang) {
                        String rtx = String.valueOf(sekarang) + akhir;
                        int rxtx = Integer.parseInt(rtx) + 1;
                        System.out.println(rxtx);
                        StringBuffer sb = new StringBuffer();
                        nourut = prefix + new StringBuffer(String.valueOf(rxtx)).insert(4, "-");
                        System.out.println(nourut);
                    } else {
                        nourut = prefix + String.valueOf(sekarang) + "-" + "0001";
                    }
                }

            }
        } catch (SQLException | NumberFormatException | NullPointerException ex) {
            Logger.getLogger(Setnumber.class.getName()).log(Level.SEVERE, null, ex);
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
        return nourut;
    }

    public String nourutv3(String prefix, String field, String table) {
        String nourut = prefix + new SimpleDateFormat("yyMM").format(new Date()) + "-" + "001";
        try {
            m.connect();
            ResultSet res = m.read("SELECT " + field + " FROM " + table + " ORDER BY substring(replace(" + field + ",'-','') from 3)::INTEGER DESC LIMIT 1").executeQuery();
            while (res.next()) {
                String cekid = res.getString(field);
                if (!cekid.equals("") || cekid != null) {
                    String idfield = res.getString(field);
                    String[] splitidfield = idfield.split("-");
                    int tengah = Integer.parseInt(splitidfield[0].substring(2));
                    String akhir = splitidfield[1];
                    int sekarang = Integer.parseInt(new SimpleDateFormat("yyMM").format(new Date()));
                    if (tengah == sekarang) {
                        String rtx = String.valueOf(sekarang) + akhir;
                        int rxtx = Integer.parseInt(rtx) + 1;
                        System.out.println(rxtx);
                        StringBuffer sb = new StringBuffer();
                        nourut = prefix + new StringBuffer(String.valueOf(rxtx)).insert(4, "-");
                        System.out.println(nourut);
                    } else {
                        nourut = prefix + String.valueOf(sekarang) + "-" + "001";
                    }
                }

            }
        } catch (SQLException | NumberFormatException | NullPointerException ex) {
            Logger.getLogger(Setnumber.class.getName()).log(Level.SEVERE, null, ex);
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
        return nourut;
    }

}
