package com.cfcc.modules.elasticsearch.service.impl;/*
 *
 *
 */

import com.cfcc.modules.elasticsearch.entity.EsSearch;
import com.cfcc.modules.elasticsearch.mapper.EsSearchMapper;
import com.cfcc.modules.elasticsearch.service.EsSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EsSearchServiceImpl implements EsSearchService {

    @Autowired
    private EsSearchMapper esSearchMapper;

    @Override
    public void addSearchfrequency(String keyWord) {
        Integer id = esSearchMapper.getSearchFrequency(keyWord);
        if (id == null){
            esSearchMapper.insertSearchFrequency(keyWord);
        }else {
            esSearchMapper.updateSearchFrequency(id);
        }
    }

    @Override
    public List<EsSearch> getEsSearchList(String keyWord) {
        List<EsSearch> esSearchList = esSearchMapper.selectEsSearch(keyWord);
        System.out.println(esSearchList);
        return esSearchList;
    }
}
