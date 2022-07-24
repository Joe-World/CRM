package org.burning.crm.workbench.web.controller;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.burning.crm.commons.constants.Constant;
import org.burning.crm.commons.domain.ReturnObject;
import org.burning.crm.commons.utils.DateUtils;
import org.burning.crm.commons.utils.HSSFUtils;
import org.burning.crm.commons.utils.UUIDUtils;
import org.burning.crm.settings.domain.User;
import org.burning.crm.settings.service.UserService;
import org.burning.crm.workbench.domain.Activity;
import org.burning.crm.workbench.domain.ActivityRemark;
import org.burning.crm.workbench.service.ActivityRemarkService;
import org.burning.crm.workbench.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.*;

@Controller
public class ActivityController {
    @Autowired
    private UserService userService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ActivityRemarkService activityRemarkService;

    @RequestMapping("/workbench/activity/index.do")
    public String index(HttpServletRequest request) {
        //调用service层方法，查询所有的用户
        List<User> userList = userService.queryAllUsers();

        //把数据保存到request中
        request.setAttribute("userList", userList);

        //请求转发到市场活动的主页面
        return "workbench/activity/index";
    }

    @RequestMapping("/workbench/activity/saveCreateActivity.do")
    public @ResponseBody
    Object saveCreateActivity(Activity activity, HttpSession session) {
        User user = (User) session.getAttribute(Constant.SESSION_USER);
        //封装参数
        activity.setId(UUIDUtils.getUUID());
        activity.setCreateTime(DateUtils.formatDateTime(new Date()));
        activity.setCreateBy(user.getId());

        ReturnObject returnObject = new ReturnObject();
        try {
            //调用service层方法，保存创建的市场活动
            int ret = activityService.saveCreateActivity(activity);
            if (ret > 0) {
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_SUCCESS);
            } else {
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("系统忙，请稍后再试...");
            }

        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Constant.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统忙，请稍后再试...");
        }

