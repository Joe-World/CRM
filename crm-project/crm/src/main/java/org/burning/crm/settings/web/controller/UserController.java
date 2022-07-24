package org.burning.crm.settings.web.controller;

import org.burning.crm.commons.constants.Constant;
import org.burning.crm.commons.domain.ReturnObject;
import org.burning.crm.commons.utils.DateUtils;
import org.burning.crm.settings.domain.User;
import org.burning.crm.settings.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UserController {
    @Autowired
    private UserService userService;
    /*
        url 要和 controller方法处理请求之后，响应信息返回的页面的目录保持一致
     */
    @RequestMapping("/settings/qx/user/toLogin.do")
    public String toLogin() {
        //请求转发到登录页面
        return "settings/qx/user/login";
    }

    @RequestMapping("/settings/qx/user/login.do")
    public @ResponseBody Object login(String loginAct, String loginPwd, String isRemPwd, HttpServletRequest request, HttpSession session, HttpServletResponse response){
        //封装参数
        Map<String, Object> map = new HashMap<>();
        map.put("loginAct",loginAct);
        map.put("loginPwd",loginPwd);
        //调用service层方法，查询用户
        User user = userService.queryUserByLoginActAndPwd(map);
        //根据查询结果，生成响应信息
        ReturnObject returnObject = new ReturnObject();
        if (user == null) {
            //登录失败，用户名或者密码错误
            returnObject.setCode(Constant.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("用户名或者密码错误");
        } else {//进一步判断账号是否合法
            if (DateUtils.formatDateTime(new Date()).compareTo(user.getExpireTime()) > 0) {
                //登录失败，账号已过期
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("账号已过期");
            } else if ("0".equals(user.getLockState())){
                //登录失败，账号已锁定
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("账号被锁定");
            } else if (!user.getAllowIps().contains(request.getRemoteAddr())) {
                //登录失败，ip受限
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("ip受限");
            } else {
                //登录成功
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_SUCCESS);

                //把user放到session中
                session.setAttribute(Constant.SESSION_USER,user);

                //如果需要记住密码，则往外写cookie
                if ("true".equals(isRemPwd)) {
                    Cookie cookie1 = new Cookie("loginAct", user.getLoginAct());
                    //设置cookie寿命10天
                    cookie1.setMaxAge(10*24*60*60);
                    response.addCookie(cookie1);
                    Cookie cookie2 = new Cookie("loginPwd", user.getLoginPwd());
                    cookie2.setMaxAge(10*24*60*60);
                    response.addCookie(cookie2);
                } else {
                    //把没有过期的cookie删除
                    Cookie cookie1 = new Cookie("loginAct", null);
                    cookie1.setMaxAge(0);
                    response.addCookie(cookie1);
                    Cookie cookie2 = new Cookie("loginPwd", null);
                    cookie2.setMaxAge(0);
                    response.addCookie(cookie2);
                }
            }
        }
        return returnObject;
    }

    @RequestMapping("/settings/qx/user/logout.do")
    public String logout(HttpServletResponse response,HttpSession session) {
        //清空cookie
        Cookie cookie1 = new Cookie("loginAct", null);
        cookie1.setMaxAge(0);
        response.addCookie(cookie1);
        Cookie cookie2 = new Cookie("loginPwd", null);
        cookie2.setMaxAge(0);
        response.addCookie(cookie2);

        //销毁session
        session.invalidate();

        //重定向到登录页面(不需要视图解析器)
        return "redirect:/";
    }
}
