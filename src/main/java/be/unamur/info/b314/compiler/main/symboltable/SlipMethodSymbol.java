package be.unamur.info.b314.compiler.main.symboltable;

import be.unamur.info.b314.compiler.SlipParser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SlipMethodSymbol extends SlipScopedSymbol {

    private ArrayList<SlipVariableSymbol> parameters;
    private List<SlipParser.InstBlockContext> body;

    public SlipMethodSymbol(String name, Type type, SlipScope parentScope) {
        super(name, type, parentScope, false);
        parameters = new ArrayList<SlipVariableSymbol>();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("function ");
        sb.append(getName());

        for (String key : symbols.keySet()) {
            sb.append("\n\t");
            sb.append(key);
            sb.append(": ");
            sb.append(symbols.get(key).getType());
        }

        sb.append("\n");

        return sb.toString();
    }

    /**
     * @modifies this
     * @effect add symbol to parameters
     */
    public void addParameter(SlipVariableSymbol symbol) {
        parameters.add(symbol);
    }

    public void setBody(List<SlipParser.InstBlockContext> body) {
        this.body = body;
    }

    public List<SlipParser.InstBlockContext> getBody() {
        return this.body;
    }

    public int getNumberOfParameters() {
        return parameters.size();
    }

    public Iterator<SlipVariableSymbol> getParameters() {
        return this.parameters.iterator();
    }

    @Override
    public SlipMethodSymbol clone() {
        SlipMethodSymbol methodCopy = new SlipMethodSymbol(this.getName(), this.getType(), this.getParentScope());

        methodCopy.body = this.body;

        for (String key : symbols.keySet()) {
            methodCopy.symbols.put(key, this.symbols.get(key).clone());
        }

        for (SlipVariableSymbol svs : parameters) {
            methodCopy.parameters.add(svs.clone());
        }

        return methodCopy;
    }
}