        return returnObject;
    }

    @RequestMapping("/workbench/activity/queryActivityByConditionForPage.do")
    public @ResponseBody
    Object queryActivityByConditionForPage(String name, String owner, String startDate, String endDate, int pageNo, int pageSize) {
        //封装参数
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("owner", owner);
        map.put("startDate", startDate);
        map.put("endDate", endDate);
        map.put("beginNo", (pageNo - 1) * pageSize);
        map.put("pageSize", pageSize);
        //调用service层方法，查询数据
        List<Activity> activityList = activityService.queryActivityByConditionForPage(map);
        int totalRows = activityService.queryCountOfActivityByCondition(map);
        //根据查询结果，生成响应信息
        Map<String, Object> retMap = new HashMap<>();
        retMap.put("activityList", activityList);
        retMap.put("totalRows", totalRows);
        return retMap;
    }

    @RequestMapping("/workbench/activity/deleteActivityByIds.do")
    public @ResponseBody
    Object deleteActivityByIds(String[] id) {
        ReturnObject returnObject = new ReturnObject();
        try {
            //调用service层方法，删除市场活动
            int ret = activityService.deleteActivityByIds(id);
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

    @RequestMapping("/workbench/activity/queryActivityById.do")
    public @ResponseBody
    Object queryActivityById(String id) {
        //调用service层方法，查询市场活动
        Activity activity = activityService.queryActivityById(id);
        //根据查询结果，返回响应信息
        return activity;
    }

    @RequestMapping("/workbench/activity/updateActivityById.do")
    public @ResponseBody
    Object updateActivityById(Activity activity, HttpSession session) {
        //封装参数
        activity.setEditTime(DateUtils.formatDateTime(new Date()));
        User user = (User) session.getAttribute(Constant.SESSION_USER);
        activity.setEditBy(user.getId());

        ReturnObject returnObject = new ReturnObject();
        try {
            //调用service层方法，保存修改的市场活动
            int ret = activityService.saveEditActivity(activity);
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

    @RequestMapping("/workbench/activity/exportAllActivities.do")
    public void exportAllActivities(HttpServletResponse response) throws Exception {
        //调用service方法查询所有的市场活动
        List<Activity> activityList = activityService.queryAllActivities();
        //创建excel文件，并且activityList写入到excel文件中
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("市场活动列表");
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("ID");
        cell = row.createCell(1);
        cell.setCellValue("所有者");
        cell = row.createCell(2);
        cell.setCellValue("名称");
        cell = row.createCell(3);
        cell.setCellValue("开始日期");
        cell = row.createCell(4);
        cell.setCellValue("结束日期");
        cell = row.createCell(5);
        cell.setCellValue("成本");
        cell = row.createCell(6);
        cell.setCellValue("描述");
        cell = row.createCell(7);
        cell.setCellValue("创建时间");
        cell = row.createCell(8);
        cell.setCellValue("创建者");
        cell = row.createCell(9);
        cell.setCellValue("修改时间");
        cell = row.createCell(10);
        cell.setCellValue("修改者");

        Activity activity = null;
        if (activityList != null && activityList.size() > 0) {
            for (int i = 0; i < activityList.size(); i++) {
                activity = activityList.get(i);

                //每遍历出一个activity，生成一行
                row = sheet.createRow(i + 1);

                //每一行创建11列，每一列的数据从activityList中获取
                cell = row.createCell(0);
                cell.setCellValue(activity.getId());
                cell = row.createCell(1);
                cell.setCellValue(activity.getOwner());
                cell = row.createCell(2);
                cell.setCellValue(activity.getName());
                cell = row.createCell(3);
                cell.setCellValue(activity.getStartDate());
                cell = row.createCell(4);
                cell.setCellValue(activity.getEndDate());
                cell = row.createCell(5);
                cell.setCellValue(activity.getCost());
                cell = row.createCell(6);
                cell.setCellValue(activity.getDescription());
                cell = row.createCell(7);
                cell.setCellValue(activity.getCreateTime());
                cell = row.createCell(8);
                cell.setCellValue(activity.getCreateBy());
                cell = row.createCell(9);
                cell.setCellValue(activity.getEditTime());
                cell = row.createCell(10);
                cell.setCellValue(activity.getEditBy());
            }
        }

        /*内存到磁盘，一会还得磁盘到内存，高并发环境下效率太低
        //根据wb对象生成excel文件
        OutputStream os = new FileOutputStream("C:\\java\\Project\\crm\\serverDir\\activityList.xls");
        wb.write(os);
        //关闭资源
        os.close();
        wb.close();
         */

        //把生成的excel文件下载到客户端
        //设置响应类型
        response.setContentType("application/octet-stream;charset=UTF-8");
        /*
            浏览器接受到响应信息之后，默认情况下，直接在显示窗口中打开响应信息；
            即使打不开，也是调用应用程序打开；
            只有实在打不开，才会激活文件下载窗口

            可以设置响应头信息，使浏览器接受到响应信息之后，直接激活文件下载窗口，即使能打开也不打开
         */
        response.addHeader("Content-Disposition", "attachment;filename=activityList.xls");
        OutputStream out = response.getOutputStream();
        //读取excel文件，将其输出到浏览器上
        InputStream is = new FileInputStream("C:\\java\\Project\\crm\\serverDir\\activityList.xls");

        /*
        byte[] buff = new byte[256];
        int len = 0;
        while ((len = is.read(buff)) != -1) {
            out.write(buff,0,len);
        }
        is.close();
         */

        //内存到内存，高并发环境下效率高
        wb.write(out);
        wb.close();
        //Tomcat自己开的流自己关
        out.flush();
        return;
    }

    @RequestMapping("/workbench/activity/exportChooseActivities.do")
    public void exportChooseActivities(String[] id, HttpServletResponse response) throws Exception {
        //调用service方法查询选择的市场活动
        List<Activity> activityList = activityService.queryChooseActivities(id);
        //创建excel文件，并且activityList写入到excel文件中
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("市场活动列表");
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("ID");
        cell = row.createCell(1);
        cell.setCellValue("所有者");
        cell = row.createCell(2);
        cell.setCellValue("名称");
        cell = row.createCell(3);
        cell.setCellValue("开始日期");
        cell = row.createCell(4);
        cell.setCellValue("结束日期");
        cell = row.createCell(5);
        cell.setCellValue("成本");
        cell = row.createCell(6);
        cell.setCellValue("描述");
        cell = row.createCell(7);
        cell.setCellValue("创建时间");
        cell = row.createCell(8);
        cell.setCellValue("创建者");
        cell = row.createCell(9);
        cell.setCellValue("修改时间");
        cell = row.createCell(10);
        cell.setCellValue("修改者");

        Activity activity = null;
        if (activityList != null && activityList.size() > 0) {
            for (int i = 0; i < activityList.size(); i++) {
                activity = activityList.get(i);

                //每遍历出一个activity，生成一行
                row = sheet.createRow(i + 1);

                //每一行创建11列，每一列的数据丛activityList中获取
                cell = row.createCell(0);
                cell.setCellValue(activity.getId());
                cell = row.createCell(1);
                cell.setCellValue(activity.getOwner());
                cell = row.createCell(2);
                cell.setCellValue(activity.getName());
                cell = row.createCell(3);
                cell.setCellValue(activity.getStartDate());
                cell = row.createCell(4);
                cell.setCellValue(activity.getEndDate());
                cell = row.createCell(5);
                cell.setCellValue(activity.getCost());
                cell = row.createCell(6);
                cell.setCellValue(activity.getDescription());
                cell = row.createCell(7);
                cell.setCellValue(activity.getCreateTime());
                cell = row.createCell(8);
                cell.setCellValue(activity.getCreateBy());
                cell = row.createCell(9);
                cell.setCellValue(activity.getEditTime());
                cell = row.createCell(10);
                cell.setCellValue(activity.getEditBy());
            }
        }

        response.setContentType("application/octet-stream;charset=UTF-8");
        response.addHeader("Content-Disposition", "attachment;filename=activityList.xls");
        OutputStream out = response.getOutputStream();
        //读取excel文件，将其输出到浏览器上
        InputStream is = new FileInputStream("C:\\java\\Project\\crm\\serverDir\\activityList.xls");

        //内存到内存，高并发环境下效率高
        wb.write(out);
        wb.close();
        //Tomcat自己开的流自己关
        out.flush();
        return;
    }

    @RequestMapping("/workbench/activity/importActivities.do")
    public @ResponseBody
    Object importActivities(MultipartFile activityFile, HttpSession session) {
        User user = (User) session.getAttribute(Constant.SESSION_USER);
        ReturnObject returnObject = new ReturnObject();
        try {
            //把excel文件写到磁盘里
            /*
            String originalFilename = activityFile.getOriginalFilename();
            File file = new File("C:\\java\\Project\\crm\\serverDir\\",originalFilename);
            activityFile.transferTo(file);
             */

            //解析excel文件，获取文件中的数据，并且封装成activitiesList
            //InputStream is = new FileInputStream("C:\\java\\Project\\crm\\serverDir\\"+originalFilename);
            InputStream is = activityFile.getInputStream();
            HSSFWorkbook workbook = new HSSFWorkbook(is);
            HSSFSheet sheet = workbook.getSheetAt(0);
            HSSFRow row = null;
            HSSFCell cell = null;
            Activity activity = null;
            List<Activity> activityList = new ArrayList<>();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {//getLastRowNum()获取的是最后一行的下标
                row = sheet.getRow(i);
                activity = new Activity();

                for (int j = 0; j < row.getLastCellNum(); j++) {//getLastCellNum()获取的是最后一行的下标+1（相当于总列数）
                    cell = row.getCell(j);
                    activity.setId(UUIDUtils.getUUID());
                    activity.setOwner(user.getId());
                    activity.setCreateTime(DateUtils.formatDateTime(new Date()));
                    activity.setCreateBy(user.getId());

                    //获取列中的数据
                    String cellValue = HSSFUtils.getCellValueForStr(cell);
                    if (j == 0) {
                        activity.setName(cellValue);
                    } else if (j == 1) {
                        activity.setStartDate(cellValue);
                    } else if (j == 2) {
                        activity.setEndDate(cellValue);
                    } else if (j == 3) {
                        activity.setCost(cellValue);
                    } else if (j == 4) {
                        activity.setDescription(cellValue);
                    }
                }

                //每一行中所有列都封装完成之后，把activity保存到list中
                activityList.add(activity);
            }

            //调用service方法，保存市场活动
            int ret = activityService.saveCreateActivitiesByList(activityList);

            returnObject.setCode(Constant.RETURN_OBJECT_CODE_SUCCESS);
            returnObject.setRetData(ret);
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Constant.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统忙，请稍后重试...");
        }

        return returnObject;
    }

    @RequestMapping("/workbench/activity/detailActivity.do")
    public String detailActivity(String id, HttpServletRequest request) {
        //调用service层方法，查询数据
        Activity activity = activityService.queryActivityForDetailById(id);
        List<ActivityRemark> remarkList = activityRemarkService.queryActivityRemarkForDetailByActivityId(id);

        //把数据保存到作用域中
        request.setAttribute("activity", activity);
        request.setAttribute("remarkList", remarkList);

        //请求转发
        return "workbench/activity/detail";
    }
}
