package com.flowring.laleents.model.explore.store;

import com.flowring.laleents.model.explore.Microapp;

public class StoreRoot {
    public String id;
    public boolean enable;
    public StoreItem storeItem;

    public String getName() {
        return storeItem.name;
    }

    public String getIntroduction() {
        return "" + storeItem.minPrice;
    }

    public String getUrl() {
        return "https://memia.lale.im/Memia/StoreItemShow.do?action=showItemOfMemia&itemID=" + storeItem.id + "&token=$token";
    }

    public String getDownloadPicUrl() {
        return "https://memia.lale.im/Memia/" + storeItem.image1;
    }

    public Microapp qetMicroapp() {
        Microapp microapp = new Microapp();
        microapp.microAppId = id;
        microapp.url = getUrl();
        microapp.name = getName();
        microapp.publish = enable;
        microapp.pictureUrl = getDownloadPicUrl();
        microapp.picture = storeItem.image1;
        return microapp;
    }

}
