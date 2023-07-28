package com.xq.tmall.controller.fore;

import com.alibaba.fastjson.JSONObject;
import com.xq.tmall.controller.BaseController;
import com.xq.tmall.entity.User;
import com.xq.tmall.service.UserService;
import com.xq.tmall.util.Constants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 前台天猫-登陆页
 */
@Api(tags = "前台天猫-登陆页")
@Controller
public class ForeLoginController extends BaseController {
    @Autowired
    private UserService userService;

    //转到前台天猫-登录页
    @ApiOperation(value = "转到前台天猫-登录页", notes = "转到前台天猫-登录页")
    @GetMapping(value = "login")
    public String goToPage(HttpSession session, Map<String, Object> map) {
        //转到前台天猫-登录页
        return "fore/loginPage";
    }

    //登陆验证-ajax
    @ApiOperation(value = "登陆验证", notes = "登陆验证")
    @ResponseBody
    @PostMapping(value = "login/doLogin", produces = "application/json;charset=utf-8")
    public String checkLogin(HttpSession session, @RequestParam String username, @RequestParam String password) {
        //用户验证登录
        User user = userService.login(username, password);

        JSONObject jsonObject = new JSONObject();
        if (user == null) {
            //登录验证失败
            jsonObject.put(Constants.SUCCESS, false);
        } else {
            //登录验证成功,用户ID传入会话
            session.setAttribute(Constants.USER_ID, user.getUser_id());
            jsonObject.put(Constants.SUCCESS, true);
        }
        return String.valueOf(jsonObject);
    }

    //退出当前账号
    @ApiOperation(value = "退出当前账号", notes = "退出当前账号")
    @GetMapping(value = "login/logout")
    public String logout(HttpSession session) {
        Object o = session.getAttribute(Constants.USER_ID);
        if (o != null) {
            session.removeAttribute(Constants.USER_ID);
            session.invalidate();
            //登录信息已清除，返回用户登录页
        }
        return "redirect:/login";
    }
}
