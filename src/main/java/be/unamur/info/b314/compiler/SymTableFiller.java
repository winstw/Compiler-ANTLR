package be.unamur.info.b314.compiler;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Fills a symbol table using ANTLR listener .
 * @author James Ortiz - james.ortizvega@unamur.be
 */
public class SymTableFiller extends PlayPlusBaseListener {

    private final Map<String, Integer> symTable;

    private int offset = 0; // The offset of the variable in the PMachine stack

    public SymTableFiller() {
        this.symTable = Maps.newHashMap();
    }

    @Override
    public void enterAffectInstr(PlayPlusParser.AffectInstrContext ctx) {
        addVariable(ctx.ID().getText());
    }

    private void addVariable(String var) {
        if (!symTable.containsKey(var)) {
            symTable.put(var, offset);
            offset++;
        }
    }

    public Map<String, Integer> getSymTable() {
        return symTable;
    }
    
}
