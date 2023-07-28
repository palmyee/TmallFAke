package com.xq.tmall.entity;

import lombok.Data;

import java.util.List;

/**
 * 产品订单
 */
@Data
public class ProductOrder {
    private Integer productOrder_id;
    /**
     * 订单号
     */
    private String productOrder_code;
    /**
     * 产品地址
     */
    private Address productOrder_address;
    /**
     * 产品详细地址
     */
    private String productOrder_detail_address;
    /**
     * 邮政编码
     */
    private String productOrder_post;
    /**
     * 收货人
     */
    private String productOrder_receiver;
    /**
     * 联系方式
     */
    private String productOrder_mobile;
    /**
     * 支付日期
     */
    private String productOrder_pay_date;
    /**
     * 发货日期
     */
    private String productOrder_delivery_date;
    /**
     * 确认日期
     */
    private String productOrder_confirm_date;
    /**
     * 订单状态(0:待付款 1:待发货 2:待确认 3:交易成功 4:交易关闭)
     */
    private Byte productOrder_status;
    /**
     * 用户
     */
    private User productOrder_user;
    /**
     * 产品订单项
     */
    private List<ProductOrderItem> productOrderItemList;
}
