package com.hd.auservice.controller;

import com.hd.auservice.Feign.IAuthFeignService;
import com.hd.common.RetResult;
import com.hd.common.model.RequiresPermissions;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Api(tags = "url权限验证Controller")
//@RefreshScope
@RestController
@Slf4j
@RequestMapping("/")
public class AuthenticationController {

    @Autowired
    IAuthFeignService iAuthFeignService;

    HashMap<String, List<String>> userPermissionList = new HashMap<>();
    HashMap<String, List<String>> scopePermissionList = new HashMap<>();

    List permissionCodeList = new ArrayList<String>(
            Arrays.asList("micro:test1", "micro:test2")
    );
    HashMap<String,String> uriCode=new HashMap<String,String>();
    {
        uriCode.put("get /test1","micro:test1");
        uriCode.put("get /test2","micro:test2");
    }

    public AuthenticationController() {
        //TODO: 权限从数据库取，放redis
        userPermissionList.put("liwei", permissionCodeList);
        scopePermissionList.put("read", permissionCodeList);
        scopePermissionList.put("write", permissionCodeList);
    }

    @RequiresPermissions("permission:auth")
    @ApiOperation(value = "url权限验证func")
    @PostMapping(value = "/auth")
    public Boolean auth(@RequestParam("account") String account, @RequestParam("scopes") String scopes, @RequestParam("uri") String uri
            , @RequestParam("method") String method, @RequestParam("companyCode") String companyCode) throws InterruptedException {

        //Thread.sleep(60);Thread.currentThread().getId()
        log.info(String.format("check auth: companyCode:%s,user:%s,scope:%s",companyCode,account,scopes));
        //scopes=scopes.toUpperCase();
        List scopeList =  Arrays.asList(scopes.split(","));
        String permissionCode=uriCode.get(method.toLowerCase() + " " + uri);
        if(permissionCode==null){
            //不限制
            return true;
        }
        //TODO: 具体的权限判断
        //sas模式 需要加入条件companycode
        //log.info("companyCode-->"+companyCode);
        //首先判断scope
        if (scopeList.contains("user")) {
            //如果scope是user，使用用户的permission判断
            if ((userPermissionList.get(account)!=null) && userPermissionList.get(account).contains(permissionCode)) {
                return true;
            }
        } else {
            //根据scope 判断,不管user是谁
            for(Object scope:scopeList){
                if((scopePermissionList.get(scope)!=null)&&scopePermissionList.get(scope).contains(permissionCode)){
                    return  true;
                }
            }
        }
        return false;
    }

    /**
     * 从网关转接下请求，使权限判断也使用负载均衡
     * @param account
     * @param scopes
     * @param uri
     * @param method
     * @param companyCode
     * @return
     * @throws Exception
     */
    @RequiresPermissions("permission:authbr")
    @ApiOperation(value = "url权限验证bridge func")
    @PostMapping(value = "/authbridge")
    public RetResult authbridge(@RequestParam("account") String account, @RequestParam("scopes") String scopes, @RequestParam("uri") String uri
            , @RequestParam("method") String method, @RequestParam("companyCode") String companyCode) throws Exception {
//        int dd = 1 / 0;
        //Thread.sleep(400);
//        int i = 0;
//        while (i++ < Integer.MAX_VALUE / 5) {
//            i += i * (new Random()).nextInt();
//        }

        RetResult retResult = iAuthFeignService.auth(account,scopes,uri,method,companyCode);

        return retResult;
    }
}