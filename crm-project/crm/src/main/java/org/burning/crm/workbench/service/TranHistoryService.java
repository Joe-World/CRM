package org.burning.crm.workbench.service;

import org.burning.crm.workbench.domain.TranHistory;

import java.util.List;

public interface TranHistoryService {
    List<TranHistory> queryTranHistoryForDetailByTranId(String id);

    int insertTranHistory(TranHistory tranHistory);
}
