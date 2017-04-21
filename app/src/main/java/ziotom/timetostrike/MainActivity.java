package ziotom.timetostrike;

import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_main);


        final LinearLayout mainActivityLayout = new LinearLayout(this);
        mainActivityLayout.setOrientation(LinearLayout.VERTICAL);
        BaseAdapter adapter = new BaseAdapter() {
            ArrayList<String> lista = new ArrayList();

            @Override
            public int getCount() {
                return lista.size();
            }

            @Override
            public Object getItem(int position) {
                return lista.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return null;
            }
        };


        ////////////////////// Object creation ///////////////////////
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
        mainActivityLayout.addView(favoriteText);
        mainActivityLayout.addView(favoriteList);
        mainActivityLayout.addView(nearbyText);
        mainActivityLayout.addView(nearbyList);
        //////////////////////////////////////////////////////////////

        setContentView(mainActivityLayout);
    }

}


