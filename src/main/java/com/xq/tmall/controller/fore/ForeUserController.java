package com.xq.tmall.controller.fore;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.xq.tmall.controller.BaseController;
import com.xq.tmall.entity.Address;
import com.xq.tmall.entity.User;
import com.xq.tmall.service.AddressService;
import com.xq.tmall.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 前台天猫-用户
 */
@Api(tags = "前台天猫-用户")
@Controller
public class ForeUserController extends BaseController {
    @Autowired
    private AddressService addressService;
    @Autowired
    private UserService userService;

    //转到前台天猫-用户详情页
    @ApiOperation(value = "转到前台天猫-用户详情页", notes = "转到前台天猫-用户详情页")
    @GetMapping(value = "userDetails")
    public String goToUserDetail(HttpSession session, Map<String, Object> map) {
        //检查用户是否登录
        Object userId = checkUser(session);
        if (userId != null) {
            //获取用户信息
            User user = userService.get(Integer.parseInt(userId.toString()));
            map.put("user", user);

            //获取用户所在地区级地址
            String districtAddressId = user.getUser_address().getAddress_areaId();
            Address districtAddress = addressService.get(districtAddressId);
            //获取市级地址信息
            Address cityAddress = addressService.get(districtAddress.getAddress_regionId().getAddress_areaId());
            //获取其他地址信息
            List<Address> addressList = addressService.getRoot();
            List<Address> cityList = addressService.getList(null, cityAddress.getAddress_regionId().getAddress_areaId());
            List<Address> districtList = addressService.getList(null, cityAddress.getAddress_areaId());

            map.put("addressList", addressList);
            map.put("cityList", cityList);
            map.put("districtList", districtList);
            map.put("addressId", cityAddress.getAddress_regionId().getAddress_areaId());
            map.put("cityAddressId", cityAddress.getAddress_areaId());
            map.put("districtAddressId", districtAddressId);
            return "fore/userDetails";
        } else {
            return "redirect:/login";
        }
    }

    //前台天猫-用户更换头像
    @ApiOperation(value = "前台天猫-用户更换头像", notes = "前台天猫-用户更换头像")
    @ResponseBody
    @PostMapping(value = "user/uploadUserHeadImage", produces = "application/json;charset=utf-8")
    public String uploadUserHeadImage(@RequestParam MultipartFile file, HttpSession session
    ) {
        String originalFileName = file.getOriginalFilename();
        if (StringUtils.isEmpty(originalFileName)) {
            throw new RuntimeException("上传失败！");
        }
        //获取图片原始文件名：{}, originalFileName
        String extension = originalFileName.substring(originalFileName.lastIndexOf('.'));
        String fileName = UUID.randomUUID() + extension;
        String filePath = session.getServletContext().getRealPath("/") + "res/images/item/userProfilePicture/" + fileName;
        //文件上传路径：{}, filePath
        JSONObject jsonObject = new JSONObject();
        try {
            //文件上传中...
            file.transferTo(new File(filePath));
            //文件上传成功！
            jsonObject.put("success", true);
            jsonObject.put("fileName", fileName);
        } catch (IOException e) {
            logger.warn("文件上传失败！");
            e.printStackTrace();
            jsonObject.put("success", false);
        }
        return String.valueOf(jsonObject);
    }

    //前台天猫-用户详情更新
    @ApiOperation(value = "前台天猫-用户详情更新", notes = "前台天猫-用户详情更新")
    @PostMapping(value = "user/update", produces = "application/json;charset=utf-8")
    public String userUpdate(HttpSession session, Map<String, Object> map,
                             @RequestParam(value = "user_nickname") String user_nickname  /*用户昵称 */,
                             @RequestParam(value = "user_realname") String user_realname  /*真实姓名*/,
                             @RequestParam(value = "user_gender") String user_gender  /*用户性别*/,
                             @RequestParam(value = "user_birthday") String user_birthday /*用户生日*/,
                             @RequestParam(value = "user_address") String user_address  /*用户所在地 */,
                             @RequestParam(value = "user_profile_picture_src", required = false) String user_profile_picture_src /* 用户头像*/,
                             @RequestParam(value = "user_password") String user_password/* 用户密码 */
    ) {
        //检查用户是否登录
        Object userId = checkUser(session);
        if (userId != null) {
            //获取用户信息
            User user = userService.get(Integer.parseInt(userId.toString()));
            map.put("user", user);
        } else {
            return "redirect:/login";
        }
        //创建用户对象
        if (user_profile_picture_src != null && "".equals(user_profile_picture_src)) {
            user_profile_picture_src = null;
        }
        User userUpdate = new User();
        userUpdate.setUser_id(Integer.parseInt(userId.toString()));
        userUpdate.setUser_nickname(user_nickname);
        userUpdate.setUser_realname(user_realname);
        userUpdate.setUser_gender(Byte.valueOf(user_gender));
        userUpdate.setUser_birthday(user_birthday);
        Address address = new Address();
        address.setAddress_areaId(user_address);
        userUpdate.setUser_address(address);
        userUpdate.setUser_profile_picture_src(user_profile_picture_src);
        userUpdate.setUser_password(user_password);
        //执行修改
        if (userService.update(userUpdate)) {
            //修改成功!跳转到用户详情页面
            return "redirect:/userDetails";
        }
        throw new RuntimeException();
    }
}
