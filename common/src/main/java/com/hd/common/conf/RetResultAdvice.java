package com.hd.common.conf;

import com.alibaba.fastjson.JSONObject;
import com.hd.common.RetCode;
import com.hd.common.RetResponse;
import com.hd.common.RetResult;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class RetResultAdvice implements ResponseBodyAdvice<Object> {

    //需要忽略拦截的类

    //这个方法表示对于哪些请求要执行beforeBodyWrite，返回true执行，返回false不执行
    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        //如果是返回了RetResult类就直接返回不做处理
        //        if(true) return  o;
        if (o instanceof RetResult) {
            return o;
        }
        //如果返回的数据是string类型的时候做的处理
        if(o instanceof String) {
            //如果o是string，且包含中文，会乱码；api中尽量统一使用RetResult
            String json = JSONObject.toJSONString(RetResponse.makeRsp("",o));
            return  json;
        }

        if(o instanceof Boolean){
            return RetResponse.makeRsp("",o);
        }
        //如果是swagger处理的对象，直接返回，否则swagger访问异常
        //return RetResponse.makeRsp("",o);
        return o;
    }
}

