package com.daishuai.weather.job;

import com.alibaba.fastjson.JSON;
import com.daishuai.es.config.RestElasticsearchApi;
import com.daishuai.weather.common.HttpUtils;
import com.daishuai.weather.entity.EarthquakeEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Daishuai
 * @description 地震爬虫
 * @date 2019/7/9 15:01
 */
@Slf4j
@Component
public class EarthquakeJob {
    
    @Autowired
    private RestElasticsearchApi restElasticsearchApi;
    
    @Scheduled(fixedRate = 1000 * 60 * 60)
    public void execute() throws Exception {
        int currentPage = 1;
        String temp = this.getEarthquakeData(currentPage);
        log.info(temp.toString());
        String totalCount = temp.substring(temp.indexOf("总记录数:") + 5, temp.indexOf("总记录数:") + 12);
        totalCount = totalCount.replaceAll("\\D", "");
        String pageSize = temp.substring(temp.indexOf("每页显示") + 4, temp.indexOf("条记录"));
        int remainder = Integer.valueOf(totalCount) % Integer.valueOf(pageSize);
        int totalPage = Integer.valueOf(totalCount) / Integer.valueOf(pageSize);
        totalPage = remainder == 0 ? totalPage : totalPage + 1;
        log.info("总记录数：{}", totalCount);
        log.info("每页显示{}条记录", pageSize);
        String substring = temp.substring(temp.indexOf("var lists =") + 11, temp.indexOf(",pt,myIcon,sContent,html='';"));
        this.handle(substring);
        /*while (currentPage < totalPage) {
            currentPage++;
            temp = this.getEarthquakeData(currentPage);
            if (StringUtils.contains(temp, "var lists =")) {
                substring = temp.substring(temp.indexOf("var lists =") + 11, temp.indexOf(",pt,myIcon,sContent,html='';"));
                this.handle(substring);
                //Thread.sleep(new Random().nextInt(500) + 500);
            }
        }*/
        
    }
    
    /**
     * @param currentPage
     * @return
     * @throws Exception
     */
    private String getEarthquakeData(int currentPage) throws Exception {
        String url = "https://www.cea.gov.cn/eportal/ui?pageId=366509&currentPage=%d";
        return HttpUtils.getGetResponse(url, currentPage);
    }
    
    /**
     * 数据处理
     *
     * @param data
     */
    private void handle(String data) {
        List<EarthquakeEntity> earthquakeEntities = JSON.parseArray(data, EarthquakeEntity.class);
        log.info(data);
        String titleTemplate = "%d月%d日%d时%d分%s发生%s级地震";
        String contentTemplate = "据中国地震台网测定，北京时间%d年%d月%d日%d时%d分在%s（%s纬%s度，%s经%s度）发生%s级地震，震源深度%s千米。";
        for (EarthquakeEntity earthquake : earthquakeEntities) {
            String orig_time = earthquake.getOrig_time();
            String sendDate = orig_time.substring(0, 19);
            Date date = null;
            try {
                date = DateUtils.parseDate(sendDate, "yyyy-MM-dd HH:mm:ss");
            } catch (ParseException e) {
                log.error("解析日期出错，发布日期：{}", sendDate, e);
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            //纬度
            String latitudes = earthquake.getLatitudes();
            String southOrNorth = StringUtils.startsWith(latitudes, "-") ? "南" : "北";
            latitudes = latitudes.replaceAll("-", "");
            latitudes = StringUtils.startsWith(latitudes, ".") ? latitudes.replaceAll("\\.", "0.") : latitudes;
            //经度
            String longitudes = earthquake.getLongitudes();
            String eastOrWest = StringUtils.startsWith(longitudes, "-") ? "西" : "东";
            longitudes = longitudes.replaceAll("-", "");
            longitudes = StringUtils.startsWith(longitudes, ".") ? longitudes.replaceAll("\\.", "0.") : longitudes;
            String title = String.format(titleTemplate, month, day, hour, minute, earthquake.getEpicenter(), earthquake.getNum_mag());
            String content = String.format(contentTemplate, year, month, day, hour, minute, earthquake.getEpicenter(), southOrNorth, latitudes, eastOrWest, longitudes, earthquake.getNum_mag(), earthquake.getDepth());
            String id = earthquake.getId();
            earthquake.setTitle(title);
            earthquake.setContent(content);
            earthquake.setSendDate(date);
            log.info("标题：{}", title);
            log.info("内容：{}", content);
            log.info("发布时间：{}", sendDate);
            restElasticsearchApi.upsertToProcessor("springboot_earthquake", "earthquake", id, JSON.toJSONString(earthquake));
        }
        
    }
}
