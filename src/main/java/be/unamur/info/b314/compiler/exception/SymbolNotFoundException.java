package be.unamur.info.b314.compiler.exception;




/**
 *
 * @author James Ortiz - james.ortizvega@unamur.be
 */
public class SymbolNotFoundException extends Exception{

    public SymbolNotFoundException() {
    }

    public SymbolNotFoundException(String message) {
        super(message);
    }

    public SymbolNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public SymbolNotFoundException(Throwable cause) {
        super(cause);
    }

    public SymbolNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
