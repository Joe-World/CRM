package org.burning.crm.workbench.service;

import org.burning.crm.workbench.domain.Clue;

import java.util.List;
import java.util.Map;

public interface ClueService {
    int saveCreateClue(Clue clue);

    List<Clue> queryClueByConditionForPage(Map<String, Object> map);

    int queryCountOfClueByCondition(Map<String,Object> map);

    int deleteClueByIds(String[] ids);

    Clue queryClueById(String id);

    int saveEditClue(Clue clue);

    Clue queryClueForDetailById(String id);

    void saveConvertClue(Map<String, Object> map);
}
