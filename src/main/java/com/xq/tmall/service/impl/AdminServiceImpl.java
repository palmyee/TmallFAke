package com.xq.tmall.service.impl;

import com.xq.tmall.dao.AdminMapper;
import com.xq.tmall.entity.Admin;
import com.xq.tmall.service.AdminService;
import com.xq.tmall.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private AdminMapper adminMapper;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public boolean add(Admin admin) {
        return adminMapper.insertOne(admin) > 0;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public boolean update(Admin admin) {
        return adminMapper.updateOne(admin) > 0;
    }

    @Override
    public List<Admin> getList(String admin_name, PageUtil pageUtil) {
        return adminMapper.selectAdminList(admin_name, pageUtil);
    }

    @Override
    public Admin get(String admin_name, Integer admin_id) {
        return adminMapper.selectOne(admin_name, admin_id);
    }

    @Override
    public Integer login(String admin_name, String admin_password) {
        return adminMapper.selectByLogin(admin_name, admin_password);
    }

    @Override
    public Integer getTotal(String admin_name) {
        return adminMapper.selectTotal(admin_name);
    }
}
