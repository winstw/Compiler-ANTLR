package be.unamur.info.b314.compiler;

import be.unamur.info.b314.compiler.PlayPlusBaseListener;
import be.unamur.info.b314.compiler.PlayPlusParser;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Fills a symbol table using ANTLR listener .
 * @author James Ortiz - james.ortizvega@unamur.be
 */
public class SymTableFiller extends PlayPlusBaseListener {

    private final Map<String, Integer> symTable;

    private int nId = 0; //

    public SymTableFiller() {
        this.symTable = Maps.newHashMap();
    }

    @Override
    public void enterAffectInstr(PlayPlusParser.AffectInstrContext ctx) {
        addVariable(ctx.ID().getText());
    }

    private void addVariable(String var) {
        if (!symTable.containsKey(var)) {
            symTable.put(var, nId);
            nId++;
        }
    }

    public Map<String, Integer> getSymTable() {
        return symTable;
    }
    
}
