package be.unamur.info.b314.compiler.symboltable;

public abstract class SlipScopedSymbol extends SlipBaseScope implements SlipSymbol {

    private Types type;

    public SlipScopedSymbol(String name, Types type, SlipScope parentScope) {
        super(name, parentScope);
        this.type = type;
    }

    @Override
    public Types getType() {
        return null;
    }

}
