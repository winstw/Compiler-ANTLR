package be.unamur.info.b314.compiler.main.checking;

import be.unamur.info.b314.compiler.SlipLexer;
import be.unamur.info.b314.compiler.SlipParser;
import be.unamur.info.b314.compiler.SlipParserBaseVisitor;

import be.unamur.info.b314.compiler.exception.SymbolAlreadyDefinedException;
import be.unamur.info.b314.compiler.exception.SymbolNotFoundException;
import be.unamur.info.b314.compiler.main.SlipErrorStrategy;
import be.unamur.info.b314.compiler.symboltable.*;
import be.unamur.info.b314.compiler.symboltable.SlipSymbol.Types;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.TerminalNode;
import be.unamur.info.b314.compiler.symboltable.SlipSymbol;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

import static be.unamur.info.b314.compiler.main.checking.SemanticChecker.printError;

public class SecondPassVisitor extends SlipParserBaseVisitor<Types> {

    public static void main(String[] args) throws IOException {
        File input = new File(System.getProperty("user.dir") + "/src/test/resources/DefPhaseTest.slip");
        SlipLexer lexer = new SlipLexer(new ANTLRInputStream(new FileInputStream(input)));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SlipParser parser = new SlipParser(tokens);
        parser.setErrorHandler(new SlipErrorStrategy());

        SlipParser.ProgramContext tree = parser.program();
        GlobalDefinitionPhase visitor = new GlobalDefinitionPhase();
        visitor.visit(tree);
        SecondPassVisitor second = new SecondPassVisitor(visitor.getScopes());
        second.visitProgram(tree);
    }

    private ParseTreeProperty<SlipScope> scopes;
    private SlipScope currentScope;
    private boolean errorOccurred = false;

    public SecondPassVisitor(ParseTreeProperty<SlipScope> scopes){
        this.scopes = scopes;
    }

    public boolean hasErrorOccurred() {
        return errorOccurred;
    }

    @Override
    public Types visitProgram(SlipParser.ProgramContext ctx) {
        System.out.println("=== START  SECOND PHASE ===");

        if (ctx.prog() != null) {
            visit(ctx.prog());
        }

        System.out.println("=== END SECOND PHASE ===");

        return Types.VOID;
    }

    @Override
    public Types visitProg(SlipParser.ProgContext ctx){
        SlipScope globalScope = scopes.get(ctx);
        this.currentScope = globalScope;
        System.out.println("SCOPE : " + currentScope.getName());

        visitChildren(ctx);

        System.out.println(this.currentScope);
        this.currentScope = null;
        return Types.VOID;
    }

    @Override
    public Types visitMainDecl(SlipParser.MainDeclContext ctx) {
        this.currentScope = scopes.get(ctx);

        visitChildren(ctx);

        this.currentScope = this.currentScope.getParentScope();

        return Types.VOID;
    }

    @Override
    public Types visitFuncDecl(SlipParser.FuncDeclContext ctx){
        // function inspection
        SlipScope localScope = scopes.get(ctx);
        this.currentScope = localScope;
        System.out.println("SCOPE : " + currentScope.getName());

        if (ctx.argList() != null) {
            visit(ctx.argList());
        }

        for (SlipParser.InstBlockContext inst: ctx.instBlock()){
            visit(inst);
        }
        System.out.println(currentScope);
        this.currentScope = localScope.getParentScope();
        System.out.println("SCOPE : " + currentScope.getName());
        return Types.VOID;
    }

    @Override
    public Types visitArgList(SlipParser.ArgListContext ctx) {

        for(SlipParser.VarDefContext varDef : ctx.varDef()) {
            visit(varDef);
        }

        return Types.VOID;
    }

    @Override
    public Types visitInstBlock(SlipParser.InstBlockContext ctx){
        for (ParseTree child: ctx.children) {
            visit(child);
        }
        return Types.VOID;
    }

