import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.nio.file.*;
import java.util.*;

public class GramaticaReal {
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Uso: java GramaticaReal <archivo_fuente.java>");
            return;
        }
        String source = Files.readString(Paths.get(args[0]));
        JavaLexer lexer = new JavaLexer(CharStreams.fromString(source));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JavaParser parser = new JavaParser(tokens);
        ParseTree tree = parser.compilationUnit();

        // Visitor personalizado
        MethodUsageVisitor visitor = new MethodUsageVisitor();
        visitor.visit(tree);

        // Generar el reporte
        System.out.println(visitor.getReport());
    }
}
