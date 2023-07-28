package com.xq.tmall.controller.admin;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.xq.tmall.controller.BaseController;
import com.xq.tmall.entity.Admin;
import com.xq.tmall.service.AdminService;
import com.xq.tmall.util.Constants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * 后台管理-账户页
 */
@Api(tags = "后台管理-账户页")
@Controller
public class AccountController extends BaseController {
    @Autowired
    private AdminService adminService;

    //转到后台管理-账户页-ajax
    @ApiOperation(value = "转到后台管理-账户页", notes = "转到后台管理-账户页")
    @GetMapping(value = "/admin/account")
    public String goToPage(HttpSession session, Map<String, Object> map) {
        //检查管理员权限
        Object adminId = checkAdmin(session);
        if (adminId == null) {
            return "admin/include/loginMessage";
        }
        //获取目前登录的管理员信息
        Admin admin = adminService.get(null, Integer.parseInt(adminId.toString()));
        map.put("admin", admin);

        //转到后台管理-账户页-ajax方式
        return "admin/accountManagePage";
    }

    //退出当前账号
    @ApiOperation(value = "退出当前账号", notes = "退出当前账号")
    @GetMapping(value = "/admin/account/logout")
    public String logout(HttpSession session) {
        Object o = session.getAttribute(Constants.ADMIN_ID);
        if (o == null) {
            logger.info("无管理权限，返回管理员登陆页");
        } else {
            session.removeAttribute(Constants.ADMIN_ID);
            session.invalidate();
            logger.info("登录信息已清除，返回管理员登陆页");
        }
        return "redirect:/admin/login";
    }

    //管理员头像上传
    @ApiOperation(value = "管理员头像上传", notes = "管理员头像上传")
    @ResponseBody
    @PostMapping(value = "/admin/uploadAdminHeadImage", produces = "application/json;charset=UTF-8")
    public String uploadAdminHeadImage(@RequestParam MultipartFile file, HttpSession session) {
        String originalFileName = file.getOriginalFilename();
        if (StringUtils.isEmpty(originalFileName)) {
            throw new RuntimeException("上传失败！");
        }
        //获取图片原始文件名：originalFileName
        String extension = originalFileName.substring(originalFileName.lastIndexOf('.'));
        String fileName = UUID.randomUUID() + extension;
        String filePath = session.getServletContext().getRealPath("/") + "res/images/item/adminProfilePicture/" + fileName;
        //文件上传路径：filePath
        JSONObject jsonObject = new JSONObject();
        try {
            file.transferTo(new File(filePath));
            logger.info("文件上传成功！");
            jsonObject.put(Constants.SUCCESS, true);
            jsonObject.put("fileName", fileName);
        } catch (IOException e) {
            logger.warn("文件上传失败！");
            e.printStackTrace();
            jsonObject.put(Constants.SUCCESS, false);
        }
        return String.valueOf(jsonObject);
    }

    //更新管理员信息
    @ApiOperation(value = "更新管理员信息", notes = "更新管理员信息")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @ResponseBody
    @PutMapping(value = "/admin/account/{admin_id}", produces = "application/json;charset=UTF-8")
    public String updateAdmin(HttpSession session, @RequestParam String admin_nickname/*管理员昵称*/,
                              @RequestParam(required = false) String admin_password/*管理员当前密码*/,
                              @RequestParam(required = false) String admin_newPassword/*管理员新密码*/,
                              @RequestParam(required = false) String admin_profile_picture_src/*管理员头像路径*/,
                              @PathVariable("admin_id") String admin_id/*管理员编号*/) {
        //检查管理员权限
        Object adminId = checkAdmin(session);
        if (adminId == null) {
            return "admin/include/loginMessage";
        }
        JSONObject jsonObject = new JSONObject();
        Admin putAdmin = new Admin();
        putAdmin.setAdmin_id(Integer.valueOf(admin_id));
        putAdmin.setAdmin_nickname(admin_nickname);

        if (StringUtils.isNotEmpty(admin_password) && StringUtils.isNotEmpty(admin_newPassword)) {
            //获取需要修改的管理员信息
            Admin admin = adminService.get(null, Integer.valueOf(adminId.toString()));
            if (adminService.login(admin.getAdmin_name(), admin_password) >0) {
                //原密码正确
                putAdmin.setAdmin_password(admin_newPassword);
            } else {
                //原密码错误，返回错误信息
                jsonObject.put(Constants.SUCCESS, false);
                jsonObject.put("message", "原密码输入有误！");
                return String.valueOf(jsonObject);
            }
        }
        if (StringUtils.isNotEmpty(admin_profile_picture_src)) {
            //管理员头像路径为, admin_profile_picture_src
            putAdmin.setAdmin_profile_picture_src(admin_profile_picture_src.substring(admin_profile_picture_src.lastIndexOf("/") + 1));
        }

        //更新管理员信息，管理员ID值为：{}", admin_id
        Boolean yn = adminService.update(putAdmin);
        if (yn) {
            //更新成功！
            jsonObject.put(Constants.SUCCESS, true);
            session.removeAttribute(Constants.ADMIN_ID);
            session.invalidate();
            logger.info("登录信息已清除");
        } else {
            jsonObject.put(Constants.SUCCESS, false);
            logger.warn("更新失败！事务回滚");
            throw new RuntimeException();
        }
        return String.valueOf(jsonObject);
    }
}