package be.unamur.info.b314.compiler.symboltable;

public abstract class SlipBaseSymbol implements SlipSymbol {
    private String value = null;
    private String name;
    private Type type;
    private boolean isAssignable;

    public SlipBaseSymbol(String name, Type type, boolean isAssignable) {
        this.name = name;
        this.type = type;
        this.isAssignable = isAssignable;
    }

    public void setValue(Object value) {
        this.value = value.toString();
    }

    public Object getValue(){
        if (this.value == null){
            return null;
        }
        switch (this.type) {
            case BOOLEAN:
                return Boolean.parseBoolean(this.value);
            case CHARACTER:
                return this.value.charAt(0);
            case INTEGER:
                return Integer.parseInt(this.value);
            default:
                return this.value;
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public boolean isAssignable() {
        return isAssignable;
    }
}
