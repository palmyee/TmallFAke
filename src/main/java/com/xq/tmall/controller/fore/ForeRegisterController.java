package com.xq.tmall.controller.fore;

import com.alibaba.fastjson.JSONObject;
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

import java.util.List;
import java.util.Map;

/**
 * 前台天猫-用户注册
 */
@Api(tags = "前台天猫-用户注册")
@Controller
public class ForeRegisterController extends BaseController {
    @Autowired
    private AddressService addressService;
    @Autowired
    private UserService userService;

    //转到前台天猫-用户注册页
    @ApiOperation(value = "转到前台天猫-用户注册页", notes = "转到前台天猫-用户注册页")
    @GetMapping(value = "register")
    public String goToPage(Map<String, Object> map) {
        String addressId = "110000";
        String cityAddressId = "110100";
        //获取省份信息
        List<Address> addressList = addressService.getRoot();
        //获取addressId为{}的市级地址信息", addressId
        List<Address> cityAddress = addressService.getList(null, addressId);
        //获取cityAddressId为{}的区级地址信息", cityAddressId
        List<Address> districtAddress = addressService.getList(null, cityAddressId);
        map.put("addressList", addressList);
        map.put("cityList", cityAddress);
        map.put("districtList", districtAddress);
        map.put("addressId", addressId);
        map.put("cityAddressId", cityAddressId);
        //转到前台-用户注册页
        return "fore/register";
    }

    //天猫前台-用户注册-ajax
    @ApiOperation(value = "天猫前台-用户注册", notes = "天猫前台-用户注册")
    @ResponseBody
    @PostMapping(value = "register/doRegister", produces = "application/json;charset=UTF-8")
    public String register(
            @RequestParam(value = "user_name") String user_name  /*用户名 */,
            @RequestParam(value = "user_nickname") String user_nickname  /*用户昵称 */,
            @RequestParam(value = "user_password") String user_password  /*用户密码*/,
            @RequestParam(value = "user_gender") String user_gender  /*用户性别*/,
            @RequestParam(value = "user_birthday") String user_birthday /*用户生日*/,
            @RequestParam(value = "user_address") String user_address  /*用户所在地 */
    ) {
        //验证用户名是否存在
        User user1 = new User();
        user1.setUser_name(user_name);
        Integer count = userService.getTotal(user1);
        if (count > 0) {
            //用户名已存在，返回错误信息!
            JSONObject object = new JSONObject();
            object.put("success", false);
            object.put("msg", "用户名已存在，请重新输入！");
            return String.valueOf(object);
        }
        //创建用户对象
        User user = new User();
        user.setUser_name(user_name);
        user.setUser_nickname(user_nickname);
        user.setUser_password(user_password);
        user.setUser_gender(Byte.valueOf(user_gender));
        user.setUser_birthday(user_birthday);
        //地址对象
        Address address = new Address();
        address.setAddress_areaId(user_address);
        user.setUser_address(address);
        user.setUser_homeplace(address);
        user.setUser_realname(user_name);
        user.setDel_flag(0);
        //用户注册
        if (userService.add(user)) {
            //注册成功
            JSONObject object = new JSONObject();
            object.put("success", true);
            return String.valueOf(object);
        } else {
            throw new RuntimeException();
        }
    }
}
