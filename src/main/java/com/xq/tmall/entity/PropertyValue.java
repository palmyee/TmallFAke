package com.xq.tmall.entity;


import lombok.Data;

/**
 * 产品属性
 */
@Data
public class PropertyValue {
    private Integer propertyValue_id;
    /**
     * 属性值
     */
    private String propertyValue_value;
    /**
     * 关联属性
     */
    private Property propertyValue_property;
    /**
     * 关联产品
     */
    private Product propertyValue_product;
}
