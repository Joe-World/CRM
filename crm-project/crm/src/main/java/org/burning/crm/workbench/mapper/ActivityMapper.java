package org.burning.crm.workbench.mapper;

import org.burning.crm.workbench.domain.Activity;

import java.util.List;
import java.util.Map;

public interface ActivityMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_activity
     *
     * @mbggenerated Thu Jun 30 16:41:15 CST 2022
     */
    int deleteByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_activity
     *
     * @mbggenerated Thu Jun 30 16:41:15 CST 2022
     */
    int insert(Activity record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_activity
     *
     * @mbggenerated Thu Jun 30 16:41:15 CST 2022
     */
    int insertSelective(Activity record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_activity
     *
     * @mbggenerated Thu Jun 30 16:41:15 CST 2022
     */
    Activity selectByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_activity
     *
     * @mbggenerated Thu Jun 30 16:41:15 CST 2022
     */
    int updateByPrimaryKeySelective(Activity record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_activity
     *
     * @mbggenerated Thu Jun 30 16:41:15 CST 2022
     */
    int updateByPrimaryKey(Activity record);

    /**
     * 保存创建的市场活动
     * @param activity
     * @return
     */
    int insertActivity(Activity activity);

    /**
     * 根据条件分页查询市场活动的列表
     * @return
     */
    List<Activity> selectActivityByConditionForPage(Map<String,Object> map);

    /**
     * 根据条件查询市场活动的总条数
     * @param map
     * @return
     */
    int selectCountOfActivityByCondition(Map<String, Object> map);

    /**
     * 根据id数组批量删除市场活动
     * @param ids
     * @return
     */
    int deleteActivityByIds(String[] ids);

    /**
     *根据id查询市场活动的信息
     * @param id
     * @return
     */
    Activity selectActivityById(String id);

    /**
     * 保存修改的市场活动
     * @param activity
     * @return
     */
    int updateActivity(Activity activity);

    /**
     * 查询所有的市场活动
     * @return
     */
    List<Activity> selectAllActivities();

    /**
     * 选择导出市场活动
     * @return
     */
    List<Activity> selectChooseActivities(String[] ids);

    /**
     * 批量保存创建的市场活动
     *
     * @param activityList
     * @return
     */
    int insertActivitiesByList(List<Activity> activityList);

    /**
     * 根据id查询市场活动明细信息
     * @param id
     * @return
     */
    Activity selectActivityForDetailById(String id);

    /**
     * 根据clueId查询该线索关联的所有市场活动的明细信息
     * @param id
     * @return
     */
    List<Activity> selectActivityForDetailByClueId(String id);

    /**
     * 根据name模糊查询市场活动，并且把已经根clueId关联过的市场活动排除
     * @param map
     * @return
     */
    List<Activity> selectActivityForDetailByNameClueId(Map<String,Object> map);

    /**
     * 根据ids查询市场活动的明细信息
     * @param id
     * @return
     */
    List<Activity> selectActivityForDetailByIds(String[] id);

    /**
     * 根据name模糊查询市场活动，并且查询和此clueId关联过的市场活动明细
     * @param map
     * @return
     */
    List<Activity> selectActivityForConvertByNameClueId(Map<String ,Object>map);

    /**
     * 根据市场活动名称模糊查询市场活动
     * @param name
     * @return
     */
    List<Activity> selectActivityForSaveByName(String name);

    /**
     * 根据联系人id查询与其相关的市场活动
     * @param id
     * @return
     */
    List<Activity> selectActivityForContactsDetailByContactsId(String id);

    /**
     * 根据name模糊查询市场活动，并且把已经根contactId关联过的市场活动排除
     * @param map
     * @return
     */
    List<Activity> selectActivityForDetailByNameContactsId(Map<String,Object> map);

}