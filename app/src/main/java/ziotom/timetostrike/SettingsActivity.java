package ziotom.timetostrike;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class SettingsActivity extends AppCompatActivity {

    DownloadTask downloadTask;
    ArrayList<String[]> listOfLists;
    ArrayList<String> selections;
    String line;
    LinearLayout layout;
    ScrollView scrollView;
    Integer numberOfEntries;
    Scanner s;
    final String cvsSplitBy = ",";
    final String csvFile = "/sdcard/province.csv";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selections = new ArrayList<>();
        try {
            s = new Scanner(new File("/sdcard/selezionate.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while (s.hasNext()){
            selections.add(s.next());
        }
        s.close();
        loadView();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Integer i = 1;
        selections.clear();
        while(i<numberOfEntries){
            Object v = layout.findViewById(i);
            if(v instanceof CheckBox && ((CheckBox) v).isChecked()){
                selections.add(((CheckBox) v).getText().toString());
                Log.e("App", "onBackPressed: ho selezionato " + ((CheckBox) v).getText().toString());
            }
            i++;
        }
        FileWriter writer = null;
        try {
            writer = new FileWriter("/sdcard/selezionate.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(String str: selections) {
            try {
                writer.write(str + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onBackPressed();
    }

    protected void loadView(){
        scrollView = new ScrollView(this);
        layout = new LinearLayout(this);
        scrollView.addView(layout);
        layout.setId(0);
        layout.setOrientation(LinearLayout.VERTICAL);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(scrollView);
        try {
            String result = new DownloadTask(this).execute("http://www.mandile.it/wp-content/uploads/province-sigle.csv").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        readFile();
        numberOfEntries = 1;
        for(String[] i:listOfLists){
            CheckBox cb = new CheckBox(this);
            cb.setId(numberOfEntries);
            cb.setText(i[0]);
            if(selections.contains(i[0])){
                cb.setChecked(true);
            }
            layout.addView(cb);
            numberOfEntries++;
        }

    }

    protected void readFile() {

        listOfLists = new ArrayList<>();
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

    protected void executeDownload(){
        // Di seguito, la task per il download del file.
        downloadTask = new DownloadTask(this);
        downloadTask.execute("http://www.mandile.it/wp-content/uploads/province-sigle.csv");
        Log.e("App", "onCreate: ho eseguito il download");
    }


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
                output = new FileOutputStream("/sdcard/province.csv");

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
