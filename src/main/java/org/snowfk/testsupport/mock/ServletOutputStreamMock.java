package org.snowfk.testsupport.mock;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletOutputStream;

public class ServletOutputStreamMock extends ServletOutputStream {

    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    
    @Override
    public void write(int arg0) throws IOException {
        bout.write(arg0);
    }
    
    @Override
    public void close() throws IOException {
        bout.close();
    }

    @Override
    public void flush() throws IOException {
        bout.flush();
    }

    public String toString(){
        try {
            return bout.toString("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    public byte[] toByArray(){
        return bout.toByteArray();
    }
    
    

}
