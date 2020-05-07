package be.unamur.info.b314.compiler.main.symboltable;

/**
 * @overview A SlipSymbol is an association of a name and type
 * In Slip language a symbol represents a variable or a method
 * A SlipSymbol is mutable
 * @specfield name: String // the identifier of the symbol
 * @specfield type: Types // the type of the symbol, the possible values in Slip are 'integer', 'char', 'boolean', 'record' or 'void'
 * @specfield assignable: boolean // is the symbol assignable or just initialisable
 */
public interface SlipSymbol {
    enum Type {
        INTEGER("integer"), CHARACTER("char"), BOOLEAN("boolean"), VOID("void"), STRUCT("record"), STRING("string");

        private String name;

        Type(String name) {
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
    Type getType();

    /**
     * @return assignable
     */
    boolean isAssignable();
    boolean isArray();

}
