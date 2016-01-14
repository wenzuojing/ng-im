package com.github.wens.serializer;

import com.caucho.hessian.io.SerializerFactory;

/**
 * Created by wens on 15-3-18.
 */
public class Hessian2SerializerFactory extends SerializerFactory {

    public static final SerializerFactory SERIALIZER_FACTORY = new Hessian2SerializerFactory();

    private Hessian2SerializerFactory() {
    }

    @Override
    public ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }


}
