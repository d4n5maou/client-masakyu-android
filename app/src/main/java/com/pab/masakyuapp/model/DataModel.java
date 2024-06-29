package com.pab.masakyuapp.model;

public class DataModel {
    private String namaResep,username;
    private String waktuMasak;
    private int porsi, id;

    public DataModel(int id,String namaResep, int porsi, String waktuMasak, String username) {
        this.id = id;
        this.waktuMasak = waktuMasak;
        this.namaResep = namaResep;
        this.porsi = porsi;
        this.username = username;
    }

    public int getId(){return id;};
    public String getNamaResep() {
        return namaResep;
    }

    public int getPorsi() {
        return porsi;
    }
    public String getWaktuMasak() {
        return waktuMasak;
    }
    public String getUsername(){return username;}
}
