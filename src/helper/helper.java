/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helper;

import control.Filecontrol;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

/**
 *
 * @author ASUS
 */
public class helper {
    Filecontrol fc=new Filecontrol();
    public String host = "jdbc:postgresql://"+fc.host()+":"+fc.port()+"/"+fc.database()+"";
    public String hostcheck = "jdbc:postgresql://"+fc.host()+":"+fc.port()+"/";
    public String user = fc.username();
    public String password = fc.password();
    public Connection conn;

    public helper() {
        try {
            Class.forName("org.postgresql.Driver").newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(helper.class.getName()).log(Level.SEVERE, null, ex);
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

    }

    public void connect() throws SQLException {
        conn = DriverManager.getConnection(host, user, password);
    }

    public void disconnect() throws SQLException {
        conn.close();
    }

    public PreparedStatement read(String sql) throws SQLException {
        PreparedStatement pre = conn.prepareStatement(sql);
        return pre;
    }

    public PreparedStatement readdetail(String sql, int row, Object[] o) throws SQLException {
        PreparedStatement pre = conn.prepareStatement(sql);
        int i = 0;
        while (i < row) {
            int z = i + 1;
            pre.setObject(z, o[i]);
            i++;
        }
        return pre;
    }

    public void insert(String sql, int row, Object[] o) throws SQLException {
        PreparedStatement pre = conn.prepareStatement(sql);
        int i = 0;
        while (i < row) {
            int z = i + 1;
            pre.setObject(z, o[i]);
            i++;
        }
        pre.executeUpdate();
    }

    public void update(String sql, int row, Object[] o) throws SQLException {
        PreparedStatement pre = conn.prepareStatement(sql);
        int i = 0;
        while (i < row) {
            int z = i + 1;
            pre.setObject(z, o[i]);
            i++;
        }
        pre.executeUpdate();
    }

    public void delete(String sql, String key) throws SQLException {
        PreparedStatement pre = conn.prepareStatement(sql);
        pre.setString(1, key);
        pre.executeUpdate();
    }

    public void exec(String sql) throws SQLException {
        PreparedStatement pre = conn.prepareStatement(sql);
        pre.executeUpdate();
    }
    
    
    public void execbat(StringBuilder sb) throws SQLException {
        PreparedStatement pre = conn.prepareStatement(sb.toString());
        pre.executeUpdate();
    }
    
    public int checkconnection(){
        int countdb=0;
        try {
            Connection con = DriverManager.getConnection(hostcheck, user, password);
            PreparedStatement pre=con.prepareStatement("SELECT COUNT(datname) AS jumlah FROM pg_catalog.pg_database WHERE lower(datname) = lower('kasirminimarketdb');");
            ResultSet res=pre.executeQuery();
            res.next();
            countdb=res.getInt("jumlah");
        } catch (SQLException ex) {
            Logger.getLogger(helper.class.getName()).log(Level.SEVERE, null, ex);
            
        }
        return countdb;
    }
    
    public void createdb(){
        try {
            Connection con = DriverManager.getConnection(hostcheck, user, password);
            PreparedStatement pre=con.prepareStatement("CREATE DATABASE kasirminimarketdb;");
            pre.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(helper.class.getName()).log(Level.SEVERE, null, ex);
            
        }
    }
    
    public String digitinputreplacer(String kata){
        String res1=kata.replace(".", "");
        String res2=res1.replace(",", ".");
        return res2;
    }

}
