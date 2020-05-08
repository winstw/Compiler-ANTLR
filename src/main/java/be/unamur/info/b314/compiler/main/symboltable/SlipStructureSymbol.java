package be.unamur.info.b314.compiler.main.symboltable;

import be.unamur.info.b314.compiler.exception.SymbolNotFoundException;

public class SlipStructureSymbol extends SlipScopedSymbol {

    public SlipStructureSymbol(String name, SlipScope parentScope, boolean isAssignable) {
        super(name, Type.STRUCT, parentScope, isAssignable);
    }

    @Override
    public SlipSymbol resolve(String name) throws SymbolNotFoundException {
        SlipSymbol symbol = symbols.get(name);

        if (symbol == null) {
            throw new SymbolNotFoundException();
        }

        return symbol;
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

    @Override
    public SlipStructureSymbol clone() {

        SlipStructureSymbol structCopy = new SlipStructureSymbol(this.getName(), this.getParentScope(), this.isAssignable());

        for (String key : symbols.keySet()) {
            structCopy.symbols.put(key, this.symbols.get(key).clone());
        }

        return structCopy;

    }
}
