/**
 * Copyright 2015-现在 广州市网络科技有限公司
 */
package com.xq.tmall.util;

import cn.hutool.core.util.RandomUtil;

import java.util.Date;


/**
 * 时间+随机数，一定几率是会重复的，但忽略这个，数据库校验了唯一性
 */
public final class NOUtil {

    private final static String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    private NOUtil() {
    }

    public static String getVerCode() {
        return RandomUtil.randomNumbers(6);
    }

    public static Long getOrderNo() {
        return Long.valueOf(DateUtil.format(new Date(), YYYYMMDDHHMMSS) + RandomUtil.randomNumbers(3));
    }

    public static Long getSerialNumber() {
        return Long.valueOf(DateUtil.format(new Date(), YYYYMMDDHHMMSS) + RandomUtil.randomNumbers(4));
    }

}
