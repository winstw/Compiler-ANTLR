package be.unamur.info.b314.compiler.symboltable;

import be.unamur.info.b314.compiler.exception.SymbolNotFoundException;

public class SlipStructureSymbol extends SlipScopedSymbol {

    public SlipStructureSymbol(String name, SlipScope parentScope) {
        super(name, Types.STRUCT, parentScope);
    }

    @Override
    public SlipSymbol resolve(String name) throws SymbolNotFoundException {
        return super.resolve(name);
    }
}
