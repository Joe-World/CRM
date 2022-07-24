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
import org.burning.crm.workbench.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
public class ContactsController {
    @Autowired
    private DicValueService dicValueService;

    @Autowired
    private ContactsService contactsService;

    @Autowired
    private UserService userService;

    @Autowired
    private ContactsRemarkService contactsRemarkService;

    @Autowired
    private TranService tranService;

    @Autowired
    private ClueActivityRelationService clueActivityRelationService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ContactsActivityRelationService contactsActivityRelationService;

    @RequestMapping("/workbench/contacts/index.do")
    public String index(HttpServletRequest request) {
        List<User> userList = userService.queryAllUsers();
        List<DicValue> sourceList = dicValueService.queryDicValueByTypeCode("source");
        List<DicValue> appellationList = dicValueService.queryDicValueByTypeCode("appellation");

        request.setAttribute("userList", userList);
        request.setAttribute("sourceList", sourceList);
        request.setAttribute("appellationList", appellationList);
        return "workbench/contacts/index";
    }

    @RequestMapping("/workbench/contacts/saveCreateContacts.do")
    public @ResponseBody
    Object saveCreateContacts(@RequestParam Map<String, Object> map, HttpSession session) {
        User user = (User) session.getAttribute(Constant.SESSION_USER);
        map.put(Constant.SESSION_USER, user);

        ReturnObject returnObject = new ReturnObject();
        try {
            //调用service层方法，保存创建的市场活动
            contactsService.saveCreateContacts(map);
            returnObject.setCode(Constant.RETURN_OBJECT_CODE_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Constant.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统忙，请稍后再试...");
        }

        return returnObject;
    }

    @RequestMapping("/workbench/contacts/queryContactsByConditionForPage.do")
    public @ResponseBody
    Object queryContactsByConditionForPage(String owner, String fullname, String customerId, String source, int pageNo, int pageSize) {
        Map<String, Object> map = new HashMap<>();

        map.put("owner", owner);
        map.put("fullname", fullname);
        map.put("customerId", customerId);
        map.put("source", source);
        map.put("beginNo", (pageNo - 1) * pageSize);
        map.put("pageSize", pageSize);

        List<Contacts> contactsList = contactsService.queryContactsByConditionForPage(map);
        int totalRows = contactsService.queryCountOfContactsByCondition(map);
        Map<String, Object> retMap = new HashMap<>();
        retMap.put("contactsList", contactsList);
        retMap.put("totalRows", totalRows);
        return retMap;
    }

    @RequestMapping("/workbench/contacts/deleteByIds.do")
    public @ResponseBody
    Object deleteByIds(String[] id) {
        ReturnObject returnObject = new ReturnObject();
        try {
            int ret = contactsService.deleteContactsByIds(id);
            if (ret > 0) {
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_SUCCESS);
            } else {
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("系统繁忙，请稍后重试...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Constant.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统繁忙，请稍后重试...");
        }

        return returnObject;
    }

    @RequestMapping("/workbench/contacts/queryContactsById.do")
    public @ResponseBody
    Object queryContactsById(String id) {
        Contacts contacts = contactsService.queryContactsById(id);
        return contacts;
    }

    @RequestMapping("/workbench/contacts/saveEditContacts.do")
    public @ResponseBody
    Object saveEditContacts(@RequestParam Map<String, Object> map, HttpSession session) {
        User user = (User) session.getAttribute(Constant.SESSION_USER);
        map.put(Constant.SESSION_USER, user);

        ReturnObject returnObject = new ReturnObject();
        try {
            contactsService.saveEditContacts(map);
            returnObject.setCode(Constant.RETURN_OBJECT_CODE_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Constant.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统忙，请稍后重试...");
        }
        return returnObject;
    }

    @RequestMapping("/workbench/contacts/detailContacts.do")
    public String detailContacts(String id,HttpServletRequest request) {
        Contacts contacts = contactsService.queryContactsForDetailById(id);
        List<ContactsRemark> remarkList = contactsRemarkService.queryContactsRemarkForDetailByContactsId(id);
        List<Tran> tranList = tranService.queryTranByContactsId(id);
        List<Activity> activityList = activityService.queryActivityForContactsDetailByContactsId(id);
        if (tranList != null) {
            for (Tran tran : tranList) {
                tran.setPossibility(ResourceBundle.getBundle("possibility").getString(tran.getStage()));
            }
        }

        request.setAttribute("contacts", contacts);
        request.setAttribute("remarkList", remarkList);
        request.setAttribute("tranList", tranList);
        request.setAttribute("activityList", activityList);

        return "workbench/contacts/detail";
    }

    @RequestMapping("/workbench/contacts/saveCreateContactsRemark.do")
    public @ResponseBody Object saveCreateContactsRemark(ContactsRemark remark, HttpSession session){
        User user = (User) session.getAttribute(Constant.SESSION_USER);
        //封装参数
        remark.setId(UUIDUtils.getUUID());
        remark.setCreateTime(DateUtils.formatDateTime(new Date()));
        remark.setCreateBy(user.getId());
        remark.setEditFlag(Constant.REMARK_EDIT_FLAG_NO_EDITED);

        ReturnObject returnObject = new ReturnObject();
        try {
            //调用service层方法，保存创建的联系人备注
            int ret = contactsRemarkService.saveCreateContactsRemark(remark);

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

    @RequestMapping("/workbench/contacts/deleteContactsRemarkById.do")
    public @ResponseBody Object deleteContactsRemarkById(String id){
        ReturnObject returnObject = new ReturnObject();
        try {
            //调用service层方法，删除备注
            int ret = contactsRemarkService.deleteContactsRemarkById(id);
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

    @RequestMapping("/workbench/contacts/saveEditContactsRemark.do")
    public @ResponseBody Object saveEditContactsRemark(ContactsRemark remark,HttpSession session){
        User user = (User) session.getAttribute(Constant.SESSION_USER);

        //封装参数
        remark.setEditTime(DateUtils.formatDateTime(new Date()));
        remark.setEditBy(user.getId());
        remark.setEditFlag(Constant.REMARK_EDIT_FLAG_YSE_EDITED);

        ReturnObject returnObject = new ReturnObject();
        try {
            //调用service层方法，保存修改的联系人备注
            int ret = contactsRemarkService.saveEditContactsRemark(remark);
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


    @RequestMapping("/workbench/contacts/queryActivityForDetailByNameContactsId.do")
    public @ResponseBody Object queryActivityForDetailByNameContactsId(String activityName,String contactsId){
        //封装参数
        Map<String, Object> map = new HashMap<>();
        map.put("activityName",activityName );
        map.put("contactsId",contactsId );
        //调用service层方法，查询数据
        List<Activity> activityList = activityService.queryActivityForDetailByNameContactsId(map);
        //根据查询结果返回响应信息
        return activityList;
    }

    @RequestMapping("/workbench/contacts/saveBund.do")
    public @ResponseBody
    Object saveBund(String[] activityId, String contactsId) {
        //封装参数
        ContactsActivityRelation car = null;
        List<ContactsActivityRelation> list = new ArrayList<>();
        for (String id : activityId) {
            car = new ContactsActivityRelation();
            car.setId(UUIDUtils.getUUID());
            car.setContactsId(contactsId);
            car.setActivityId(id);
            list.add(car);
        }

        ReturnObject returnObject = new ReturnObject();
        try {
            //调用service方法，批量保存联系人和市场活动的关联关系
            int ret = contactsActivityRelationService.saveCreateContactsActivityRelationByList(list);
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
    
    @RequestMapping("/workbench/contacts/saveUnbund.do")
    public @ResponseBody
    Object saveUnbund(ContactsActivityRelation relation) {
        ReturnObject returnObject = new ReturnObject();
        try {
            //调用service层方法，删除联系人和市场活动的关联关系
            int ret = contactsActivityRelationService.deleteContactsActivityRelationByContactsIdActivityId(relation);
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

    @RequestMapping("/workbench/contacts/toSave.do")
    public String toSave(String contactsId,String fullname,String appellation,HttpServletRequest request){
        //调用service层方法，查询动态数据
        List<User> userList=userService.queryAllUsers();
        List<DicValue> stageList=dicValueService.queryDicValueByTypeCode("stage");
        List<DicValue> transactionTypeList=dicValueService.queryDicValueByTypeCode("transactionType");
        List<DicValue> sourceList=dicValueService.queryDicValueByTypeCode("source");
        //把数据保存到request中
        request.setAttribute("userList",userList);
        request.setAttribute("stageList",stageList);
        request.setAttribute("transactionTypeList",transactionTypeList);
        request.setAttribute("sourceList",sourceList);
        String name = fullname;
        if (appellation != null) {
            name += appellation;
        }
        request.setAttribute("id",contactsId);
        request.setAttribute("name",name);
        //请求转发
        return "workbench/transaction/save";
    }
}
