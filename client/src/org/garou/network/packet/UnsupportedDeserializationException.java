package org.garou.network.packet;

public class UnsupportedDeserializationException extends Exception {

    public UnsupportedDeserializationException(String string) {
        super(string);
    }
    
    public UnsupportedDeserializationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public UnsupportedDeserializationException(Throwable cause) {
        super(cause);
    }
    
    protected UnsupportedDeserializationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
