package be.unamur.info.b314.compiler.symboltable;

public class SlipGlobalScope extends SlipBaseScope {

    public SlipGlobalScope() {
        super("global", null);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("global");

        for (String key : symbols.keySet()) {
            sb.append("\n\t");
            sb.append(key);
            sb.append(": ");
            sb.append(symbols.get(key).getType());
        }

        return sb.toString();
    }

}
