package be.unamur.info.b314.compiler.symboltable;

import be.unamur.info.b314.compiler.exception.SymbolAlreadyDefinedException;
import be.unamur.info.b314.compiler.exception.SymbolNotFoundException;

import java.util.HashMap;
import java.util.Map;

public abstract class SlipBaseScope implements SlipScope{

    private String name;
    protected Map<String, SlipSymbol> symbols;
    private SlipScope parentScope;

    public SlipBaseScope(String name, SlipScope parentScope) {
        this.name = name;
        this.parentScope= parentScope;
        this.symbols = new HashMap<>();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void define(SlipSymbol symbol) throws NullPointerException, SymbolAlreadyDefinedException {

        if (symbol == null) {
            throw new NullPointerException();
        }

        if (symbols.get(symbol.getName()) == null) {
            symbols.put(symbol.getName(), symbol);
        } else {
            throw  new SymbolAlreadyDefinedException();
        }

    }

    @Override
    public SlipSymbol resolve(String name) throws SymbolNotFoundException {

        SlipSymbol symbol = symbols.get(name);

        if (parentScope != null && symbol == null) {
            symbol = parentScope.resolve(name);
        }

        if (symbol == null) {
            throw new SymbolNotFoundException();
        }

        return symbol;

    }

    @Override
    public SlipScope getParentScope() {
        return parentScope;
    }
}
