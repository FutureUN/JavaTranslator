import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
public class Translate {
    public static void main(String[]args) throws Exception{
        JavaLexer lexer ;
        if (args.length>0)
            lexer = new JavaLexer(CharStreams.fromFileName(args[0]));
        else
            lexer = new JavaLexer(CharStreams.fromStream((System.in)));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JavaParser parser = new JavaParser(tokens);
        ParseTree tree = parser.compilationUnit();
        NUBtrad<String> traducinador = new NUBtrad<>();
        traducinador.visit(tree);
    }
}