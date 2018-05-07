package com.sj.http;

import android.support.annotation.Keep;

import com.alibaba.fastjson.JSON;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 创建时间: on 2018/4/4.
 * 创建人: 孙杰
 * 功能描述:Gson解析类
 */
@Keep
public class GsonResponsePasare<T> implements ParameterizedType {

    public T deal(String response) {
        Type gsonType = this;
//        BaseResponse<T> commonResponse = new Gson().fromJson(response, gsonType);
        BaseResponse<T> commonResponse = JSON.parseObject(response, gsonType);
        return commonResponse.object;
    }

    @Override
    public Type[] getActualTypeArguments() {
        Class clz = this.getClass();
        //这里必须注意在外面使用new GsonResponsePasare<GsonResponsePasare.DataInfo>(){};实例化时必须带上{},否则获取到的superclass为Object
        Type superclass = clz.getGenericSuperclass(); //getGenericSuperclass()获得带有泛型的父类
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        return parameterized.getActualTypeArguments();
    }

    @Override
    public Type getRawType() {
        return BaseResponse.class;
    }

    @Override
    public Type getOwnerType() {
        return null;
    }
}
