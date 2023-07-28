package com.xq.tmall.entity;

import lombok.Data;


/**
 * 评论
 */
@Data
public class Review {
    private Integer review_id;
    /**
     * 内容
     */
    private String review_content;
    /**
     * 创建日期
     */
    private String review_createDate;
    /**
     * 关联用户
     */
    private User review_user;
    /**
     * 关联产品
     */
    private Product review_product;
    /**
     * 关联订单详细
     */
    private ProductOrderItem review_orderItem;
}
