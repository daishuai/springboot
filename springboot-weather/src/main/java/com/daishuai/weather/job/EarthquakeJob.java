package com.daishuai.weather.job;

import com.alibaba.fastjson.JSON;
import com.daishuai.es.config.RestElasticsearchApi;
import com.daishuai.weather.entity.EarthquakeEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.*;

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
    
    //@Scheduled(cron = "0 0/20 * * * ?")
    @Scheduled(fixedRate = 1000 * 60 * 60)
    public void execute() throws Exception {
        int currentPage = 1;
        StringBuilder temp = this.getEarthquakeData(currentPage);
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
//        while (currentPage < totalPage) {
//            currentPage++;
//            temp = this.getEarthquakeData(currentPage);
//            if (StringUtils.contains(temp, "var lists =")) {
//                substring = temp.substring(temp.indexOf("var lists =") + 11, temp.indexOf(",pt,myIcon,sContent,html='';"));
//                this.handle(substring);
//                //Thread.sleep(new Random().nextInt(500) + 500);
//            }
//        }
        
    }
    
    /**
     * @param currentPage
     * @return
     * @throws Exception
     */
    private StringBuilder getEarthquakeData(int currentPage) throws Exception {
        String url = "https://www.cea.gov.cn/eportal/ui?pageId=366509&currentPage=%d";
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setSocketTimeout(60000)
                .setConnectTimeout(60000)
                .setConnectionRequestTimeout(60000)
                .setStaleConnectionCheckEnabled(true)
                .build();
        //创建HttpClient对象
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(defaultRequestConfig)
                .build();
        //创建并设置URI
        URIBuilder uri = new URIBuilder(String.format(url, currentPage));
        //创建GET请求
        HttpGet httpGet = new HttpGet(uri.build());
        httpGet.setHeader("Host", "www.cea.gov.cn");
        httpGet.setHeader("Connection", "keep-alive");
        httpGet.setHeader("Pragma", "no-cache");
        httpGet.setHeader("Cache-Control", "no-cache");
        httpGet.setHeader("Upgrade-Insecure-Requests", "1");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36");
        httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
        httpGet.setHeader("Accept-Encoding", "gzip, deflate, br");
        httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
        httpGet.setHeader("Cookie", "JSESSIONID=1BAED4CEE4CE951B91791EBB9C1CE2C4; __jsluid_s=aeb05d18e7cc549a7c71b33f69b56329; _gscu_1877217718=6264947408n5y223; wdcid=5c9bab0eee5936bc; wdlast=1562649474; __jsl_clearance=1562654211.681|0|KHzm1yxyrF37xl3L6k7AYkzNrJs%3D; _gscbrs_1877217718=1; _gscs_1877217718=62654204yis40s13|pv:141");
        //创建响应对象
        CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
        
        //获取请求结果
        HttpEntity entity = httpResponse.getEntity();
        InputStream inputStream = entity.getContent();
        Scanner scanner = new Scanner(inputStream, "UTF-8");
        StringBuilder temp = new StringBuilder();
        while (scanner.hasNextLine()) {
            temp.append(scanner.nextLine());
        }
        httpClient.close();
        httpResponse.close();
        return temp;
    }
    
    /**
     * 数据处理
     *
     * @param data
     */
    private void handle(String data) throws Exception {
        List<EarthquakeEntity> earthquakeEntities = JSON.parseArray(data, EarthquakeEntity.class);
        log.info(data);
        String titleTemplate = "%d月%d日%d时%d分%s发生%s级地震";
        String contentTemplate = "据中国地震台网测定，北京时间%d年%d月%d日%d时%d分在%s（%s纬%s度，%s经%s度）发生%s级地震，震源深度%s千米。";
        for (EarthquakeEntity earthquake : earthquakeEntities) {
            String orig_time = earthquake.getOrig_time();
            String sendDate = orig_time.substring(0, 19);
            Date date = DateUtils.parseDate(sendDate, "yyyy-MM-dd HH:mm:ss");
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
            /*StringBuilder titleAndContent = this.getTitleAndContent(id);
            log.info(titleAndContent.toString());
            String title = titleAndContent.substring(titleAndContent.indexOf("<div class=\"normal\">  <h1>") + 26, titleAndContent.indexOf("</h1>  <div class=\"pages-date\">"));
            String content = titleAndContent.substring(titleAndContent.indexOf("<div id=\"news_content\">   <p>") + 29, titleAndContent.indexOf("</p>  </div>  <p class=\"wz_hits\">"));
            String sendDate = titleAndContent.substring(titleAndContent.indexOf("发布时间：") + 5, titleAndContent.indexOf("  </div>  <div id=\"news_content\">"));*/
            earthquake.setTitle(title);
            earthquake.setContent(content);
            earthquake.setSendDate(DateUtils.parseDate(sendDate, "yyyy-MM-dd HH:mm:ss"));
            log.info("标题：{}", title);
            log.info("内容：{}", content);
            log.info("发布时间：{}", sendDate);
            restElasticsearchApi.upsertToProcessor("springboot_earthquake", "earthquake", id, JSON.toJSONString(earthquake));
        }
        
    }
    
    private StringBuilder getTitleAndContent(String id) throws Exception {
        log.info(id);
        String url = "https://www.cea.gov.cn/cea/xwzx/zqsd/zqsdlb/%s/index.html";
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setSocketTimeout(60000)
                .setConnectTimeout(60000)
                .setConnectionRequestTimeout(60000)
                .setStaleConnectionCheckEnabled(true)
                .build();
        //创建HttpClient对象
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(defaultRequestConfig)
                .build();
        //创建并设置URI
        URIBuilder uri = new URIBuilder(String.format(url, id));
        //创建GET请求
        HttpGet httpGet = new HttpGet(uri.build());
        httpGet.setHeader("Host", "www.cea.gov.cn");
        httpGet.setHeader("Connection", "keep-alive");
        httpGet.setHeader("Pragma", "no-cache");
        httpGet.setHeader("Cache-Control", "no-cache");
        httpGet.setHeader("Upgrade-Insecure-Requests", "1");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36");
        httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
        httpGet.setHeader("Accept-Encoding", "gzip, deflate, br");
        httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
        httpGet.setHeader("Referer", String.format(url, id));
        httpGet.setHeader("Cookie", "__jsluid_s=aeb05d18e7cc549a7c71b33f69b56329; _gscu_1877217718=6264947408n5y223; wdcid=5c9bab0eee5936bc; wdlast=1562649474; _gscbrs_1877217718=1; Hm_lvt_d7682ab43891c68a00de46e9ce5b76aa=1562677239; Hm_lpvt_d7682ab43891c68a00de46e9ce5b76aa=1562677239; __jsluid=d64ebf99658b9b9ae1bbf19e0cf3385a; __jsl_clearance=1562678157.72|0|PruaTXAYWikfG0PW00kzPWoiewg%3D; _gscs_1877217718=62672330h874ic13|pv:23");
        //创建响应对象
        CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
        
        //获取请求结果
        HttpEntity entity = httpResponse.getEntity();
        InputStream inputStream = entity.getContent();
        Scanner scanner = new Scanner(inputStream, "UTF-8");
        StringBuilder temp = new StringBuilder();
        while (scanner.hasNextLine()) {
            temp.append(scanner.nextLine());
        }
        httpClient.close();
        httpResponse.close();
        return temp;
    }
}
