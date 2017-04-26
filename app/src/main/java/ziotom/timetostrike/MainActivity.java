package ziotom.timetostrike;

import android.app.DownloadManager;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.content.ContentResolver;
import android.location.LocationManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_main);


        final LinearLayout mainActivityLayout = new LinearLayout(this);
        mainActivityLayout.setOrientation(LinearLayout.VERTICAL);

        // We don't want the app to switch from Portrait to Landscape view,
        // so we're going to fix the orientation.

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ArrayList<String> favoriteArray = new ArrayList<String>();
        ArrayList<String> nearbyArray = new ArrayList<String>();


        ////////////////////// Object creation ///////////////////////
        //ProgressDialog downloadProgressDialog = new ProgressDialog(this);
        ListView nearbyList = new ListView(this);
        ListView favoriteList = new ListView(this);
        TextView favoriteText = new TextView(this);
        TextView nearbyText = new TextView(this);

        // In the following lines, we're going to specify IDs for both the favorite and nearby lists,
        // as we're going to need them when calling the constructor for the ArrayAdapter, which is necessary
        // if we want to add items to the ListViews.

        favoriteList.setId(1);
        nearbyList.setId(2);

        ArrayAdapter<String> favoriteAdapter = new ArrayAdapter<String>(this, 1, favoriteArray);
        ArrayAdapter<String> nearbyAdapter = new ArrayAdapter<String>(this, 2, nearbyArray);

        // Next, we're going to effectively link the adapters to each ListViews.

        favoriteList.setAdapter(favoriteAdapter);
        nearbyList.setAdapter(nearbyAdapter);



        //////////////////////////////////////////////////////////////

        final DownloadTask downloadTask = new DownloadTask(this);
        downloadTask.execute("http://dati.mit.gov.it/catalog/dataset/2f3ef05b-27b9-459b-8380-d2ffa0fe3f98/resource/6838feb1-1f3d-40dc-845f-d304088a92cd/download/scioperi.csv");

        ///////////////////// Setting values /////////////////////////
        /*downloadProgressDialog.setMessage("Sto aggiornando la lista degli scioperi...");
        downloadProgressDialog.setIndeterminate(true);
        downloadProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        downloadProgressDialog.setCancelable(false);*/
        favoriteText.setText("Preferiti:");
        nearbyText.setText("Nelle vicinanze:");
        //////////////////////////////////////////////////////////////

        // Di seguito, la task per il download del file.

        File f = new File("/sdcard/dati.csv");
        if(f.exists() && !f.isDirectory()) {
            TextView error = new TextView(this);
            error.setText("error");
            mainActivityLayout.addView(error);
        }

        /////////////////////// Object adding ////////////////////////
        mainActivityLayout.addView(favoriteText);
        mainActivityLayout.addView(favoriteList);
        mainActivityLayout.addView(nearbyText);
        mainActivityLayout.addView(nearbyList);
        //////////////////////////////////////////////////////////////

        CSVReader reader = new CSVReader();
        if(reader.isEmpty()){
            TextView error = new TextView(this);
            error.setText("empty");
            mainActivityLayout.addView(error);
        }

        for(String[] e:reader.getCSV()){
            favoriteArray.add(e[2]);
        }
        favoriteAdapter.notifyDataSetChanged();

        setContentView(mainActivityLayout);



    }

    // This method will return true or false, depending on the status of the
    // GPS location service.

    private Boolean displayGpsStatus(){
        ContentResolver contentResolver = getBaseContext().getContentResolver();
        boolean gpsStatus = Settings.Secure.isLocationProviderEnabled(contentResolver,LocationManager.GPS_PROVIDER);
        if(gpsStatus){
            return true;
        } else {
            return false;
        }
    }

    // This is going to be the code snippet required to download the .csv file.
    // "http://dati.mit.gov.it/catalog/dataset/2f3ef05b-27b9-459b-8380-d2ffa0fe3f98/resource/6838feb1-1f3d-40dc-845f-d304088a92cd/download/scioperi.csv"

    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream("/sdcard/file_name.extension");

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }
    }





    public class CSVReader{
        String csvFile = "/sdcard/dati.csv";
        String line = "";
        String cvsSplitBy = ",";
        ArrayList<String[]> listOfLists = new ArrayList<>();

        public ArrayList<String[]> getCSV(){
            readCSV();
            return listOfLists;
        }

        public Boolean isEmpty(){
            return listOfLists.isEmpty();
        }

        public void readCSV() {
            try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

                while ((line = br.readLine()) != null) {

                    // Use comma as separator.
                    String[] listOfStrings = line.split(cvsSplitBy);
                    listOfLists.add(listOfStrings);

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



}


