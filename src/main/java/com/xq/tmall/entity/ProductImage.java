package com.xq.tmall.entity;

import lombok.Data;

/**
 * 产品图片
 */
@Data
public class ProductImage {
    private Integer productImage_id;
    /**
     * 类型(0:概述图片 1:详情图片)
     */
    private Byte productImage_type;
    /**
     * 图片地址
     */
    private String productImage_src;
    /**
     * 产品主表
     */
    private Product productImage_product;

}
