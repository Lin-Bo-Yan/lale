package com.flowring.laleents.model.explore.news;

import com.flowring.laleents.model.explore.Microapp;
import com.flowring.laleents.tools.phone.AllData;

import java.util.ArrayList;

public class RecommendedNews {
    public String jouId;
    public String name;
    public String creator;
    public String publishTime;
    public String creatorName;
    public String creatorLoginId;
    public String creatorImg;
    public ArrayList<TinyArticle> tinyArticles;
    public String lalePassId;

    public String getName() {
        return tinyArticles.get(0).title;
    }

    public String getIntroduction() {
        return tinyArticles.get(0).summary;
    }

    public String getUrl() {
        return "https://news.lale.im/_news/article/" + tinyArticles.get(0).artId + "?token=$token";
    }

    public String getDownloadPicUrl() {
        return AllData.getNewsDomain() + "/" + tinyArticles.get(0).cover;
    }

    public Microapp qetMicroapp() {
        Microapp microapp = new Microapp();
        microapp.microAppId = jouId;
        microapp.url = getUrl();
        microapp.name = name;
        microapp.publish = true;
        microapp.pictureUrl = getDownloadPicUrl();

        return microapp;
    }
}

