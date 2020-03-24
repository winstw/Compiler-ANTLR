package be.unamur.info.b314.compiler.exception;

public class SymbolAlreadyDefinedException extends RuntimeException {

    public SymbolAlreadyDefinedException() {
    }

    public SymbolAlreadyDefinedException(String message) {
        super(message);
    }

    public SymbolAlreadyDefinedException(String message, Throwable cause) {
        super(message, cause);
    }

    public SymbolAlreadyDefinedException(Throwable cause) {
        super(cause);
    }

    public SymbolAlreadyDefinedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
