package com.xq.tmall.dao;

import com.xq.tmall.entity.User;
import com.xq.tmall.util.OrderUtil;
import com.xq.tmall.util.PageUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface UserMapper {
    /**
     * 新增用户
     * @param user
     * @return
     */
    Integer insertOne(@Param("user") User user);

    /**
     * 修改用户
     * @param user
     * @return
     */
    Integer updateOne(@Param("user") User user);

    /**
     * 查询用户列表
     * @param user
     * @param orderUtil
     * @param pageUtil
     * @return
     */
    List<User> selectUserList(@Param("user") User user, @Param("orderUtil") OrderUtil orderUtil, @Param("pageUtil") PageUtil pageUtil);

    /**
     * 查询单条用户
     * @param user_id
     * @return
     */
    User selectOne(Integer user_id);

    /**
     * 查询用户登录信息
     * @param user_name
     * @param user_password
     * @return
     */
    User selectByLogin(@Param("user_name") String user_name, @Param("user_password") String user_password);

    /**
     * 查询用户总数
     * @param user
     * @return
     */
    Integer selectTotal(@Param("user") User user);
}
