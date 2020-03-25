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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("record ");
        sb.append(getName());

        for (String key : symbols.keySet()) {
            sb.append("\n\t");
            sb.append(key);
            sb.append(": ");
            sb.append(symbols.get(key).getType());
        }

        sb.append("\n");

        return sb.toString();
    }

}
