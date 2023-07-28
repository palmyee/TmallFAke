package com.xq.tmall.dao;

import com.xq.tmall.entity.Product;
import com.xq.tmall.util.OrderUtil;
import com.xq.tmall.util.PageUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductMapper {
    /**
     * 新增产品
     *
     * @param product
     * @return
     */
    Integer insertOne(@Param("product") Product product);

    /**
     * 修改产品
     *
     * @param product
     * @return
     */
    Integer updateOne(@Param("product") Product product);

    /**
     * 删除产品
     *
     * @param id
     * @return
     */
    Integer deleteOne(Integer id);

    /**
     * 查询列表
     *
     * @param product
     * @param product_isEnabled_array
     * @param orderUtil
     * @param pageUtil
     * @return
     */
    List<Product> selectProductList(@Param("product") Product product, @Param("product_isEnabled_array") Byte[] product_isEnabled_array, @Param("orderUtil") OrderUtil orderUtil, @Param("pageUtil") PageUtil pageUtil);

    /**
     * 查询产品标题列表
     *
     * @param product
     * @param pageUtil
     * @return
     */
    List<Product> selectTitle(@Param("product") Product product, @Param("pageUtil") PageUtil pageUtil);

    /**
     * 查询单条产品
     *
     * @param product_Id
     * @return
     */
    Product selectOne(Integer product_Id);

    /**
     * 查询产品总数
     *
     * @param product
     * @param product_isEnabled_array
     * @return
     */
    Integer selectTotal(@Param("product") Product product, @Param("product_isEnabled_array") Byte[] product_isEnabled_array);

    /**
     * 获取组合商品列表
     *
     * @param product
     * @param bytes
     * @param orderUtil
     * @param pageUtil
     * @param product_name_split
     * @return
     */
    List<Product> selectMoreList(@Param("product") Product product, @Param("product_isEnabled_array") Byte[] bytes, @Param("orderUtil") OrderUtil orderUtil, @Param("pageUtil") PageUtil pageUtil, @Param("product_name_split") String[] product_name_split);

    /**
     * 按组合条件获取产品总数量
     *
     * @param product
     * @param product_isEnabled_array
     * @param product_name_split
     * @return
     */
    Integer selectMoreListTotal(@Param("product") Product product, @Param("product_isEnabled_array") Byte[] product_isEnabled_array, @Param("product_name_split") String[] product_name_split);
}
