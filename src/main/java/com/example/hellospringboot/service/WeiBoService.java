package com.example.hellospringboot.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.hellospringboot.model.Rss;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.util.Date;

@Service
public class WeiBoService {
    Logger logger= LoggerFactory.getLogger(this.getClass());
    @Cacheable(value = "rss_cache")
    public Rss getWeiBo(String uid){
        RestTemplate restTemplate=new RestTemplate();
        //请求头,反爬
        HttpHeaders headers=new HttpHeaders();
        headers.set("User-Agent","Mozilla/5.0 (iPhone; CPU iPhone OS 11_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A356 Safari/604.1");
        headers.set("Referer", "https://m.weibo.cn/u/"+uid);
        headers.set("MWeibo-Pwa","1");
        headers.set("X-Requested-With","XMLHttpRequest");
        HttpEntity request = new HttpEntity(headers);
        ResponseEntity<JSONObject> getid= restTemplate.exchange("https://m.weibo.cn/api/container/getIndex?type=uid&value="+uid, HttpMethod.GET,request, JSONObject.class);
        String containerid=getid.getBody().getJSONObject("data").getJSONObject("tabsInfo").getJSONArray("tabs").getJSONObject(1).getString("containerid");
        ResponseEntity<JSONObject> data= restTemplate.exchange("https://m.weibo.cn/api/container/getIndex?type=uid&value=&containerid="+containerid, HttpMethod.GET,request, JSONObject.class);
        JSONArray array=data.getBody().getJSONObject("data").getJSONArray("cards");
        Date date=new Date();
        Rss weibo=new Rss(uid,"weibo",date,array);
        logger.info("无缓存调用weibo");
        return weibo;
    }
}
