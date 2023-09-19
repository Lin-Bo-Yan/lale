package com.flowring.laleents.ui.model.FileReader;

import java.io.Serializable;

public class WatermarkDefault implements Serializable {
    public String wmtmplId;
    public String name;
    public String pageDirect;
    public String image;
    public int imageScale;
    public int imageOpacity;
    public String textContent;
    public String textFont;
    public String textColor;
    public int textSize;
    public int textOpacity;
    public int textRotate;
    public boolean isDefault;

    public WatermarkDefault(){
        wmtmplId = "";
        name = "";
        pageDirect = "";
        image = "";
        imageScale = 0;
        imageOpacity = 0;
        textContent = "";
        textFont = "";
        textColor = "";
        textSize = 0;
        textOpacity = 0;
        textRotate = 0;
        isDefault = true;
    }

}
