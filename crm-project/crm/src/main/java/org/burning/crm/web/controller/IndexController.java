package org.burning.crm.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {
    /*
        理论上，给Controller方法分配url：http://192.168.101.131:8080/crm/
        为了简便：
            协议://ip:port/应用名称   必须省去，用/代表应用根目录下的/
     */
    @RequestMapping("/")
    public String index() {
        //请求转发
        return "index";
    }
}
