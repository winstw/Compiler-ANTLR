package be.unamur.info.b314.compiler.main.checking;

import be.unamur.info.b314.compiler.SlipParser;
import be.unamur.info.b314.compiler.main.nbc.Evaluator;
import be.unamur.info.b314.compiler.main.nbc.NbcCompiler;
import be.unamur.info.b314.compiler.symboltable.SlipSymbol;

import java.io.File;

public class SemanticChecker {
    boolean errorOccurred = false;
    /**
     * @modifies System.err
     * @effect print a message on System.err if semantic is wrong
     * @return true if there is no semantic error, else return false
     */
    public static boolean run(SlipParser.ProgramContext tree, String inputPath, File outputFile) {
        ErrorHandler errorHandler = new ErrorHandler();
        GlobalDefinitionPhase definitionPhase = new GlobalDefinitionPhase(errorHandler);
        definitionPhase.visit(tree);
        CheckPhaseVisitor checkPhase = new CheckPhaseVisitor(definitionPhase.getScopes(), errorHandler);
        checkPhase.visit(tree);
        if (!errorHandler.isErrorOccurred()){
            NbcCompiler compiler = new NbcCompiler(outputFile);
            Evaluator eval = new Evaluator(checkPhase.getScopes(), errorHandler, inputPath, compiler);
            eval.visit(tree);
            if (!errorHandler.isErrorOccurred() && outputFile.isFile()){
                compiler.compile();
            }
            compiler.toString();
        }
        return !errorHandler.isErrorOccurred();
    }

    public static SlipSymbol.Type getType(int typeToken) {
        switch (typeToken) {
            case SlipParser.VOIDTYPE: return SlipSymbol.Type.VOID;
            case SlipParser.INTEGERTYPE: return SlipSymbol.Type.INTEGER;
            case SlipParser.CHARTYPE: return SlipSymbol.Type.CHARACTER;
            case SlipParser.BOOLEANTYPE: return SlipSymbol.Type.BOOLEAN;
            default: return SlipSymbol.Type.STRUCT;
        }
    }




}
