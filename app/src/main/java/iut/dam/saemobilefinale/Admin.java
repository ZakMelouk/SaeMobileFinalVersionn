package iut.dam.saemobilefinale;

import com.google.gson.Gson;

import org.json.JSONException;

public class Admin {
    String firstname;
    String lastname;
    String email;
    String designation;

    public static Admin getFromJson(String json) throws Exception {
        Gson gson = new Gson();
        Admin obj = gson.fromJson(json, Admin.class);
        return obj;
    }
}
