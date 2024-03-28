package iut.dam.saemobilefinale;

import com.google.gson.Gson;

public class Medicament {
    String CIS;
    String CIP_13;
    String nom;
    public static Medicament getFromJson(String json) throws Exception {
        Gson gson = new Gson();
        Medicament obj = gson.fromJson(json, Medicament.class);
        return obj;
    }
}
