package be.unamur.info.b314.compiler.symboltable;


import java.util.List;

public class SlipArraySymbol extends SlipBaseSymbol {
    private String[][] value;

    public SlipArraySymbol(String name, Type type, boolean isAssignable, List<Integer> sizes) {
        super(name, type, isAssignable);
        System.out.println("ARRAY SIZES :" + sizes);
        this.value = new String[sizes.get(0)][sizes.size() > 1 ? sizes.get(1) : 1];
    }

    public void setValue(List<Integer> indexes, Object value) {
        if (value != null) {
            this.value[indexes.get(0)][indexes.size() > 1 ? indexes.get(1) : 0] = value.toString();
        }
    }

    public Object getValue(List<Integer> indexes) {
        String rawValue = this.value[indexes.get(0)][indexes.size() > 1 ? indexes.get(1) : 0];
        if (rawValue == null){
            return null;
        }
        return this.switchValue(rawValue);
    }

//    public Object getValue(){
//        int length = this.value.length;
//        Object[] values = new Object[length];
//        for (int i = 0; i < length; i ++){
//            values[i] = this.getValue(i);
//        }
//        return values;
//    }

    @Override
    public boolean isArray(){
        return true;
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
