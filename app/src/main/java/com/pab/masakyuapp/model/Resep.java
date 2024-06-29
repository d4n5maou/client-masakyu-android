package com.pab.masakyuapp.model;
public class Resep {
    private String judul;
    private int porsi;
    private String pengirim;

    public Resep(String judul, int porsi, String pengirim) {
        this.judul = judul;
        this.porsi = porsi;
        this.pengirim = pengirim;
    }

    // Buatlah getter untuk masing-masing field
    public String getJudul() {
        return judul;
    }

    public int getPorsi() {
        return porsi;
    }

    public String getPengirim() {
        return pengirim;
    }
}
