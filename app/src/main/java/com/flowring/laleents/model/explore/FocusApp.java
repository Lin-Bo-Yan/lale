package com.flowring.laleents.model.explore;

public class FocusApp {
    public String microAppPromotionImageId;
    public String microAppId;
    public String picture;
    public int siblingOrder;
    public boolean enable;
    public String synopsis;
    public String name;
    public String url;
    public String microAppTabId;
    public String microAppMenuId;
    public boolean publish;
    public String pictureUrl;

    public Microapp qetMicroapp() {
        Microapp microapp = new Microapp();
        microapp.microAppId = microAppId;
        microapp.url = url;
        microapp.name = name;
        microapp.publish = enable;
        microapp.pictureUrl = pictureUrl;
        microapp.picture = picture;
        return microapp;
    }
}