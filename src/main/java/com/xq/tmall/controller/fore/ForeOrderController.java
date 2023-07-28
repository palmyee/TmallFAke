package com.xq.tmall.controller.fore;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.thoughtworks.xstream.core.BaseException;
import com.xq.tmall.controller.BaseController;
import com.xq.tmall.entity.*;
import com.xq.tmall.pay.PayFace;
import com.xq.tmall.pay.req.TradeOrderReq;
import com.xq.tmall.pay.resp.TradeOrderResp;
import com.xq.tmall.pay.util.PayModelEnum;
import com.xq.tmall.service.*;
import com.xq.tmall.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 前台天猫-订单
 */
@Api(tags = "前台天猫-订单")
@Controller
public class ForeOrderController extends BaseController {
    @Autowired
    private ProductService productService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductOrderItemService productOrderItemService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductImageService productImageService;
    @Autowired
    private ProductOrderService productOrderService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private LastIDService lastIDService;
    public static final String URL = "redirect:/order/0/10";

    public static final String LOGIN = "redirect:/login";
    public static final String ORDER = "/order/0/10";
    @NotNull
    private final Map<String, PayFace> payFaceMap;
    public static final String CART = "redirect:/cart";

    public ForeOrderController(Map<String, PayFace> payFaceMap) {
        this.payFaceMap = payFaceMap;
    }


    //转到前台天猫-订单列表页
    @ApiOperation(value = "转到前台天猫-订单列表页", notes = "转到前台天猫-订单列表页")
    @GetMapping(value = "order")
    public String goToPageSimple() {
        return URL;
    }

    @ApiOperation(value = "退出当前账号", notes = "退出当前账号")
    @GetMapping(value = "order/{index}/{count}")
    public String goToPage(HttpSession session, Map<String, Object> map,
                           @RequestParam(required = false) Byte status,
                           @PathVariable("index") Integer index/* 页数 */,
                           @PathVariable("count") Integer count/* 行数*/) {
        //检查用户是否登录
        Object userId = checkUser(session);
        User user;
        if (userId != null) {
            //获取用户信息
            user = userService.get(Integer.parseInt(userId.toString()));
            map.put("user", user);
        } else {
            return LOGIN;
        }
        Byte[] status_array = null;
        if (status != null) {
            status_array = new Byte[]{status};
        }

        PageUtil pageUtil = new PageUtil(index, count);
        //根据用户ID:{}获取订单列表, userId
        User user1 = new User();
        user1.setUser_id(Integer.valueOf(userId.toString()));
        ProductOrder productOrder = new ProductOrder();
        productOrder.setProductOrder_user(user1);
        List<ProductOrder> productOrderList = productOrderService.getList(productOrder, status_array, new OrderUtil("productOrder_id", true), pageUtil);

        //订单总数量
        Integer orderCount = 0;
        if (CollectionUtils.isNotEmpty(productOrderList)) {
            orderCount = productOrderService.getTotal(productOrder, status_array);
            //获取订单项信息及对应的产品信息
            if (CollectionUtils.isNotEmpty(productOrderList)) {
                for (ProductOrder order : productOrderList) {
                    List<ProductOrderItem> productOrderItemList = productOrderItemService.getListByOrderId(order.getProductOrder_id(), null);
                    if (CollectionUtils.isNotEmpty(productOrderItemList)) {
                        for (ProductOrderItem productOrderItem : productOrderItemList) {
                            Integer product_id = productOrderItem.getProductOrderItem_product().getProduct_id();
                            Product product = productService.get(product_id);
                            product.setSingleProductImageList(productImageService.getList(product_id, (byte) 0, new PageUtil(0, 1)));
                            productOrderItem.setProductOrderItem_product(product);
                            if (order.getProductOrder_status() == 3) {
                                productOrderItem.setIsReview(reviewService.getTotalByOrderItemId(productOrderItem.getProductOrderItem_id()) > 0);
                            }
                        }
                    }
                    order.setProductOrderItemList(productOrderItemList);
                }
            }
        }
        pageUtil.setTotal(orderCount);

        //获取产品分类列表信息
        List<Category> categoryList = categoryService.getList(null, new PageUtil(0, 5));
        map.put("pageUtil", pageUtil);
        map.put("productOrderList", productOrderList);
        map.put("categoryList", categoryList);
        map.put("status", status);

        //转到前台天猫-订单列表页
        return "fore/orderListPage";
    }

