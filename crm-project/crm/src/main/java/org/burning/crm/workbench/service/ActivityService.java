package org.burning.crm.workbench.service;

import org.burning.crm.workbench.domain.Activity;

import java.util.List;
import java.util.Map;

public interface ActivityService {
    int saveCreateActivity(Activity activity);

    List<Activity> queryActivityByConditionForPage(Map<String, Object> map);

    int queryCountOfActivityByCondition(Map<String, Object> map);

    int deleteActivityByIds(String[] ids);

    Activity queryActivityById(String id);

    int saveEditActivity(Activity activity);

    List<Activity> queryAllActivities();

    List<Activity> queryChooseActivities(String[] ids);

    int saveCreateActivitiesByList(List<Activity> activityList);

    Activity queryActivityForDetailById(String id);

    List<Activity> queryActivityForDetailByClueId(String id);

    List<Activity> queryActivityForDetailByNameClueId(Map<String, Object> map);

    List<Activity> queryActivityForDetailByIds(String[] ids);

    List<Activity> queryActivityForConvertByNameClueId(Map<String, Object> map);

    List<Activity> queryActivityForSaveByName(String name);

    List<Activity> queryActivityForContactsDetailByContactsId(String id);

    List<Activity> queryActivityForDetailByNameContactsId(Map<String,Object> map);
}


