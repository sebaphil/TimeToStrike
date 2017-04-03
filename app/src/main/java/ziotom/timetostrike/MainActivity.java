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
import android.widget.Button;
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
            // Se manca la connessione a internet, fai vedere un testo e bottone per ricaricare
            Button retryButton = new Button(this);
            TextView retryText = new TextView(this);
            retryText.setText("Connessione a internet assente.");
            retryButton.setText("Riprova");
            retryButton.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(isNetworkAvailable()){
                                // Rimuovo la view e riempio con quella giusta
                                mainActivityLayout.removeAllViews();
                                loadComponents(mainActivityLayout);
                            }
                        }
                    }
            );
            mainActivityLayout.addView(retryText);
            mainActivityLayout.addView(retryButton);
            /////////////////////////////////////////////////////////////////////////////////////
        }

        setContentView(mainActivityLayout);
    }

    private void loadComponents(RelativeLayout mainActivityLayout){

        // declare the dialog as a member field of your activity
        ProgressDialog mProgressDialog;

        // instantiate it within the onCreate method
        mProgressDialog = new ProgressDialog(MainActivity.this);
        mProgressDialog.setMessage("A message");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);

        // execute this when the downloader must be fired
        final DownloadTask downloadTask = new DownloadTask(MainActivity.this);
        downloadTask.execute("http://dati.mit.gov.it/catalog/dataset/2f3ef05b-27b9-459b-8380-d2ffa0fe3f98/resource/6838feb1-1f3d-40dc-845f-d304088a92cd/download/scioperi.csv");

        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                downloadTask.cancel(true);
            }
        });

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

    // usually, subclasses of AsyncTask are declared inside the activity class.
    // that way, you can easily modify the UI thread from here
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
                output = new FileOutputStream("/sdcard/scioperi.csv");

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
}


