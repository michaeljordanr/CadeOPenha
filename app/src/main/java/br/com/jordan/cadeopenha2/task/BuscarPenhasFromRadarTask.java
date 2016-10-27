package br.com.jordan.cadeopenha2.task;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import br.com.jordan.cadeopenha.R;
import br.com.jordan.cadeopenha2.interfaces.AsyncTaskListenerBuscarPenhas;
import br.com.jordan.cadeopenha2.model.Penhas;

/**
 * Created by techresult on 03/08/2015.
 */
public class BuscarPenhasFromRadarTask  extends AsyncTask<LatLng, Void, List<Penhas>> {
    private static final String URL_Auth = "http://api.olhovivo.sptrans.com.br/v0/Login/Autenticar?token=";
    private static final String URL_SearchPenhas = "http://api.olhovivo.sptrans.com.br/v0/Posicao?codigoLinha=33000";
    private static final String URL_SearchPenhasOff = "http://api.olhovivo.sptrans.com.br/v0/Posicao?codigoLinha=232";

    private String responseStr;

    private AsyncTaskListenerBuscarPenhas callback;
    private Context context;

    private Penhas resultPenhas;
    private Penhas resultPenhasOff;
    private List<Penhas> result;

    public BuscarPenhasFromRadarTask(Context context, AsyncTaskListenerBuscarPenhas callback) {
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected List<Penhas> doInBackground(LatLng... params) {
        try {
            HttpClient http = new DefaultHttpClient();

            HttpPost post = new HttpPost(URL_Auth + context.getString(R.string.token));
            HttpResponse responseAuth = http.execute(post);
            responseStr = EntityUtils.toString(responseAuth.getEntity());

            if(responseStr.equals("true")){
                HttpGet get = new HttpGet(URL_SearchPenhas);
                HttpResponse responsePenhas = http.execute(get);
                responseStr = EntityUtils.toString(responsePenhas.getEntity());

                Type type = new TypeToken<Penhas>(){}.getType();
                resultPenhas = new Gson().fromJson(responseStr, type);

                //VOLTA DO PENHA
                get = new HttpGet(URL_SearchPenhasOff);
                responsePenhas = http.execute(get);
                responseStr = EntityUtils.toString(responsePenhas.getEntity());

                type = new TypeToken<Penhas>(){}.getType();
                resultPenhasOff = new Gson().fromJson(responseStr, type);
            }

            result = new ArrayList<>();

            result.add(resultPenhas);
            result.add(resultPenhasOff);

            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<Penhas> penhas) {
        try {
            callback.onTaskCompleteAutenticarAPI(penhas);
        } catch (final IllegalArgumentException e) {
            e.printStackTrace();
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
        }
    }
}
