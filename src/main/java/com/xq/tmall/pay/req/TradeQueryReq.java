package com.xq.tmall.pay.req;

import com.xq.tmall.pay.util.AliPayConfig;
import com.xq.tmall.pay.util.WxPayConfig;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 交易查询
 *
 */
@Data
@Accessors(chain = true)
public class TradeQueryReq implements Serializable {

    private static final long serialVersionUID = 7142265021176128634L;

    /**
     * 交易订单号
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
