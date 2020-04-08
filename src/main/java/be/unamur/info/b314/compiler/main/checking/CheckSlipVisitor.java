package be.unamur.info.b314.compiler.main.checking;

import be.unamur.info.b314.compiler.SlipBaseVisitor;
import org.antlr.v4.runtime.Token;

public abstract class CheckSlipVisitor<T> extends SlipBaseVisitor<T> {
    protected ErrorHandler errorHandler;

    CheckSlipVisitor(ErrorHandler errorHandler) {
        super();
        this.errorHandler = errorHandler;
    }

    protected void signalError(Token t, String msg) {
        this.errorHandler.signalError(t, msg);
    }

    protected <U> boolean checkEqual(U firstValue, U secondValue, Token t, String message){
        boolean equal = true;
        if (firstValue != secondValue){
            equal = false;
            signalError(t, message);
        }
        return equal;
    }
}
