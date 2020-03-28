package be.unamur.info.b314.compiler.symboltable;

public class SlipArraySymbol extends SlipBaseSymbol {

    public SlipArraySymbol(String name, Types type, boolean isAssignable) {
        super(name, type, isAssignable);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("array: ");
        sb.append(getName());
        sb.append(": ");
        sb.append(getType());
        sb.append("\n");
        return sb.toString();
    }

}
