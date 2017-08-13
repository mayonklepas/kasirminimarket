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
public class CatatanEntity {
    String id, tanggal, kode_perkiraan,nama_perkiraan,kode_keuangan,nama_keuangan,nama, jumlah, keterangan;

    public CatatanEntity(String id, String tanggal, String kode_perkiraan, String nama_perkiraan, String kode_keuangan, String nama_keuangan, String nama, String jumlah, String keterangan) {
        this.id = id;
        this.tanggal = tanggal;
        this.kode_perkiraan = kode_perkiraan;
        this.nama_perkiraan = nama_perkiraan;
        this.kode_keuangan = kode_keuangan;
        this.nama_keuangan = nama_keuangan;
        this.nama = nama;
        this.jumlah = jumlah;
        this.keterangan = keterangan;
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

    public String getKode_perkiraan() {
        return kode_perkiraan;
    }

    public void setKode_perkiraan(String kode_perkiraan) {
        this.kode_perkiraan = kode_perkiraan;
    }

    public String getNama_perkiraan() {
        return nama_perkiraan;
    }

    public void setNama_perkiraan(String nama_perkiraan) {
        this.nama_perkiraan = nama_perkiraan;
    }

    public String getKode_keuangan() {
        return kode_keuangan;
    }

    public void setKode_keuangan(String kode_keuangan) {
        this.kode_keuangan = kode_keuangan;
    }

    public String getNama_keuangan() {
        return nama_keuangan;
    }

    public void setNama_keuangan(String nama_keuangan) {
        this.nama_keuangan = nama_keuangan;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getJumlah() {
        return jumlah;
    }

    public void setJumlah(String jumlah) {
        this.jumlah = jumlah;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }
    
    
    
    
}
