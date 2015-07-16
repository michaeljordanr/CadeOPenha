package br.com.jordan.cadeopenha.task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.SystemClock;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.jordan.cadeopenha.AsyncTaskListenerAutenticarAPI;
import br.com.jordan.cadeopenha.R;

public class AutenticarAPITask extends AsyncTask<String, Void, String> {

    private static final String URL = "http://code.softblue.com.br:8080/web/GerarNumeros";
    private String responseStr;
    private ProgressDialog progress;

    private AsyncTaskListenerAutenticarAPI callback;
    private Context context;

    public AutenticarAPITask(Context context, AsyncTaskListenerAutenticarAPI callback){
        this.context = context;
        this.callback = callback;

        progress = new ProgressDialog(context);
        progress.setMessage("Aguarde, autenticando!");
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        progress.show();
    }

    @Override
    protected String doInBackground(String... params) {
        /*try {
            String result = params[0];

            HttpClient http = new DefaultHttpClient();

            List<NameValuePair> getParams = new ArrayList<>();
            getParams.add(new BasicNameValuePair("token", context.getString(R.string.token)));
            String getParamsStr = URLEncodedUtils.format(getParams, HTTP.UTF_8);
            HttpGet get = new HttpGet(URL + "?" + getParamsStr);

            HttpResponse response = http.execute(get);
            responseStr = EntityUtils.toString(response.getEntity());

            return null;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }*/

        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(2000);
            }
        }).run();

        return "";
    }

    @Override
    protected void onPostExecute(String s) {
        try {
            if (null != progress && progress.isShowing()) {
                progress.dismiss();
            }

            //callback.onTaskCompleteAutenticarAPI(null);
        } catch (final IllegalArgumentException e) {
        } catch (final Exception e) {
        } finally {
            this.progress = null;
        }
    }
}
