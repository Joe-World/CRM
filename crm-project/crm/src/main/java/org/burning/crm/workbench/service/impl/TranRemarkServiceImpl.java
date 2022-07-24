package org.burning.crm.workbench.service.impl;

import org.burning.crm.workbench.domain.TranRemark;
import org.burning.crm.workbench.mapper.TranRemarkMapper;
import org.burning.crm.workbench.service.TranRemarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("tranRemarkService")
public class TranRemarkServiceImpl implements TranRemarkService {
    @Autowired
    private TranRemarkMapper tranRemarkMapper;

    @Override
    public int saveCreateTranRemarkByList(List<TranRemark> remarkList) {
        return tranRemarkMapper.insertTranRemarkByList(remarkList);
    }

    @Override
    public int saveCreateTranRemark(TranRemark remark) {
        return tranRemarkMapper.insertTranRemark(remark);
    }

    @Override
    public int deleteTranRemarkById(String id) {
        return tranRemarkMapper.deleteTranRemarkById(id);
    }

    @Override
    public int saveEditTranRemark(TranRemark remark) {
        return tranRemarkMapper.updateTranRemark(remark);
    }

    @Override
    public List<TranRemark> queryTranRemarkForDetailByTranId(String id) {
        return tranRemarkMapper.selectTranRemarkForDetailByTranId(id);
    }
}
