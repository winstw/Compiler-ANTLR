package be.unamur.info.b314.compiler;

import com.google.common.base.Preconditions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 *
 */
public class NBCPrinter {

    public enum NBCCodeTypes {
        Int("sdword"), Char("sbyte"), Bool("byte");

        private final String representation;

        private NBCCodeTypes(String r) {
            representation = r;
        }

    }

    private final PrintWriter writer;

    public NBCPrinter(File outFile) throws FileNotFoundException {
        writer = new PrintWriter(outFile);
    }

    public NBCPrinter(String fileName) throws FileNotFoundException {
        writer = new PrintWriter(fileName);
    }

    public NBCPrinter(OutputStream out) {
        writer = new PrintWriter(out);
    }

    public void printLoad(NBCCodeTypes type, int diff, int offset) {
        writer.printf("lod %s %d %d", type.representation, diff, offset).println();
    }

    public void printLoadConstant(NBCCodeTypes type, int value) {
        writer.printf("ldc %s %d", type.representation, value).println();
    }

    public void printLoadAdress(NBCCodeTypes type, int diff, int offset) {
        writer.printf("lda %s %d %d", type.representation, diff, offset).println();
    }

    public void printStop() {
        writer.println("stp");
    }

    public void printComments(String comment) {
        writer.printf("; %s", comment).println();
    }


    public void printAdd(NBCCodeTypes type) {
        Preconditions.checkArgument(type.equals(NBCCodeTypes.Int) || type.equals(NBCCodeTypes.Char));
        writer.printf("add %s", type.representation).println();
    }

    public void printSub(NBCCodeTypes type) {
        Preconditions.checkArgument(type.equals(NBCCodeTypes.Int) || type.equals(NBCCodeTypes.Char));
        writer.printf("sub %s", type.representation).println();
    }

    public void printMul(NBCCodeTypes type) {
        Preconditions.checkArgument(type.equals(NBCCodeTypes.Int) || type.equals(NBCCodeTypes.Char));
        writer.printf("mul %s", type.representation).println();
    }

    public void printDiv(NBCCodeTypes type) {
        Preconditions.checkArgument(type.equals(NBCCodeTypes.Int) || type.equals(NBCCodeTypes.Char));
        writer.printf("div %s", type.representation).println();
    }

    public void printMod(NBCCodeTypes type) {
        Preconditions.checkArgument(type.equals(NBCCodeTypes.Int) || type.equals(NBCCodeTypes.Char));
        writer.printf("mod %s", type.representation).println();
    }


    public void printOr() {
        writer.println("or b");
    }

    public void printAnd() {
        writer.println("and b");
    }

    public void printNot() {
        writer.println("not b");
    }

    public void printRead() {
        writer.println("read");
    }

    public void printPrin() {
        writer.println("prin");
    }

    public void printCheck(int p, int q) {
        writer.printf("chk %d %d", p, q).println();
    }
    
    public void printIndexedAdressComputation(int q) {
        writer.printf("ixa %d", q).println();
    }
    
    public void flush() {
        writer.flush();
    }

    public void close() {
        writer.flush();
        writer.close();
    }

}
