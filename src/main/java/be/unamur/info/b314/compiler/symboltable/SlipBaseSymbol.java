package be.unamur.info.b314.compiler.symboltable;

public abstract class SlipBaseSymbol implements SlipSymbol {

    private String name;
    private SlipSymbol.Types type;
    private boolean isAssignable;

    public SlipBaseSymbol(String name, Types type, boolean isAssignable) {
        this.name = name;
        this.type = type;
        this.isAssignable = isAssignable;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Types getType() {
        return type;
    }

    @Override
    public boolean isAssignable() {
        return isAssignable;
    }
}
