package com.xq.tmall.controller.fore;

import com.alibaba.fastjson.JSON;
import com.xq.tmall.controller.BaseController;
import com.xq.tmall.entity.Category;
import com.xq.tmall.entity.Product;
import com.xq.tmall.entity.User;
import com.xq.tmall.service.*;
import com.xq.tmall.util.OrderUtil;
import com.xq.tmall.util.PageUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * 前台天猫-产品搜索列表
 */
@Api(tags = "前台天猫-产品搜索列表")
@Controller
public class ForeProductListController extends BaseController {
    @Autowired
    private ProductService productService;
    @Autowired
    private UserService userService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductImageService productImageService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private ProductOrderItemService productOrderItemService;


    //转到前台天猫-产品搜索列表页
    @ApiOperation(value = "转到前台天猫-产品搜索列表页", notes = "转到前台天猫-产品搜索列表页")
    @GetMapping(value = "product")
    public String goToPage(HttpSession session, Map<String, Object> map,
                           @RequestParam(value = "category_id", required = false) Integer category_id/* 分类ID */,
                           @RequestParam(value = "product_name", required = false) String product_name/* 产品名称 */) throws UnsupportedEncodingException {
        //检查用户是否登录
        Object userId = checkUser(session);
        if (userId != null) {
            //获取用户信息
            User user = userService.get(Integer.parseInt(userId.toString()));
            map.put("user", user);
        }
        if (category_id == null && product_name == null) {
            return "redirect:/";
        }
        if (product_name != null && "".equals(product_name.trim())) {
            return "redirect:/";
        }

        //整合搜索信息
        Product product = new Product();
        String searchValue = null;
        Integer searchType = null;

        if (category_id != null) {
            Category category = new Category();
            category.setCategory_id(category_id);
            product.setProduct_category(category);
            searchType = category_id;
        }
        //关键词数组
        String[] product_name_split = null;
        //产品列表
        List<Product> productList;
        //产品总数量
        Integer productCount;
        //分页工具
        PageUtil pageUtil = new PageUtil(0, 20);
        if (product_name != null) {
            product_name_split = product_name.split(" ");
            //提取的关键词有{}, Arrays.toString(product_name_split)
            product.setProduct_name(product_name);
            searchValue = product_name;
        }
        if (product_name_split != null && product_name_split.length > 1) {
            //获取组合商品列表
            productList = productService.getMoreList(product, new Byte[]{0, 2}, null, pageUtil, product_name_split);
            //按组合条件获取产品总数量
            productCount = productService.getMoreListTotal(product, new Byte[]{0, 2}, product_name_split);
        } else {
            //获取商品列表
            productList = productService.getList(product, new Byte[]{0, 2}, null, pageUtil);
            //按条件获取产品总数量
            productCount = productService.getTotal(product, new Byte[]{0, 2});
        }
        //获取商品列表的对应信息
        for (Product p : productList) {
            p.setSingleProductImageList(productImageService.getList(p.getProduct_id(), (byte) 0, null));
            p.setProduct_sale_count(productOrderItemService.getSaleCountByProductId(p.getProduct_id()));
            p.setProduct_review_count(reviewService.getTotalByProductId(p.getProduct_id()));
            p.setProduct_category(categoryService.get(p.getProduct_category().getCategory_id()));
        }
        //获取分类列表
        List<Category> categoryList = categoryService.getList(null, new PageUtil(0, 5));
        //获取分页信息
        pageUtil.setTotal(productCount);

        map.put("categoryList", categoryList);
        map.put("totalPage", pageUtil.getTotalPage());
        map.put("pageUtil", pageUtil);
        map.put("productList", productList);
        map.put("searchValue", searchValue);
        map.put("searchType", searchType);

        //转到前台天猫-产品搜索列表页
        return "fore/productListPage";
    }

    //产品高级查询
    @ApiOperation(value = "产品高级查询", notes = "产品高级查询")
    @GetMapping(value = "product/{index}/{count}")
    public String searchProduct(HttpSession session, Map<String, Object> map,
                                @PathVariable("index") Integer index/* 页数 */,
                                @PathVariable("count") Integer count/* 行数*/,
                                @RequestParam(value = "category_id", required = false) Integer category_id/* 分类ID */,
                                @RequestParam(value = "product_name", required = false) String product_name/* 产品名称 */,
                                @RequestParam(required = false) String orderBy/* 排序字段 */,
                                @RequestParam(required = false, defaultValue = "true") Boolean isDesc/* 是否倒序 */) {
        //整合搜索信息
        Product product = new Product();
        OrderUtil orderUtil = null;
        String searchValue = null;
        Integer searchType = null;

        if (category_id != null) {
            Category category = new Category();
            category.setCategory_id(category_id);
            product.setProduct_category(category);
            searchType = category_id;
        }
        if (product_name != null) {
            product.setProduct_name(product_name);
        }
        if (orderBy != null) {
            //根据{}排序，是否倒序:{}, orderBy, isDesc
            orderUtil = new OrderUtil(orderBy, isDesc);
        }
        //关键词数组
        String[] product_name_split = null;
        //产品列表
        List<Product> productList;
        //产品总数量
        Integer productCount;
        //分页工具
        PageUtil pageUtil = new PageUtil(0, 20);
        if (product_name != null) {
            product_name_split = product_name.split(" ");
            //提取的关键词有{}, Arrays.toString(product_name_split)
            product.setProduct_name(product_name);
            searchValue = product_name;
        }
        if (product_name_split != null && product_name_split.length > 1) {
            //获取组合商品列表
            productList = productService.getMoreList(product, new Byte[]{0, 2}, orderUtil, pageUtil, product_name_split);
            //按组合条件获取产品总数量
            productCount = productService.getMoreListTotal(product, new Byte[]{0, 2}, product_name_split);
        } else {
            //获取商品列表
            productList = productService.getList(product, new Byte[]{0, 2}, orderUtil, pageUtil);
            //按条件获取产品总数量
            productCount = productService.getTotal(product, new Byte[]{0, 2});
        }
        //获取商品列表的对应信息
        for (Product p : productList) {
            p.setSingleProductImageList(productImageService.getList(p.getProduct_id(), (byte) 0, null));
            p.setProduct_sale_count(productOrderItemService.getSaleCountByProductId(p.getProduct_id()));
            p.setProduct_review_count(reviewService.getTotalByProductId(p.getProduct_id()));
            p.setProduct_category(categoryService.get(p.getProduct_category().getCategory_id()));
        }
        //获取分类列表
        List<Category> categoryList = categoryService.getList(null, new PageUtil(0, 5));
        //获取分页信息
        pageUtil.setTotal(productCount);

        map.put("productCount", productCount);
        map.put("totalPage", pageUtil.getTotalPage());
        map.put("pageUtil", pageUtil);
        map.put("productList", JSON.parseArray(JSON.toJSONString(productList)));
        map.put("orderBy", orderBy);
        map.put("isDesc", isDesc);
        map.put("searchValue", searchValue);
        map.put("searchType", searchType);
        map.put("categoryList", categoryList);

        return "fore/productListPage";
    }
}