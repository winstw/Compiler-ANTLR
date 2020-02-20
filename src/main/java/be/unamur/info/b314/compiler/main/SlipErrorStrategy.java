package be.unamur.info.b314.compiler.main;

import org.antlr.v4.runtime.*;

public class SlipErrorStrategy extends DefaultErrorStrategy {

    public class ParserException extends RuntimeException {
        public ParserException() {
        }

        public ParserException(String message) {
            super(message);
        }

        public ParserException(String message, Throwable cause) {
            super(message, cause);
        }

        public ParserException(Throwable cause) {
            super(cause);
        }
    }

    @Override
    public void recover(Parser recognizer, RecognitionException e) {
        System.out.println("recover");
        throw new ParserException(e);
    }

    @Override
    public Token recoverInline(Parser recognizer) throws RecognitionException {
        System.out.println("recoverInLine");
        throw new ParserException(new InputMismatchException(recognizer));
    }

    @Override
    public void sync(Parser recognizer) {
        // ne rien faire
    }
}