    @Override
    public Types visitFuncExpr(SlipParser.FuncExprContext ctx) {
        try {
            SlipMethodSymbol scopedFunc = (SlipMethodSymbol) this.currentScope.resolve(ctx.ID().getText());
            System.out.println("FUNC CALL : " + scopedFunc + " Type : " + scopedFunc.getType());

            if (ctx.exprD().size() != scopedFunc.getNumberOfParameters()) {
                errorOccurred = true;
                printError(ctx.start, String.format("function %s expects %d argument(s)", scopedFunc.getName(), scopedFunc.getNumberOfParameters()));
                return scopedFunc.getType();
            }

            Iterator<Types> declaredParams = scopedFunc.getParameterTypes();
            for (SlipParser.ExprDContext param : ctx.exprD()){
                Types effectiveParam = visit(param);
                if (declaredParams.hasNext()) {
                    Types declaredParam = declaredParams.next();
                    System.out.println("EFFECTIVE : " + effectiveParam + " DECLARED : " + declaredParam);
                    if (effectiveParam != declaredParam){
                        errorOccurred =  true;
                        printError(param.start, String.format("parameter %s should be of type %s instead of %s", param.getText(), declaredParam, effectiveParam));
                    }
                } else {
                    errorOccurred =  true;
                    System.out.println("too many arguments");
                }
            }

            return scopedFunc.getType();
        } catch (SymbolNotFoundException e) {
            errorOccurred = true;
            printError(ctx.start, String.format("function %s is never defined", ctx.ID().getText()));
        }
        return Types.VOID;
    }

