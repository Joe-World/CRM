package org.burning.crm.workbench.web.controller;

import org.burning.crm.commons.constants.Constant;
import org.burning.crm.commons.domain.ReturnObject;
import org.burning.crm.commons.utils.DateUtils;
import org.burning.crm.commons.utils.UUIDUtils;
import org.burning.crm.settings.domain.DicValue;
import org.burning.crm.settings.domain.User;
import org.burning.crm.settings.mapper.DicValueMapper;
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
public class TranController {
    @Autowired
    private DicValueService dicValueService;

    @Autowired
    private UserService userService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ContactsService contactsService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private TranService tranService;

    @Autowired
    private TranRemarkService tranRemarkService;

    @Autowired
    private TranHistoryService tranHistoryService;

    @RequestMapping("/workbench/transaction/index.do")
    public String index(HttpServletRequest request){
        //调用service层方法，查询动态数据
        List<DicValue> transactionTypeList =dicValueService.queryDicValueByTypeCode("transactionType");
        List<DicValue> sourceList =dicValueService.queryDicValueByTypeCode("source");
        List<DicValue> stageList =dicValueService.queryDicValueByTypeCode("stage");

        request.setAttribute("transactionTypeList",transactionTypeList);
        request.setAttribute("sourceList",sourceList);
        request.setAttribute("stageList",stageList);
        return "workbench/transaction/index";
    }

    @RequestMapping("/workbench/transaction/toSave.do")
    public String toSave(HttpServletRequest request){
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
        //请求转发
        return "workbench/transaction/save";
    }

    @RequestMapping("/workbench/transaction/queryActivityForSaveByName.do")
    public @ResponseBody
    Object queryActivityForSaveByName(String activityName) {
        List<Activity> activityList = activityService.queryActivityForSaveByName(activityName);

        return activityList;
    }

    @RequestMapping("workbench/transaction/queryContactsForSaveByName.do")
    public @ResponseBody
    Object queryContactsForSaveByName(String contactsName) {
        List<Contacts> contactList = contactsService.queryContactsForSaveByName(contactsName);

        return contactList;
    }

    @RequestMapping("workbench/transaction/queryPossibilityByStageValue.do")
    public @ResponseBody Object queryPossibilityByStageValue(String stageValue){
        //解析properties配置文件，根据阶段获取可能性
        ResourceBundle bundle = ResourceBundle.getBundle("possibility");
        String possibility = bundle.getString(stageValue);

        //返回响应信息
        return possibility;
    }

    @RequestMapping("workbench/transaction/queryCustomerNamesByName.do")
    public @ResponseBody
    Object queryAllCustomerName(String customerName) {
        String[] customerNames = customerService.queryCustomerNamesByName(customerName);

        return customerNames;
    }

