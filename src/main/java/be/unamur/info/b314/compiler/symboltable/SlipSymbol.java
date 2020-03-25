package be.unamur.info.b314.compiler.symboltable;

/**
 * @overview A SlipSymbol is an association of a name and type
 * In Slip language a symbol represents a variable or a method
 * A SlipSymble is mutable
 * @specfield name: String // the identifier of the symbol
 * @specfield type: Types // the type of the symbol, the possible values in Slip are 'integer', 'char', 'boolean' or 'void'
 */
public interface SlipSymbol {
    enum Types {
        INTEGER("integer"), CHARACTER("char"), BOOLEAN("boolean"), VOID("void"), STRUCT("record");

        private String name;

        Types(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    /**
     * @return name
     */
    String getName();

    /**
     * @return type
     */
    Types getType();

}
