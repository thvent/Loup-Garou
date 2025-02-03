package org.garou.network.packet;

public class UnsupportedSerializationException extends Exception {

    public UnsupportedSerializationException(String string) {
        super(string);
    }
    
    public UnsupportedSerializationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public UnsupportedSerializationException(Throwable cause) {
        super(cause);
    }
    
    protected UnsupportedSerializationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
