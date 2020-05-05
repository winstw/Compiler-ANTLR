package be.unamur.info.b314.compiler.main.checking;

import be.unamur.info.b314.compiler.SlipBaseVisitor;
import be.unamur.info.b314.compiler.symboltable.SlipScope;
import be.unamur.info.b314.compiler.symboltable.SlipSymbol;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

public abstract class CheckSlipVisitor extends SlipBaseVisitor<SlipSymbol.Type> {
    protected ErrorHandler eh;
    protected ParseTreeProperty<SlipScope> scopes;
    protected SlipScope currentScope;

    public CheckSlipVisitor(ErrorHandler errorHandler, ParseTreeProperty<SlipScope> scopes) {
        super();
        this.eh = errorHandler;
        this.scopes = scopes;
    }

    public ParseTreeProperty<SlipScope> getScopes() {
        return scopes;
    }

}
