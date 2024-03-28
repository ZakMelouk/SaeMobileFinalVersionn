package iut.dam.saemobilefinale;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    Button btn_scan;

    Map<Signalement,Medicament> MesSignalements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_scan = findViewById(R.id.btn_Scan);
        Button btn_signaler = findViewById(R.id.btn_Signaler);
        EditText editTextCIP = findViewById(R.id.editTextCIP);
        MesSignalements = new HashMap<>();

        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scan_code();
            }
        });

        btn_signaler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String CIP = editTextCIP.getText().toString();
                if(verifierCIP(CIP)){
                    if(!(estSignale(CIP)))
                        insererSignalement(CIP);
                    else
                        Toast.makeText(getApplicationContext(), "Vous venez de signaler ce medicament.", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(getApplicationContext(), "Le code doit avoir une longueur de 13 et ne contenir que des chiffres.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void scan_code(){
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volumn up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result ->{

        if(result.getContents()==null){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Result");
            builder.setMessage(result.getContents());
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();
        }
    });

    public boolean verifierCIP(String cip) {
        // Vérifier la longueur
        if (cip.length() != 13) {
            return false;
        }

        // Vérifier que tous les caractères sont des chiffres
        for (int i = 0; i < cip.length(); i++) {
            if (!Character.isDigit(cip.charAt(i))) {
                return false;
            }
        }

        return true;
    }
    public void insererSignalement(String CIP){
        Calendar currentCalendar = Calendar.getInstance();
        Date currentDate = currentCalendar.getTime();

        // Ajouter une heure à la date actuelle
        currentCalendar.add(Calendar.HOUR_OF_DAY, 1);
        Date newDate = currentCalendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String formattedNewDate = dateFormat.format(newDate);
        String urlString = "http://192.168.1.13/Pharmacie/insertSignalement.php?cip_13=" + CIP + "&current_date=" + formattedNewDate ;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(urlString)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseData = response.body().string();
                try {
                    JSONObject jsonResponse = new JSONObject(responseData);
                    String message = jsonResponse.getString("message");

                    if (message.contains("Nouveau signalement insere avec succes")) {
                        String medicamentJson = jsonResponse.getString("medicament");
                        Medicament m = Medicament.getFromJson(medicamentJson);
                        Signalement s = new Signalement(CIP,formattedNewDate);
                        MesSignalements.put(s,m);
                        runOnUiThread(() -> {
                            Toast.makeText(getApplicationContext(), "Nouveau signalement inséré avec succès!", Toast.LENGTH_SHORT).show();
                        });
                    } else if (message.contains("Aucun medicament trouve avec ce CIP_13")) {
                        runOnUiThread(() -> {
                            Toast.makeText(getApplicationContext(), "Aucun médicament trouvé avec ce CIP_13", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(getApplicationContext(), "Erreur lors de l'insertion du signalement: " + message, Toast.LENGTH_SHORT).show();
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    Log.d("Erreur de connexion", "Erreur lors de la connexion au serveur : " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "ERREUR", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    public boolean estSignale(String cip){
        for (Map.Entry<Signalement, Medicament> entry : MesSignalements.entrySet()) {
            if(entry.getKey().getCIP().equals(cip))
                return true;
        }
        return false;
    }
}