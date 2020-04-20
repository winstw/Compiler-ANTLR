package be.unamur.info.b314.compiler.main.nbc;

import be.unamur.info.b314.compiler.SlipBaseVisitor;
import be.unamur.info.b314.compiler.SlipLexer;
import be.unamur.info.b314.compiler.SlipParser;
import be.unamur.info.b314.compiler.main.checking.CheckPhaseVisitor;
import be.unamur.info.b314.compiler.main.checking.ErrorHandler;
import be.unamur.info.b314.compiler.main.checking.GlobalDefinitionPhase;
import be.unamur.info.b314.compiler.symboltable.SlipScope;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import java.io.File;
import java.io.FileInputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NbcVisitor extends SlipBaseVisitor<String> {
    private ParseTreeProperty<SlipScope> scopes;
    static public void main(String[] args) throws IOException {
        File input = new File(System.getProperty("user.dir") + "/src/test/resources/DefPhaseTest.slip");
        SlipLexer lexer = new SlipLexer(new ANTLRInputStream(new FileInputStream(input)));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SlipParser parser = new SlipParser(tokens);
        SlipParser.ProgramContext tree = parser.program();
        ErrorHandler errorHandler = new ErrorHandler();
        GlobalDefinitionPhase visitor = new GlobalDefinitionPhase(errorHandler);
        visitor.visit(tree);
        CheckPhaseVisitor second = new CheckPhaseVisitor(visitor.getScopes(), errorHandler);
        second.visitProgram(tree);
        NbcVisitor nbcVisitor = new NbcVisitor(second.getScopes());
        System.out.println(nbcVisitor.visitProgram(tree));
    }

    public NbcVisitor(ParseTreeProperty<SlipScope> scopes){
        this.scopes = scopes;
    }
    @Override
    public String visitProgram(SlipParser.ProgramContext ctx) {
        System.out.println("=== START CODE GENERATION ===");
        String code = "#include \"NXTDefs.h\"\n";
        if (ctx.prog() != null) {
            code += visit(ctx.prog());
        }
        System.out.println("=== END CODE GENERATION ===");
        return code;
    }

        @Override
    public String visitProg(SlipParser.ProgContext ctx){
        String prog_code = "";
        for (ParseTree child : ctx.children) {
            prog_code += (visit(child));
        }
        return prog_code;
    }

    @Override
    public String visitMainDecl(SlipParser.MainDeclContext ctx) {
        String main_code = "thread main\n";

        visitChildren(ctx);
        main_code += "endt\n";
        return main_code;
    }

    @Override
    public String visitFuncDecl(SlipParser.FuncDeclContext ctx){
        String fun_code = "#define " + ctx.ID().getText() + "(";
        if (ctx.argList() != null) {
            fun_code += visit(ctx.argList());
        }

        fun_code += ")";

        for (SlipParser.InstBlockContext inst: ctx.instBlock()){
            fun_code += visit(inst) + "\\\n";
        }
        return fun_code;
    }

    @Override
    public String visitArgList(SlipParser.ArgListContext ctx) {
        List<String> args = new ArrayList<>();
        for(SlipParser.VarDefContext varDef : ctx.varDef()) {
            args.add(varDef.ID().get(0).getText());
        }
        return String.join(", ", args);
    }

    @Override
    public String visitInstBlock(SlipParser.InstBlockContext ctx){
        List<String> insts = new ArrayList<>();
        for (ParseTree inst: ctx.instruction()) {
            insts.add(visit(inst));
        }
        return String.join("\\\n", insts);
    }

    public String visitDeclaration(SlipParser.DeclarationContext ctx) {
        return visitChildren(ctx);
    }

    public String visitVarDecl(SlipParser.VarDeclContext ctx) {
//        for (ParseTree var: ctx.ID()) {
//            compiler.addVar()
//        }
        return null;
    }


}
