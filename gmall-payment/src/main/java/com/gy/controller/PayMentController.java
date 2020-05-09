package com.gy.controller;

import com.gy.webutil.annotations.NeedLogin;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

/**
 * Created by gaoyong on 2020/5/9.
 */
@Controller
public class PayMentController {

    @RequestMapping("/index")
    @NeedLogin(LoginSucess = true)
    public String index(String outTradeNo, BigDecimal totalAmount, HttpServletRequest request,ModelMap map){


        map.put("outTradeNo",outTradeNo);
        map.put("totalAmount",totalAmount);
        return "index";
    }
}
