package com.flowring.laleents.model.device;

import java.io.Serializable;

public class SystemProgram implements Serializable {
    public String settingKey;
    public String settingValue;
    public String additionalValue;

    public SystemProgram(){
        settingKey = null;
        settingValue = null;
        additionalValue = null;
    }
}
