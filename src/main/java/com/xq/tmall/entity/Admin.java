package com.xq.tmall.entity;

import lombok.Data;

/**
 * 管理员类
 */
@Data
public class Admin {
    /**
     * 编号
     */
    private Integer admin_id;
    /**
     * 账户名
     */
    private String admin_name;
    /**
     * 昵称
     */
    private String admin_nickname;
    /**
     * 密码
     */
    private String admin_password;
    /**
     * 头像地址
     */
    private String admin_profile_picture_src;
}
