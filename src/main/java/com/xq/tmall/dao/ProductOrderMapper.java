package com.xq.tmall.dao;

import com.xq.tmall.entity.OrderGroup;
import com.xq.tmall.entity.ProductOrder;
import com.xq.tmall.util.OrderUtil;
import com.xq.tmall.util.PageUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ProductOrderMapper {
    /**
     * 新增产品订单
     *
     * @param productOrder
     * @return
     */
    Integer insertOne(@Param("productOrder") ProductOrder productOrder);

    /**
     * 修改产品订单
     *
     * @param productOrder
     * @return
     */
    Integer updateOne(@Param("productOrder") ProductOrder productOrder);

    /**
     * 批量删除产品订单
     *
     * @param productOrder_id_list
     * @return
     */
    Integer deleteList(@Param("productOrder_id_list") Integer[] productOrder_id_list);

    /**
     * 查询产品订单列表
     *
     * @param productOrder
     * @param productOrder_status_array
     * @param orderUtil
     * @param pageUtil
     * @return
     */
    List<ProductOrder> selectProductOrderList(@Param("productOrder") ProductOrder productOrder, @Param("productOrder_status_array") Byte[] productOrder_status_array, @Param("orderUtil") OrderUtil orderUtil, @Param("pageUtil") PageUtil pageUtil);

    /**
     * 查询单条产品订单
     *
     * @param productOrder_id
     * @return
     */
    ProductOrder selectOne(Integer productOrder_id);

    /**
     * 根据订单号查询产品订单
     *
     * @param productOrder_code
     * @return
     */
    ProductOrder selectByCode(@Param("productOrder_code") String productOrder_code);

    /**
     * 查询产品订单总数
     *
     * @param productOrder
     * @param productOrder_status_array
     * @return
     */
    Integer selectTotal(@Param("productOrder") ProductOrder productOrder, @Param("productOrder_status_array") Byte[] productOrder_status_array);

    /**
     * 查询订单组列表
     *
     * @param beginDate
     * @param endDate
     * @return
     */
    List<OrderGroup> getTotalByDate(@Param("beginDate") Date beginDate, @Param("endDate") Date endDate);
}
