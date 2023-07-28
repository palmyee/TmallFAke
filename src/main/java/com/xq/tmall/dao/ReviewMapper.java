package com.xq.tmall.dao;

import com.xq.tmall.entity.Review;
import com.xq.tmall.util.OrderUtil;
import com.xq.tmall.util.PageUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewMapper {
    /**
     * 新增评论
     *
     * @param review
     * @return
     */
    Integer insertOne(@Param("review") Review review);

    /**
     * 修改评论
     *
     * @param review
     * @return
     */
    Integer updateOne(@Param("review") Review review);

    /**
     * 批量删除评论
     *
     * @param review_id_list
     * @return
     */
    Integer deleteList(@Param("review_id_list") Integer[] review_id_list);

    /**
     * 删除单条数据
     *
     * @param id
     * @return
     */
    Integer deleteData(Integer id);

    /**
     * 查询评论列表
     *
     * @param review
     * @param pageUtil
     * @return
     */
    List<Review> selectReviewList(@Param("review") Review review, @Param("orderUtil") OrderUtil orderUtil, @Param("pageUtil") PageUtil pageUtil);

    /**
     * 通过用户id查询评论
     *
     * @param user_id
     * @param pageUtil
     * @return
     */
    List<Review> selectByUserId(@Param("user_id") Integer user_id, @Param("pageUtil") PageUtil pageUtil);

    /**
     * 通过产品id查询评论
     *
     * @param product_id
     * @param pageUtil
     * @return
     */
    List<Review> selectByProductId(@Param("product_id") Integer product_id, @Param("pageUtil") PageUtil pageUtil);

    /**
     * 查询单条评论
     *
     * @param review_id
     * @return
     */
    Review selectOne(Integer review_id);

    /**
     * 查询评论总数
     *
     * @param review
     * @return
     */
    Integer selectTotal(@Param("review") Review review);

    /**
     * 根据用户id查询评论总数
     *
     * @param user_id
     * @return
     */
    Integer selectTotalByUserId(Integer user_id);

    /**
     * 根据产品id查询评论总数
     *
     * @param product_id
     * @return
     */
    Integer selectTotalByProductId(Integer product_id);

    /**
     * 根据产品订单id查询评论总数
     *
     * @param productOrderItem_id
     * @return
     */
    Integer selectTotalByOrderItemId(Integer productOrderItem_id);
}
