package com.xq.tmall.dao;

import com.xq.tmall.entity.Address;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface AddressMapper {
    /**
     * 新增地址
     * @param address
     * @return
     */
    Integer insertOne(@Param("address") Address address);

    /**
     * 修改地址
     * @param address
     * @return
     */
    Integer updateOne(@Param("address") Address address);

    /**
     * 按条件查询
     * @param address_name
     * @param address_regionId
     * @return
     */
    List<Address> selectAddressList(@Param("address_name") String address_name, @Param("address_regionId") String address_regionId);

    /**
     * 根据id查询地址
     * @param address_areaId
     * @return
     */
    Address selectOne(@Param("address_areaId") String address_areaId);

    /**
     * 查询地区编码==父级省市id的地址
     * @return
     */
    List<Address> selectRoot();
}