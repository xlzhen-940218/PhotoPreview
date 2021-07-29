package com.imobie.photopreview;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Stark on 2017/2/8.
 * 快速将Json或实体类进行相互转换
 */
public class FastTransJson {
    public static String toJson(Object obj) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeSpecialFloatingPointValues();
        Gson gson = gsonBuilder.create();
        return gson.toJson(obj);
    }

    //根据泛型返回解析制定的类型
    public static  <T> T fromToJson(String json,Class<T> cls){
        try{
            return new Gson().fromJson(json, cls);
        }catch (JsonSyntaxException pEx){
            return null;
        }
    }

    public static <T> List<T> fromToListJson(String json,Class<T> cls){
        try{
            Type type = new ParameterizedTypeImpl(cls);
            return new Gson().fromJson(json, type);
        }catch (JsonSyntaxException pEx){
            return null;
        }
    }
    private static class ParameterizedTypeImpl implements ParameterizedType {
        Class clazz;

        public ParameterizedTypeImpl(Class clz) {
            clazz = clz;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return new Type[]{clazz};
        }

        @Override
        public Type getRawType() {
            return List.class;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    }
}
