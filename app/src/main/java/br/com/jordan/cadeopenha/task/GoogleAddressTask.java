package br.com.jordan.cadeopenha.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


import br.com.jordan.cadeopenha.R;
import br.com.jordan.cadeopenha.interfaces.AsyncTaskListenerGetWaypoints;


public class GoogleAddressTask extends AsyncTask<String[], Void, List<JSONObject>> {

    List<LatLng> lstLagLong = new ArrayList<>();
    private AsyncTaskListenerGetWaypoints callback;

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


    protected List<JSONObject> doInBackground(String[]... arrAddress) {
        List<JSONObject> lstJsonObj = new ArrayList<>();

        for (String address : arrAddress[0]) {
            StringBuilder stringBuilder = new StringBuilder();
            try {

                address = address.replaceAll(" ", "%20");

                HttpPost httppost = new HttpPost("http://maps.google.com/maps/api/geocode/json?address=" + address + "&sensor=false");
                HttpClient client = new DefaultHttpClient();
                HttpResponse response;
                stringBuilder = new StringBuilder();

                response = client.execute(httppost);
                HttpEntity entity = response.getEntity();
                InputStream stream = entity.getContent();
                int b;
                while ((b = stream.read()) != -1) {
                    stringBuilder.append((char) b);
                }
            } catch (ClientProtocolException e) {
            } catch (IOException e) {
                e.printStackTrace();
            }

            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject = new JSONObject(stringBuilder.toString());
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            lstJsonObj.add(jsonObject);
        }

        return lstJsonObj;
    }

    @Override
    protected void onPostExecute(List<JSONObject> jsonObjects) {
        try {
            if (null != progress && progress.isShowing()) {
                progress.dismiss();
            }
            lstLagLong = getListLatLong(jsonObjects);

            callback.onTaskCompleteGetWaypoints(lstLagLong);
        } catch (final IllegalArgumentException e) {
        } catch (final Exception e) {
        } finally {
            this.progress = null;
        }
    }

    public List<LatLng> getListLatLong(List<JSONObject> lstJsonObject) {
        List<LatLng> lstLatLong = new ArrayList<>();

        for (JSONObject jsonObject : lstJsonObject) {
            double latitude;
            double longitute;

            try {

                longitute = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                        .getJSONObject("geometry").getJSONObject("location")
                        .getDouble("lng");

                latitude = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                        .getJSONObject("geometry").getJSONObject("location")
                        .getDouble("lat");

            } catch (JSONException e) {
                return null;

            }

            lstLatLong.add(new LatLng(latitude, longitute));
        }

        return lstLatLong;
    }

}
