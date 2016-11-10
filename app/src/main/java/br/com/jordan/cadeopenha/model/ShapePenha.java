package br.com.jordan.cadeopenha.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by techresult on 22/07/2015.
 */
public class ShapePenha implements Serializable {

    @SerializedName("shape_id")
    @Expose
    public int id;

    @SerializedName("shape_pt_lat")
    @Expose
    public double latitude;

    @SerializedName("shape_pt_lon")
    @Expose
    public double longitude;

    @SerializedName("shape_pt_sequence")
    @Expose
    public int sequence;

    @SerializedName("shape_dist_traveled")
    @Expose
    public double distTraveled;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public double getDistTraveled() {
        return distTraveled;
    }

    public void setDistTraveled(double distTraveled) {
        this.distTraveled = distTraveled;
    }
}
