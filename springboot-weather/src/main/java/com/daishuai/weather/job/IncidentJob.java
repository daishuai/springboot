package com.daishuai.weather.job;

import com.alibaba.fastjson.JSON;
import com.daishuai.es.config.RestElasticsearchApi;
import com.daishuai.weather.common.HttpUtils;
import com.daishuai.weather.entity.IncidentEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.ParseException;

/**
 * @author Daishuai
 * @description TODO
 * @date 2019/7/11 21:02
 */
@Slf4j
@Component
public class IncidentJob {
    
    @Autowired
    private RestElasticsearchApi restElasticsearchApi;
    
    @Scheduled(fixedRate = 1000 * 60 * 60)
    public void getIncidentData() {
        String url = "http://www.12379.cn/html/gzaq/fmytplz/index%s.shtml";
        String doc = HttpUtils.getGetResponse(url, "");
        boolean isContinue = this.handle(doc);
        int pageNo = 2;
        /*while (isContinue) {
            log.info("pageNo:{}", pageNo);
            doc = HttpUtils.getGetResponse(url, "_" + pageNo);
            isContinue = this.handle(doc);
            pageNo++;
        }*/
    }
    
    private boolean handle(String data) {
        String temp = data;
        int startIndex = temp.indexOf("<li><div class=\"tt\"><h2><a href=\"");
        if (startIndex == -1) {
            return false;
        }
        while (startIndex >= 0) {
            temp = temp.substring(startIndex + 33);
            String url = temp.substring(0, temp.indexOf("shtml\">") + 5);
            startIndex = temp.indexOf("<li><div class=\"tt\"><h2><a href=\"");
            this.getIncidentDetail(url);
        }
        return true;
    }
    
    private void getIncidentDetail(String url) {
        String prefix = "http://www.12379.cn%s";
        String doc = HttpUtils.getGetResponse(prefix, url);
        int index = doc.indexOf("<div class=\"content_title\">");
        if (index == -1) {
            log.info(String.format(prefix, url));
            return;
        }
        doc = doc.substring(index);
        
        String id = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("."));
        String title = doc.substring(doc.indexOf("<h1>") + 4, doc.indexOf("</h1>"));
        String sendDateStr = doc.substring(doc.indexOf("发布时间：") + 5, doc.indexOf("</span><img"));
        String datasource = doc.substring(doc.indexOf("发布来源：") + 5, doc.indexOf("</span></p>"));
        doc = doc.substring(doc.indexOf("<div class=\"content_text\">"));
        doc = doc.substring(0, doc.indexOf("</div>"));
        String content = doc.replaceAll("<div class=\"content_text\">\t\t", "")
                .replaceAll("<SPAN style=\"FONT-SIZE: 14pt\">", "")
                .replaceAll("</SPAN>", "")
                .replaceAll("<P style=\"FONT-SIZE: 14pt\">　　", "")
                .replaceAll("</P>", "")
                .replaceAll("<P>", "")
                .replaceAll("\t", "");
        IncidentEntity incident = new IncidentEntity();
        incident.setTitle(title);
        incident.setDatasource(datasource);
        incident.setContent(content);
        incident.setUrl(String.format(prefix, url));
        incident.setId(id);
        try {
            incident.setSendDate(DateUtils.parseDate(sendDateStr, "yyyy-MM-dd HH:mm:ss"));
            log.info(JSON.toJSONString(incident));
        } catch (ParseException e) {
            log.error("发布日期格式错误，发布日期：{}，异常：{}", sendDateStr, e.getMessage());
        }
        restElasticsearchApi.upsertToProcessor("springboot_incident", "incident", id, JSON.toJSONString(incident));
    }
}
