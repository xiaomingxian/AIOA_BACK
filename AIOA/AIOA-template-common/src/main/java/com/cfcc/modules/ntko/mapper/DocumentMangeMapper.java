package com.cfcc.modules.ntko.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cfcc.modules.docnum.entity.DocNumManage;
import com.cfcc.modules.papertitle.entity.DocNumSet;

public interface DocumentMangeMapper extends BaseMapper<DocNumSet> {

    DocNumManage queryById(Integer id);
}
