package be.unamur.info.b314.compiler.main.symboltable;

import java.util.List;

public class SlipArraySymbol extends SlipBaseSymbol {
    private String[][] value;

    public SlipArraySymbol(String name, Type type, boolean isAssignable, int first_dim, int second_dim) {
        super(name, type, isAssignable);
        this.value = new String[first_dim][second_dim];

        for (int i = 0; i < first_dim; i++) {
            for (int j = 0; j < second_dim; j++) {
                this.value[i][j] = this.getInitValue();
            }
        }
    }

    @Override
    public SlipArraySymbol clone() {
        return new SlipArraySymbol(this.getName(), this.getType(), this.isAssignable(), this.value.length, this.value[0].length);
    }

    /**
     * @modifies this
     * @effects this.value[indexes.get(0)][indexes.get(1) || 0] = value.toString()
     */
    public void setValue(List<Integer> indexes, Object value) {
        if (value != null) {
            this.value[indexes.get(0)][indexes.size() > 1 ? indexes.get(1) : 0] = value.toString();
        }
    }

    /**
     * @modifies this
     * @effects this.value[i][j] = value(i * value[0].length + j).toString() forall 0 <= i <= value.length; 0 <= j <= value[0].length
     */
    public void setValues(List<Object> values) {
        int first_dim = this.value.length;
        int second_dim = this.value[0].length;
        for (int i = 0; i < first_dim; i++) {
            for (int j = 0; j < second_dim; j++) {
                int index = i * second_dim + j;
                this.value[i][j] = values.get(index).toString();
            }
        }
    }

    /**
     * @return value of this.value[indexes.get(0)][indexes.get(1) || 0]
     */
    public Object getValue(List<Integer> indexes) {
        String rawValue = this.value[indexes.get(0)][indexes.size() > 1 ? indexes.get(1) : 0];
        if (rawValue == null) {
            return null;
        }
        return this.switchValue(rawValue);
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
