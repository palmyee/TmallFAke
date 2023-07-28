package com.xq.tmall.controller.fore;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xq.tmall.controller.BaseController;
import com.xq.tmall.entity.*;
import com.xq.tmall.service.*;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 前台天猫-产品详情页
 */
@Api(tags = "前台天猫-产品详情页")
@Controller
public class ForeProductDetailsController extends BaseController {
    @Autowired
    private ProductService productService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductImageService productImageService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private PropertyValueService propertyValueService;
    @Autowired
    private PropertyService propertyService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private ProductOrderItemService productOrderItemService;

    //转到前台天猫-产品详情页
    @ApiOperation(value = "转到前台天猫-产品详情页", notes = "转到前台天猫-产品详情页")
    @GetMapping(value = "product/{pid}")
    public String goToPage(HttpSession session, Map<String, Object> map,
                           @PathVariable("pid") String pid /*产品ID*/) {
        //检查用户是否登录
        Object userId = checkUser(session);
        if (userId != null) {
            //获取用户信息
            User user = userService.get(Integer.parseInt(userId.toString()));
            map.put("user", user);
        }
        //获取产品ID
        Integer product_id = Integer.parseInt(pid);
        //获取产品信息
        Product product = productService.get(product_id);
        if (product == null || product.getProduct_isEnabled() == 1) {
            return "redirect:/404";
        }
        //获取产品子信息-分类信息
        product.setProduct_category(categoryService.get(product.getProduct_category().getCategory_id()));
        //获取产品子信息-产品图片信息
        List<ProductImage> productImageList = productImageService.getList(product_id, null, null);
        List<ProductImage> singleProductImageList = new ArrayList<>(5);
        List<ProductImage> detailsProductImageList = new ArrayList<>(8);
        for (ProductImage productImage : productImageList) {
            if (productImage.getProductImage_type() == 0) {
                singleProductImageList.add(productImage);
            } else {
                detailsProductImageList.add(productImage);
            }
        }
        product.setSingleProductImageList(singleProductImageList);
        product.setDetailProductImageList(detailsProductImageList);
        //获取产品子信息-产品属性值信息
        PropertyValue propertyValue1 = new PropertyValue();
        propertyValue1.setPropertyValue_product(product);
        List<PropertyValue> propertyValueList = propertyValueService.getList(propertyValue1, null);
        //获取产品子信息-分类信息对应的属性列表
        Property property1 = new Property();
        property1.setProperty_category(product.getProduct_category());
        List<Property> propertyList = propertyService.getList(property1, null);
        //属性列表和属性值列表合并
        for (Property property : propertyList) {
            for (PropertyValue propertyValue : propertyValueList) {
                if (property.getProperty_id().equals(propertyValue.getPropertyValue_property().getProperty_id())) {
                    List<PropertyValue> property_value_item = new ArrayList<>(1);
                    property_value_item.add(propertyValue);
                    property.setPropertyValueList(property_value_item);
                    break;
                }
            }
        }
        //获取产品子信息-产品评论信息
        product.setReviewList(reviewService.getListByProductId(product_id, null));
        if (product.getReviewList() != null) {
            for (Review review : product.getReviewList()) {
                review.setReview_user(userService.get(review.getReview_user().getUser_id()));
            }
        }

        //获取产品子信息-销量数和评论数信息
        product.setProduct_sale_count(productOrderItemService.getSaleCountByProductId(product_id));
        product.setProduct_review_count(reviewService.getTotalByProductId(product_id));

        //获取猜你喜欢列表
        Integer category_id = product.getProduct_category().getCategory_id();
        Product product1 = new Product();
        Category category = new Category();
        category.setCategory_id(category_id);
        product1.setProduct_category(category);
        Integer total = productService.getTotal(product1, new Byte[]{0, 2});
        //分类ID为{}的产品总数为{}条, category_id, total
        //生成随机数
        int i = new Random().nextInt(total);
        if (i + 2 >= total) {
            i = total - 3;
        }
        if (i < 0) {
            i = 0;
        }
        List<Product> loveProductList = productService.getList(product1, new Byte[]{0, 2}, null, new PageUtil().setCount(3).setPageStart(i));
        if (loveProductList != null) {
            //获取产品列表的相应的一张预览图片
            for (Product loveProduct : loveProductList) {
                loveProduct.setSingleProductImageList(productImageService.getList(loveProduct.getProduct_id(), (byte) 0, new PageUtil(0, 1)));
            }
        }
        //获取分类列表
        List<Category> categoryList = categoryService.getList(null, new PageUtil(0, 3));

        map.put("loveProductList", loveProductList);
        map.put("categoryList", categoryList);
        map.put("propertyList", propertyList);
        map.put("product", product);
        map.put("guessNumber", i);
        map.put("pageUtil", new PageUtil(0, 10).setTotal(product.getProduct_review_count()));
        //转到前台-产品详情页
        return "fore/productDetailsPage";
    }

