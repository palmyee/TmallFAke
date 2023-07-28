package com.xq.tmall.dao;

import com.xq.tmall.entity.Category;
import com.xq.tmall.util.PageUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryMapper {
    /**
     * 新增类别
     *
     * @param category
     * @return
     */
    Integer insertOne(@Param("category") Category category);

    /**
     * 修改类别
     *
     * @param category
     * @return
     */
    Integer updateOne(@Param("category") Category category);

    /**
     * 分页查询类别列表
     *
     * @param category_name
     * @param pageUtil
     * @return
     */
    List<Category> selectCategoryList(@Param("category_name") String category_name, @Param("pageUtil") PageUtil pageUtil);

    /**
     * 查询单条类别
     *
     * @param category_id
     * @return
     */
    Category selectOne(Integer category_id);

    /**
     * 查询类别总数
     *
     * @param category_name
     * @return
     */
    Integer selectTotal(String category_name);
}