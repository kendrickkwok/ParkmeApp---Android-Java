package com.example.joseph.parkmeapplication.POJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Example {

    @SerializedName("routes")
    @Expose
    private List<com.example.joseph.parkmeapplication.POJO.Route> routes = new ArrayList<>();

    /**
     *
     * @return
     * The routes
     */
    public List<com.example.joseph.parkmeapplication.POJO.Route> getRoutes() {
        return routes;
    }

    /**
     *
     * @param routes
     * The routes
     */
    public void setRoutes(List<com.example.joseph.parkmeapplication.POJO.Route> routes) {
        this.routes = routes;
    }

}