    //转到前台天猫-订单建立页
    @ApiOperation(value = "转到前台天猫-订单建立页", notes = "转到前台天猫-订单建立页")
    @GetMapping(value = "order/create/{product_id}")
    public String goToOrderConfirmPage(@PathVariable("product_id") Integer product_id,
                                       @RequestParam(required = false, defaultValue = "1") Short product_number,
                                       Map<String, Object> map,
                                       HttpSession session,
                                       HttpServletRequest request) throws UnsupportedEncodingException {
        //检查用户是否登录
        Object userId = checkUser(session);
        User user;
        if (userId != null) {
            //获取用户信息
            user = userService.get(Integer.parseInt(userId.toString()));
            map.put("user", user);
        } else {
            return LOGIN;
        }
        //通过产品ID获取产品信息：{}, product_id
        Product product = productService.get(product_id);
        if (product == null) {
            return "redirect:/";
        }
        //获取产品的详细信息
        product.setProduct_category(categoryService.get(product.getProduct_category().getCategory_id()));
        product.setSingleProductImageList(productImageService.getList(product_id, (byte) 0, new PageUtil(0, 1)));

        //封装订单项对象
        ProductOrderItem productOrderItem = new ProductOrderItem();
        productOrderItem.setProductOrderItem_product(product);
        productOrderItem.setProductOrderItem_number(product_number);
        productOrderItem.setProductOrderItem_price(product.getProduct_sale_price() * product_number);
        User user1 = new User();
        user1.setUser_id(Integer.valueOf(userId.toString()));
        productOrderItem.setProductOrderItem_user(user1);

        String addressId = "110000";
        String cityAddressId = "110100";
        String districtAddressId = "110101";
        String detailsAddress = null;
        String order_post = null;
        String order_receiver = null;
        String order_phone = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                String cookieName = cookie.getName();
                String cookieValue = cookie.getValue();
                switch (cookieName) {
                    case "addressId":
                        addressId = cookieValue;
                        break;
                    case "cityAddressId":
                        cityAddressId = cookieValue;
                        break;
                    case "districtAddressId":
                        districtAddressId = cookieValue;
                        break;
                    case "order_post":
                        order_post = URLDecoder.decode(cookieValue, Constants.UTF);
                        break;
                    case "order_receiver":
                        order_receiver = URLDecoder.decode(cookieValue, Constants.UTF);
                        break;
                    case "order_phone":
                        order_phone = URLDecoder.decode(cookieValue, Constants.UTF);
                        break;
                    case "detailsAddress":
                        detailsAddress = URLDecoder.decode(cookieValue, Constants.UTF);
                        break;
                    default:
                }
            }
        }
        //获取省份信息
        List<Address> addressList = addressService.getRoot();
        //获取addressId为{}的市级地址信息, addressId
        List<Address> cityAddress = addressService.getList(null, addressId);
        //获取cityAddressId为{}的区级地址信息, cityAddressId
        List<Address> districtAddress = addressService.getList(null, cityAddressId);

        List<ProductOrderItem> productOrderItemList = new ArrayList<>();
        productOrderItemList.add(productOrderItem);

        map.put("orderItemList", productOrderItemList);
        map.put("addressList", addressList);
        map.put("cityList", cityAddress);
        map.put("districtList", districtAddress);
        map.put("orderTotalPrice", productOrderItem.getProductOrderItem_price());

        map.put("addressId", addressId);
        map.put("cityAddressId", cityAddressId);
        map.put("districtAddressId", districtAddressId);
        map.put("order_post", order_post);
        map.put("order_receiver", order_receiver);
        map.put("order_phone", order_phone);
        map.put("detailsAddress", detailsAddress);

        //转到前台天猫-订单建立页
        return "fore/productBuyPage";
    }

    //转到前台天猫-购物车订单建立页
    @ApiOperation(value = "转到前台天猫-购物车订单建立页", notes = "转到前台天猫-购物车订单建立页")
    @GetMapping(value = "order/create/byCart")
    public String goToOrderConfirmPageByCart(Map<String, Object> map,
                                             HttpSession session, HttpServletRequest request,
                                             @RequestParam(required = false) Integer[] order_item_list) throws UnsupportedEncodingException {
        //检查用户是否登录
        Object userId = checkUser(session);
        User user;
        if (userId != null) {
            //获取用户信息
            user = userService.get(Integer.parseInt(userId.toString()));
            map.put("user", user);
        } else {
            return LOGIN;
        }
        if (order_item_list == null || order_item_list.length == 0) {
            //用户订单项数组不存在，回到购物车页
            return CART;
        }
        //通过订单项ID数组获取订单信息
        List<ProductOrderItem> orderItemList = new ArrayList<>(order_item_list.length);
        for (Integer orderItem_id : order_item_list) {
            orderItemList.add(productOrderItemService.get(orderItem_id));
        }
        //------检查订单项合法性------
        if (orderItemList.size() == 0) {
            //用户订单项获取失败，回到购物车页
            return CART;
        }
        for (ProductOrderItem orderItem : orderItemList) {
            if (orderItem.getProductOrderItem_user().getUser_id() != userId) {
                //用户订单项与用户不匹配，回到购物车页
                return CART;
            }
            if (orderItem.getProductOrderItem_order() != null) {
                //用户订单项不属于购物车，回到购物车页
                return CART;
            }
        }
        //验证通过，获取订单项的产品信息
        double orderTotalPrice = 0.0;
        for (ProductOrderItem orderItem : orderItemList) {
            Product product = productService.get(orderItem.getProductOrderItem_product().getProduct_id());
            product.setProduct_category(categoryService.get(product.getProduct_category().getCategory_id()));
            product.setSingleProductImageList(productImageService.getList(product.getProduct_id(), (byte) 0, new PageUtil(0, 1)));
            orderItem.setProductOrderItem_product(product);
            orderTotalPrice += orderItem.getProductOrderItem_price();
        }
        String addressId = "110000";
        String cityAddressId = "110100";
        String districtAddressId = "110101";
        String detailsAddress = null;
        String order_post = null;
        String order_receiver = null;
        String order_phone = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                String cookieName = cookie.getName();
                String cookieValue = cookie.getValue();
                switch (cookieName) {
                    case "addressId":
                        addressId = cookieValue;
                        break;
                    case "cityAddressId":
                        cityAddressId = cookieValue;
                        break;
                    case "districtAddressId":
                        districtAddressId = cookieValue;
                        break;
                    case "order_post":
                        order_post = URLDecoder.decode(cookieValue, Constants.UTF);
                        break;
                    case "order_receiver":
                        order_receiver = URLDecoder.decode(cookieValue, Constants.UTF);
                        break;
                    case "order_phone":
                        order_phone = URLDecoder.decode(cookieValue, Constants.UTF);
                        break;
                    case "detailsAddress":
                        detailsAddress = URLDecoder.decode(cookieValue, Constants.UTF);
                        break;
                    default:
                }
            }
        }
        //获取省份信息
        List<Address> addressList = addressService.getRoot();
        //获取addressId为{}的市级地址信息, addressId
        List<Address> cityAddress = addressService.getList(null, addressId);
        //获取cityAddressId为{}的区级地址信息, cityAddressId
        List<Address> districtAddress = addressService.getList(null, cityAddressId);

        map.put("orderItemList", orderItemList);
        map.put("addressList", addressList);
        map.put("cityList", cityAddress);
        map.put("districtList", districtAddress);
        map.put("orderTotalPrice", orderTotalPrice);

        map.put("addressId", addressId);
        map.put("cityAddressId", cityAddressId);
        map.put("districtAddressId", districtAddressId);
        map.put("order_post", order_post);
        map.put("order_receiver", order_receiver);
        map.put("order_phone", order_phone);
        map.put("detailsAddress", detailsAddress);

        //转到前台天猫-订单建立页
        return "fore/productBuyPage";
    }

    //转到前台天猫-订单支付页
    @ApiOperation(value = "转到前台天猫-订单支付页", notes = "转到前台天猫-订单支付页")
    @GetMapping(value = "order/pay/{order_code}")
    public String goToOrderPayPage(Map<String, Object> map, HttpSession session,
                                   @PathVariable("order_code") String order_code) {
        //检查用户是否登录
        Object userId = checkUser(session);
        User user;
        if (userId != null) {
            //获取用户信息
            user = userService.get(Integer.parseInt(userId.toString()));
            map.put("user", user);
        } else {
            return LOGIN;
        }
        //------验证订单信息------
        //查询订单是否存在
        ProductOrder order = productOrderService.getByCode(order_code);
        if (order == null) {
            //订单不存在，返回订单列表页
            return URL;
        }
        //验证订单状态
        if (order.getProductOrder_status() != 0) {
            //订单状态不正确，返回订单列表页
            return URL;
        }
        //验证用户与订单是否一致
        if (order.getProductOrder_user().getUser_id() != Integer.parseInt(userId.toString())) {
            //用户与订单信息不一致，返回订单列表页
            return URL;
        }

        order.setProductOrderItemList(productOrderItemService.getListByOrderId(order.getProductOrder_id(), null));

        double orderTotalPrice = 0.00;
        if (order.getProductOrderItemList().size() == 1) {
            //获取单订单项的产品信息
            ProductOrderItem productOrderItem = order.getProductOrderItemList().get(0);
            Product product = productService.get(productOrderItem.getProductOrderItem_product().getProduct_id());
            product.setProduct_category(categoryService.get(product.getProduct_category().getCategory_id()));
            productOrderItem.setProductOrderItem_product(product);
            orderTotalPrice = productOrderItem.getProductOrderItem_price();
        } else {
            for (ProductOrderItem productOrderItem : order.getProductOrderItemList()) {
                orderTotalPrice += productOrderItem.getProductOrderItem_price();
            }
        }
        //订单总金额为：{}元, orderTotalPrice
        // 创建支付
       /* TradeOrderResp orderResp = createPay(1, "", order,orderTotalPrice);
        if (orderResp.isSuccess()) {
        map.put("productOrder", order);
        map.put("orderTotalPrice", orderTotalPrice);
        }*/

        map.put("productOrder", order);
        map.put("orderTotalPrice", orderTotalPrice);

        //转到前台天猫-订单支付页
        return "fore/productPayPage";
    }

    private TradeOrderResp createPay(Integer payType, String quitUrl, ProductOrder order, double orderTotalPrice) {
        // 下单支付
        String impl = PayTypeEnum.byCode(payType).getImpl();
        PayFace payFace = payFaceMap.get(impl);
        if (ObjectUtil.isNull(payFace)) {
            throw new RuntimeException();
        }
        TradeOrderReq orderReq = new TradeOrderReq();
        // 直连模式
        orderReq.setPayModel(PayModelEnum.DIRECT_SALES.getCode());
        orderReq.setTradeSerialNo(order.getProductOrder_code());
        orderReq.setAmount(BigDecimal.valueOf(orderTotalPrice));
        orderReq.setGoodsName("商品");
        //orderReq.setUserClientIp(userClientIp);
        orderReq.setNotifyUrl("fore/productPayPage");
        if (StringUtils.hasText(quitUrl)) {
            orderReq.setQuitUrl(quitUrl + "?tradeSerialNo=" + orderReq.getTradeSerialNo());
        }
        return payFace.tradeOrder(orderReq);
    }

    //转到前台天猫-订单支付成功页
    @ApiOperation(value = "转到前台天猫-订单支付成功页", notes = "转到前台天猫-订单支付成功页")
    @GetMapping(value = "order/pay/success/{order_code}")
    public String goToOrderPaySuccessPage(Map<String, Object> map, HttpSession session,
                                          @PathVariable("order_code") String order_code) {
        //检查用户是否登录
        Object userId = checkUser(session);
        User user;
        if (userId != null) {
            //获取用户信息
            user = userService.get(Integer.parseInt(userId.toString()));
            map.put("user", user);
        } else {
            return LOGIN;
        }
        //------验证订单信息------
        //查询订单是否存在
        ProductOrder order = productOrderService.getByCode(order_code);
        if (order == null) {
            //订单不存在，返回订单列表页
            return URL;
        }
        //验证订单状态
        if (order.getProductOrder_status() != 1) {
            //订单状态不正确，返回订单列表页
            return URL;
        }
        //验证用户与订单是否一致
        if (order.getProductOrder_user().getUser_id() != Integer.parseInt(userId.toString())) {
            //用户与订单信息不一致，返回订单列表页
            return URL;
        }
        order.setProductOrderItemList(productOrderItemService.getListByOrderId(order.getProductOrder_id(), null));

        double orderTotalPrice = 0.00;
        if (order.getProductOrderItemList().size() == 1) {
            //获取单订单项的产品信息
            ProductOrderItem productOrderItem = order.getProductOrderItemList().get(0);
            orderTotalPrice = productOrderItem.getProductOrderItem_price();
        } else {
            for (ProductOrderItem productOrderItem : order.getProductOrderItemList()) {
                orderTotalPrice += productOrderItem.getProductOrderItem_price();
            }
        }
        //订单总金额为：{}元, orderTotalPrice

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

        map.put("productOrder", order);
        map.put("orderTotalPrice", orderTotalPrice);

        //转到前台天猫-订单支付成功页
        return "fore/productPaySuccessPage";
    }

    //转到前台天猫-订单确认页
    @ApiOperation(value = "转到前台天猫-订单确认页", notes = "转到前台天猫-订单确认页")
    @GetMapping(value = "order/confirm/{order_code}")
    public String goToOrderConfirmPage(Map<String, Object> map, HttpSession session,
                                       @PathVariable("order_code") String order_code) {
        //检查用户是否登录
        Object userId = checkUser(session);
        User user;
        if (userId != null) {
            //获取用户信息
            user = userService.get(Integer.parseInt(userId.toString()));
            map.put("user", user);
        } else {
            return LOGIN;
        }
        //------验证订单信息------
        //查询订单是否存在
        ProductOrder order = productOrderService.getByCode(order_code);
        if (order == null) {
            //订单不存在，返回订单列表页
            return URL;
        }
        //验证订单状态
        if (order.getProductOrder_status() != 2) {
            //订单状态不正确，返回订单列表页
            return URL;
        }
        //验证用户与订单是否一致
        if (order.getProductOrder_user().getUser_id() != Integer.parseInt(userId.toString())) {
            //用户与订单信息不一致，返回订单列表页
            return URL;
        }
        order.setProductOrderItemList(productOrderItemService.getListByOrderId(order.getProductOrder_id(), null));

        double orderTotalPrice = 0.00;
        if (order.getProductOrderItemList().size() == 1) {
            //获取单订单项的产品信息
            ProductOrderItem productOrderItem = order.getProductOrderItemList().get(0);
            Integer product_id = productOrderItem.getProductOrderItem_product().getProduct_id();
            Product product = productService.get(product_id);
            product.setSingleProductImageList(productImageService.getList(product_id, (byte) 0, new PageUtil(0, 1)));
            productOrderItem.setProductOrderItem_product(product);
            orderTotalPrice = productOrderItem.getProductOrderItem_price();
        } else {
            //获取多订单项的产品信息
            for (ProductOrderItem productOrderItem : order.getProductOrderItemList()) {
                Integer product_id = productOrderItem.getProductOrderItem_product().getProduct_id();
                Product product = productService.get(product_id);
                product.setSingleProductImageList(productImageService.getList(product_id, (byte) 0, new PageUtil(0, 1)));
                productOrderItem.setProductOrderItem_product(product);
                orderTotalPrice += productOrderItem.getProductOrderItem_price();
            }
        }
        //订单总金额为：{}元, orderTotalPrice

        map.put("productOrder", order);
        map.put("orderTotalPrice", orderTotalPrice);

        //转到前台天猫-订单确认页
        return "fore/orderConfirmPage";
    }

    //转到前台天猫-订单完成页
    @ApiOperation(value = "转到前台天猫-订单完成页", notes = "转到前台天猫-订单完成页")
    @GetMapping(value = "order/success/{order_code}")
    public String goToOrderSuccessPage(Map<String, Object> map, HttpSession session,
                                       @PathVariable("order_code") String order_code) {
        //检查用户是否登录
        Object userId = checkUser(session);
        User user;
        if (userId != null) {
            //获取用户信息
            user = userService.get(Integer.parseInt(userId.toString()));
            map.put("user", user);
        } else {
            return LOGIN;
        }
        //------验证订单信息------
        //查询订单是否存在
        ProductOrder order = productOrderService.getByCode(order_code);
        if (order == null) {
            //订单不存在，返回订单列表页
            return URL;
        }
        //验证订单状态
        if (order.getProductOrder_status() != 3) {
            //订单状态不正确，返回订单列表页
            return URL;
        }
        //验证用户与订单是否一致
        if (order.getProductOrder_user().getUser_id() != Integer.parseInt(userId.toString())) {
            //用户与订单信息不一致，返回订单列表页
            return URL;
        }
        //获取订单中订单项数量
        Integer count = productOrderItemService.getTotalByOrderId(order.getProductOrder_id());
        Product product = null;
        if (count == 1) {
            //获取订单中的唯一订单项
            ProductOrderItem productOrderItem = productOrderItemService.getListByOrderId(order.getProductOrder_id(), new PageUtil(0, 1)).get(0);
            if (productOrderItem != null) {
                //获取订单项评论数量
                count = reviewService.getTotalByOrderItemId(productOrderItem.getProductOrderItem_id());
                if (count == 0) {
                    //获取订单项产品信息
                    product = productService.get(productOrderItem.getProductOrderItem_product().getProduct_id());
                    if (product != null) {
                        product.setSingleProductImageList(productImageService.getList(product.getProduct_id(), (byte) 0, new PageUtil(0, 1)));
                    }
                }
            }
            map.put("orderItem", productOrderItem);
        }

        map.put("product", product);

        //转到前台天猫-订单完成页
        return "fore/orderSuccessPage";
    }

    //转到前台天猫-购物车页
    @ApiOperation(value = "转到前台天猫-购物车页", notes = "转到前台天猫-购物车页")
    @GetMapping(value = "cart")
    public String goToCartPage(Map<String, Object> map, HttpSession session) {
        //检查用户是否登录
        Object userId = checkUser(session);
        User user;
        if (userId != null) {
            //获取用户信息
            user = userService.get(Integer.parseInt(userId.toString()));
            map.put("user", user);
        } else {
            return LOGIN;
        }
        //获取用户购物车信息
        List<ProductOrderItem> orderItemList = productOrderItemService.getListByUserId(Integer.valueOf(userId.toString()), null);
        Integer orderItemTotal = 0;
        if (CollectionUtils.isNotEmpty(orderItemList)) {
            //获取用户购物车的商品总数
            orderItemTotal = productOrderItemService.getTotalByUserId(Integer.valueOf(userId.toString()));
            //获取用户购物车内的商品信息
            for (ProductOrderItem orderItem : orderItemList) {
                Integer product_id = orderItem.getProductOrderItem_product().getProduct_id();
                Product product = productService.get(product_id);
                product.setSingleProductImageList(productImageService.getList(product_id, (byte) 0, null));
                product.setProduct_category(categoryService.get(product.getProduct_category().getCategory_id()));
                orderItem.setProductOrderItem_product(product);
            }
        }
        map.put("orderItemList", orderItemList);
        map.put("orderItemTotal", orderItemTotal);

        //转到前台天猫-购物车页
        return "fore/productBuyCarPage";
    }

    //更新订单信息为已支付，待发货-ajax
    @ApiOperation(value = "更新订单信息为已支付，待发货", notes = "更新订单信息为已支付，待发货")
    @ResponseBody
    @PutMapping(value = "order/pay/{order_code}")
    public String orderPay(HttpSession session, @PathVariable("order_code") String order_code) {
        JSONObject object = new JSONObject();
        //检查用户是否登录
        Object userId = checkUser(session);
        if (userId == null) {
            object.put(Constants.SUCCESS, false);
            object.put("url", Constants.LOGIN);
            return String.valueOf(object);
        }
        //------验证订单信息------
        //查询订单是否存在
        ProductOrder order = productOrderService.getByCode(order_code);
        if (order == null) {
            //订单不存在，返回订单列表页
            object.put(Constants.SUCCESS, false);
            object.put("url", ORDER);
            return String.valueOf(object);
        }
        //验证订单状态
        if (order.getProductOrder_status() != 0) {
            //订单状态不正确，返回订单列表页
            object.put(Constants.SUCCESS, false);
            object.put("url", ORDER);
            return String.valueOf(object);
        }
        //验证用户与订单是否一致
        if (order.getProductOrder_user().getUser_id() != Integer.parseInt(userId.toString())) {
            //用户与订单信息不一致，返回订单列表页
            object.put(Constants.SUCCESS, false);
            object.put("url", ORDER);
            return String.valueOf(object);
        }
        order.setProductOrderItemList(productOrderItemService.getListByOrderId(order.getProductOrder_id(), null));
        if (order.getProductOrderItemList().size() == 1) {
            //获取单订单项的产品信息
            ProductOrderItem productOrderItem = order.getProductOrderItemList().get(0);
            Product product = productService.get(productOrderItem.getProductOrderItem_product().getProduct_id());
            product.setProduct_category(categoryService.get(product.getProduct_category().getCategory_id()));
            productOrderItem.setProductOrderItem_product(product);
        }
        //总共支付金额为：{}元, orderTotalPrice
        //更新订单信息
        ProductOrder productOrder = new ProductOrder();
        productOrder.setProductOrder_id(order.getProductOrder_id());
        SimpleDateFormat time = new SimpleDateFormat(Constants.DATE, Locale.UK);
        productOrder.setProductOrder_pay_date(time.format(new Date()));
        productOrder.setProductOrder_status((byte) 1);

        boolean yn = productOrderService.update(productOrder);
        if (yn) {
            object.put(Constants.SUCCESS, true);
            object.put("url", "/order/pay/success/" + order_code);
        } else {
            object.put(Constants.SUCCESS, false);
            object.put("url", ORDER);
        }
        return String.valueOf(object);
    }

    //更新订单信息为已发货，待确认-ajax
    @ApiOperation(value = "更新订单信息为已发货，待确认", notes = "更新订单信息为已发货，待确认")
    @GetMapping(value = "order/delivery/{order_code}")
    public String orderDelivery(HttpSession session, @PathVariable("order_code") String order_code) {
        //检查用户是否登录
        Object userId = checkUser(session);
        if (userId == null) {
            return URL;
        }
        //------验证订单信息------
        //查询订单是否存在
        ProductOrder order = productOrderService.getByCode(order_code);
        if (order == null) {
            //订单不存在，返回订单列表页
            return URL;
        }
        //验证订单状态
        if (order.getProductOrder_status() != 1) {
            //订单状态不正确，返回订单列表页
            return URL;
        }
        //验证用户与订单是否一致
        if (order.getProductOrder_user().getUser_id() != Integer.parseInt(userId.toString())) {
            //用户与订单信息不一致，返回订单列表页
            return URL;
        }
        //更新订单信息
        ProductOrder productOrder = new ProductOrder();
        productOrder.setProductOrder_id(order.getProductOrder_id());
        SimpleDateFormat time = new SimpleDateFormat(Constants.DATE, Locale.UK);
        productOrder.setProductOrder_delivery_date(time.format(new Date()));
        productOrder.setProductOrder_status((byte) 2);

        productOrderService.update(productOrder);

        return URL;
    }

    //更新订单信息为交易成功-ajax
    @ApiOperation(value = "更新订单信息为交易成功", notes = "更新订单信息为交易成功")
    @ResponseBody
    @PutMapping(value = "order/success/{order_code}", produces = "application/json;charset=utf-8")
    public String orderSuccess(HttpSession session, @PathVariable("order_code") String order_code) {
        JSONObject object = new JSONObject();
        //检查用户是否登录
        Object userId = checkUser(session);
        if (userId == null) {
            object.put(Constants.SUCCESS, false);
            object.put("url", Constants.LOGIN);
            return String.valueOf(object);
        }
        //------验证订单信息------
        //查询订单是否存在
        ProductOrder order = productOrderService.getByCode(order_code);
        if (order == null) {
            //订单不存在，返回订单列表页
            object.put(Constants.SUCCESS, false);
            object.put("url", ORDER);
            return String.valueOf(object);
        }
        //验证订单状态
        if (order.getProductOrder_status() != 2) {
            //订单状态不正确，返回订单列表页
            object.put(Constants.SUCCESS, false);
            object.put("url", ORDER);
            return String.valueOf(object);
        }
        //验证用户与订单是否一致
        if (order.getProductOrder_user().getUser_id() != Integer.parseInt(userId.toString())) {
            //用户与订单信息不一致，返回订单列表页
            object.put(Constants.SUCCESS, false);
            object.put("url", ORDER);
            return String.valueOf(object);
        }
        //更新订单信息
        ProductOrder productOrder = new ProductOrder();
        productOrder.setProductOrder_id(order.getProductOrder_id());
        productOrder.setProductOrder_status((byte) 3);
        SimpleDateFormat time = new SimpleDateFormat(Constants.DATE, Locale.UK);
        productOrder.setProductOrder_confirm_date(time.format(new Date()));

        boolean yn = productOrderService.update(productOrder);
        if (yn) {
            object.put(Constants.SUCCESS, true);
        } else {
            object.put(Constants.SUCCESS, false);
        }
        return String.valueOf(object);
    }

    //更新订单信息为交易关闭-ajax
    @ApiOperation(value = "更新订单信息为交易关闭", notes = "更新订单信息为交易关闭")
    @ResponseBody
    @PutMapping(value = "order/close/{order_code}", produces = "application/json;charset=utf-8")
    public String orderClose(HttpSession session, @PathVariable("order_code") String order_code) {
        JSONObject object = new JSONObject();
        //检查用户是否登录
        Object userId = checkUser(session);
        if (userId == null) {
            object.put(Constants.SUCCESS, false);
            object.put("url", Constants.LOGIN);
            return String.valueOf(object);
        }
        //------验证订单信息------
        //查询订单是否存在
        ProductOrder order = productOrderService.getByCode(order_code);
        if (order == null) {
            //订单不存在，返回订单列表页
            object.put(Constants.SUCCESS, false);
            object.put("url", ORDER);
            return String.valueOf(object);
        }
        //验证订单状态
        if (order.getProductOrder_status() != 0) {
            //订单状态不正确，返回订单列表页
            object.put(Constants.SUCCESS, false);
            object.put("url", ORDER);
            return String.valueOf(object);
        }
        //验证用户与订单是否一致
        if (order.getProductOrder_user().getUser_id() != Integer.parseInt(userId.toString())) {
            //用户与订单信息不一致，返回订单列表页
            object.put(Constants.SUCCESS, false);
            object.put("url", ORDER);
            return String.valueOf(object);
        }
        //更新订单信息
        ProductOrder productOrder = new ProductOrder();
        productOrder.setProductOrder_id(order.getProductOrder_id());
        productOrder.setProductOrder_status((byte) 4);

        boolean yn = productOrderService.update(productOrder);
        if (yn) {
            object.put(Constants.SUCCESS, true);
        } else {
            object.put(Constants.SUCCESS, false);
        }
        return String.valueOf(object);
    }

    //更新购物车订单项数量-ajax
    @ApiOperation(value = "更新购物车订单项数量", notes = "更新购物车订单项数量")
    @ResponseBody
    @PutMapping(value = "orderItem", produces = "application/json;charset=utf-8")
    public String updateOrderItem(HttpSession session, Map<String, Object> map, HttpServletResponse response,
                                  @RequestParam String orderItemMap) {
        JSONObject object = new JSONObject();
        //检查用户是否登录
        Object userId = checkUser(session);
        if (userId == null) {
            object.put(Constants.SUCCESS, false);
            return String.valueOf(object);
        }

        JSONObject orderItemString = JSON.parseObject(orderItemMap);
        Set<String> orderItemIDSet = orderItemString.keySet();
        if (CollectionUtils.isNotEmpty(orderItemIDSet)) {
            //更新产品订单项数量
            for (String key : orderItemIDSet) {
                ProductOrderItem productOrderItem = productOrderItemService.get(Integer.valueOf(key));
                if (productOrderItem == null || !productOrderItem.getProductOrderItem_user().getUser_id().equals(userId)) {
                    //订单项为空或用户状态不一致！
                    object.put(Constants.SUCCESS, false);
                    return String.valueOf(object);
                }
                if (productOrderItem.getProductOrderItem_order() != null) {
                    //用户订单项不属于购物车，回到购物车页
                    return CART;
                }
                Short number = Short.valueOf(orderItemString.getString(key));
                if (number <= 0 || number > 500) {
                    //订单项产品数量不合法！
                    object.put(Constants.SUCCESS, false);
                    return String.valueOf(object);
                }
                double price = productOrderItem.getProductOrderItem_price() / productOrderItem.getProductOrderItem_number();
                ProductOrderItem productOrderItem1 = new ProductOrderItem();
                productOrderItem1.setProductOrderItem_id(Integer.valueOf(key));
                productOrderItem1.setProductOrderItem_number(number);
                productOrderItem1.setProductOrderItem_price(number * price);
                Boolean yn = productOrderItemService.update(productOrderItem1);
                if (!yn) {
                    throw new RuntimeException();
                }
            }
            Object[] orderItemIDArray = orderItemIDSet.toArray();
            object.put(Constants.SUCCESS, true);
            object.put("orderItemIDArray", orderItemIDArray);
            return String.valueOf(object);
        } else {
            //无订单项可以处理
            object.put(Constants.SUCCESS, false);
            return String.valueOf(object);
        }
    }

    //创建新订单-单订单项-ajax
    @ApiOperation(value = "创建新订单-单订单项", notes = "创建新订单-单订单项")
    @ResponseBody
    @PostMapping(value = "order", produces = "application/json;charset=utf-8")
    public String createOrderByOne(HttpSession session, Map<String, Object> map, HttpServletResponse response,
                                   @RequestParam String addressId,
                                   @RequestParam String cityAddressId,
                                   @RequestParam String districtAddressId,
                                   @RequestParam String productOrder_detail_address,
                                   @RequestParam String productOrder_post,
                                   @RequestParam String productOrder_receiver,
                                   @RequestParam String productOrder_mobile,
                                   @RequestParam String userMessage,
                                   @RequestParam Integer orderItem_product_id,
                                   @RequestParam Short orderItem_number) throws UnsupportedEncodingException {
        JSONObject object = new JSONObject();
        //检查用户是否登录
        Object userId = checkUser(session);
        if (userId == null) {
            object.put(Constants.SUCCESS, false);
            object.put("url", Constants.LOGIN);
            return String.valueOf(object);
        }
        Product product = productService.get(orderItem_product_id);
        if (product == null) {
            object.put(Constants.SUCCESS, false);
            object.put("url", "/");
            return String.valueOf(object);
        }
        //将收货地址等相关信息存入Cookie中
        Cookie cookie1 = new Cookie("addressId", addressId);
        Cookie cookie2 = new Cookie("cityAddressId", cityAddressId);
        Cookie cookie3 = new Cookie("districtAddressId", districtAddressId);
        Cookie cookie4 = new Cookie("order_post", URLEncoder.encode(productOrder_post, Constants.UTF));
        Cookie cookie5 = new Cookie("order_receiver", URLEncoder.encode(productOrder_receiver, Constants.UTF));
        Cookie cookie6 = new Cookie("order_phone", URLEncoder.encode(productOrder_mobile, Constants.UTF));
        Cookie cookie7 = new Cookie("detailsAddress", URLEncoder.encode(productOrder_detail_address, Constants.UTF));
        //设置过期时间为一年
        int maxAge = 60 * 60 * 24 * 365;
        cookie1.setMaxAge(maxAge);
        cookie2.setMaxAge(maxAge);
        cookie3.setMaxAge(maxAge);
        cookie4.setMaxAge(maxAge);
        cookie5.setMaxAge(maxAge);
        cookie6.setMaxAge(maxAge);
        cookie7.setMaxAge(maxAge);
        response.addCookie(cookie1);
        response.addCookie(cookie2);
        response.addCookie(cookie3);
        response.addCookie(cookie4);
        response.addCookie(cookie5);
        response.addCookie(cookie6);
        response.addCookie(cookie7);

        StringBuilder productOrder_code = new StringBuilder()
                .append(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()))
                .append(0)
                .append(userId);
        //生成的订单号为：{}, productOrder_code
        //整合订单对象
        ProductOrder productOrder = new ProductOrder();
        productOrder.setProductOrder_status((byte) 0);
        Address address = new Address();
        address.setAddress_areaId(districtAddressId);
        productOrder.setProductOrder_address(address);
        productOrder.setProductOrder_post(productOrder_post);
        User user = new User();
        user.setUser_id(Integer.valueOf(userId.toString()));
        productOrder.setProductOrder_user(user);
        productOrder.setProductOrder_mobile(productOrder_mobile);
        productOrder.setProductOrder_receiver(productOrder_receiver);
        productOrder.setProductOrder_detail_address(productOrder_detail_address);
        SimpleDateFormat time = new SimpleDateFormat(Constants.DATE, Locale.UK);
        productOrder.setProductOrder_pay_date(time.format(new Date()));
        productOrder.setProductOrder_code(productOrder_code.toString());
        Boolean yn = productOrderService.add(productOrder);
        if (!yn) {
            throw new RuntimeException();
        }
        Integer order_id = lastIDService.selectLastID();
        //整合订单项对象
        ProductOrderItem productOrderItem = new ProductOrderItem();
        productOrderItem.setProductOrderItem_user(user);
        productOrderItem.setProductOrderItem_product(productService.get(orderItem_product_id));
        productOrderItem.setProductOrderItem_number(orderItem_number);
        productOrderItem.setProductOrderItem_price(product.getProduct_sale_price() * orderItem_number);
        productOrderItem.setProductOrderItem_userMessage(userMessage);
        ProductOrder productOrder1 = new ProductOrder();
        productOrder1.setProductOrder_id(order_id);
        productOrderItem.setProductOrderItem_order(productOrder1);
        yn = productOrderItemService.add(productOrderItem);
        if (!yn) {
            throw new RuntimeException();
        }

        object.put(Constants.SUCCESS, true);
        object.put("url", "/order/pay/" + productOrder.getProductOrder_code());
        return String.valueOf(object);
    }

    //创建新订单-多订单项-ajax
    @ApiOperation(value = "创建新订单-多订单项", notes = "创建新订单-多订单项")
    @ResponseBody
    @PostMapping(value = "order/list", produces = "application/json;charset=utf-8")
    public String createOrderByList(HttpSession session, Map<String, Object> map, HttpServletResponse response,
                                    @RequestParam String addressId,
                                    @RequestParam String cityAddressId,
                                    @RequestParam String districtAddressId,
                                    @RequestParam String productOrder_detail_address,
                                    @RequestParam String productOrder_post,
                                    @RequestParam String productOrder_receiver,
                                    @RequestParam String productOrder_mobile,
                                    @RequestParam String orderItemJSON) throws UnsupportedEncodingException {
        JSONObject object = new JSONObject();
        //检查用户是否登录
        Object userId = checkUser(session);
        if (userId == null) {
            object.put(Constants.SUCCESS, false);
            object.put("url", Constants.LOGIN);
            return String.valueOf(object);
        }
        JSONObject orderItemMap = JSONObject.parseObject(orderItemJSON);
        Set<String> orderItem_id = orderItemMap.keySet();
        List<ProductOrderItem> productOrderItemList = new ArrayList<>(3);
        if (CollectionUtils.isNotEmpty(orderItem_id)) {
            for (String id : orderItem_id) {
                ProductOrderItem orderItem = productOrderItemService.get(Integer.valueOf(id));
                if (orderItem == null || !orderItem.getProductOrderItem_user().getUser_id().equals(userId)) {
                    //订单项为空或用户状态不一致！
                    object.put(Constants.SUCCESS, false);
                    object.put("url", "/cart");
                    return String.valueOf(object);
                }
                if (orderItem.getProductOrderItem_order() != null) {
                    //用户订单项不属于购物车，回到购物车页
                    object.put(Constants.SUCCESS, false);
                    object.put("url", "/cart");
                    return String.valueOf(object);
                }
                ProductOrderItem productOrderItem = new ProductOrderItem();
                productOrderItem.setProductOrderItem_id(Integer.valueOf(id));
                productOrderItem.setProductOrderItem_userMessage(orderItemMap.getString(id));
                boolean yn = productOrderItemService.update(productOrderItem);
                if (!yn) {
                    throw new RuntimeException();
                }
                orderItem.setProductOrderItem_product(productService.get(orderItem.getProductOrderItem_product().getProduct_id()));
                productOrderItemList.add(orderItem);
            }
        } else {
            object.put(Constants.SUCCESS, false);
            object.put("url", "/cart");
            return String.valueOf(object);
        }
        //将收货地址等相关信息存入Cookie中
        Cookie cookie1 = new Cookie("addressId", addressId);
        Cookie cookie2 = new Cookie("cityAddressId", cityAddressId);
        Cookie cookie3 = new Cookie("districtAddressId", districtAddressId);
        Cookie cookie4 = new Cookie("order_post", URLEncoder.encode(productOrder_post, Constants.UTF));
        Cookie cookie5 = new Cookie("order_receiver", URLEncoder.encode(productOrder_receiver, Constants.UTF));
        Cookie cookie6 = new Cookie("order_phone", URLEncoder.encode(productOrder_mobile, Constants.UTF));
        Cookie cookie7 = new Cookie("detailsAddress", URLEncoder.encode(productOrder_detail_address, Constants.UTF));
        //设置过期时间为一年
        int maxAge = 60 * 60 * 24 * 365;
        cookie1.setMaxAge(maxAge);
        cookie2.setMaxAge(maxAge);
        cookie3.setMaxAge(maxAge);
        cookie4.setMaxAge(maxAge);
        cookie5.setMaxAge(maxAge);
        cookie6.setMaxAge(maxAge);
        cookie7.setMaxAge(maxAge);
        response.addCookie(cookie1);
        response.addCookie(cookie2);
        response.addCookie(cookie3);
        response.addCookie(cookie4);
        response.addCookie(cookie5);
        response.addCookie(cookie6);
        response.addCookie(cookie7);
        StringBuilder productOrder_code = new StringBuilder()
                .append(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()))
                .append(0)
                .append(userId);
        //生成的订单号为：{}, productOrder_code
        //整合订单对象
        ProductOrder productOrder = new ProductOrder();
        productOrder.setProductOrder_status((byte) 0);
        Address address = new Address();
        address.setAddress_areaId(districtAddressId);
        productOrder.setProductOrder_address(address);
        productOrder.setProductOrder_post(productOrder_post);

        User user = new User();
        user.setUser_id(Integer.valueOf(userId.toString()));
        productOrder.setProductOrder_user(user);
        productOrder.setProductOrder_mobile(productOrder_mobile);
        productOrder.setProductOrder_receiver(productOrder_receiver);
        productOrder.setProductOrder_detail_address(productOrder_detail_address);
        SimpleDateFormat time = new SimpleDateFormat(Constants.DATE, Locale.UK);
        productOrder.setProductOrder_pay_date(time.format(new Date()));
        productOrder.setProductOrder_code(productOrder_code.toString());
        Boolean yn = productOrderService.add(productOrder);
        if (!yn) {
            throw new RuntimeException();
        }
        Integer order_id = lastIDService.selectLastID();
        //整合订单项对象
        for (ProductOrderItem orderItem : productOrderItemList) {
            ProductOrder productOrder1 = new ProductOrder();
            productOrder1.setProductOrder_id(order_id);
            orderItem.setProductOrderItem_order(productOrder1);
            yn = productOrderItemService.update(orderItem);
        }
        if (!yn) {
            throw new RuntimeException();
        }

        object.put(Constants.SUCCESS, true);
        object.put("url", "/order/pay/" + productOrder.getProductOrder_code());
        return String.valueOf(object);
    }

    //创建订单项-购物车-ajax
    @ApiOperation(value = "创建订单项-购物车", notes = "创建订单项-购物车")
    @ResponseBody
    @PostMapping(value = "orderItem/create/{product_id}", produces = "application/json;charset=utf-8")
    public String createOrderItem(@PathVariable("product_id") Integer product_id,
                                  @RequestParam(required = false, defaultValue = "1") Short product_number,
                                  HttpSession session,
                                  HttpServletRequest request) {
        JSONObject object = new JSONObject();
        //检查用户是否登录
        Object userId = checkUser(session);
        if (userId == null) {
            object.put("url", Constants.LOGIN);
            object.put(Constants.SUCCESS, false);
            return String.valueOf(object);
        }

        //通过产品ID获取产品信息：{}, product_id
        Product product = productService.get(product_id);
        if (product == null) {
            object.put("url", Constants.LOGIN);
            object.put(Constants.SUCCESS, false);
            return String.valueOf(object);
        }

        ProductOrderItem productOrderItem = new ProductOrderItem();
        //检查用户的购物车项
        List<ProductOrderItem> orderItemList = productOrderItemService.getListByUserId(Integer.valueOf(userId.toString()), null);
        if (CollectionUtils.isNotEmpty(orderItemList)) {
            for (ProductOrderItem orderItem : orderItemList) {
                if (orderItem.getProductOrderItem_product().getProduct_id().equals(product_id)) {
                    //找到已有的产品，进行数量追加
                    int number = orderItem.getProductOrderItem_number();
                    number += 1;
                    productOrderItem.setProductOrderItem_id(orderItem.getProductOrderItem_id());
                    productOrderItem.setProductOrderItem_number((short) number);
                    productOrderItem.setProductOrderItem_price(number * product.getProduct_sale_price());
                    boolean yn = productOrderItemService.update(productOrderItem);
                    if (yn) {
                        object.put(Constants.SUCCESS, true);
                    } else {
                        object.put(Constants.SUCCESS, false);
                    }
                    return String.valueOf(object);
                }
            }
        }
        //封装订单项对象
        productOrderItem.setProductOrderItem_product(product);
        productOrderItem.setProductOrderItem_number(product_number);
        productOrderItem.setProductOrderItem_price(product.getProduct_sale_price() * product_number);
        User user = new User();
        user.setUser_id(Integer.valueOf(userId.toString()));
        productOrderItem.setProductOrderItem_user(user);
        boolean yn = productOrderItemService.add(productOrderItem);
        if (yn) {
            object.put(Constants.SUCCESS, true);
        } else {
            object.put(Constants.SUCCESS, false);
        }
        return String.valueOf(object);
    }

    //删除订单项-购物车-ajax
    @ApiOperation(value = "删除订单项-购物车", notes = "删除订单项-购物车")
    @ResponseBody
    @DeleteMapping(value = "orderItem/{orderItem_id}", produces = "application/json;charset=utf-8")
    public String deleteOrderItem(@PathVariable("orderItem_id") Integer orderItem_id,
                                  HttpSession session,
                                  HttpServletRequest request) {
        JSONObject object = new JSONObject();
        //检查用户是否登录
        Object userId = checkUser(session);
        if (userId == null) {
            object.put("url", Constants.LOGIN);
            object.put(Constants.SUCCESS, false);
            return String.valueOf(object);
        }
        //检查用户的购物车项
        List<ProductOrderItem> orderItemList = productOrderItemService.getListByUserId(Integer.valueOf(userId.toString()), null);
        boolean isMine = false;
        for (ProductOrderItem orderItem : orderItemList) {
            //找到匹配的购物车项
            if (orderItem.getProductOrderItem_id().equals(orderItem_id)) {
                isMine = true;
                break;
            }
        }
        if (isMine) {
            //删除订单项信息
            boolean yn = productOrderItemService.deleteList(new Integer[]{orderItem_id});
            if (yn) {
                object.put(Constants.SUCCESS, true);
            } else {
                object.put(Constants.SUCCESS, false);
            }
        } else {
            object.put(Constants.SUCCESS, false);
        }
        return String.valueOf(object);
    }
}