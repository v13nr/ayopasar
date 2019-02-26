package nanang.application.lsp.model;

import java.io.Serializable;

public class aset implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    int id;
    String jenisbarang, kodebarang, identitasbarang, jumlah_barang, apbdesa, lain, kekayaan, tanggal_aset, keterangan, gambar;

    public aset(int id, String jenisbarang, String kodebarang, String identitasbarang, String jumlah_barang, String apbdesa, String lain, String kekayaan, String tanggal_aset, String keterangan, String gambar) {
        this.id    = id;
        this.jenisbarang  = jenisbarang;
        this.kodebarang = kodebarang;
        this.identitasbarang = identitasbarang;
        this.jumlah_barang = jumlah_barang;
        this.apbdesa = apbdesa;
        this.lain = lain;
        this.kekayaan = kekayaan;
        this.tanggal_aset = tanggal_aset;
        this.keterangan = keterangan;
        this.gambar = gambar;
    }

    public int getId() {
        return this.id;
    }

    public String getJenisbarang() {
        return this.jenisbarang;
    }

    public String getKodebarang() {
        return this.kodebarang;
    }

    public String getIdentitasbarang() {
        return this.identitasbarang;
    }

    public String getJumlah_barang() {
        return this.jumlah_barang;
    }

    public String getApbdesa() {
        return this.apbdesa;
    }

    public String getLain() {
        return this.lain;
    }

    public String getKekayaan() {
        return this.kekayaan;
    }

    public String getTanggal_aset() {
        return this.tanggal_aset;
    }

    public String getKeterangan() {
        return this.keterangan;
    }

    public String getGambar() {
        return this.gambar;
    }
}
