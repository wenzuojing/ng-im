package com.github.wens.exchange;

import java.io.IOException;

/**
 * Created by wens on 15-3-11.
 */
public interface Serializer {

    public String name();

    public <T> byte[] serialize(T obj) throws IOException;

    public <T> T deserialize(byte[] bytes) throws IOException;

}
