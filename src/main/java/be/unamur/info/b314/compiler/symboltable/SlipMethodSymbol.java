package be.unamur.info.b314.compiler.symboltable;

import java.util.ArrayList;
import java.util.Iterator;

public class SlipMethodSymbol extends SlipScopedSymbol {

    ArrayList<SlipSymbol.Types> parameterTypes;

    public SlipMethodSymbol(String name, Types type, SlipScope parentScope) {
        super(name, type, parentScope, false);
        parameterTypes = new ArrayList<>();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("function ");
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

    /**
     * @modifies this
     * @effect add type to parameterTypes
     */
    public void addParameter(SlipSymbol.Types type) {
        parameterTypes.add(type);
    }

    public Iterator<SlipSymbol.Types> getParameterTypes() {
        return this.parameterTypes.iterator();
    }
}
