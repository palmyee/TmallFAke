package com.xq.tmall.entity;

import lombok.Data;

/**
 * 地址类
 */
@Data
public class Address {
    /**
     * 地区编码
     */
    private String address_areaId;
    /**
     * 省市名称
     */
    private String address_name;
    /**
     * 父级省市id
     */
    private Address address_regionId;
}
