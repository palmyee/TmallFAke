package com.xq.tmall.entity;

import lombok.Data;

import java.util.List;

/**
 * 类别属性
 */
@Data
public class Property {
    private Integer property_id;
    /**
     * 属性名称
     */
    private String property_name;
    /**
     * 关联类别
     */
    private Category property_category;
    /**
     * 产品属性
     */
    private List<PropertyValue> propertyValueList;
}