    //按产品ID加载产品评论列表-ajax
    @ApiOperation(value = "按产品ID加载产品评论列表", notes = "按产品ID加载产品评论列表")
    @Deprecated
    @ResponseBody
    @GetMapping(value = "review/{pid}", produces = "application/json;charset=utf-8")
    public String loadProductReviewList(@PathVariable("pid") String pid/*产品ID*/,
                                        @RequestParam Integer index/* 页数 */,
                                        @RequestParam Integer count/* 行数 */) {
        //获取产品ID
        Integer product_id = Integer.parseInt(pid);
        //获取产品评论列表
        List<Review> reviewList = reviewService.getListByProductId(product_id, new PageUtil(index, count));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("reviewList", JSON.parseArray(JSON.toJSONString(reviewList)));

        return String.valueOf(jsonObject);
    }

    //按产品ID加载产品属性列表-ajax
    @ApiOperation(value = "按产品ID加载产品属性列表", notes = "按产品ID加载产品属性列表")
    @Deprecated
    @ResponseBody
    @GetMapping(value = "property/{pid}", produces = "application/json;charset=utf-8")
    public String loadProductPropertyList(@PathVariable("pid") String pid/*产品ID*/) {
        //获取产品ID
        Integer product_id = Integer.parseInt(pid);

        //获取产品详情-属性值信息
        Product product = new Product();
        product.setProduct_id(product_id);
        PropertyValue propertyValue1 = new PropertyValue();
        propertyValue1.setPropertyValue_product(product);
        List<PropertyValue> propertyValueList = propertyValueService.getList(propertyValue1, null);

        //获取产品详情-分类信息对应的属性列表
        Property property1 = new Property();
        property1.setProperty_category(product.getProduct_category());
        List<Property> propertyList = propertyService.getList(property1, null);

        //属性列表和属性值列表合并
        for (Property property : propertyList) {
            for (PropertyValue propertyValue : propertyValueList) {
                if (property.getProperty_id().equals(propertyValue.getPropertyValue_property().getProperty_id())) {
                    List<PropertyValue> property_value_item = new ArrayList<>(1);
                    property_value_item.add(propertyValue);
                    property.setPropertyValueList(property_value_item);
                    break;
                }
            }
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("propertyList", JSON.parseArray(JSON.toJSONString(propertyList)));

        return String.valueOf(jsonObject);
    }

    //加载猜你喜欢列表-ajax
    @ApiOperation(value = "加载猜你喜欢列表", notes = "加载猜你喜欢列表")
    @ResponseBody
    @GetMapping(value = "guess/{cid}", produces = "application/json;charset=utf-8")
    public String guessYouLike(@PathVariable("cid") Integer cid, @RequestParam Integer guessNumber) {
        Random rand = new Random();
        //获取猜你喜欢列表
        Category category = new Category();
        category.setCategory_id(cid);
        Product product = new Product();
        product.setProduct_category(category);
        Integer total = productService.getTotal(product, new Byte[]{0, 2});
        //分类ID为{}的产品总数为{}条, cid, total
        //生成随机数
        int i = rand.nextInt(total);
        if (i + 2 >= total) {
            i = total - 3;
        }
        if (i < 0) {
            i = 0;
        }
        while (i == guessNumber) {
            i = rand.nextInt(total);
            if (i + 2 >= total) {
                i = total - 3;
            }
            if (i < 0) {
                i = 0;
                break;
            }
        }

        //guessNumber值为{}，新guessNumber值为{}, guessNumber, i
        List<Product> loveProductList = productService.getList(product, new Byte[]{0, 2}, null, new PageUtil().setCount(3).setPageStart(i));
        if (loveProductList != null) {
            //获取产品列表的相应的一张预览图片
            for (Product loveProduct : loveProductList) {
                loveProduct.setSingleProductImageList(productImageService.getList(loveProduct.getProduct_id(), (byte) 0, new PageUtil(0, 1)));
            }
        }

        JSONObject jsonObject = new JSONObject();
        //获取数据成功！
        jsonObject.put("success", true);
        jsonObject.put("loveProductList", JSON.parseArray(JSON.toJSONString(loveProductList)));
        jsonObject.put("guessNumber", i);
        return String.valueOf(jsonObject);
    }
}
