package iut.dam.saemobilefinale;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class MainActivity extends AppCompatActivity {

    Button btn_scan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_scan = findViewById(R.id.btn_Scan);
        Button btn_signaler = findViewById(R.id.btn_Signaler);
        EditText editTextCIP = findViewById(R.id.editTextCIP);

        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scan_code();
            }
        });

        btn_signaler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(verifierCIP(editTextCIP))
                    insererSignalement(editTextCIP.getText().toString());
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

    public boolean verifierCIP(EditText editText) {
        String cip = editText.getText().toString().trim();

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

    }
}