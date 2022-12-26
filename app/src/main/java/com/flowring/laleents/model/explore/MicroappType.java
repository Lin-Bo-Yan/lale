package com.flowring.laleents.model.explore;

import java.util.ArrayList;

public class MicroappType {
    public String microAppTabId;
    public String parentId;
    public String microAppMenuId;
    public int siblingOrder;
    public String name;
    public String picture;
    public boolean enable;
    public String lastUpdatedTime;
    public String pictureUrl;
    public ArrayList<MicroappType> childTypes;
    public ArrayList<Microapp> childes;

    public boolean bSelect = false;
}
