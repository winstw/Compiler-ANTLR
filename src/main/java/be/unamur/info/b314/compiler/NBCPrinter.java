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


    public void printLoadConstant(NBCCodeTypes type, int value) {
        writer.printf("ldc %s %d", type.representation, value).println();
    }

    public void printLoadAdress(String var, int nvar) {
        writer.printf("mov %s %d", var, nvar).println();
    }


    public void printLoad(NBCCodeTypes type,  String  var) {
        writer.printf("%s %s", var, type.representation).println();
    }

    public void printStop() {
        writer.println("");
    }

    public void printComments(String comment) {
        writer.printf("; %s", comment).println();
    }


    public void printAdd(NBCCodeTypes type, String nvar, int value) {
        Preconditions.checkArgument(type.equals(NBCCodeTypes.Int));
        writer.printf("add %s %d  ",  nvar, value).println();
    }

    public void printSub(NBCCodeTypes type, String nvar, int value) {
        Preconditions.checkArgument(type.equals(NBCCodeTypes.Int) );
        writer.printf("sub %s %d ", nvar, value).println();
    }



    public void flush() {
        writer.flush();
    }

    public void close() {
        writer.flush();
        writer.close();
    }

}
