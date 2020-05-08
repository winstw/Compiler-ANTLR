package be.unamur.info.b314.compiler.main.symboltable;

import be.unamur.info.b314.compiler.exception.SymbolAlreadyDefinedException;
import be.unamur.info.b314.compiler.exception.SymbolNotFoundException;

/**
 * @overview A SlipScope is a dictionary associating a String with a Slip symbol
 * A SlipScope contains the symbols defined in its context
 * There is 3 types of scope in Slip: Global, Method and Structure
 * A SlipScope can also be a Slip symbol -> a scope can contain other scopes
 * A SlipScope is mutable
 * @specfield name: String // the name of the scope ("global", method name, or structure name)
 * @specfield parentScope: SlipScope // the scope in which the current scope is defined
 */
public interface SlipScope {

    /**
     * @return name
     */
    String getName();

    /**
     * @throws NullPointerException          if symbol == null
     * @throws SymbolAlreadyDefinedException if symbol is already in this
     * @modifies this
     * @effect this_post = this + symbol
     */
    void define(SlipSymbol symbol) throws NullPointerException, SymbolAlreadyDefinedException;

    /**
     * @return this[name] || parentScope.resolve(name)
     * @throws SymbolNotFoundException if there is no symbol named name in this
     */
    SlipSymbol resolve(String name) throws SymbolNotFoundException;


    /**
     * @return parentScope || null if this is the root scope
     */
    SlipScope getParentScope();

}
