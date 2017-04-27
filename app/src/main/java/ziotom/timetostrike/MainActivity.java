package ziotom.timetostrike;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
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
import android.os.WorkSource;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    // NOT WORKING HERE
    //int fineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
    //int writeExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    //int readExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
    ArrayAdapter<String> favoriteAdapter;
    ArrayAdapter<String> nearbyAdapter;
    String[] permissions;
    LinearLayout mainActivityLayout;
    ArrayList<String> favoriteArray;
    ArrayList<String> nearbyArray;
    ListView nearbyList;
    ListView favoriteList;
    TextView favoriteText;
    TextView nearbyText;
    DownloadTask downloadTask;
    ArrayList<String[]> listOfLists;
    final Integer favoriteListViewID = 41;
    final Integer nearbyListViewID = 42;

    final String csvFile = "/sdcard/scioperi.csv";
    String line = "";
    final String cvsSplitBy = ",";
    final int simpleListItemID = 17367043;


    protected void loadUI(){
        mainActivityLayout = new LinearLayout(this);
        mainActivityLayout.setOrientation(LinearLayout.VERTICAL);
        mainActivityLayout.setId(1);
        Log.e("App", "onCreate: ho definito e istanziato il layout");


        nearbyList = new ListView(this);
        favoriteList = new ListView(this);
        favoriteText = new TextView(this);
        nearbyText = new TextView(this);


        // We don't want the app to switch from Portrait to Landscape view,
        // so we're going to fix the orientation.

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        /*File prova = new File("/sdcard/scioperi.csv");
        if(prova.exists()){
            Log.e("App", "loadUI: file esiste!");
        }*/
        favoriteArray = new ArrayList<String>();
        nearbyArray = new ArrayList<String>();
        Log.e("App", "onCreate: ho definito gli arraylist di supporto");


        favoriteList.setId(favoriteListViewID);
        nearbyList.setId(nearbyListViewID);
        favoriteAdapter = new ArrayAdapter<String>(this, simpleListItemID, favoriteArray);
        nearbyAdapter = new ArrayAdapter<String>(this, simpleListItemID, nearbyArray);

        favoriteList.setAdapter(favoriteAdapter);
        nearbyList.setAdapter(nearbyAdapter);

        ////////////////////// Object creation ///////////////////////
        //ProgressDialog downloadProgressDialog = new ProgressDialog(this);

        // In the following lines, we're going to specify IDs for both the favorite and nearby lists,
        // as we're going to need them when calling the constructor for the ArrayAdapter, which is necessary
        // if we want to add items to the ListViews.




        // Next, we're going to effectively link the adapters to each ListViews.





        //////////////////////////////////////////////////////////////




        ///////////////////// Setting values /////////////////////////
        /*downloadProgressDialog.setMessage("Sto aggiornando la lista degli scioperi...");
        downloadProgressDialog.setIndeterminate(true);
        downloadProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        downloadProgressDialog.setCancelable(false);*/
        favoriteText.setText("Preferiti:");
        nearbyText.setText("Nelle vicinanze:");
        //////////////////////////////////////////////////////////////


        /////////////////////// Object adding ////////////////////////
        mainActivityLayout.addView(favoriteText);
        mainActivityLayout.addView(favoriteList);
        mainActivityLayout.addView(nearbyText);
        mainActivityLayout.addView(nearbyList);

        //////////////////////////////////////////////////////////////


        //favoriteAdapter.notifyDataSetChanged();

        setContentView(mainActivityLayout);


        listOfLists = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            while ((line = br.readLine()) != null) {

                // Use comma as separator.
                String[] listOfStrings = line.split(cvsSplitBy);
                //Log.e("App", "readCSV: " + listOfStrings[0]);
                listOfLists.add(listOfStrings);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        favoriteArray.add(listOfLists.get(1)[0].toString());
        Log.e("App", "loadUI: " + listOfLists.get(1)[0].toString());
        favoriteAdapter.notifyDataSetChanged();

        /*for(String[] e:listOfLists){
            //favoriteArray.add(e[2]);
            //Log.e("App", "loadUI: " + e.toString());
            favoriteAdapter.add(e[2]);
        }*/
    }

    protected void executeDownload(){
        // Di seguito, la task per il download del file.
        downloadTask = new DownloadTask(this);
        downloadTask.execute("http://dati.mit.gov.it/catalog/dataset/2f3ef05b-27b9-459b-8380-d2ffa0fe3f98/resource/6838feb1-1f3d-40dc-845f-d304088a92cd/download/scioperi.csv");
        Log.e("App", "onCreate: ho eseguito il download");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_main);
        permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        Log.e("App", "onCreate: sono nell'onCreate");
        ////////////////////////////////////////////////////////////////////////
        /////////////////////// PERMISSIONS ////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////
        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        // controlliamo se la permission è concessa
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            Log.e("App", "onCreate: Permissions denied" + ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) +
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)+
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE));
            // se arriviamo qui è perchè la permission non è stata ancora concessa
            ActivityCompat.requestPermissions(this, permissions, 0);
            Log.e("App", "onCreate: ho controllato le autorizzazioni perché non c'erano");
        } else {
            try {
                String result = new DownloadTask(this).execute("http://dati.mit.gov.it/catalog/dataset/2f3ef05b-27b9-459b-8380-d2ffa0fe3f98/resource/6838feb1-1f3d-40dc-845f-d304088a92cd/download/scioperi.csv").get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            loadUI();
            Log.e("App", "onCreate: autorizzazioni granted, ho eseguito il download e caricato la UI");
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("App", "onRequestPermissionsResult: le autorizzazioni sono granted");
                    try {
                        String result = new DownloadTask(this).execute("http://dati.mit.gov.it/catalog/dataset/2f3ef05b-27b9-459b-8380-d2ffa0fe3f98/resource/6838feb1-1f3d-40dc-845f-d304088a92cd/download/scioperi.csv").get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    loadUI();
                } else {
                    Log.e("App", "onRequestPermissionsResult: le autorizzazioni sono denied");
                    System.exit(0);
                }
                return;
            }
        }
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
                Log.e("App", "doInBackground: started writing");

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream("/sdcard/scioperi.csv");

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                    //Log.e("App", "doInBackground: " + data.toString());
                }
            } catch (Exception e) {
                Log.e("App", "doInBackground: "+e.getLocalizedMessage());
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
            Log.e("App", "doInBackground: finished writing");
            return null;
        }
    }






}


