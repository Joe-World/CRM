package org.burning.crm.workbench.web.controller;

import org.burning.crm.commons.constants.Constant;
import org.burning.crm.commons.domain.ReturnObject;
import org.burning.crm.commons.utils.DateUtils;
import org.burning.crm.commons.utils.UUIDUtils;
import org.burning.crm.settings.domain.DicValue;
import org.burning.crm.settings.domain.User;
import org.burning.crm.settings.service.DicValueService;
import org.burning.crm.settings.service.UserService;
import org.burning.crm.workbench.domain.*;
import org.burning.crm.workbench.service.ActivityService;
import org.burning.crm.workbench.service.ClueActivityRelationService;
import org.burning.crm.workbench.service.ClueRemarkService;
import org.burning.crm.workbench.service.ClueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
public class ClueController {
    @Autowired
    private UserService userService;

    @Autowired
    private DicValueService dicValueService;

    @Autowired
    private ClueService clueService;

    @Autowired
    private ClueRemarkService clueRemarkService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ClueActivityRelationService clueActivityRelationService;

    @RequestMapping("/workbench/clue/index.do")
    public String index(HttpServletRequest request) {
        //调用service层，查询所有的动态数据
        List<User> userList = userService.queryAllUsers();
        List<DicValue> appellationList = dicValueService.queryDicValueByTypeCode("appellation");
        List<DicValue> clueStateList = dicValueService.queryDicValueByTypeCode("clueState");
        List<DicValue> sourceList = dicValueService.queryDicValueByTypeCode("source");

        //把数据保存到request中
        request.setAttribute("userList", userList);
        request.setAttribute("appellationList", appellationList);
        request.setAttribute("clueStateList", clueStateList);
        request.setAttribute("sourceList", sourceList);
        //请求转发
        return "workbench/clue/index";
    }

    @RequestMapping("/workbench/clue/saveCreateClue.do")
    public @ResponseBody
    Object saveCreateClue(Clue clue, HttpSession session) {
        User user = (User) session.getAttribute(Constant.SESSION_USER);

        //封装参数
        clue.setId(UUIDUtils.getUUID());
        clue.setCreateTime(DateUtils.formatDateTime(new Date()));
        clue.setCreateBy(user.getId());

        //调用service层方法，保存创建的线索
        ReturnObject returnObject = new ReturnObject();
        try {
            int ret = clueService.saveCreateClue(clue);
            if (ret > 0) {
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_SUCCESS);
            } else {
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("系统忙，请稍后重试");
            }
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Constant.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统忙，请稍后重试");
        }

