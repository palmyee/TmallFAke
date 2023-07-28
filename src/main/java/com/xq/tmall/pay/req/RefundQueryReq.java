package com.xq.tmall.pay.req;

import com.xq.tmall.pay.util.AliPayConfig;
import com.xq.tmall.pay.util.WxPayConfig;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 退款查询参数
 *
 */
@Data
@Accessors(chain = true)
public class RefundQueryReq implements Serializable {

    private static final long serialVersionUID = -3368290074178625768L;

    /**
     * 退款订单号
     */
    private String refundOrderNo;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 支付模式
     */
    private Integer payModel;

    /**
     * 支付宝支付配置
     */
    private AliPayConfig aliPayConfig;

    /**
     * 微信支付配置
     */
    private WxPayConfig wxPayConfig;
}
