package be.unamur.info.b314.compiler.symboltable;

public abstract class SlipScopedSymbol extends SlipBaseScope implements SlipSymbol {

    private Types type;
    private boolean isAssignable;

    public SlipScopedSymbol(String name, Types type, SlipScope parentScope, boolean isAssignable) {
        super(name, parentScope);
        this.type = type;
        this.isAssignable = isAssignable;
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
