/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

/**
 *
 * @author Minami
 */
public class PenjualanEntity {
    
    String id,tanggal,kode_barang,nama_barang,satuan,jenis_penjualan,harga_barang,jumlah,total;

    public PenjualanEntity() {
    }

    public PenjualanEntity(String id, String tanggal, String kode_barang, String nama_barang, String satuan,String jenis_penjualan,String harga_barang, String jumlah, String total) {
        this.id = id;
        this.tanggal = tanggal;
        this.kode_barang = kode_barang;
        this.nama_barang = nama_barang;
        this.satuan = satuan;
        this.jenis_penjualan=jenis_penjualan;
        this.harga_barang = harga_barang;
        this.jumlah = jumlah;
        this.total = total;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getKode_barang() {
        return kode_barang;
    }

    public void setKode_barang(String kode_barang) {
        this.kode_barang = kode_barang;
    }

    public String getNama_barang() {
        return nama_barang;
    }

    public void setNama_barang(String nama_barang) {
        this.nama_barang = nama_barang;
    }

    public String getSatuan() {
        return satuan;
    }

    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }

    public String getJenis_penjualan() {
        return jenis_penjualan;
    }

    public void setJenis_penjualan(String jenis_penjualan) {
        this.jenis_penjualan = jenis_penjualan;
    }
    
    public String getHarga_barang() {
        return harga_barang;
    }

    public void setHarga_barang(String harga_barang) {
        this.harga_barang = harga_barang;
    }

    public String getJumlah() {
        return jumlah;
    }

    public void setJumlah(String jumlah) {
        this.jumlah = jumlah;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

   
    
    
    
    
}
