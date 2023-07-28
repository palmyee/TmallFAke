package com.xq.tmall.controller.admin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xq.tmall.controller.BaseController;
import com.xq.tmall.entity.Product;
import com.xq.tmall.entity.Review;
import com.xq.tmall.entity.User;
import com.xq.tmall.service.ReviewService;
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


/**
 * 后台管理-评论页
 */
@Api(tags = "后台管理-评论页")
@Controller
public class ReviewController extends BaseController {
    @Autowired
    private ReviewService reviewService;

    //转到后台管理-评论页-ajax
    @ApiOperation(value = "转到后台管理-评论页", notes = "转到后台管理-评论页")
    @GetMapping(value = "admin/review")
    public String goToPage(HttpSession session, Map<String, Object> map) {
        //检查管理员权限
        Object adminId = checkAdmin(session);
        if (adminId == null) {
            return "admin/include/loginMessage";
        }
        //根据{}排序，是否倒序:{}, orderBy, isDesc
        OrderUtil orderUtil = new OrderUtil("review_createdate", true);
        //获取前10条评论列表
        PageUtil pageUtil = new PageUtil(0, 10);
        List<Review> reviewList = reviewService.getList(new Review(), orderUtil, pageUtil);
        map.put("reviewList", reviewList);
        //获取评论总数量
        Integer reviewCount = reviewService.getTotal(new Review());
        map.put("reviewCount", reviewCount);
        //获取分页信息
        pageUtil.setTotal(reviewCount);
        map.put("pageUtil", pageUtil);

        //转到后台管理-评论页-ajax方式
        return "admin/reviewManagePage";
    }

    //转到后台管理-评论详情页-ajax
    @ApiOperation(value = "转到后台管理-评论详情页", notes = "转到后台管理-评论详情页")
    @GetMapping(value = "admin/review/{cid}")
    public String goToDetailsPage(HttpSession session, Map<String, Object> map, @PathVariable Integer cid/* 评论ID */) {
        //检查管理员权限
        Object adminId = checkAdmin(session);
        if (adminId == null) {
            return "admin/include/loginMessage";
        }
        //获取review_id为{}的评论信息, cid
        Review review = reviewService.get(cid);
        map.put("review", review);

        //转到后台管理-评论详情页-ajax方式
        return "admin/include/reviewDetails";
    }


    //按条件查询评论-ajax
    @ApiOperation(value = "按条件查询评论", notes = "按条件查询评论")
    @ResponseBody
    @GetMapping(value = "admin/review/{index}/{count}", produces = "application/json;charset=utf-8")
    public String getreviewBySearch(@RequestParam(required = false) String review_name/* 评论产品 */,
                                    @RequestParam(required = false) String review_content/* 评论内容 */,
                                    @RequestParam(required = false) String review_userName/* 评论人 */,
                                    @RequestParam(required = false) String review_createDate/* 评论时间 */,
                                    @RequestParam(required = false) String orderBy/* 排序字段 */,
                                    @RequestParam(required = false, defaultValue = "true") Boolean isDesc/* 是否倒序 */,
                                    @PathVariable Integer index/* 页数 */,
                                    @PathVariable Integer count/* 行数 */) throws UnsupportedEncodingException {
        //移除不必要条件
        if (review_name != null) {
            //如果为非空字符串则解决中文乱码
            review_name = "".equals(review_name) ? null : URLDecoder.decode(review_name, "UTF-8");
        }
        if (review_content != null) {
            //如果为非空字符串则解决中文乱码
            review_content = "".equals(review_content) ? null : URLDecoder.decode(review_content, "UTF-8");
        }
        if (review_userName != null) {
            //如果为非空字符串则解决中文乱码
            review_userName = "".equals(review_userName) ? null : URLDecoder.decode(review_userName, "UTF-8");
        }
        if (orderBy == null || "".equals(orderBy)) {
            orderBy = "review_createdate";
        }
        OrderUtil orderUtil = null;
        if (orderBy != null) {
            //根据{}排序，是否倒序:{}, orderBy, isDesc
            orderUtil = new OrderUtil(orderBy, isDesc);
        }
        JSONObject object = new JSONObject();
        Review review = new Review();
        Product product = new Product();
        product.setProduct_name(review_name);
        review.setReview_product(product);
        review.setReview_content(review_content);
        User user = new User();
        user.setUser_name(review_userName);
        review.setReview_user(user);
        review.setReview_createDate(review_createDate);
        //按条件获取第{}页的{}条评论, index + 1, count
        PageUtil pageUtil = new PageUtil(index, count);
        List<Review> reviewList = reviewService.getList(review, orderUtil, pageUtil);
        object.put("reviewList", JSON.parseArray(JSON.toJSONString(reviewList)));
        //按条件获取评论总数量
        Integer reviewCount = reviewService.getTotal(review);
        object.put("reviewCount", reviewCount);
        //获取分页信息
        pageUtil.setTotal(reviewCount);
        object.put("totalPage", pageUtil.getTotalPage());
        object.put("pageUtil", pageUtil);

        return String.valueOf(object);
    }

    //按ID删除评论并返回最新结果-ajax
    @ApiOperation(value = "按ID删除评论并返回最新结果", notes = "按ID删除评论并返回最新结果")
    @ResponseBody
    @GetMapping(value = "admin/review/del/{id}", produces = "application/json;charset=utf-8")
    public String deleteProductById(@PathVariable Integer id) {
        JSONObject object = new JSONObject();
        boolean yn = reviewService.deleteData(id);
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