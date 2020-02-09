package com.cfcc.modules.elasticsearch.service;

import com.cfcc.modules.elasticsearch.entity.EsSearch;

import java.util.List;

public interface EsSearchService {

    void addSearchfrequency(String keyWord);

    List<EsSearch> getEsSearchList(String keyWord);
}
