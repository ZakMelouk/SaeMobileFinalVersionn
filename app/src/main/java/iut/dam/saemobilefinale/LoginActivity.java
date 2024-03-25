package iut.dam.saemobilefinale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private Context context = this;
    Admin admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialisation des vues
        Button buttonLogin = findViewById(R.id.buttonLogin);
        EditText emailET = findViewById(R.id.TextEmail);
        EditText passwordET = findViewById(R.id.TextPassword);
        int buttonColor = Color.parseColor("#FFD700");
        buttonLogin.setBackgroundColor(buttonColor);
        buttonLogin.setOnClickListener(v -> Login(emailET.getText().toString(), passwordET.getText().toString()));

    }

    private void Login(String email, String password) {
        if (!(android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
            Toast.makeText(getApplicationContext(), R.string.email_not_valid, Toast.LENGTH_SHORT).show();
            return;
        }

        // Vérifier si le mot de passe est vide
        if (password.isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.empty_field, Toast.LENGTH_SHORT).show();
            return;
        }

        String urlString = "http://192.168.1.13/Pharmacie/login.php?email=" + email + "&password=" + password;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(urlString)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = response.body().string();
                try {
                    admin = Admin.getFromJson(responseBody);

                    runOnUiThread(() -> {
                        // Utiliser les données extraites comme nécessaire
                        Toast.makeText(getApplicationContext(), R.string.successful_login, Toast.LENGTH_SHORT).show();
                            /*Intent newIntent = new Intent(LoginActivity.this, EditProfileActivity.class);
                            newIntent.putExtra("FIRSTNAME", firstname);
                            newIntent.putExtra("LASTNAME", lastname);
                            newIntent.putExtra("EMAIL", email);
                            newIntent.putExtra("DESIGNATION", designation);
                            startActivity(newIntent);
                             finish();
                                */
                    });

                } catch (Exception e) {
                    runOnUiThread(() -> {
                        // Gérer les erreurs de parsing JSON
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
}