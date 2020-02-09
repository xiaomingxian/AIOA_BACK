package com.cfcc.modules.elasticsearch.mapper;

import com.cfcc.modules.elasticsearch.entity.EsSearch;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EsSearchMapper {

    Integer getSearchFrequency(String keyWord);

    void insertSearchFrequency(String keyWord);

    void updateSearchFrequency(Integer id);

    List<EsSearch> selectEsSearch(@Param("keyWord") String keyWord);
}
