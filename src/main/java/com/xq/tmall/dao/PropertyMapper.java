package com.xq.tmall.dao;

import com.xq.tmall.entity.Property;
import com.xq.tmall.util.PageUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyMapper {
    /**
     * 新增类别属性
     *
     * @param property
     * @return
     */
    Integer insertOne(@Param("property") Property property);

    /**
     * 批量新增类别属性
     *
     * @param propertyList
     * @return
     */
    Integer insertList(@Param("property_list") List<Property> propertyList);

    /**
     * 修改类别属性
     *
     * @param property
     * @return
     */
    Integer updateOne(@Param("property") Property property);

    /**
     * 批量删除类别属性
     *
     * @param property_id_list
     * @return
     */
    Integer deleteList(@Param("property_id_list") Integer[] property_id_list);

    /**
     * 查询类别属性列表
     *
     * @param property
     * @param pageUtil
     * @return
     */
    List<Property> selectPropertyList(@Param("property") Property property, @Param("pageUtil") PageUtil pageUtil);

    /**
     * 查询单条类别属性
     *
     * @param property_id
     * @return
     */
    Property selectOne(Integer property_id);

    /**
     * 查询类别属性总数
     *
     * @param property
     * @return
     */
    Integer selectTotal(@Param("property") Property property);
}
