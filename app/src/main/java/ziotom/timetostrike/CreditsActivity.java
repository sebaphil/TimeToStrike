package ziotom.timetostrike;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CreditsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout layout = new LinearLayout(this);
        TextView credits = new TextView(this);
        credits.setText("CEVID - Centro Veneto di Innovazione Digitale \n" +
                "per Cittadini, Imprese e Pubblica Amministrazione.\n" +
                "Università Ca' Foscari & Regione del Veneto\n" +
                "\n" +
                "Azione 1: Open Data della Regione Veneto: \n" +
                "dal paradigma DaaS per la fruizione dei dati alle smart app\n" +
                "\n" +
                "coordinatore: prof. A.Cortesi \n" +
                "\n" +
                "app progettata e realizzata da:\n" +
                "- Antonio Panfili\n" +
                "- Sebastiano Filippetto\n" +
                "\n" +
                "Corso di laurea in Informatica - Università Ca' Foscari");
        layout.addView(credits);
        setContentView(layout);
    }
}
