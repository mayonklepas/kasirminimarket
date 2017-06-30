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
public class PembelianEntity {

    String id_pembelian, tanggal_pembelian,id_barang,nama_barang,satuan,jumlah,harga_beli,total;

    public PembelianEntity() {
    }

    public PembelianEntity(String id_pembelian, String tanggal_pembelian, String id_barang, String nama_barang, String satuan, String jumlah, String harga_beli, String total) {
        this.id_pembelian = id_pembelian;
        this.tanggal_pembelian = tanggal_pembelian;
        this.id_barang = id_barang;
        this.nama_barang = nama_barang;
        this.satuan = satuan;
        this.jumlah = jumlah;
        this.harga_beli = harga_beli;
        this.total = total;
    }

    public String getId_pembelian() {
        return id_pembelian;
    }

    public void setId_pembelian(String id_pembelian) {
        this.id_pembelian = id_pembelian;
    }

    public String getTanggal_pembelian() {
        return tanggal_pembelian;
    }

    public void setTanggal_pembelian(String tanggal_pembelian) {
        this.tanggal_pembelian = tanggal_pembelian;
    }

    public String getId_barang() {
        return id_barang;
    }

    public void setId_barang(String id_barang) {
        this.id_barang = id_barang;
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

    public String getJumlah() {
        return jumlah;
    }

    public void setJumlah(String jumlah) {
        this.jumlah = jumlah;
    }

    public String getHarga_beli() {
        return harga_beli;
    }

    public void setHarga_beli(String harga_beli) {
        this.harga_beli = harga_beli;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

   
    
    
}