    @Override
    public Types visitDeclaration(SlipParser.DeclarationContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Types visitVarDecl(SlipParser.VarDeclContext ctx){
        Types definedVarType = visit(ctx.scalar());
        System.out.println("VAR DECLARATION : " + definedVarType);

        if (this.currentScope.getName() != "global") {
            for (TerminalNode id : ctx.ID()) {
                try {
                    this.currentScope.define(new SlipVariableSymbol(id.getText(), definedVarType, true));
                } catch (SymbolAlreadyDefinedException e) {
                    errorOccurred = true;
                    printError(id.getSymbol(), String.format("variable symbol \"%s\" already exists in %s scope", id.getText(), currentScope.getName()));
                }
            }
        }

        // type check initialisation
        if (ctx.exprD() != null) {
            Types initVarType = visit(ctx.exprD());
            System.out.println("in visitVarDECL, init var : " + initVarType);
            if (initVarType != definedVarType) {
                errorOccurred = true;
                printError(ctx.exprD().start, String.format("cannot assign expression of type %s to variable of type %s", initVarType, definedVarType));
            }
        }

        return definedVarType;
    }

    @Override
    public Types visitArrayDecl(SlipParser.ArrayDeclContext ctx) {

        Types definedVarType = visit(ctx.scalar());

        if (currentScope.getName() != "global") {
            for (TerminalNode node : ctx.ID()) {
                String name = node.getText();
                SlipSymbol symbol = new SlipArraySymbol(name, definedVarType, true);

                try {
                    currentScope.define(symbol);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (SymbolAlreadyDefinedException e) {
                    errorOccurred = true;
                    printError(node.getSymbol(), String.format("array symbol \"%s\" already exists in %s scope", name, currentScope.getName()));

                }

            }
        }

        // type check initialisation
        if (ctx.initArrays() != null) {
            Types initVarType = visit(ctx.initArrays());

            System.out.println("in visitVarDECL, init var : " + initVarType);


//            System.out.println(ctx.number(0).getText() +  ctx.initArrays().initVar().size());

            int expectedFirstDimLength = Integer.parseInt(ctx.number(0).getText());
            int actualFirstDimLength = ctx.initArrays().initVar().size();
            if (expectedFirstDimLength != actualFirstDimLength) {
                errorOccurred = true;
                printError(ctx.start, "First dimension size does not match between declaration and initialization");
            }

            if (ctx.number().size() > 1) {
                int expectedSecondDimLength = Integer.parseInt(ctx.number(1).getText());
                for (SlipParser.InitVarContext init : ctx.initArrays().initVar()) {
                    if (init.initArrays().initVar().size() != expectedSecondDimLength) {
                        errorOccurred = true;
                        printError(init.start, "Second dimension size does not match between declaration and initialization");
                    }
                }
            }
            if (initVarType != definedVarType) {
                errorOccurred = true;
                printError(ctx.initArrays().start, String.format("cannot assign expression of type %s to variable of type %s", initVarType, definedVarType));
            }
        }

        return definedVarType;
    }

    @Override
    public Types visitInitArrays(SlipParser.InitArraysContext ctx) {

        // TODO
        // Comment faire avec les tableaux de tableaux??
        Types type = null;
        for (SlipParser.InitVarContext var : ctx.initVar()) {
//            System.out.println(visit(var));
            if (type == null) {
                type = visit(var);
            } else if (type != visit(var)) {
                errorOccurred = true;
                printError(var.start, "type Array INit error IMPROVE THIS ERROR");
            }
        }

        return type;
    }

    @Override
    public Types visitStructDecl(SlipParser.StructDeclContext ctx) {

        if (currentScope.getName() != "global") {
            for (TerminalNode node : ctx.ID()) {
                String name = node.getText();
                SlipStructureSymbol symbol = new SlipStructureSymbol(name, currentScope, true);

                try {
                    currentScope.define(symbol);
                    currentScope = symbol;

                    for (SlipParser.DeclarationContext var : ctx.declaration()) {
                        visit(var);
                    }

                    currentScope = currentScope.getParentScope();

                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (SymbolAlreadyDefinedException e) {
                    errorOccurred = true;
                    printError(node.getSymbol(), String.format("structure symbol \"%s\" already exists in %s scope", name, currentScope.getName()));
                }

            }
        }

        return Types.STRUCT;
    }

    @Override
    public Types visitVarDef(SlipParser.VarDefContext ctx){
        Types definedVarType = visit(ctx.scalar());
        // add new var definition to the local scope (already done in first pass for global scope)
        if (this.currentScope.getName() != "global") {
            for (TerminalNode id : ctx.ID()) {
                try {
                    this.currentScope.define(new SlipVariableSymbol(id.getText(), definedVarType, true));
                } catch (SymbolAlreadyDefinedException e) {
                    errorOccurred = true;
                    printError(id.getSymbol(), String.format("param symbol \"%s\" already exists in %s scope", id.getText(), currentScope.getName()));
                }
            }
        }
        return definedVarType;
    }

    @Override
    public Types visitInitVar(SlipParser.InitVarContext ctx){
        if (ctx.exprD() != null) {
            return visit(ctx.exprD());
        }
        else return visit(ctx.initArrays());
    }

    @Override
    public Types visitConstDecl(SlipParser.ConstDeclContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Types visitConstVar(SlipParser.ConstVarContext ctx) {

        Types definedVarType = visit(ctx.scalar());
        System.out.println("VAR DECLARATION : " + definedVarType);

        if (this.currentScope.getName() != "global") {
            try {
                this.currentScope.define(new SlipVariableSymbol(ctx.ID().getText(), definedVarType, false));
            } catch (SymbolAlreadyDefinedException e) {
                errorOccurred = true;
                printError(ctx.ID().getSymbol(), String.format("constant symbol \"%s\" already exists in %s scope", ctx.ID().getText(), currentScope.getName()));
            }
        }

        // type check initialisation
        if (ctx.exprD() != null) {
            Types initVarType = visit(ctx.exprD());
            System.out.println("in visitVarDECL, init var : " + initVarType);
            if (initVarType != definedVarType) {
                errorOccurred = true;
                printError(ctx.exprD().start, String.format("cannot assign expression of type %s to variable of type %s", initVarType, definedVarType));
            }
        }

        return definedVarType;

    }

    @Override
    public Types visitConstArray(SlipParser.ConstArrayContext ctx) {

        Types definedVarType = visit(ctx.scalar());

        if (currentScope.getName() != "global") {
            String name = ctx.ID().getText();
            SlipSymbol symbol = new SlipArraySymbol(name, definedVarType, false);

            try {
                currentScope.define(symbol);
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (SymbolAlreadyDefinedException e) {
                errorOccurred = true;
                printError(ctx.start, String.format("constant array symbol \"%s\" already exists in %s scope", name, currentScope.getName()));

            }

        }

        // type check initialisation
        if (ctx.initArrays() != null) {
            Types initVarType = visit(ctx.initArrays());
            System.out.println("in visitVarDECL, init var : " + initVarType);
            if (initVarType != definedVarType) {
                errorOccurred = true;
                printError(ctx.initArrays().start, String.format("cannot assign expression of type %s to variable of type %s", initVarType, definedVarType));
            }
        }

        return definedVarType;

    }

    @Override
    public Types visitConstStruct(SlipParser.ConstStructContext ctx) {

        if (currentScope.getName() != "global") {
            String name = ctx.ID().getText();
            SlipStructureSymbol symbol = new SlipStructureSymbol(name, currentScope, false);

            try {
                currentScope.define(symbol);
                currentScope = symbol;

                for (SlipParser.DeclarationContext var : ctx.declaration()) {
                    visit(var);
                }

                currentScope = currentScope.getParentScope();

            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (SymbolAlreadyDefinedException e) {
                errorOccurred = true;
                printError(ctx.start, String.format("constant structure symbol \"%s\" already exists in %s scope", name, currentScope.getName()));
            }

        }

        return Types.STRUCT;
    }

    @Override
    public Types visitScalar(SlipParser.ScalarContext ctx){
        if (ctx.BOOLEANTYPE() != null){
            return Types.BOOLEAN;
        }
        if (ctx.CHARTYPE() != null){
            return Types.CHARACTER;
        }
        else {
            return Types.INTEGER;
        }
    }

    @Override
    public Types visitExprGExpr(SlipParser.ExprGExprContext ctx){
        return visit(ctx.exprG());
    }

    @Override
    public Types visitLeftExprID(SlipParser.LeftExprIDContext ctx){
        String idName = ctx.ID().getText();

        try {
            SlipSymbol declaredId = currentScope.resolve(idName);
            if (declaredId instanceof SlipArraySymbol) {
                errorOccurred = true;
                printError(ctx.ID().getSymbol(), String.format("assignation to array \"%s\" requires an index between brackets", idName));
            }
            return declaredId.getType();
        } catch (SymbolNotFoundException e){
            errorOccurred = true;
            printError(ctx.ID().getSymbol(), String.format("use of undeclared identifier %s", idName));
        }
        return Types.VOID;
    }

    @Override
    public Types visitLeftExprArray(SlipParser.LeftExprArrayContext ctx){
        String idName = ctx.ID().getText();
        try {
            SlipSymbol declaredId = currentScope.resolve(idName);
            return declaredId.getType();
        } catch (SymbolNotFoundException e){
            errorOccurred = true;
            printError(ctx.ID().getSymbol(), String.format("use of undeclared identifier %s", idName));
        }

        return Types.VOID;
    }

    @Override
    public Types visitLeftExprRecord(SlipParser.LeftExprRecordContext ctx){

        StructExprGVisitor visitor = new StructExprGVisitor(currentScope);
        Types type = visitor.visit(ctx);

        if (visitor.hasErrorOccurred()) {
            errorOccurred = true;
        }

        return type;
    }

    @Override
    public Types visitAssignInstr(SlipParser.AssignInstrContext ctx) {
        Types exprGType = visit(ctx.exprG());
        Types exprDType = visit(ctx.exprD());

        if (exprGType != exprDType) {
            errorOccurred = true;
            printError(ctx.start, String.format("cannot assign expression of type %s to variable of type %s", exprDType, exprGType));
        }

        return Types.VOID;
    }

    @Override
    public Types visitParens(SlipParser.ParensContext ctx){
        return visit(ctx.exprD());
    }


    @Override
    public Types visitString(SlipParser.StringContext ctx){
        return Types.STRING;
    }

    @Override
    public Types visitChar(SlipParser.CharContext ctx) {
        return Types.CHARACTER;
    }

    @Override
    public Types visitIntExpr(SlipParser.IntExprContext ctx){
        return Types.INTEGER;
    }
    @Override
    public Types visitUnaryMinusExpr(SlipParser.UnaryMinusExprContext ctx){
        Types type = visit(ctx.exprD());

        if (type != Types.INTEGER) {
            errorOccurred = true;
            printError(ctx.exprD().start, "can only negate integer expression");
        }

        return Types.INTEGER;
    }
    @Override
    public Types visitTimesDivideExpr(SlipParser.TimesDivideExprContext ctx){

        for (SlipParser.ExprDContext expr: ctx.exprD()) {
            Types type = visit(expr);

            if (type != Types.INTEGER) {
                errorOccurred = true;
                printError(expr.start, "can only use '*', '/' and '%' operators on expressions of type integer");
            }

        }

        return Types.INTEGER;
    }
    @Override
    public Types visitPlusMinusExpr(SlipParser.PlusMinusExprContext ctx){

        for (SlipParser.ExprDContext expr: ctx.exprD()) {
            Types type = visit(expr);

            if (type != Types.INTEGER) {
                errorOccurred = true;
                printError(expr.start, "can only use '+' and '-' operators on expressions of type integer");
            }

        }

        return Types.INTEGER;
    }

    @Override
    public Types visitTrueExpr(SlipParser.TrueExprContext ctx){
        return Types.BOOLEAN;
    }

    @Override
    public Types visitAndOrExpr(SlipParser.AndOrExprContext ctx){

        for (SlipParser.ExprDContext expr : ctx.exprD()) {
            Types type = visit(expr);

            if (type != Types.BOOLEAN) {
                errorOccurred = true;
                printError(expr.start, "can only compare expressions of type boolean");
            }

        }

        return Types.BOOLEAN;
    }

    @Override
    public Types visitFalseExpr(SlipParser.FalseExprContext ctx){
        return Types.BOOLEAN;
    }

    @Override
    public Types visitComparExpr(SlipParser.ComparExprContext ctx){

        Types leftExprType = visit(ctx.exprD(0));
        Types rightExprType = visit(ctx.exprD(1));

        if (leftExprType != rightExprType) {
            errorOccurred = true;
            printError(ctx.start, "can only compare expressions of the same type");
        }

        return Types.BOOLEAN;
    }

    @Override
    public Types visitComparIntExpr(SlipParser.ComparIntExprContext ctx) {

        for (SlipParser.ExprDContext expr : ctx.exprD()) {
            Types type = visit(expr);

            if (type != Types.INTEGER) {
                errorOccurred = true;
                printError(expr.start, "can only compare integer expressions");
            }

        }

        return Types.BOOLEAN;
    }

    @Override
    public Types visitNotExpr(SlipParser.NotExprContext ctx){
        if (visit(ctx.exprD()) != Types.BOOLEAN) {
            errorOccurred = true;
            printError(ctx.exprD().start, String.format("%s must be of boolean type", ctx.exprD().getText()));
        }
        return Types.BOOLEAN;
    }

    @Override
    public Types visitIfThenInstr(SlipParser.IfThenInstrContext ctx) {

        Types type = visit(ctx.exprD());

        if (type != Types.BOOLEAN) {
            errorOccurred = true;
            printError(ctx.exprD().start, "expression must be of type boolean");
        }

        for (SlipParser.InstructionContext inst : ctx.instruction()) {
            visit(inst);
        }

        return Types.VOID;
    }

    @Override
    public Types visitIfThenElseInstr(SlipParser.IfThenElseInstrContext ctx) {
        
        Types type = visit(ctx.exprD());

        if (type != Types.BOOLEAN) {
            errorOccurred = true;
            printError(ctx.exprD().start, "expression must be of type boolean");
        }

        for (SlipParser.InstructionContext inst : ctx.instruction()) {
            visit(inst);
        }

        return Types.VOID;
    }

    @Override
    public Types visitWhileInstr(SlipParser.WhileInstrContext ctx) {

        Types type = visit(ctx.exprD());

        if (type != Types.BOOLEAN) {
            errorOccurred = true;
            printError(ctx.exprD().start, "expression must be of type boolean");
        }

        for (SlipParser.InstructionContext inst : ctx.instruction()) {
            visit(inst);
        }

        return Types.VOID;
    }

    @Override
    public Types visitUntilInstr(SlipParser.UntilInstrContext ctx) {

        Types type = visit(ctx.exprD());

        if (type != Types.BOOLEAN) {
            errorOccurred = true;
            printError(ctx.exprD().start, "expression must be of type boolean");
        }

        for (SlipParser.InstructionContext inst : ctx.instruction()) {
            visit(inst);
        }

        return Types.VOID;
    }

    @Override
    public Types visitForInstr(SlipParser.ForInstrContext ctx) {

        Types initValueType = visit(ctx.exprD(0));
        Types conditionValueType = visit(ctx.exprD(1));

        if (initValueType != Types.INTEGER) {
            errorOccurred = true;
            printError(ctx.exprD(0).start, "expression must be of type integer");
        }

        if (conditionValueType != Types.BOOLEAN) {
            errorOccurred = true;
            printError(ctx.exprD(1).start, "expression must be of type boolean");
        }

        for (SlipParser.InstructionContext inst : ctx.instruction()) {
            visit(inst);
        }

        return Types.VOID;
    }

    @Override
    public Types visitActionType(SlipParser.ActionTypeContext ctx) {

        if (ctx.exprD() != null) {
            Types type = visit(ctx.exprD());

            if (type != Types.INTEGER) {
                errorOccurred = true;
                printError(ctx.exprD().start, "expression must be of type integer");
            }
        }

        return Types.VOID;
    }
}
