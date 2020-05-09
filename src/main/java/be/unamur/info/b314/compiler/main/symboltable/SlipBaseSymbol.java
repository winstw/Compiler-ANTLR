package be.unamur.info.b314.compiler.main.symboltable;

public abstract class SlipBaseSymbol implements SlipSymbol {
    private String name;
    protected Type type;
    private boolean isAssignable;

    public SlipBaseSymbol(String name, Type type, boolean isAssignable) {
        this.name = name;
        this.type = type;
        this.isAssignable = isAssignable;
    }

    protected String getInitValue() {
        switch (this.type) {
            case CHARACTER:
                return "\000";
            case INTEGER:
                return "0";
            case BOOLEAN:
                return "false";
        }
        return null;
    }

    protected Object switchValue(String value) {
        switch (this.type) {
            case BOOLEAN:
                return Boolean.parseBoolean(value);
            case CHARACTER:
                return value.charAt(0);
            case INTEGER:
                return Integer.parseInt(value);
            default:
                return value;
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

    @Override
    public abstract SlipBaseSymbol clone();
}
