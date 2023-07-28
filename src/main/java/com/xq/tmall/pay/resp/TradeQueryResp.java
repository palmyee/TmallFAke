package com.xq.tmall.pay.resp;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 交易查询结果
 *
 */
@Data
@Accessors(chain = true)
public class TradeQueryResp implements Serializable {

    private static final long serialVersionUID = 5715723609895043867L;

    /**
     * 是否请求成功
     */
    private boolean success;

    /**
     * 请求响应信息
     */
    private String msg;

    /**
     * 交易状态(1:待支付、2:支付成功、3:支付失败、4:未知)
     */
    private Integer tradeStatus;

    /**
     * 交易订单号
     */
    private String orderNo;

    /**
     * 交易号--支付通道的订单号
     */
    private String tradeNo;

    /**
     * 附加信息
     */
    private String attach;

    /**
     * 订单金额
     */
    private BigDecimal amount;

    /**
     * 成功时间
     */
    private Date successTime;
}
