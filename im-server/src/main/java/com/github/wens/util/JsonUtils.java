package com.github.wens.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * Created by wens on 16-1-12.
 */
public class JsonUtils {

    public static <T> T unmarshalFromByte(byte[] bytes, Class<T> targetClass) {
        return JSON.parseObject(bytes, targetClass, new Feature[0]);
    }

    public static <T> T unmarshalFromByte(byte[] bytes, TypeReference<T> type) {
        return JSON.parseObject(bytes, type.getType(), new Feature[0]);
    }

    public static byte[] marshalToByte(Object obj) {
        return JSON.toJSONBytes(obj, new SerializerFeature[0]);
    }

    public static byte[] marshalToByte(Object obj, SerializerFeature... features) {
        return JSON.toJSONBytes(obj, features);
    }
}
