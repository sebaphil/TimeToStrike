package ziotom.timetostrike;

import android.app.ListFragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_main);


        final RelativeLayout mainActivityLayout = new RelativeLayout(this);

        if(isNetworkAvailable()) {
            loadComponents(mainActivityLayout);
        }else{
            Button retryButton = new Button(this);
            TextView retryText = new TextView(this);
            retryText.setText("Connessione a internet assente.");
            retryButton.setText("Riprova");
            retryButton.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(isNetworkAvailable()){
                                mainActivityLayout.removeAllViews();
                                loadComponents(mainActivityLayout);
                            }
                        }
                    }
            );
            mainActivityLayout.addView(retryText);
            mainActivityLayout.addView(retryButton);
        }

        setContentView(mainActivityLayout);
    }

    private void loadComponents(RelativeLayout mainActivityLayout){
        ////////////////////// Object creation ///////////////////////
        ScrollView verticalScroll = new ScrollView(this);
        ListView nearbyList = new ListView(this);
        ListView favoriteList = new ListView(this);
        TextView favoriteText = new TextView(this);
        TextView nearbyText = new TextView(this);
        //////////////////////////////////////////////////////////////

        ///////////////////// Setting values /////////////////////////
        favoriteText.setText("Preferiti:");
        nearbyText.setText("Nelle vicinanze");
        //////////////////////////////////////////////////////////////

        /////////////////////// Object adding ////////////////////////
        mainActivityLayout.addView(verticalScroll);
        mainActivityLayout.addView(favoriteText);
        mainActivityLayout.addView(favoriteList);
        mainActivityLayout.addView(nearbyText);
        mainActivityLayout.addView(nearbyList);
        //////////////////////////////////////////////////////////////
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}


