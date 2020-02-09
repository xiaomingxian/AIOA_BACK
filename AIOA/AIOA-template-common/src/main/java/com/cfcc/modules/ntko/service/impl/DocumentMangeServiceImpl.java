package com.cfcc.modules.ntko.service.impl;

import com.cfcc.modules.docnum.entity.DocNumManage;
import com.cfcc.modules.ntko.mapper.DocumentMangeMapper;
import com.cfcc.modules.ntko.service.DocumentMangeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service
@Transactional
public class DocumentMangeServiceImpl implements DocumentMangeService {

    @Resource
    private DocumentMangeMapper documentMangeMapper;

    @Override
    public DocNumManage queryById(Integer id) {
        DocNumManage  docNumManage=documentMangeMapper.queryById(id);
        return docNumManage;
    }
}
