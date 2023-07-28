package com.xq.tmall.controller.fore;

import com.alibaba.fastjson.JSONObject;
import com.xq.tmall.controller.BaseController;
import com.xq.tmall.entity.Address;
import com.xq.tmall.service.AddressService;
import com.xq.tmall.util.Constants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 前台天猫-地址
 */
@Api(tags = "前台天猫-地址")
@Controller
public class ForeAddressController extends BaseController {
    @Autowired
    private AddressService addressService;

    //根据address_areaId获取地址信息-ajax
    @ApiOperation(value = "根据address_areaId获取地址信息", notes = "根据address_areaId获取地址信息")
    @ResponseBody
    @GetMapping(value = "address/{areaId}", produces = "application/json;charset=utf-8")
    protected String getAddressByAreaId(@PathVariable String areaId) {
        JSONObject object = new JSONObject();
        //获取AreaId为{}的地址信息
        List<Address> addressList = addressService.getList(null, areaId);
        if (addressList.isEmpty()) {
            object.put(Constants.SUCCESS, false);
            return String.valueOf(object);
        }
        //获取该地址可能的子地址信息
        List<Address> childAddressList = addressService.getList(null, addressList.get(0).getAddress_areaId());
        object.put(Constants.SUCCESS, true);
        object.put("addressList", addressList);
        object.put("childAddressList", childAddressList);
        return String.valueOf(object);
    }
}
