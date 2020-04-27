package be.unamur.info.b314.compiler.symboltable;

public abstract class SlipScopedSymbol extends SlipBaseScope implements SlipSymbol {

    private Type type;
    private boolean isAssignable;

    public SlipScopedSymbol(String name, Type type, SlipScope parentScope, boolean isAssignable) {
        super(name, parentScope);
        this.type = type;
        this.isAssignable = isAssignable;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public boolean isAssignable() {
        return isAssignable;
    }

    @Override
    public boolean isArray() {
        return false;
    }

}