        return returnObject;
    }

    @RequestMapping("/workbench/clue/queryClueByConditionForPage.do")
    public @ResponseBody
    Object queryClueByConditionForPage(String name, String company, String phone, String source, String owner, String mphone, String state, int pageNo, int pageSize) {
        Map<String, Object> map = new HashMap<>();

        map.put("name", name);
        map.put("company", company);
        map.put("phone", phone);
        map.put("source", source);
        map.put("owner", owner);
        map.put("mphone", mphone);
        map.put("state", state);
        map.put("beginNo", (pageNo - 1) * pageSize);
        map.put("pageSize", pageSize);

        List<Clue> clueList = clueService.queryClueByConditionForPage(map);
        int totalRows = clueService.queryCountOfClueByCondition(map);
        Map<String, Object> retMap = new HashMap<>();
        retMap.put("clueList", clueList);
        retMap.put("totalRows", totalRows);
        return retMap;
    }

    @RequestMapping("/workbench/clue/deleteByIds.do")
    public @ResponseBody
    Object deleteByIds(String[] id) {
        ReturnObject returnObject = new ReturnObject();
        try {
            int ret = clueService.deleteClueByIds(id);
            if (ret > 0) {
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_SUCCESS);
            } else {
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("系统忙，请稍后重试");
            }
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Constant.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统忙，请稍后重试");
        }

        return returnObject;
    }

    @RequestMapping("/workbench/clue/queryClueById.do")
    public @ResponseBody
    Object queryClueById(String id) {
        Clue clue = clueService.queryClueById(id);
        return clue;
    }

    @RequestMapping("/workbench/clue/saveEditClue.do")
    public @ResponseBody
    Object saveEditClue(Clue clue, HttpSession session) {
        User user = (User) session.getAttribute(Constant.SESSION_USER);
        //封装参数
        clue.setEditTime(DateUtils.formatDateTime(new Date()));
        clue.setEditBy(user.getId());

        ReturnObject returnObject = new ReturnObject();
        try {
            //调用service层方法，保存修改的市场活动
            int ret = clueService.saveEditClue(clue);
            if (ret > 0) {
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_SUCCESS);
            } else {
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("系统忙，请稍后重试...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Constant.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统忙，请稍后重试...");
        }
        return returnObject;
    }

    @RequestMapping("/workbench/clue/detailClue.do")
    public String detailClue(String id, HttpServletRequest request) {
        //调用service层方法，查询数据
        Clue clue = clueService.queryClueForDetailById(id);
        List<ClueRemark> remarkList = clueRemarkService.queryClueRemarkForDetailByClueId(id);
        List<Activity> activityList = activityService.queryActivityForDetailByClueId(id);

        //把数据保存到request中
        request.setAttribute("clue", clue);
        request.setAttribute("remarkList", remarkList);
        request.setAttribute("activityList", activityList);

        //请求转发
        return "workbench/clue/detail";
    }

    @RequestMapping("/workbench/clue/saveBund.do")
    public @ResponseBody
    Object saveBund(String[] activityId, String clueId) {
        //封装参数
        ClueActivityRelation car = null;
        List<ClueActivityRelation> list = new ArrayList<>();
        for (String id : activityId) {
            car = new ClueActivityRelation();
            car.setId(UUIDUtils.getUUID());
            car.setClueId(clueId);
            car.setActivityId(id);
            list.add(car);
        }

        ReturnObject returnObject = new ReturnObject();
        try {
            //调用service方法，批量保存线索和市场活动的关联关系
            int ret = clueActivityRelationService.saveCreateClueActivityRelationByList(list);
            if (ret > 0) {
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_SUCCESS);
                List<Activity> activityList = activityService.queryActivityForDetailByIds(activityId);
                returnObject.setRetData(activityList);
            } else {
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("系统忙，请稍后重试...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Constant.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统忙，请稍后重试...");
        }
        return returnObject;
    }

    @RequestMapping("/workbench/clue/saveUnbund.do")
    public @ResponseBody
    Object saveUnbund(ClueActivityRelation relation) {
        ReturnObject returnObject = new ReturnObject();
        try {
            //调用service层方法，删除线索和市场活动的关联关系
            int ret = clueActivityRelationService.deleteClueActivityRelationByClueIdActivityId(relation);
            if (ret > 0) {
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_SUCCESS);
            } else {
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("系统忙，请稍后重试...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Constant.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统忙，请稍后重试...");
        }

        return returnObject;
    }

    @RequestMapping("/workbench/clue/toConvert.do")
    public String toConvert(String clueId, HttpServletRequest request) {
        //调用service层方法，查询线索的明细信息
        Clue clue = clueService.queryClueForDetailById(clueId);
        List<DicValue> stageList = dicValueService.queryDicValueByTypeCode("stage");
        //把数据保存到request中
        request.setAttribute("clue", clue);
        request.setAttribute("stageList", stageList);
        //请求转发
        return "workbench/clue/convert";
    }

    @RequestMapping("/workbench/clue/queryActivityForConvertByNameClueId.do")
    public @ResponseBody
    Object queryActivityForConvertByNameClueId(String activityName, String clueId) {
        Map<String, Object> map = new HashMap<>();
        map.put("activityName", activityName);
        map.put("clueId", clueId);

        List<Activity> activityList = activityService.queryActivityForConvertByNameClueId(map);

        return activityList;
    }

    @RequestMapping("workbench/clue/convertClue.do")
    public @ResponseBody
    Object convertClue(String clueId,String money,String name,String expectedDate,String stage,String activityId,String isCreateTran,HttpSession session) {
        //封装参数
        Map<String,Object> map=new HashMap<>();
        map.put("clueId",clueId);
        map.put("money",money);
        map.put("name",name);
        map.put("expectedDate",expectedDate);
        map.put("stage",stage);
        map.put("activityId",activityId);
        map.put("isCreateTran",isCreateTran);
        map.put(Constant.SESSION_USER,session.getAttribute(Constant.SESSION_USER));

        ReturnObject returnObject=new ReturnObject();
        try {
            //调用service层方法，保存线索转换
            clueService.saveConvertClue(map);
            returnObject.setCode(Constant.RETURN_OBJECT_CODE_SUCCESS);
        }catch (Exception e){
            e.printStackTrace();
            returnObject.setCode(Constant.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统忙，请稍后重试....");
        }

        return returnObject;
    }

    @RequestMapping("/workbench/clue/saveCreateClueRemark.do")
    public @ResponseBody Object saveCreateClueRemark(ClueRemark remark, HttpSession session){
        User user = (User) session.getAttribute(Constant.SESSION_USER);
        //封装参数
        remark.setId(UUIDUtils.getUUID());
        remark.setCreateTime(DateUtils.formatDateTime(new Date()));
        remark.setCreateBy(user.getId());
        remark.setEditFlag(Constant.REMARK_EDIT_FLAG_NO_EDITED);

        ReturnObject returnObject = new ReturnObject();
        try {
            //调用service层方法，保存创建的线索备注
            int ret = clueRemarkService.saveCreateClueRemark(remark);

            if (ret > 0) {
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_SUCCESS);
                returnObject.setRetData(remark);
            } else {
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("系统忙，请稍后重试...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Constant.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统忙，请稍后重试...");
        }

        return returnObject;
    }

    @RequestMapping("/workbench/clue/deleteClueRemarkById.do")
    public @ResponseBody Object deleteClueRemarkById(String id){
        ReturnObject returnObject = new ReturnObject();
        try {
            //调用service层方法，删除备注
            int ret = clueRemarkService.deleteClueRemarkById(id);
            if (ret > 0) {
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_SUCCESS);
            } else {
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("系统忙，请稍后重试..,");
            }
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Constant.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统忙，请稍后重试..,");
        }

        return returnObject;
    }

    @RequestMapping("/workbench/clue/saveEditClueRemark.do")
    public @ResponseBody Object saveEditClueRemark(ClueRemark remark,HttpSession session){
        User user = (User) session.getAttribute(Constant.SESSION_USER);

        //封装参数
        remark.setEditTime(DateUtils.formatDateTime(new Date()));
        remark.setEditBy(user.getId());
        remark.setEditFlag(Constant.REMARK_EDIT_FLAG_YSE_EDITED);

        ReturnObject returnObject = new ReturnObject();
        try {
            //调用service层方法，保存修改的线索备注
            int ret = clueRemarkService.saveEditClueRemark(remark);
            if (ret > 0) {
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_SUCCESS);
                returnObject.setRetData(remark);
            } else {
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("系统忙，请稍后重试..,");
            }
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Constant.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统忙，请稍后重试..,");
        }

        return returnObject;
    }

    @RequestMapping("/workbench/clue/queryActivityForDetailByNameClueId.do")
    public @ResponseBody Object queryActivityForDetailByNameClueId(String activityName,String clueId){
        //封装参数
        Map<String, Object> map = new HashMap<>();
        map.put("activityName",activityName );
        map.put("clueId",clueId );
        //调用service层方法，查询数据
        List<Activity> activityList = activityService.queryActivityForDetailByNameClueId(map);
        //根据查询结果返回响应信息
        return activityList;
    }
}
