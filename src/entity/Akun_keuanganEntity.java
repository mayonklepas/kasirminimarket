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
public class Akun_keuanganEntity {
    
    String kode_akun_keuangan,nama_akun_keuangan,keterangan;

    public Akun_keuanganEntity(String kode_akun_keuangan, String nama_akun_keuangan, String keterangan) {
        this.kode_akun_keuangan = kode_akun_keuangan;
        this.nama_akun_keuangan = nama_akun_keuangan;
        this.keterangan = keterangan;
    }

    public String getKode_akun_keuangan() {
        return kode_akun_keuangan;
    }

    public void setKode_akun_keuangan(String kode_akun_keuangan) {
        this.kode_akun_keuangan = kode_akun_keuangan;
    }

    public String getNama_akun_keuangan() {
        return nama_akun_keuangan;
    }

    public void setNama_akun_keuangan(String nama_akun_keuangan) {
        this.nama_akun_keuangan = nama_akun_keuangan;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    
        
    
}
