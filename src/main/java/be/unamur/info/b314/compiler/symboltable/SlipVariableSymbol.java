package be.unamur.info.b314.compiler.symboltable;

public class SlipVariableSymbol extends SlipBaseSymbol {

    public SlipVariableSymbol(String name, Types type, boolean isAssignable) {
        super(name, type, isAssignable);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("variable: ");
        sb.append(getName());
        sb.append(": ");
        sb.append(getType());
        sb.append("\n");
        return sb.toString();
    }
}
