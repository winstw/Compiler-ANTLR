package be.unamur.info.b314.compiler.main.checking;

import be.unamur.info.b314.compiler.SlipBaseVisitor;
import be.unamur.info.b314.compiler.symboltable.SlipScope;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

public abstract class CheckSlipVisitor<T> extends SlipBaseVisitor<T> {
    protected ErrorHandler errorHandler;
    protected ParseTreeProperty<SlipScope> scopes;

    public CheckSlipVisitor(ErrorHandler errorHandler) {
        super();
        this.errorHandler = errorHandler;
    }

    public ParseTreeProperty<SlipScope> getScopes() {
        return scopes;
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
