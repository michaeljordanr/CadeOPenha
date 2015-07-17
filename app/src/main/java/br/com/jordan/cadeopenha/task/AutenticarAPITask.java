package br.com.jordan.cadeopenha.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.widget.ProgressBar;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.jordan.cadeopenha.R;
import br.com.jordan.cadeopenha.interfaces.AsyncTaskListenerAutenticarAPI;

public class AutenticarAPITask extends AsyncTask<String, Void, String> {

    private static final String URL = "http://api.olhovivo.sptrans.com.br/v0/Login/Autenticar?token=";
    private String responseStr;
    private ProgressDialog progress;
    private GoogleMap map;

    private AsyncTaskListenerAutenticarAPI callback;
    private Context context;

    public AutenticarAPITask(Context context, AsyncTaskListenerAutenticarAPI callback) {
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progress = new ProgressDialog(context);
        progress.setMessage("Aguarde, autenticando!");
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        progress.show();
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            HttpClient http = new DefaultHttpClient();

            //List<NameValuePair> getParams = new ArrayList<>();
            //getParams.add(new BasicNameValuePair("token", context.getString(R.string.token)));
            //String getParamsStr = URLEncodedUtils.format(getParams, HTTP.UTF_8);
            HttpPost post = new HttpPost(URL + context.getString(R.string.token));

            HttpResponse response = http.execute(post);
            responseStr = EntityUtils.toString(response.getEntity());

            return responseStr;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        try {
            if (null != progress && progress.isShowing()) {
                progress.dismiss();
            }

            callback.onTaskCompleteAutenticarAPI(s);
        } catch (final IllegalArgumentException e) {
        } catch (final Exception e) {
        } finally {
            this.progress = null;
        }
    }
}
