package br.com.jordan.cadeopenha.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.com.jordan.cadeopenha.R;
import br.com.jordan.cadeopenha.interfaces.AsyncTaskListenerBuscarPenhas;
import br.com.jordan.cadeopenha.util.Parameter;
import br.com.jordan.cadeopenha.model.Penhas;
import br.com.jordan.cadeopenha.util.Constantes;
import br.com.jordan.cadeopenha.util.Retorno;

public class BuscarPenhasTask extends AsyncTask<LatLng, Void, List<Penhas>> {

    private static final String URL_Auth = "http://api.olhovivo.sptrans.com.br/v0/Login/Autenticar?token=";
    private static final String URL_SearchPenhas = "http://api.olhovivo.sptrans.com.br/v0/Posicao?codigoLinha=33000";
    private static final String URL_SearchPenhasOff = "http://api.olhovivo.sptrans.com.br/v0/Posicao?codigoLinha=232";

    private String responseStr;
    private ProgressDialog progress;

    private AsyncTaskListenerBuscarPenhas callback;
    private Context context;

    private Penhas resultPenhas;
    private Penhas resultPenhasOff;
    private List<Penhas> result;
    private Retorno retornoAuth;
    private Retorno retornoGet;
    static final String COOKIES_HEADER = "Set-Cookie";
    static CookieManager msCookieManager = new CookieManager();


    public BuscarPenhasTask(Context context, AsyncTaskListenerBuscarPenhas callback) {
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progress = new ProgressDialog(context);
        progress.setMessage(context.getString(R.string.oh_wait));
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        progress.show();
    }

    @Override
    protected List<Penhas> doInBackground(LatLng... params) {
        try {
            retornoAuth = request(URL_Auth + context.getString(R.string.token), Constantes.REQUEST_METHOD_POST,null ,null ,null, null, true);

            if(retornoAuth.getStatusCode() == HttpURLConnection.HTTP_OK) {
                if (retornoAuth.getResponse().equals("true")) {
                    retornoGet = request("http://api.olhovivo.sptrans.com.br/v0/Posicao?codigoLinha=33000", Constantes.REQUEST_METHOD_GET, null, null, null, null, false);
                    responseStr = retornoGet.getResponse();

                    Type type = new TypeToken<Penhas>() {
                    }.getType();
                    resultPenhas = new Gson().fromJson(responseStr, type);

                    //VOLTA DO PENHA
                    retornoGet = request(URL_SearchPenhasOff, Constantes.REQUEST_METHOD_GET, null, null, null, null, false);
                    responseStr = retornoGet.getResponse();

                    type = new TypeToken<Penhas>() {
                    }.getType();
                    resultPenhasOff = new Gson().fromJson(responseStr, type);
                }
            }

            result = new ArrayList<>();

            result.add(resultPenhas);
            result.add(resultPenhasOff);

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<Penhas> penhas) {
        try {
            if (null != progress && progress.isShowing()) {
                progress.dismiss();
            }

            callback.onTaskCompleteAutenticarAPI(penhas);
        } catch (final IllegalArgumentException e) {
            e.printStackTrace();
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            this.progress = null;
        }
    }

    public Retorno request(String urlString, String method, List<Parameter> listHeader, String user, String pass, String json, boolean setCookie) throws SocketTimeoutException, IOException, Exception {
        HttpURLConnection connection = null;
        Retorno retorno = new Retorno();

        try {
            URL url = new URL(urlString);

            connection = (HttpURLConnection) url.openConnection();

            if(user != null && pass != null){
                connection.setRequestProperty(Constantes.REQUEST_PROPERTY_AUTHORIZATION, buildBasicAuthorizationString(user, pass));
            }

            connection.setReadTimeout(Constantes.READ_TIMEOUT);
            connection.setConnectTimeout(Constantes.CONNECT_TIMEOUT);
            connection.setRequestMethod(method);
            connection.setRequestProperty(Constantes.WS_ACCEPT, Constantes.WS_APPLICATION_JSON);
            connection.setRequestProperty(Constantes.WS_CONTENT_TYPE, Constantes.WS_APPLICATION_JSON);



            if(listHeader != null){
                for(int i = 0; i < listHeader.size(); i++){
                    connection.setRequestProperty(listHeader.get(i).getName(), listHeader.get(i).getValue());
                }
            }

            connection.setDoInput(true);

            if(method.equalsIgnoreCase(Constantes.REQUEST_METHOD_POST) && json != null){
                connection.setRequestProperty(Constantes.REQUEST_PROPERTY_CONTENT_LENGHT, String.valueOf(json.getBytes().length));

                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write(json);
                writer.flush();
                writer.close();
            }

            connection.connect();

            if(setCookie){
                Map<String, List<String>> headerFields = connection.getHeaderFields();
                List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);

                if (cookiesHeader != null) {
                    for (String cookie : cookiesHeader) {
                        msCookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
                    }
                }
            }

            if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                retorno.setStatusCode(connection.getResponseCode());
                retorno.setResponse(convertStreamToString(connection.getInputStream()));
            } else if(connection.getResponseCode() == HttpURLConnection.HTTP_CREATED){
                retorno.setStatusCode(connection.getResponseCode());
                retorno.setResponse(convertStreamToString(connection.getInputStream()));
            } else if(connection.getResponseCode() == HttpURLConnection.HTTP_ACCEPTED){
                retorno.setStatusCode(connection.getResponseCode());
                retorno.setResponse(convertStreamToString(connection.getInputStream()));
            } else if(connection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND){
                retorno.setStatusCode(connection.getResponseCode());
                retorno.setResponse(convertStreamToString(connection.getErrorStream()));
            } else if(connection.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST){
                retorno.setStatusCode(connection.getResponseCode());
                retorno.setResponse(convertStreamToString(connection.getErrorStream()));
            } else {
                retorno.setStatusCode(connection.getResponseCode());
                if(connection.getInputStream() != null){
                    retorno.setResponse(convertStreamToString(connection.getInputStream()));
                } else {
                    retorno.setResponse(convertStreamToString(connection.getErrorStream()));
                }
            }

            return retorno;
        } finally {
            connection.disconnect();
        }
    }

    public String buildBasicAuthorizationString(String username, String password) {
        String credentials = username + ":" + password;
//      return "Basic " + new String(Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP));
        return "Basic " + new String(Base64.encode(credentials.getBytes(), Base64.DEFAULT));
    }

    public static String convertStreamToString(InputStream is)
            throws IOException {
        if (is != null) {
            Writer writer = new StringWriter();
            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is,
                        "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
            return writer.toString();
        } else {
            return "";
        }
    }
}
