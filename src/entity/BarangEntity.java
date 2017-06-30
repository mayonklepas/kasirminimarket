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
public class BarangEntity {
    String kode_barang,nama_barang,satuan,harga_beli,harga_jual_ecer,harga_jual_grosir,jumlah;

    public BarangEntity() {
    }

    public BarangEntity(String kode_barang, String nama_barang, String satuan, String harga_beli, String harga_jual_ecer,String harga_jual_grosir, String jumlah) {
        this.kode_barang = kode_barang;
        this.nama_barang = nama_barang;
        this.satuan = satuan;
        this.harga_beli = harga_beli;
        this.harga_jual_ecer = harga_jual_ecer;
        this.harga_jual_grosir = harga_jual_grosir;
        this.jumlah = jumlah;
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

    public String getHarga_beli() {
        return harga_beli;
    }

    public void setHarga_beli(String harga_beli) {
        this.harga_beli = harga_beli;
    }

    public String getHarga_jual_ecer() {
        return harga_jual_ecer;
    }

    public void setHarga_jual_ecer(String harga_jual_ecer) {
        this.harga_jual_ecer = harga_jual_ecer;
    }

    public String getHarga_jual_grosir() {
        return harga_jual_grosir;
    }

    public void setHarga_jual_grosir(String harga_jual_grosir) {
        this.harga_jual_grosir = harga_jual_grosir;
    }
    
    

    public String getJumlah() {
        return jumlah;
    }

    public void setJumlah(String jumlah) {
        this.jumlah = jumlah;
    }
    
    
}
