package com.xq.tmall.controller.admin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.xq.tmall.controller.BaseController;
import com.xq.tmall.entity.Address;
import com.xq.tmall.entity.Product;
import com.xq.tmall.entity.ProductOrder;
import com.xq.tmall.entity.ProductOrderItem;
import com.xq.tmall.service.*;
import com.xq.tmall.util.Constants;
import com.xq.tmall.util.OrderUtil;
import com.xq.tmall.util.PageUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 后台管理-订单页
 */
@Api(tags = "后台管理-订单页")
@Controller
public class OrderController extends BaseController {
    @Autowired
    private ProductOrderService productOrderService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductOrderItemService productOrderItemService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductImageService productImageService;

    //转到后台管理-订单页-ajax
    @ApiOperation(value = "转到后台管理-订单页", notes = "转到后台管理-订单页")
    @GetMapping(value = "admin/order")
    public String goToPage(HttpSession session, Map<String, Object> map) {
        //检查管理员权限
        Object adminId = checkAdmin(session);
        if (adminId == null) {
            return "admin/include/loginMessage";
        }

        //获取前10条订单列表
        PageUtil pageUtil = new PageUtil(0, 10);
        List<ProductOrder> productOrderList = productOrderService.getList(null, null, new OrderUtil("productOrder_id", true), pageUtil);
        map.put("productOrderList", productOrderList);
        //获取订单总数量
        Integer productOrderCount = productOrderService.getTotal(null, null);
        map.put("productOrderCount", productOrderCount);
        //获取分页信息
        pageUtil.setTotal(productOrderCount);
        map.put("pageUtil", pageUtil);
        //转到后台管理-订单页-ajax方式
        return "admin/orderManagePage";
    }

    //转到后台管理-订单详情页-ajax
    @ApiOperation(value = "转到后台管理-订单详情页", notes = "转到后台管理-订单详情页")
    @GetMapping(value = "admin/order/{oid}")
    public String goToDetailsPage(HttpSession session, Map<String, Object> map, @PathVariable Integer oid/* 订单ID */) {
        //检查管理员权限
        Object adminId = checkAdmin(session);
        if (adminId == null) {
            return "admin/include/loginMessage";
        }

        //获取order_id为{}的订单信息, oid
        ProductOrder order = productOrderService.get(oid);
        //获取订单详情-地址信息
        Address address = addressService.get(order.getProductOrder_address().getAddress_areaId());
        Stack<String> addressStack = new Stack<>();
        //详细地址
        addressStack.push(order.getProductOrder_detail_address());
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
        //订单地址字符串：{}, builder
        order.setProductOrder_detail_address(builder.toString());
        //获取订单详情-用户信息
        order.setProductOrder_user(userService.get(order.getProductOrder_user().getUser_id()));
        //获取订单详情-订单项信息
        List<ProductOrderItem> productOrderItemList = productOrderItemService.getListByOrderId(oid, null);
        if (CollectionUtils.isNotEmpty(productOrderItemList)) {
            //获取订单详情-订单项对应的产品信息
            for (ProductOrderItem productOrderItem : productOrderItemList) {
                Integer productId = productOrderItem.getProductOrderItem_product().getProduct_id();
                //获取产品ID为{}的产品信息, productId
                Product product = productService.get(productId);
                if (product != null) {
                    //获取产品ID为{}的第一张预览图片信息, productId
                    product.setSingleProductImageList(productImageService.getList(productId, (byte) 0, new PageUtil(0, 1)));
                }
                productOrderItem.setProductOrderItem_product(product);
            }
        }
        order.setProductOrderItemList(productOrderItemList);
        map.put("order", order);
        //转到后台管理-订单详情页-ajax方式
        return "admin/include/orderDetails";
    }

