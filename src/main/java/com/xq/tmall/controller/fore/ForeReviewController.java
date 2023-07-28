package com.xq.tmall.controller.fore;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.xq.tmall.controller.BaseController;
import com.xq.tmall.entity.*;
import com.xq.tmall.service.*;
import com.xq.tmall.util.PageUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 前台天猫-评论添加
 */
@Api(tags = "前台天猫-评论添加")
@Controller
public class ForeReviewController extends BaseController {
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductOrderItemService productOrderItemService;
    @Autowired
    private ProductOrderService productOrderService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductImageService productImageService;
    public static final String URL = "redirect:/order/0/10";

    //转到前台天猫-评论添加页
    @ApiOperation(value = "转到前台天猫-评论添加页", notes = "转到前台天猫-评论添加页")
    @GetMapping(value = "review/{orderItem_id}")
    public String goToPage(HttpSession session, Map<String, Object> map,
                           @PathVariable("orderItem_id") Integer orderItem_id) {
        //检查用户是否登录
        Object userId = checkUser(session);
        User user;
        if (userId != null) {
            //获取用户信息
            user = userService.get(Integer.parseInt(userId.toString()));
            map.put("user", user);
        } else {
            return "redirect:/login";
        }
        //获取订单项信息
        ProductOrderItem orderItem = productOrderItemService.get(orderItem_id);
        if (orderItem == null) {
            //订单项不存在，返回订单页
            return URL;
        }
        if (!orderItem.getProductOrderItem_user().getUser_id().equals(userId)) {
            //订单项与用户不匹配，返回订单页
            return URL;
        }
        if (orderItem.getProductOrderItem_order() == null) {
            //订单项状态有误，返回订单页
            return URL;
        }
        ProductOrder order = productOrderService.get(orderItem.getProductOrderItem_order().getProductOrder_id());
        if (order == null || order.getProductOrder_status() != 3) {
            //订单项状态有误，返回订单页
            return URL;
        }
        if (reviewService.getTotalByOrderItemId(orderItem_id) > 0) {
            //订单项所属商品已被评价，返回订单页
            return URL;
        }
        //获取订单项所属产品信息和产品评论信息
        Product product = productService.get(orderItem.getProductOrderItem_product().getProduct_id());
        product.setProduct_review_count(reviewService.getTotalByProductId(product.getProduct_id()));
        product.setSingleProductImageList(productImageService.getList(product.getProduct_id(), (byte) 0, new PageUtil(0, 1)));
        orderItem.setProductOrderItem_product(product);

        map.put("orderItem", orderItem);

        //转到前台天猫-评论添加页
        return "fore/addReview";
    }

    //添加一条评论
    @ApiOperation(value = "添加一条评论", notes = "添加一条评论")
    @PostMapping(value = "review")
    public String addReview(HttpSession session, Map<String, Object> map,
                            @RequestParam Integer orderItem_id,
                            @RequestParam String review_content) {
        //检查用户是否登录
        Object userId = checkUser(session);
        User user;
        if (userId != null) {
            //获取用户信息
            user = userService.get(Integer.parseInt(userId.toString()));
            map.put("user", user);
        } else {
            return "redirect:/login";
        }
        //获取订单项信息
        ProductOrderItem orderItem = productOrderItemService.get(orderItem_id);
        if (orderItem == null) {
            //订单项不存在，返回订单页
            return URL;
        }
        if (!orderItem.getProductOrderItem_user().getUser_id().equals(userId)) {
            //订单项与用户不匹配，返回订单页
            return URL;
        }
        if (orderItem.getProductOrderItem_order() == null) {
            //订单项状态有误，返回订单页
            return URL;
        }
        ProductOrder order = productOrderService.get(orderItem.getProductOrderItem_order().getProductOrder_id());
        if (order == null || order.getProductOrder_status() != 3) {
            //订单项状态有误，返回订单页
            return URL;
        }
        if (reviewService.getTotalByOrderItemId(orderItem_id) > 0) {
            //订单项所属商品已被评价，返回订单页
            return URL;
        }
        //整合评论信息
        Review review = new Review();
        review.setReview_product(orderItem.getProductOrderItem_product());
        review.setReview_content(review_content);
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
        review.setReview_createDate(time.format(new Date()));
        review.setReview_user(user);
        review.setReview_orderItem(orderItem);
        //添加评论
        Boolean yn = reviewService.add(review);
        if (!yn) {
            throw new RuntimeException();
        }
        return "redirect:/product/" + orderItem.getProductOrderItem_product().getProduct_id();
    }

    //获取产品评论信息-ajax
    @ApiOperation(value = "获取产品评论信息", notes = "获取产品评论信息")
    @ResponseBody
    @GetMapping(value = "review", produces = "application/json;charset=utf-8")
    public String getReviewInfo(@RequestParam("product_id") Integer product_id,
                                @RequestParam("index") Integer index/* 页数 */,
                                @RequestParam("count") Integer count/* 行数*/) {
        //获取产品评论信息
        List<Review> reviewList = reviewService.getListByProductId(product_id, new PageUtil(index, 10));
        if (CollectionUtils.isNotEmpty(reviewList)) {
            for (Review review : reviewList) {
                review.setReview_user(userService.get(review.getReview_user().getUser_id()));
            }
        }
        Integer total = reviewService.getTotalByProductId(product_id);

        JSONObject object = new JSONObject();
        object.put("reviewList", reviewList);
        object.put("pageUtil", new PageUtil().setTotal(total).setIndex(index).setCount(count));

        return String.valueOf(object);
    }
}
