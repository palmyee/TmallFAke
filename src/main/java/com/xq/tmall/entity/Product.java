package com.xq.tmall.entity;

import lombok.Data;

import java.util.List;

/**
 * 产品
 */
@Data
public class Product {
    private Integer product_id;
    /**
     * 产品名称
     */
    private String product_name;
    /**
     * 产品标题
     */
    private String product_title;
    /**
     * 原价
     */
    private Double product_price;
    /**
     * 促销价
     */
    private Double product_sale_price;
    /**
     * 创建日期
     */
    private String product_create_date;
    /**
     * 类别id
     */
    private Category product_category;
    /**
     * 是否可用
     */
    private Byte product_isEnabled;
    /**
     * 产品属性管理
     */
    private List<PropertyValue> propertyValueList;
    /**
     * 单一产品图片
     */
    private List<ProductImage> singleProductImageList;
    /**
     * 详细产品图片
     */
    private List<ProductImage> detailProductImageList;
    /**
     * 评论
     */
    private List<Review> reviewList;
    /**
     * 产品订单项
     */
    private List<ProductOrderItem> productOrderItemList;
    /**
     * 销量数
     */
    private Integer product_sale_count;
    /**
     * 评论数
     */
    private Integer product_review_count;
    /**
     *产品出售
     */
    public Object setProduct_sale_co;

}
