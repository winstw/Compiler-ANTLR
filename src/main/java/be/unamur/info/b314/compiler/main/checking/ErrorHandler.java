package be.unamur.info.b314.compiler.main.checking;

import org.antlr.v4.runtime.Token;

public class ErrorHandler {
    private boolean errorOccurred = false;

    public void signalError(Token t, String msg) {
        this.errorOccurred = true;
        System.out.println("signalERROR : ");
        if (msg != null) {
            System.err.printf("line %d:%d %s\n", t.getLine(), t.getCharPositionInLine(), msg);
        }
    }

    public <U> boolean checkEqual(U firstValue, U secondValue, Token t, String message){
        boolean equal = true;
        if (!firstValue.equals(secondValue)){
            equal = false;
            signalError(t, message);
        }
        return equal;
    }

    public boolean hasErrorOccurred() {
        return errorOccurred;
    }


}
