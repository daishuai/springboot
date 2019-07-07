package com.daishuai.weather.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author Daishuai
 * @description 天气
 * @date 2019/7/5 11:25
 */
@Data
public class WeatherEntity {
    
    @JSONField(name = "nameen")
    private String cityNameE;
    
    @JSONField(name = "cityname")
    private String cityNameC;
    
    @JSONField(name = "city")
    private String cityCode;
    
    /**
     * 摄氏度(Centigrade)
     */
    @JSONField(name = "temp")
    private String temperatureC;
    
    /**
     * 华氏度(Fahrenheit)
     */
    @JSONField(name = "tempf")
    private String temperatureF;
    
    @JSONField(name = "WD")
    private String windDirectionC;
    
    @JSONField(name = "wde")
    private String windDirectionE;
    
    @JSONField(name = "WS")
    private String windSpeedC;
    
    @JSONField(name = "wse")
    private String windSpeedE;
    
    @JSONField(name = "SD")
    private String humidity;
    
    @JSONField(name = "time")
    private String time;
    
    @JSONField(name = "weather")
    private String weatherC;
    
    @JSONField(name = "weathere")
    private String weatherE;
    
    @JSONField(name = "weathercode")
    private String weatherCode;
    
    @JSONField(name = "qy")
    private String pressure;
    
    @JSONField(name = "njd")
    private String visibility;
    
    @JSONField(name = "rain")
    private String rain;
    
    @JSONField(name = "rain24h")
    private String rain24h;
    
    /**
     * 空气指数AQI(Air Quality Index)
     */
    @JSONField(name = "aqi")
    private String aqi;
    
    @JSONField(name = "limitnumber")
    private String limitNumber;
    
    @JSONField(name = "aqi_pm25")
    private String aqi_pm25;
    
    @JSONField(name = "date")
    private String date;
}
