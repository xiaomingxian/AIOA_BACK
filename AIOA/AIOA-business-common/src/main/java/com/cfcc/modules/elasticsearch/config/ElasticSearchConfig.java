package com.cfcc.modules.elasticsearch.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: 杨帆
 */
@Configuration
public class ElasticSearchConfig {
    private  static  final String  SCHEMA="http";

    @Value("${spring.data.elasticsearch.host}")
    private  String  hosts;//192.168.13.13
    @Value("${spring.data.elasticsearch.port}")
    private  int  port;

    private List<HttpHost> httpHostList;

    private RestHighLevelClient highLevelClient;

    private RestClientBuilder restClientBuilder;

    @Bean
    @ConditionalOnMissingBean(RestHighLevelClient.class)
    public   RestHighLevelClient restHighLevelClient(){

        httpHostList = new ArrayList<>();
        String  s[] = hosts.split(",");
        for (String s1 : s) {
            //String hostname, int port, String scheme
            HttpHost httpHost = new HttpHost(s1,port,SCHEMA);
            httpHostList.add(httpHost);
        }

        restClientBuilder  = RestClient.builder(httpHostList.toArray(new HttpHost[]{}));//HttpHost... hosts



        highLevelClient = new RestHighLevelClient(restClientBuilder);

        return  highLevelClient;
    }


}
