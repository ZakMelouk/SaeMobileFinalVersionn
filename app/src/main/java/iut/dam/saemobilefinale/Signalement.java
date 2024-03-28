package iut.dam.saemobilefinale;

import com.google.gson.Gson;

public class Signalement {
    private String Code_CIP;
    private String Date;

    public static Signalement getFromJson(String json) throws Exception {
        Gson gson = new Gson();
        Signalement obj = gson.fromJson(json, Signalement.class);
        return obj;
    }
    public Signalement(String cip, String date){
        this.Code_CIP = cip;
        this.Date = date;
    }
    public String getCIP(){
        return Code_CIP;
    }

}
