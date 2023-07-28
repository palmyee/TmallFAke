package com.xq.tmall.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 订单组
 */
@Data
public class OrderGroup {
    /**
     * 付款日期
     */
    @JsonFormat(pattern = "MM/dd")
    private Date productOrder_pay_date;
    /**
     * 产品订单总数
     */
    private Integer productOrder_count;
    /**
     * 产品订单状态
     */
    private Byte productOrder_status;
}