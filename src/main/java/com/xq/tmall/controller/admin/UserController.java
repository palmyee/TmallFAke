package com.xq.tmall.controller.admin;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xq.tmall.controller.BaseController;
import com.xq.tmall.entity.Address;
import com.xq.tmall.entity.Product;
import com.xq.tmall.entity.ProductOrderItem;
import com.xq.tmall.entity.User;
import com.xq.tmall.service.*;
import com.xq.tmall.util.Constants;
import com.xq.tmall.util.OrderUtil;
import com.xq.tmall.util.PageUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * 后台管理-用户页
 */
@Api(tags = "后台管理-用户页")
@Controller
public class UserController extends BaseController {
    @Autowired
    private UserService userService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private ProductOrderItemService productOrderItemService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductImageService productImageService;

    //转到后台管理-用户页-ajax
    @ApiOperation(value = "转到后台管理-用户页", notes = "转到后台管理-用户页")
    @GetMapping(value = "admin/user")
    public String goUserManagePage(HttpSession session, Map<String, Object> map) {
        //检查管理员权限
        Object adminId = checkAdmin(session);
        if (adminId == null) {
            return "admin/include/loginMessage";
        }

        //获取前十条用户信息
        PageUtil pageUtil = new PageUtil(0, 10);
        List<User> userList = userService.getList(null, null, pageUtil);
        map.put("userList", userList);
        //获取用户总数量
        Integer userCount = userService.getTotal(null);
        map.put("userCount", userCount);
        //获取分页信息
        pageUtil.setTotal(userCount);
        map.put("pageUtil", pageUtil);

        //转到后台管理-用户页-ajax方式
        return "admin/userManagePage";
    }


    //转到后台管理-用户详情页-ajax
    @ApiOperation(value = "转到后台管理-用户详情页", notes = "转到后台管理-用户详情页")
    @GetMapping(value = "admin/user/{uid}")
    public String getUserById(HttpSession session, Map<String, Object> map, @PathVariable Integer uid/* 用户ID */) {
        //检查管理员权限
        Object adminId = checkAdmin(session);
        if (adminId == null) {
            return "admin/include/loginMessage";
        }

        //获取user_id为{}的用户信息, uid
        User user = userService.get(uid);
        //获取用户详情-所在地地址信息
        Address address = addressService.get(user.getUser_address().getAddress_areaId());
        Stack<String> addressStack = new Stack<>();
        //最后一级地址
        addressStack.push(address.getAddress_name() + " ");
        //如果不是第一级地址
        while (!address.getAddress_areaId().equals(address.getAddress_regionId().getAddress_areaId())) {
            address = addressService.get(address.getAddress_regionId().getAddress_areaId());
            addressStack.push(address.getAddress_name() + " ");
        }
        StringBuilder builder = new StringBuilder();
        while (!addressStack.empty()) {
            builder.append(addressStack.pop());
        }
        //所在地地址字符串：{}, builder
        Address add = new Address();
        add.setAddress_name(builder.toString());
        user.setUser_address(add);

        //获取用户详情-家乡地址信息
        address = addressService.get(user.getUser_homeplace().getAddress_areaId());
        //最后一级地址
        addressStack.push(address.getAddress_name() + " ");
        //如果不是第一级地址
        while (!address.getAddress_areaId().equals(address.getAddress_regionId().getAddress_areaId())) {
            address = addressService.get(address.getAddress_regionId().getAddress_areaId());
            addressStack.push(address.getAddress_name() + " ");
        }
        builder = new StringBuilder();
        while (!addressStack.empty()) {
            builder.append(addressStack.pop());
        }
        //家乡地址字符串：{}, builder
        user.setUser_homeplace(add);

        //获取用户详情-购物车订单项信息
        List<ProductOrderItem> productOrderItemList = productOrderItemService.getListByUserId(user.getUser_id(), null);
        if (productOrderItemList != null) {
            //获取用户详情-购物车订单项对应的产品信息
            for (ProductOrderItem productOrderItem : productOrderItemList) {
                Integer productId = productOrderItem.getProductOrderItem_product().getProduct_id();
                logger.warn("获取产品ID为{}的产品信息", productId);
                Product product = productService.get(productId);
                if (product != null) {
                    logger.warn("获取产品ID为{}的第一张预览图片信息", productId);
                    product.setSingleProductImageList(productImageService.getList(productId, (byte) 0, new PageUtil(0, 1)));
                }
                productOrderItem.setProductOrderItem_product(product);
            }
        }
        user.setProductOrderItemList(productOrderItemList);

        if (!StringUtils.isEmpty(user.getUser_realname())) {
            //用户隐私加密
            user.setUser_realname(user.getUser_realname().charAt(0) + "*");
        } else {
            user.setUser_realname("未命名");
        }
        map.put("user", user);
        //转到后台管理-用户详情页-ajax方式
        return "admin/include/userDetails";
    }

