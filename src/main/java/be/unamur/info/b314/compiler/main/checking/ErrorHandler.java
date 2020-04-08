package be.unamur.info.b314.compiler.main.checking;

import org.antlr.v4.runtime.Token;

public class ErrorHandler {
    boolean errorOccurred = false;

    void signalError(Token t, String msg) {
        this.errorOccurred = true;
        System.out.println("signalERROR : ");
        if (msg != null) {
            System.err.printf("line %d:%d %s\n", t.getLine(), t.getCharPositionInLine(), msg);
        }
    }

    public boolean isErrorOccurred() {
        return errorOccurred;
    }


}
