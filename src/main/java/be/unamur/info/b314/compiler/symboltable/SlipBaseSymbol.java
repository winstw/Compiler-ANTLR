package be.unamur.info.b314.compiler.symboltable;

public abstract class SlipBaseSymbol implements SlipSymbol {

    private String name;
    private SlipSymbol.Types type;

    public SlipBaseSymbol(String name, Types type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Types getType() {
        return type;
    }

}