    @RequestMapping("workbench/transaction/saveCreateTran.do")
    public @ResponseBody
    Object saveCreateTran(@RequestParam Map<String,Object> map, HttpSession session) {
        map.put(Constant.SESSION_USER, session.getAttribute(Constant.SESSION_USER));
        ReturnObject returnObject = new ReturnObject();
        try {
            tranService.saveCreateTran(map);
            returnObject.setCode(Constant.RETURN_OBJECT_CODE_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Constant.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统忙，请稍后重试...");
        }

        return returnObject;
    }

    @RequestMapping("workbench/transaction/detailTran.do")
    public String detailTran(String tranId,HttpServletRequest request){
        Tran tran  = tranService.queryTranForDetailById(tranId);
        List<TranRemark> remarkList = tranRemarkService.queryTranRemarkForDetailByTranId(tranId);
        List<TranHistory> historyList = tranHistoryService.queryTranHistoryForDetailByTranId(tranId);
        List<DicValue> stageList = dicValueService.queryDicValueByTypeCode("stage");

        ResourceBundle bundle = ResourceBundle.getBundle("possibility");
        tran.setPossibility(bundle.getString(tran.getStage()));

        request.setAttribute("tran",tran);
        request.setAttribute("remarkList",remarkList);
        request.setAttribute("historyList",historyList);
        request.setAttribute("stageList",stageList);
        return "workbench/transaction/detail";
    }

    @RequestMapping("/workbench/tran/saveCreateTranRemark.do")
    public @ResponseBody Object saveCreateTranRemark(TranRemark remark, HttpSession session){
        User user = (User) session.getAttribute(Constant.SESSION_USER);
        //封装参数
        remark.setId(UUIDUtils.getUUID());
        remark.setCreateTime(DateUtils.formatDateTime(new Date()));
        remark.setCreateBy(user.getId());
        remark.setEditFlag(Constant.REMARK_EDIT_FLAG_NO_EDITED);

        ReturnObject returnObject = new ReturnObject();
        try {
            //调用service层方法，保存创建的交易备注
            int ret = tranRemarkService.saveCreateTranRemark(remark);

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

    @RequestMapping("/workbench/tran/deleteTranRemarkById.do")
    public @ResponseBody Object deleteTranRemarkById(String id){
        ReturnObject returnObject = new ReturnObject();
        try {
            //调用service层方法，删除备注
            int ret = tranRemarkService.deleteTranRemarkById(id);
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

    @RequestMapping("/workbench/tran/saveEditTranRemark.do")
    public @ResponseBody Object saveEditTranRemark(TranRemark remark,HttpSession session){
        User user = (User) session.getAttribute(Constant.SESSION_USER);

        //封装参数
        remark.setEditTime(DateUtils.formatDateTime(new Date()));
        remark.setEditBy(user.getId());
        remark.setEditFlag(Constant.REMARK_EDIT_FLAG_YSE_EDITED);

        ReturnObject returnObject = new ReturnObject();
        try {
            //调用service层方法，保存修改的交易备注
            int ret = tranRemarkService.saveEditTranRemark(remark);
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

    @RequestMapping("/workbench/tran/deleteById.do")
    public @ResponseBody
    Object deleteById(String id) {
        ReturnObject returnObject = new ReturnObject();
        try {
            //调用service层方法，删除市场活动
            int ret = tranService.deleteTranById(id);
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

    @RequestMapping("/workbench/tran/queryTranByConditionForPage.do")
    public @ResponseBody
    Object queryTranByConditionForPage(String name, String owner, String customerId, String source,  String stage, String type,String contactsId,int pageNo, int pageSize) {
        Map<String, Object> map = new HashMap<>();

        map.put("name", name);
        map.put("customerId", customerId);
        map.put("type", type);
        map.put("source", source);
        map.put("owner", owner);
        map.put("contactsId", contactsId);
        map.put("stage", stage);
        map.put("beginNo", (pageNo - 1) * pageSize);
        map.put("pageSize", pageSize);

        List<Tran> tranList = tranService.queryTranByConditionForPage(map);
        int totalRows = tranService.queryCountOfTranByCondition(map);
        Map<String, Object> retMap = new HashMap<>();
        retMap.put("tranList", tranList);
        retMap.put("totalRows", totalRows);
        return retMap;
    }

    @RequestMapping("/workbench/tran/deleteByIds.do")
    public @ResponseBody
    Object deleteByIds(String[] id) {
        ReturnObject returnObject = new ReturnObject();
        try {
            int ret = tranService.deleteTranByIds(id);
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

    @RequestMapping("/workbench/tran/toEdit.do")
    public String toEdit(String id,HttpServletRequest request) {
        //调用service层方法，查询动态数据
        List<User> userList=userService.queryAllUsers();
        List<DicValue> stageList=dicValueService.queryDicValueByTypeCode("stage");
        List<DicValue> transactionTypeList=dicValueService.queryDicValueByTypeCode("transactionType");
        List<DicValue> sourceList=dicValueService.queryDicValueByTypeCode("source");
        Tran tran = tranService.queryTranById(id);

        //把数据保存到request中
        request.setAttribute("userList",userList);
        request.setAttribute("stageList",stageList);
        request.setAttribute("transactionTypeList",transactionTypeList);
        request.setAttribute("sourceList",sourceList);
        request.setAttribute("tran",tran);
        //请求转发
        return "workbench/transaction/edit";
    }
}
