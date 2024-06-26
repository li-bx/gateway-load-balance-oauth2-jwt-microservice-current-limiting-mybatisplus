package com.hd.microsysservice.service.Impl;

import com.hd.common.RetResponse;
import com.hd.common.RetResult;
import com.hd.microsysservice.service.UserCenterFeignService;
import feign.Response;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

@Component
public
class UserCenterServiceImpl implements UserCenterFeignService {
    @Override
    public RetResult add(String account, String enterprise, String password) {
        return RetResponse.makeTimeOutRsp("sorry! 网络异常，服务暂时无法访问!");
    }

    @Override
    public RetResult remove(Long id) {
        return RetResponse.makeTimeOutRsp("sorry! 网络异常，服务暂时无法访问!");
    }

    @Override
    public RetResult changepwd(String account,String enterprise, String password,String passwordOld) {
        return RetResponse.makeTimeOutRsp("sorry! 网络异常，服务暂时无法访问!");
    }

    @Override
    public RetResult resetpwd(Long id) {
        return RetResponse.makeTimeOutRsp("sorry! 网络异常，服务暂时无法访问!");
    }

    @Override
    public RetResult userExist(String account, String enterprise) {
        return RetResponse.makeTimeOutRsp("sorry! 网络异常，服务暂时无法访问!");
    }

    @Override
    public Response downloadLicense(String machineCode,Long userCount,Long days,String enterpriseId) {
        return null;
    }
}
