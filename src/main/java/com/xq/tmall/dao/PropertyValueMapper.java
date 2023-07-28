package com.xq.tmall.dao;

import com.xq.tmall.entity.PropertyValue;
import com.xq.tmall.util.PageUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyValueMapper {
    /**
     * 新增产品属性
     *
     * @param propertyValue
     * @return
     */
    Integer insertOne(@Param("propertyValue") PropertyValue propertyValue);

    /**
     * 批量新增产品属性
     *
     * @param propertyValueList
     * @return
     */
    Integer insertList(@Param("propertyValue_list") List<PropertyValue> propertyValueList);

    /**
     * 修改产品属性
     *
     * @param propertyValue
     * @return
     */
    Integer updateOne(@Param("propertyValue") PropertyValue propertyValue);

    /**
     * 批量删除产品属性
     *
     * @param propertyValue_id_list
     * @return
     */
    Integer deleteList(@Param("propertyValue_id_list") Integer[] propertyValue_id_list);

    /**
     * 删除对应产品属性
     *
     * @param id
     * @return
     */
    Integer deleteOne(Integer id);

    /**
     * 查询产品属性列表
     *
     * @param propertyValue
     * @param pageUtil
     * @return
     */
    List<PropertyValue> selectPropertyValueList(@Param("propertyValue") PropertyValue propertyValue, @Param("pageUtil") PageUtil pageUtil);

    /**
     * 查询单条产品属性
     *
     * @param propertyValue_id
     * @return
     */
    PropertyValue selectOne(Integer propertyValue_id);

    /**
     * 查询产品属性总数
     *
     * @param propertyValue
     * @return
     */
    Integer selectTotal(@Param("propertyValue") PropertyValue propertyValue);
}
