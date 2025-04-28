import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.util.*;

public class AnalizadorIDE {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Analizador de Métodos Eliminables");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 700);

        JTextArea codeArea = new JTextArea();
        codeArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JButton analyzeButton = new JButton("Analizar");
        JTextArea reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        reportArea.setBackground(new Color(245,245,245));

        analyzeButton.addActionListener(e -> {
            String source = codeArea.getText();
            try {
                JavaLexer lexer = new JavaLexer(CharStreams.fromString(source));
                CommonTokenStream tokens = new CommonTokenStream(lexer);
                JavaParser parser = new JavaParser(tokens);
                ParseTree tree = parser.compilationUnit();

                MethodUsageVisitor visitor = new MethodUsageVisitor();
                visitor.visit(tree);

                reportArea.setText(visitor.getReport());
            } catch (Exception ex) {
                reportArea.setText("Error de análisis:\n" + ex.getMessage());
            }
        });

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
            new JScrollPane(codeArea),
            new JScrollPane(reportArea)
        );
        splitPane.setDividerLocation(350);
        splitPane.setResizeWeight(0.6);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(splitPane, BorderLayout.CENTER);
        topPanel.add(analyzeButton, BorderLayout.SOUTH);

        frame.setContentPane(topPanel);
        frame.setVisible(true);
    }
}

// Visitor para recolectar definiciones y llamadas
class MethodUsageVisitor extends JavaParserBaseVisitor<Void> {
    Set<String> definedMethods = new HashSet<>();
    Map<String, Integer> methodCalls = new HashMap<>();

    @Override
    public Void visitMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
        String methodName = ctx.identifier().getText();
        definedMethods.add(methodName);
        return super.visitMethodDeclaration(ctx);
    }

    @Override
    public Void visitMethodCall(JavaParser.MethodCallContext ctx) {
        String methodName = null;
        if (ctx.identifier() != null) {
            methodName = ctx.identifier().getText();
        }
        if (methodName != null) {
            methodCalls.put(methodName, methodCalls.getOrDefault(methodName, 0) + 1);
        }
        return super.visitMethodCall(ctx);
    }

    public String getReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("--- Métodos que pueden ser eliminados ---\n");
        boolean alguno = false;
        for (String method : definedMethods) {
            int calls = methodCalls.getOrDefault(method, 0);
            if (calls <= 1) {
                sb.append(String.format("- %s (llamado %d vez%s)\n", method, calls, calls == 1 ? "" : "es"));
                alguno = true;
            }
        }
        if (!alguno) {
            sb.append("No se encontraron métodos eliminables.\n");
        }
        return sb.toString();
    }
}
