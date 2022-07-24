package org.burning.crm.workbench.service.impl;

import org.burning.crm.workbench.domain.Activity;
import org.burning.crm.workbench.domain.ActivityRemark;
import org.burning.crm.workbench.mapper.ActivityMapper;
import org.burning.crm.workbench.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("activityService")
public class ActivityServiceImpl implements ActivityService {
    @Autowired
    private ActivityMapper activityMapper;

    @Override
    public int saveCreateActivity(Activity activity) {
        return activityMapper.insertActivity(activity);
    }

    @Override
    public List<Activity> queryActivityByConditionForPage(Map<String, Object> map) {
        return activityMapper.selectActivityByConditionForPage(map);
    }

    @Override
    public int queryCountOfActivityByCondition(Map<String, Object> map) {
        return activityMapper.selectCountOfActivityByCondition(map);
    }

    @Override
    public int deleteActivityByIds(String[] ids) {
        return activityMapper.deleteActivityByIds(ids);
    }

    @Override
    public Activity queryActivityById(String id) {
        return activityMapper.selectActivityById(id);
    }

    @Override
    public int saveEditActivity(Activity activity) {
        return activityMapper.updateActivity(activity);
    }

    @Override
    public List<Activity> queryAllActivities() {
        return activityMapper.selectAllActivities();
    }

    @Override
    public List<Activity> queryChooseActivities(String[] ids) {
        return activityMapper.selectChooseActivities(ids);
    }

    @Override
    public int saveCreateActivitiesByList(List<Activity> activityList) {
        return activityMapper.insertActivitiesByList(activityList);
    }

    @Override
    public Activity queryActivityForDetailById(String id) {
        return activityMapper.selectActivityForDetailById(id);
    }

    @Override
    public List<Activity> queryActivityForDetailByClueId(String id) {
        return activityMapper.selectActivityForDetailByClueId(id);
    }

    @Override
    public List<Activity> queryActivityForDetailByNameClueId(Map<String, Object> map) {
        return activityMapper.selectActivityForDetailByNameClueId(map);
    }

    @Override
    public List<Activity> queryActivityForDetailByIds(String[] ids) {
        return activityMapper.selectActivityForDetailByIds(ids);
    }

    @Override
    public List<Activity> queryActivityForConvertByNameClueId(Map<String, Object> map) {
        return activityMapper.selectActivityForConvertByNameClueId(map);
    }

    @Override
    public List<Activity> queryActivityForSaveByName(String name) {
        return activityMapper.selectActivityForSaveByName(name);
    }

    @Override
    public List<Activity> queryActivityForContactsDetailByContactsId(String id) {
        return activityMapper.selectActivityForContactsDetailByContactsId(id);
    }

    @Override
    public List<Activity> queryActivityForDetailByNameContactsId(Map<String, Object> map) {
        return activityMapper.selectActivityForDetailByNameContactsId(map);
    }
}

