package br.com.jordan.cadeopenha.util;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.jordan.cadeopenha.model.Penha;
import br.com.jordan.cadeopenha.model.PenhaMaisProximo;
import br.com.jordan.cadeopenha.model.Penhas;

public class PenhaUtil {

    public PenhaMaisProximo getPenhaMaisProximo(Penhas penhas, LatLng currentLocation) {
        if(penhas != null) {
            Map<Float, LatLng> dist = new HashMap<>();

            PenhaMaisProximo obj = new PenhaMaisProximo();

            Location location = new Location("");
            location.setLatitude(currentLocation.latitude);
            location.setLongitude(currentLocation.longitude);

            for (Penha penha : penhas.getListPenha()) {
                Location penhaLocation = new Location("");
                penhaLocation.setLatitude(penha.getLatitude());
                penhaLocation.setLongitude(penha.getLongitude());
                dist.put(location.distanceTo(penhaLocation), new LatLng(penha.getLatitude(), penha.getLongitude()));
            }

            List<Float> keys = new ArrayList(dist.keySet());
            Collections.sort(keys);

            obj.setPosicao(dist.get(keys.get(0)));
            obj.setDistancia(keys.get(0));

            return obj;
        }else{
            return null;
        }
    }
}
