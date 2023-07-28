package com.xq.tmall.dao;

import com.xq.tmall.entity.ProductImage;
import com.xq.tmall.util.PageUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageMapper {
    /**
     * 新增产品图片
     *
     * @param productImage
     * @return
     */
    Integer insertOne(@Param("productImage") ProductImage productImage);

    /**
     * 批量新增产品图片
     *
     * @param productImageList
     * @return
     */
    Integer insertList(@Param("productImage_list") List<ProductImage> productImageList);

    /**
     * 修改产品图片
     *
     * @param productImage
     * @return
     */
    Integer updateOne(@Param("productImage") ProductImage productImage);

    /**
     * 批量删除产品图片
     *
     * @param productImage_id_list
     * @return
     */
    Integer deleteList(@Param("productImage_id_list") Integer[] productImage_id_list);

    /**
     * 查询产品图片列表
     *
     * @param product_id
     * @param productImage_type
     * @param pageUtil
     * @return
     */
    List<ProductImage> selectProductImageList(@Param("product_id") Integer product_id, @Param("productImage_type") Byte productImage_type, @Param("pageUtil") PageUtil pageUtil);

    /**
     * 查询单条产品图片
     *
     * @param productImage_id
     * @return
     */
    ProductImage selectOne(Integer productImage_id);

    /**
     * 查询产品图片总数
     *
     * @param product_id
     * @param productImage_type
     * @return
     */
    Integer selectTotal(@Param("product_id") Integer product_id, @Param("productImage_type") Byte productImage_type);
}
