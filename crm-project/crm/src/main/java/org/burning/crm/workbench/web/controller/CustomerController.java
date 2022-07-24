package org.burning.crm.workbench.web.controller;

import org.burning.crm.commons.constants.Constant;
import org.burning.crm.commons.domain.ReturnObject;
import org.burning.crm.commons.utils.DateUtils;
import org.burning.crm.commons.utils.UUIDUtils;
import org.burning.crm.settings.domain.User;
import org.burning.crm.settings.service.UserService;
import org.burning.crm.workbench.domain.*;
import org.burning.crm.workbench.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
public class CustomerController {
    @Autowired
    private UserService userService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRemarkService customerRemarkService;

    @Autowired
    private ContactsService contactsService;

    @Autowired
    private TranService tranService;

    @RequestMapping("/workbench/customer/index.do")
    public String index(HttpServletRequest request){
        List<User> userList = userService.queryAllUsers();
        request.setAttribute("userList",userList);
        return "workbench/customer/index";
    }

    @RequestMapping("/workbench/customer/saveCreateCustomer.do")
    public @ResponseBody
    Object saveCreateCustomer(Customer customer, HttpSession session) {
        User user = (User) session.getAttribute(Constant.SESSION_USER);
        customer.setCreateBy(user.getId());
        customer.setCreateTime(DateUtils.formatDateTime(new Date()));
        customer.setId(UUIDUtils.getUUID());

        ReturnObject returnObject = new ReturnObject();
        try {
            int ret = customerService.saveCreateCustomer(customer);
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

    @RequestMapping("/workbench/customer/queryCustomerByConditionForPage.do")
    public @ResponseBody
    Object queryCustomerByConditionForPage(String name, String owner, String phone, String website, int pageNo, int pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("owner", owner);
        map.put("phone", phone);
        map.put("website", website);
        map.put("beginNo", (pageNo-1)*pageSize);
        map.put("pageSize", pageSize);

        List<Customer> customerList = customerService.queryCustomerByConditionForPage(map);
        int totalRows = customerService.queryCountOfCustomerByCondition(map);
        Map<String, Object> retMap = new HashMap<>();
        retMap.put("customerList",customerList);
        retMap.put("totalRows",totalRows);

        return retMap;
    }

    @RequestMapping("/workbench/customer/deleteByIds.do")
    public @ResponseBody Object deleteByIds(String[] id){
        ReturnObject returnObject = new ReturnObject();
        try {
            int ret = customerService.deleteCustomerByIds(id);
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

    @RequestMapping("/workbench/customer/queryCustomerById.do")
    public @ResponseBody
    Object queryCustomerById(String id) {
        Customer customer = customerService.queryCustomerById(id);
        return customer;
    }

    @RequestMapping("/workbench/customer/saveEditCustomer.do")
    public @ResponseBody
    Object saveEditCustomer(Customer customer, HttpSession session) {
        customer.setEditTime(DateUtils.formatDateTime(new Date()));
        User user = (User) session.getAttribute(Constant.SESSION_USER);
        customer.setEditBy(user.getId());

        ReturnObject returnObject = new ReturnObject();
        try {
            int ret = customerService.saveEditCustomer(customer);
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

    @RequestMapping("/workbench/customer/queryCustomerForDetailById.do")
    public String queryCustomerForDetailById(String id,HttpServletRequest request) {
        Customer customer = customerService.queryCustomerForDetailById(id);
        List<CustomerRemark> customerRemarkList =  customerRemarkService.queryCustomerRemarkForDetailByCustomerId(id);
        List<Tran> tranList = tranService.queryTranByCustomerId(id);
        for (Tran tran:tranList) {
            tran.setPossibility(ResourceBundle.getBundle("possibility").getString(tran.getStage()));
        }
        List<Contacts> contactsList = contactsService.queryContactsByCustomerId(id);

        request.setAttribute("customer",customer);
        request.setAttribute("remarkList",customerRemarkList);
        request.setAttribute("tranList",tranList);
        request.setAttribute("contactsList",contactsList);
        return "workbench/customer/detail";
    }

    @RequestMapping("/workbench/customer/saveCreateCustomerRemark.do")
    public @ResponseBody Object saveCreateCustomerRemark(CustomerRemark remark, HttpSession session){
        User user = (User) session.getAttribute(Constant.SESSION_USER);
        //封装参数
        remark.setId(UUIDUtils.getUUID());
        remark.setCreateTime(DateUtils.formatDateTime(new Date()));
        remark.setCreateBy(user.getId());
        remark.setEditFlag(Constant.REMARK_EDIT_FLAG_NO_EDITED);

        ReturnObject returnObject = new ReturnObject();
        try {
            //调用service层方法，保存创建的客户备注
            int ret = customerRemarkService.saveCreateCustomerRemark(remark);

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

    @RequestMapping("/workbench/customer/deleteCustomerRemarkById.do")
    public @ResponseBody Object deleteCustomerRemarkById(String id){
        ReturnObject returnObject = new ReturnObject();
        try {
            //调用service层方法，删除备注
            int ret = customerRemarkService.deleteCustomerRemarkById(id);
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

    @RequestMapping("/workbench/customer/saveEditCustomerRemark.do")
    public @ResponseBody Object saveEditCustomerRemark(CustomerRemark remark,HttpSession session){
        User user = (User) session.getAttribute(Constant.SESSION_USER);

        //封装参数
        remark.setEditTime(DateUtils.formatDateTime(new Date()));
        remark.setEditBy(user.getId());
        remark.setEditFlag(Constant.REMARK_EDIT_FLAG_YSE_EDITED);

        ReturnObject returnObject = new ReturnObject();
        try {
            //调用service层方法，保存修改的客户备注
            int ret = customerRemarkService.saveEditCustomerRemark(remark);
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
}