    //更新订单信息-ajax
    @ApiOperation(value = "更新订单信息", notes = "更新订单信息")
    @ResponseBody
    @PutMapping(value = "admin/order/{order_id}", produces = "application/json;charset=UTF-8")
    public String updateOrder(@PathVariable("order_id") String order_id) {
        JSONObject jsonObject = new JSONObject();
        //整合订单信息
        ProductOrder productOrder = new ProductOrder();
        productOrder.setProductOrder_id(Integer.valueOf(order_id));
        productOrder.setProductOrder_status((byte) 2);
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
        productOrder.setProductOrder_delivery_date(time.format(new Date()));
        //更新订单信息，订单ID值为：{}, order_id
        boolean yn = productOrderService.update(productOrder);
        if (yn) {
            //更新成功！
            jsonObject.put(Constants.SUCCESS, true);
        } else {
            //更新失败！事务回滚
            jsonObject.put(Constants.SUCCESS, false);
            throw new RuntimeException();
        }
        jsonObject.put("order_id", order_id);
        return String.valueOf(jsonObject);
    }

    //更新订单信息-ajax
    @ApiOperation(value = "更新订单信息", notes = "更新订单信息")
    @ResponseBody
    @GetMapping(value = "admin/order/{index}/{count}", produces = "application/json;charset=UTF-8")
    public String getOrderBySearch(@RequestParam(required = false) String productOrder_code/* 订单号 */,
                                   @RequestParam(required = false) String productOrder_post/* 订单邮政编码 */,
                                   @RequestParam(required = false) String productOrder_receiver/* 收货人 */,
                                   @RequestParam(required = false) String productOrder_mobile/* 联系方式 */,
                                   @RequestParam(required = false) Byte[] productOrder_status_array/* 订单状态数组 */,
                                   @RequestParam(required = false) String orderBy/* 排序字段 */,
                                   @RequestParam(required = false, defaultValue = "true") Boolean isDesc/* 是否倒序 */,
                                   @PathVariable Integer index/* 页数 */,
                                   @PathVariable Integer count/* 行数 */) throws UnsupportedEncodingException {
        //移除不必要条件
        if (productOrder_status_array != null && (productOrder_status_array.length <= 0 || productOrder_status_array.length >= 5)) {
            productOrder_status_array = null;
        }
        if (productOrder_code != null) {
            productOrder_code = "".equals(productOrder_code) ? null : productOrder_code;
        }
        if (productOrder_post != null) {
            productOrder_post = "".equals(productOrder_post) ? null : productOrder_post;
        }
        if (productOrder_receiver != null) {
            //如果为非空字符串则解决中文乱码
            productOrder_receiver = "".equals(productOrder_receiver) ? null : URLDecoder.decode(productOrder_receiver, "UTF-8");
        }
        if (productOrder_mobile != null) {
            productOrder_mobile = "".equals(productOrder_mobile) ? null : productOrder_mobile;
        }
        if (orderBy == null || "".equals(orderBy)) {
            orderBy = "productorder_pay_date";
        }
        //封装查询条件
        ProductOrder productOrder = new ProductOrder();
        productOrder.setProductOrder_code(productOrder_code);
        productOrder.setProductOrder_post(productOrder_post);
        productOrder.setProductOrder_receiver(productOrder_receiver);
        productOrder.setProductOrder_mobile(productOrder_mobile);
        OrderUtil orderUtil = null;
        if (orderBy != null) {
            //根据{}排序，是否倒序:{}, orderBy, isDesc
            orderUtil = new OrderUtil(orderBy, isDesc);
        }
        JSONObject object = new JSONObject();
        //按条件获取第{}页的{}条订单, index + 1, count
        PageUtil pageUtil = new PageUtil(index, count);
        List<ProductOrder> productOrderList = productOrderService.getList(productOrder, productOrder_status_array, orderUtil, pageUtil);
        object.put("productOrderList", JSON.parseArray(JSON.toJSONString(productOrderList)));
        //按条件获取订单总数量
        Integer productOrderCount = productOrderService.getTotal(productOrder, productOrder_status_array);
        object.put("productOrderCount", productOrderCount);
        //获取分页信息
        pageUtil.setTotal(productOrderCount);
        object.put("totalPage", pageUtil.getTotalPage());
        object.put("pageUtil", pageUtil);

        return String.valueOf(object);
    }
}
