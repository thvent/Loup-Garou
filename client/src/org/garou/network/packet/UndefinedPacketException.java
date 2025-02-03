package org.garou.network.packet;

public class UndefinedPacketException extends Exception{
    
    public UndefinedPacketException(String string) {
        super(string);
    }
    
    public UndefinedPacketException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public UndefinedPacketException(Throwable cause) {
        super(cause);
    }
    
    protected UndefinedPacketException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
