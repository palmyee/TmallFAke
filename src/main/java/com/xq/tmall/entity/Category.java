package com.xq.tmall.entity;

import lombok.Data;

import java.util.List;

/**
 * 类别
 */
@Data
public class Category {
    private Integer category_id;
    /**
     * 类别名称
     */
    private String category_name;
    /**
     * 类别图片
     */
    private String category_image_src;
    /**
     * 删除标识(1删除 0未删除）
     */
    private Integer del_flag;
    //产品列表
    private List<Product> productList;
    //产品二维集合
    private List<List<Product>> complexProductList;
    //属性列表
    private List<Property> propertyList;
}
