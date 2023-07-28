package com.xq.tmall.controller.fore;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.xq.tmall.controller.BaseController;
import com.xq.tmall.entity.Category;
import com.xq.tmall.entity.Product;
import com.xq.tmall.entity.User;
import com.xq.tmall.service.CategoryService;
import com.xq.tmall.service.ProductImageService;
import com.xq.tmall.service.ProductService;
import com.xq.tmall.service.UserService;
import com.xq.tmall.util.Constants;
import com.xq.tmall.util.OrderUtil;
import com.xq.tmall.util.PageUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 前台天猫-主页
 */
@Api(tags = "前台天猫-主页")
@Controller
public class ForeHomeController extends BaseController {
    @Autowired
    private UserService userService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductImageService productImageService;

    //转到前台天猫-主页
    @ApiOperation(value = "转到前台天猫-主页", notes = "转到前台天猫-主页")
    @GetMapping(value = "/")
    public String goToPage(HttpSession session, Map<String, Object> map) {
        //检查用户是否登录
        Object userId = checkUser(session);
        if (userId != null) {
            //获取用户信息
            User user = userService.get(Integer.parseInt(userId.toString()));
            map.put("user", user);
        }
        //获取产品分类列表
        List<Category> categoryList = categoryService.getList(null, null);
        //获取每个分类下的产品列表
        if (CollectionUtils.isNotEmpty(categoryList)) {
            for (Category category : categoryList) {
                //获取分类id为{}的产品集合，按产品ID倒序排序, category.getCategory_id()
                Product product1 = new Product();
                product1.setProduct_category(category);
                List<Product> productList = productService.getList(product1, new Byte[]{0, 2}, new OrderUtil("product_id", true), new PageUtil(0, 8));
                if (CollectionUtils.isNotEmpty(productList)) {
                    for (Product product : productList) {
                        //获取产品id为{}的产品预览图片信息, product_id
                        product.setSingleProductImageList(productImageService.getList(product.getProduct_id(), (byte) 0, new PageUtil(0, 1)));
                    }
                }
                category.setProductList(productList);
            }
        }
        map.put("categoryList", categoryList);
        //获取促销产品列表
        List<Product> specialProductList = productService.getList(null, new Byte[]{2}, null, new PageUtil(0, 6));
        map.put("specialProductList", specialProductList);

        //转到前台主页
        return "fore/homePage";
    }

    //转到前台天猫-错误页
    @ApiOperation(value = "转到前台天猫-错误页", notes = "转到前台天猫-错误页")
    @GetMapping(value = "error")
    public String goToErrorPage() {
        return "fore/errorPage";
    }

    //获取主页分类下产品信息-ajax
    @ApiOperation(value = "获取主页分类下产品信息", notes = "获取主页分类下产品信息")
    @ResponseBody
    @GetMapping(value = "product/nav/{category_id}", produces = "application/json;charset=utf-8")
    public String getProductByNav(@PathVariable("category_id") Integer category_id) {
        JSONObject object = new JSONObject();
        if (category_id == null) {
            object.put(Constants.SUCCESS, false);
            return String.valueOf(object);
        }
        //获取分类ID为{}的产品标题数据, category_id
        Category category1 = new Category();
        category1.setCategory_id(category_id);
        Product product = new Product();
        product.setProduct_category(category1);
        List<Product> productList = productService.getTitle(product, new PageUtil(0, 40));
        List<List<Product>> complexProductList = new ArrayList<>(8);
        List<Product> products = new ArrayList<>(5);
        if (CollectionUtils.isNotEmpty(productList)) {
            for (int i = 0; i < productList.size(); i++) {
                //如果临时集合中产品数达到5个，加入到产品二维集合中，并重新实例化临时集合
                if (i % 5 == 0) {
                    complexProductList.add(products);
                    products = new ArrayList<>(5);
                }
                products.add(productList.get(i));
            }
        }
        complexProductList.add(products);
        Category category = new Category();
        category.setCategory_id(category_id);
        category.setComplexProductList(complexProductList);
        object.put(Constants.SUCCESS, true);
        object.put("category", category);
        return String.valueOf(object);
    }
}
