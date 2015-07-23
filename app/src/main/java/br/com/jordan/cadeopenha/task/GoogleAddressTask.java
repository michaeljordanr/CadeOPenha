package br.com.jordan.cadeopenha.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


import br.com.jordan.cadeopenha.R;
import br.com.jordan.cadeopenha.interfaces.AsyncTaskListenerGetWaypoints;
import br.com.jordan.cadeopenha.model.Penhas;
import br.com.jordan.cadeopenha.model.ShapePenha;


public class GoogleAddressTask extends AsyncTask<String[], Void, List<ShapePenha>> {

    private static final String USER_AUTH = "cadeopenha@cadeopenha.com";
    private static final String PASS_AUTH = "CadeOPenhaaaAPP";

    List<LatLng> lstLagLong = new ArrayList<>();
    private AsyncTaskListenerGetWaypoints callback;
    List<ShapePenha> resultShapes = new ArrayList<>();
    String responseStr;

    private ProgressDialog progress;
    private Context context;

    public GoogleAddressTask(AsyncTaskListenerGetWaypoints callback, Context context) {
        this.context = context;
        this.callback = callback;
    }


/*    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progress = new ProgressDialog(context);
        progress.setMessage(context.getString(R.string.searchPenhaRote));
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        progress.show();
    }*/


//    protected List<JSONObject> doInBackground(String[]... arrAddress) {
//        List<JSONObject> lstJsonObj = new ArrayList<>();
//
//        for (String address : arrAddress[0]) {
//            StringBuilder stringBuilder = new StringBuilder();
//            try {
//
//                address = address.replaceAll(" ", "%20");
//
//                HttpPost httppost = new HttpPost("http://maps.google.com/maps/api/geocode/json?address=" + address + "&sensor=false");
//                HttpClient client = new DefaultHttpClient();
//                HttpResponse response;
//                stringBuilder = new StringBuilder();
//
//                response = client.execute(httppost);
//                HttpEntity entity = response.getEntity();
//                InputStream stream = entity.getContent();
//                int b;
//                while ((b = stream.read()) != -1) {
//                    stringBuilder.append((char) b);
//                }
//            } catch (ClientProtocolException e) {
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            JSONObject jsonObject = new JSONObject();
//
//            try {
//                jsonObject = new JSONObject(stringBuilder.toString());
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            lstJsonObj.add(jsonObject);
//        }
//
//        return lstJsonObj;
// }


    @Override
    protected List<ShapePenha> doInBackground(String[]... strings) {

        try {

            HttpGet httpget = new HttpGet("http://apibus.smed.xyz/api/shapes/55264");
            httpget.addHeader("Authorization", "Basic " + Base64.encodeToString((USER_AUTH + ":" + PASS_AUTH).getBytes(), Base64.NO_WRAP));
            DefaultHttpClient client = new DefaultHttpClient();
            HttpResponse response;

            /* authentication */
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
                    USER_AUTH + ":" + PASS_AUTH);
            client.getCredentialsProvider().setCredentials(
                    new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                    credentials);


            response = client.execute(httpget);
            responseStr = EntityUtils.toString(response.getEntity());

            Type type = new TypeToken<List<ShapePenha>>() {
            }.getType();
            resultShapes = new Gson().fromJson(responseStr, type);

            return resultShapes;

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        }

        return null;
    }

    @Override
    protected void onPostExecute(List<ShapePenha> resultShapes) {
        try {
            if (null != progress && progress.isShowing()) {
                progress.dismiss();
            }
            //lstLagLong = getListLatLong(jsonObjects);
            for (ShapePenha shape : resultShapes) {
                LatLng latLng = new LatLng(shape.getLatitude(), shape.getLongitude());
                lstLagLong.add(latLng);
            }

            callback.onTaskCompleteGetWaypoints(lstLagLong);
        } catch (final IllegalArgumentException e) {
        } catch (final Exception e) {
        } finally {
            this.progress = null;
        }
    }

//    public List<LatLng> getListLatLong(List<JSONObject> lstJsonObject) {
//        List<LatLng> lstLatLong = new ArrayList<>();
//
//        for (JSONObject jsonObject : lstJsonObject) {
//            double latitude;
//            double longitute;
//
//            try {
//
//                longitute = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
//                        .getJSONObject("geometry").getJSONObject("location")
//                        .getDouble("lng");
//
//                latitude = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
//                        .getJSONObject("geometry").getJSONObject("location")
//                        .getDouble("lat");
//
//            } catch (JSONException e) {
//                return null;
//
//            }
//
//            lstLatLong.add(new LatLng(latitude, longitute));
//        }
//
//        return lstLatLong;
//    }

}
