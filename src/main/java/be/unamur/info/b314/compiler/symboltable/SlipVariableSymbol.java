package be.unamur.info.b314.compiler.symboltable;

public class SlipVariableSymbol extends SlipBaseSymbol {

    public SlipVariableSymbol(String name, Type type, boolean isAssignable) {
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


    public Object getValue(){
        if (this.value == null){
            return null;
        }
        return switchValue(this.value);
    }

    public void setValue(Object value) {
        if (value != null) {
            this.value = value.toString();
        }
    }

}
