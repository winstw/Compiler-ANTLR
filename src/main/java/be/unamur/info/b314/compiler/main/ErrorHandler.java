package be.unamur.info.b314.compiler.main;

import org.antlr.v4.runtime.Token;

/**
 * @overview a ErrorHandler is a helper class to track and print errors occurred during compilation
 * @specfield errorOccurred: boolean // did an error occurred, once true, it cannot become false back
 */
public class ErrorHandler {
    private boolean errorOccurred = false;

    /**
     * @modifies this, System.err
     * @effects errorOccurred = true, print msg on System.err
     */
    public void signalError(Token t, String msg) {
        this.errorOccurred = true;
        System.out.println("signalERROR : ");
        if (msg != null) {
            System.err.printf("line %d:%d %s\n", t.getLine(), t.getCharPositionInLine(), msg);
        }
    }

    /**
     * @modifies this, System.err
     * @effects if firstValue != secondValue this.signalError(t, message)
     * @return firstValue = secondValue
     */
    public <U> boolean checkEqual(U firstValue, U secondValue, Token t, String message){
        boolean equal = true;
        if (!firstValue.equals(secondValue)){
            equal = false;
            signalError(t, message);
        }
        return equal;
    }

    /**
     * @return errorOccurred
     */
    public boolean hasErrorOccurred() {
        return errorOccurred;
    }


}
