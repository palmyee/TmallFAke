package com.xq.tmall.pay.req;

import com.xq.tmall.pay.util.AliPayConfig;
import com.xq.tmall.pay.util.WxPayConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 交易关闭
 *
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class TradeCloseReq implements Serializable {

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
