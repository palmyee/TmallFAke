package com.xq.tmall.dao;

import com.xq.tmall.entity.OrderGroup;
import com.xq.tmall.entity.ProductOrderItem;
import com.xq.tmall.util.PageUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ProductOrderItemMapper {
    /**
     * 新增产品订单项
     *
     * @param productOrderItem
     * @return
     */
    Integer insertOne(@Param("productOrderItem") ProductOrderItem productOrderItem);

    /**
     * 修改产品订单项
     *
     * @param productOrderItem
     * @return
     */
    Integer updateOne(@Param("productOrderItem") ProductOrderItem productOrderItem);

    /**
     * 批量删除产品订单项
     *
     * @param productOrderItem_id_list
     * @return
     */
    Integer deleteList(@Param("productOrderItem_id_list") Integer[] productOrderItem_id_list);

    /**
     * 查询产品订单项列表
     *
     * @param pageUtil
     * @return
     */
    List<ProductOrderItem> selectProductOrderItemList(@Param("pageUtil") PageUtil pageUtil);

    /**
     * 根据订单id查询产品订单项
     *
     * @param order_id
     * @param pageUtil
     * @return
     */
    List<ProductOrderItem> selectByOrderId(@Param("order_id") Integer order_id, @Param("pageUtil") PageUtil pageUtil);

    /**
     * 根据用户id查询产品订单项
     *
     * @param user_id
     * @param pageUtil
     * @return
     */
    List<ProductOrderItem> selectByUserId(@Param("user_id") Integer user_id, @Param("pageUtil") PageUtil pageUtil);

    /**
     * 根据产品id查询产品订单项
     *
     * @param product_id
     * @param pageUtil
     * @return
     */
    List<ProductOrderItem> selectByProductId(@Param("product_id") Integer product_id, @Param("pageUtil") PageUtil pageUtil);

    /**
     * 查询单条产品订单项
     *
     * @param productOrderItem_id
     * @return
     */
    ProductOrderItem selectOne(Integer productOrderItem_id);

    /**
     * 查询产品订单项总数
     *
     * @return
     */
    Integer selectTotal();

    /**
     * 根据订单id查询产品订单项总数
     *
     * @param order_id
     * @return
     */
    Integer selectTotalByOrderId(Integer order_id);

    /**
     * 根据用户id查询产品订单项总数
     *
     * @param user_id
     * @return
     */
    Integer selectTotalByUserId(Integer user_id);

    /**
     * 根据茶品id查询产品订单项
     *
     * @param product_id
     * @return
     */
    Integer selectSaleCount(Integer product_id);

    /**
     * 根据产品id查询订单组
     *
     * @param product_id
     * @param beginDate
     * @param endDate
     * @return
     */
    List<OrderGroup> getTotalByProductId(@Param("product_id") Integer product_id, @Param("beginDate") Date beginDate, @Param("endDate") Date endDate);
}
