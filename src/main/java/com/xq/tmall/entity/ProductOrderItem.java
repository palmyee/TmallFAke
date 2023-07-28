package com.xq.tmall.entity;

import lombok.Data;

/**
 * 产品订单项
 */
@Data
public class ProductOrderItem {
    private Integer productOrderItem_id;
    /**
     * 数量
     */
    private Short productOrderItem_number;
    /**
     * 单价
     */
    private Double productOrderItem_price;
    /**
     * 关联产品
     */
    private Product productOrderItem_product;
    /**
     * 关联订单
     */
    private ProductOrder productOrderItem_order;
    /**
     * 关联用户
     */
    private User productOrderItem_user;
    /**
     * 用户备注
     */
    private String productOrderItem_userMessage;
    /**
     * 订单产品是否已经评价
     */
    private Boolean isReview;
}
