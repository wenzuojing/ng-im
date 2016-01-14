package com.github.wens.serializer;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by wens on 15-3-18.
 */
public class Hessian2Serializer implements Serializer {

    @Override
    public String name() {
        return "hessian2";
    }


    @Override
    public <T> byte[] serialize(T obj) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Hessian2Output mH2o = null;
        byte[] bytes = null;
        try {
            mH2o = new Hessian2Output(baos);
            mH2o.startMessage();
            mH2o.setSerializerFactory(Hessian2SerializerFactory.SERIALIZER_FACTORY);
            mH2o.writeObject(obj);
            mH2o.completeMessage();
            mH2o.flush();
            bytes = baos.toByteArray();

        } finally {
            if (mH2o != null) {
                try {
                    mH2o.close();
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }

            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }
        }

        return bytes;
    }

    @Override
    public <T> T deserialize(byte[] bytes) throws IOException {

        if (bytes == null || bytes.length == 0) {
            return null;
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        Hessian2Input mH2i = null;

        try {
            mH2i = new Hessian2Input(bais);

            mH2i.setSerializerFactory(Hessian2SerializerFactory.SERIALIZER_FACTORY);
            mH2i.startMessage();
            T obj = (T) mH2i.readObject();
            mH2i.completeMessage();
            return obj;
        } finally {
            if (mH2i != null) {
                try {
                    mH2i.close();
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }
            if (bais != null) {
                try {
                    bais.close();
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }
        }

    }
}
