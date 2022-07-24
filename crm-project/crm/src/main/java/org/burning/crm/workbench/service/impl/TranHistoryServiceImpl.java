package org.burning.crm.workbench.service.impl;

import org.burning.crm.workbench.domain.TranHistory;
import org.burning.crm.workbench.mapper.TranHistoryMapper;
import org.burning.crm.workbench.service.TranHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("tranHistoryService")
public class TranHistoryServiceImpl implements TranHistoryService {
    @Autowired
    private TranHistoryMapper tranHistoryMapper;

    @Override
    public List<TranHistory> queryTranHistoryForDetailByTranId(String id) {
        return tranHistoryMapper.selectTranHistoryForDetailByTranId(id);
    }

    @Override
    public int insertTranHistory(TranHistory tranHistory) {
        return tranHistoryMapper.insertTranHistory(tranHistory);
    }
}
