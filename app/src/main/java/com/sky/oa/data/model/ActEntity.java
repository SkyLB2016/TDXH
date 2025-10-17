package com.sky.oa.data.model;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.sky.oa.gson.ActivityTypeAdapter;
import com.sky.oa.gson.GsonUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@JsonAdapter(ActivityTypeAdapter.class)
public class ActEntity implements Serializable, Comparable<ActEntity>, Cloneable {
    private static final long serialVersionUID = -7780617194676472734L;
    @Expose
//    @SerializedName(value = "activityName", alternate = {"cla", "ca"})
    @SerializedName(value = "activityName")
    private String name;
    @Expose
    @SerializedName("describe")
    private String des;
    @Expose
    @SerializedName("componentName")
    private String component;

    @SerializedName("img")
    private int image;

    private double version = 99.9;
    private List<ActEntity> objList;

    public ActEntity() {
    }

    @Override
    public String toString() {
        return GsonUtils.toJson(this);
    }

    public String toString1() {
        JsonObject json = new JsonObject();
        json.addProperty("name", getName());
        json.addProperty("des", getDes());
        json.addProperty("component", getComponent());
        json.addProperty("image", getImage());
        json.addProperty("version", getVersion());
        return json.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActEntity that = (ActEntity) o;
        return image == that.image &&
                Double.compare(that.version, version) == 0 &&
                Objects.equals(name, that.name) &&
                Objects.equals(des, that.des) &&
                Objects.equals(component, that.component);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, des, component, image);
    }

    @Override
    public int compareTo(@NonNull ActEntity o) {
        return name.compareTo(o.name);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        ActEntity entity = (ActEntity) super.clone();
        entity.name = this.name;
        entity.des = this.des;
        entity.component = this.component;
        entity.image = this.image;
        return entity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public double getVersion() {
        return version;
    }

    public void setVersion(double version) {
        this.version = version;
    }

    public List<ActEntity> getObjList() {
        return objList;
    }

    public void setObjList(List<ActEntity> objList) {
        this.objList = objList;
    }
}
