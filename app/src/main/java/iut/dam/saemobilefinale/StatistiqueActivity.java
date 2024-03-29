package iut.dam.saemobilefinale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class StatistiqueActivity extends AppCompatActivity {
    List<Signalement> mesSignalements;
    List<Medicament> medicamentSignalés;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistique);
        mesSignalements = new ArrayList<>();
        medicamentSignalés = new ArrayList<>();
        getStatistique();
        Button btn_Rechercher = findViewById(R.id.btn_Rechercher);
        EditText editTextRechercherMedicament = findViewById(R.id.editTextRechercherMedicament);
        btn_Rechercher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nb_signalements = 0;
                String code = editTextRechercherMedicament.getText().toString();
                if(code.length() == 13){
                    for(Medicament m : medicamentSignalés){
                        if(code.equals(m.getCIP_13())){
                            for(Signalement s : mesSignalements){
                                if(s.getCODE_CIS().equals(m.getCIS()))
                                    nb_signalements++;
                            }
                            break;
                        }
                    }
                }
                else if(code.length() == 8){
                    for(Medicament m : medicamentSignalés){
                        if(code.equals(m.getCIS())){
                            for(Signalement s : mesSignalements){
                                if(s.getCODE_CIS().equals(code))
                                    nb_signalements++;
                            }
                            break;
                        }
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "Vous devez entrer le code CIP_13 ou le code CIS", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(getApplicationContext(), "Il y a " + nb_signalements + " pour le médicament correspondant au code "+code, Toast.LENGTH_SHORT).show();

            }
        });
    }
    private void getStatistique(){
        String urlString = "http://192.168.1.13/Pharmacie/getSignalements.php";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(urlString)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(responseBody);
                    String medicamentsJson = jsonObject.optString("medicaments");
                    String signalementsJson = jsonObject.optString("signalements");
                    medicamentSignalés = Medicament.getListFromJson(medicamentsJson);
                    mesSignalements = Signalement.getListFromJson(signalementsJson);
                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), "c carre", Toast.LENGTH_SHORT).show();
                        afficherSignalement();
                        afficherNbSignalement();
                    });

                } catch (Exception e) {
                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), "Utilisateur inconnu, veuillez réessayer", Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), R.string.login_failure_message, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    public void afficherSignalement(){
        // Supposons que vous ayez une liste de signalements appelée mesSignalements
        Signalement.sortSignalementsByDateDescending(mesSignalements);
        for(Signalement s : mesSignalements){
            for(Medicament m : medicamentSignalés){
                if(s.getCODE_CIS().equals(m.getCIS()))
                    afficherSignalement(s,m);

            }
        }
    }
    private void afficherSignalement(Signalement signalement, Medicament medicament) {
        TableLayout tableLayout = findViewById(R.id.tableLayoutSignalement2); // TableLayout dans le layout XML

        // Créer une nouvelle rangée pour le signalement
        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
        ));

        // Créer une cellule pour le code CIP
        TextView textViewCIP = new TextView(this);
        textViewCIP.setText(medicament.getCIP_13());
        textViewCIP.setTextColor(Color.WHITE); // Changer la couleur du texte en blanc
        textViewCIP.setLayoutParams(new TableRow.LayoutParams(
                0,
                TableRow.LayoutParams.WRAP_CONTENT,
                1f
        ));
        textViewCIP.setPadding(0, 0, 0, 20);
        row.addView(textViewCIP); // Ajouter la cellule à la rangée

        // Créer une cellule pour le nom du médicament
        TextView textViewNom = new TextView(this);
        textViewNom.setText(medicament.getNom());
        textViewNom.setGravity(Gravity.CENTER_VERTICAL); // Aligner le texte verticalement au centre
        textViewNom.setTextColor(Color.WHITE); // Changer la couleur du texte en blanc
        textViewNom.setLayoutParams(new TableRow.LayoutParams(
                0,
                TableRow.LayoutParams.WRAP_CONTENT,
                1f
        ));
        textViewNom.setPadding(0, 0, 0, 20);
        row.addView(textViewNom); // Ajouter la cellule à la rangée

        // Créer une cellule pour la date du signalement
        TextView textViewDate = new TextView(this);
        textViewDate.setText(signalement.getDate());
        textViewDate.setTextColor(Color.WHITE); // Changer la couleur du texte en blanc
        textViewDate.setLayoutParams(new TableRow.LayoutParams(
                0,
                TableRow.LayoutParams.WRAP_CONTENT,
                1f
        ));
        textViewDate.setPadding(0, 0, 0, 20);
        row.addView(textViewDate); // Ajouter la cellule à la rangée

        // Ajouter la rangée au TableLayout
        tableLayout.addView(row);
    }

    public void afficherNbSignalement() {
        Map<String, Integer> nbSignalementsParMedicament = new HashMap<>();

        // Compter le nombre de signalements pour chaque médicament
        for (Signalement s : mesSignalements) {
            String codeCIS = s.getCODE_CIS();
            nbSignalementsParMedicament.put(codeCIS, nbSignalementsParMedicament.getOrDefault(codeCIS, 0) + 1);
        }

        // Passer la map à la fonction pour afficher les médicaments avec le nombre de signalements associés
        afficherMedicamentsAvecNbSignalements(nbSignalementsParMedicament);
    }

    public void afficherMedicamentsAvecNbSignalements(Map<String, Integer> nbSignalementsParMedicament) {
        // Trier les médicaments par ordre décroissant du nombre de signalements
        medicamentSignalés.sort((m1, m2) -> {
            Integer nbSignalements1 = nbSignalementsParMedicament.getOrDefault(m1.getCIS(), 0);
            Integer nbSignalements2 = nbSignalementsParMedicament.getOrDefault(m2.getCIS(), 0);
            return nbSignalements2.compareTo(nbSignalements1);
        });

        // Afficher les médicaments dans l'ordre trié
        for (Medicament m : medicamentSignalés) {
            Integer nbSignalements = nbSignalementsParMedicament.getOrDefault(m.getCIS(), 0);
            afficherSignalement(m, nbSignalements);
        }
    }


    public void afficherSignalement(Medicament medicament, int nbSignalements) {
        TableLayout tableLayout = findViewById(R.id.tableLayoutCompterSignalement); // TableLayout dans le layout XML

        // Créer une nouvelle rangée pour le signalement
        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
        ));

        // Créer une cellule pour le code CIP
        TextView textViewCIP = new TextView(this);
        textViewCIP.setText(medicament.getCIP_13());
        textViewCIP.setGravity(Gravity.CENTER); // Centrer le texte horizontalement et verticalement
        textViewCIP.setTextColor(Color.WHITE); // Changer la couleur du texte en blanc
        textViewCIP.setLayoutParams(new TableRow.LayoutParams(
                0,
                TableRow.LayoutParams.WRAP_CONTENT,
                1f
        ));
        textViewCIP.setPadding(0, 0, 0, 20);
        row.addView(textViewCIP); // Ajouter la cellule à la rangée

        // Créer une cellule pour le nom du médicament
        TextView textViewNom = new TextView(this);
        textViewNom.setText(medicament.getNom());
        textViewNom.setGravity(Gravity.CENTER_VERTICAL); // Aligner le texte verticalement au centre
        textViewNom.setGravity(Gravity.CENTER); // Centrer le texte horizontalement et verticalement
        textViewNom.setTextColor(Color.WHITE); // Changer la couleur du texte en blanc
        textViewNom.setLayoutParams(new TableRow.LayoutParams(
                0,
                TableRow.LayoutParams.WRAP_CONTENT,
                1f
        ));
        textViewNom.setPadding(0, 0, 0, 20);
        row.addView(textViewNom); // Ajouter la cellule à la rangée

        // Créer une cellule pour la date du signalement
        TextView textViewNb = new TextView(this);
        textViewNb.setText(nbSignalements+"");
        textViewNb.setGravity(Gravity.CENTER); // Centrer le texte horizontalement et verticalement
        textViewNb.setTextColor(Color.WHITE); // Changer la couleur du texte en blanc
        textViewNb.setLayoutParams(new TableRow.LayoutParams(
                0,
                TableRow.LayoutParams.WRAP_CONTENT,
                1f
        ));
        textViewNb.setPadding(0, 0, 0, 20);
        row.addView(textViewNb); // Ajouter la cellule à la rangée

        // Ajouter la rangée au TableLayout
        tableLayout.addView(row);
    }



}