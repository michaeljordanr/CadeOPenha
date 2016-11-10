package br.com.jordan.cadeopenha.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


import br.com.jordan.cadeopenha.interfaces.AsyncTaskListenerGetWaypoints;
import br.com.jordan.cadeopenha.model.ShapePenha;


public class GoogleAddressTask extends AsyncTask<String, Void, List<ShapePenha>> {

    private static final String USER_AUTH = "cadeopenha@cadeopenha.com";
    private static final String PASS_AUTH = "CadeOPenhaaaAPP";

    private static String URL_PENHA = "http://apibus.smed.xyz/api/shapes/55264";
    private static String URL_VOLTA_PENHA = "http://apibus.smed.xyz/api/shapes/58713";

    int sentido = 0;

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
    protected List<ShapePenha> doInBackground(String... strings) {

        try {

            if(strings[0] == URL_PENHA){
                sentido = 1;
            }else if(strings[0] == URL_VOLTA_PENHA){
                sentido = 2;
            }

            if(sentido == 1){
                responseStr = loadJSONFromAsset("shapes_penha_ida.json");
            }else if(sentido == 2){
                responseStr = loadJSONFromAsset("shapes_penha_volta.json");
            }

            Type type = new TypeToken<List<ShapePenha>>() {
            }.getType();
            resultShapes = new Gson().fromJson(responseStr, type);

            return resultShapes;

        } catch (Exception e) {
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

            callback.onTaskCompleteGetWaypoints(lstLagLong, sentido);
        } catch (final IllegalArgumentException e) {
        } catch (final Exception e) {
        } finally {
            this.progress = null;
        }
    }

    public String loadJSONFromAsset(String file) {
        String json = null;
        try {

            AssetManager mngr = context.getAssets();
            InputStream is = mngr.open(file);

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

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