    //按条件查询用户-ajax
    @ApiOperation(value = "按条件查询用户", notes = "按条件查询用户")
    @ResponseBody
    @GetMapping(value = "admin/user/{index}/{count}", produces = "application/json;charset=UTF-8")
    public String getUserBySearch(@RequestParam(required = false) String user_name/* 用户名称 */,
                                  @RequestParam(required = false) Byte[] user_gender_array/* 用户性别数组 */,
                                  @RequestParam(required = false) String orderBy/* 排序字段 */,
                                  @RequestParam(required = false, defaultValue = "true") Boolean isDesc/* 是否倒序 */,
                                  @PathVariable Integer index/* 页数 */,
                                  @PathVariable Integer count/* 行数 */) throws UnsupportedEncodingException {
        //移除不必要条件
        Byte gender = null;
        if (user_gender_array != null && user_gender_array.length == 1) {
            gender = user_gender_array[0];
        }
        if (user_name != null) {
            //如果为非空字符串则解决中文乱码
            user_name = "".equals(user_name) ? null : URLDecoder.decode(user_name, "UTF-8");
        }
        if (orderBy != null && "".equals(orderBy)) {
            orderBy = null;
        }
        //封装查询条件
        User user = new User();
        user.setUser_name(user_name);
        user.setUser_gender(gender);

        OrderUtil orderUtil = null;
        if (orderBy != null) {
            //根据{}排序，是否倒序:{}, orderBy, isDesc
            orderUtil = new OrderUtil(orderBy, isDesc);
        }

        JSONObject object = new JSONObject();
        //按条件获取第{}页的{}条用户, index + 1, count
        PageUtil pageUtil = new PageUtil(index, count);
        List<User> userList = userService.getList(user, orderUtil, pageUtil);
        object.put("userList", JSON.parseArray(JSON.toJSONString(userList)));
        //按条件获取用户总数量
        Integer userCount = userService.getTotal(user);
        object.put("userCount", userCount);
        //获取分页信息
        pageUtil.setTotal(userCount);
        object.put("totalPage", pageUtil.getTotalPage());
        object.put("pageUtil", pageUtil);

        return String.valueOf(object);
    }

    //按ID删除用户并返回最新结果-ajax
    @ApiOperation(value = "按ID删除用户并返回最新结果", notes = "按ID删除用户并返回最新结果")
    @ResponseBody
    @GetMapping(value = "admin/user/del/{id}", produces = "application/json;charset=utf-8")
    public String deleteProductById(@PathVariable Integer id) {
        JSONObject object = new JSONObject();
        User user = userService.get(id);
        user.setDel_flag(1);
        boolean yn = userService.update(user);
        if (yn) {
            //删除成功！
            object.put(Constants.SUCCESS, true);
        } else {
            //删除失败！事务回滚
            object.put(Constants.SUCCESS, false);
            throw new RuntimeException();
        }
        return String.valueOf(object);
    }

}
