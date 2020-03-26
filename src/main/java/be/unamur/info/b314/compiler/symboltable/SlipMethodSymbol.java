package be.unamur.info.b314.compiler.symboltable;

public class SlipMethodSymbol extends SlipScopedSymbol {

    public SlipMethodSymbol(String name, Types type, SlipScope parentScope) {
        super(name, type, parentScope, false);
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

}
