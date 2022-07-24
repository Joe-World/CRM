package org.burning.crm.workbench.web.controller;

import org.burning.crm.workbench.domain.FunnelVO;
import org.burning.crm.workbench.service.TranService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class ChatController {
    @Autowired
    private TranService tranService;

    @RequestMapping("/workbench/chart/transaction/index.do")
    public String index() {
        //跳转页面
        return "workbench/chart/transaction/index";
    }

    @RequestMapping("/workbench/chart/transaction/queryCountOfTranGroupByStage.do")
    public @ResponseBody
    Object queryCountOfTranGroupByStage() {
        //查询漏斗图所需的数据集合
        List<FunnelVO> funnelVOList = tranService.queryCountOfTranGroupByStage();

        return funnelVOList;
    }
}
