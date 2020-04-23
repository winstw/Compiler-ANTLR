package be.unamur.info.b314.compiler.symboltable;

import be.unamur.info.b314.compiler.SlipParser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SlipMethodSymbol extends SlipScopedSymbol {

    private ArrayList<SlipBaseSymbol> parameters;
    private List<SlipParser.InstBlockContext> body;
    public SlipMethodSymbol(String name, Type type, SlipScope parentScope) {
        super(name, type, parentScope, false);
        parameters = new ArrayList<SlipBaseSymbol>();
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
     * @effect add type to parameterTypes
     */
    public void addParameter(SlipBaseSymbol symbol) {
        parameters.add(symbol);
    }

    public void setBody(List<SlipParser.InstBlockContext> body){
        this.body = body;
    }
    public List<SlipParser.InstBlockContext> getBody(){
        return this.body;
    }

    public int getNumberOfParameters() {
        return parameters.size();
    }

    public Iterator<SlipBaseSymbol> getParameters() {
        return this.parameters.iterator();
    }

}
