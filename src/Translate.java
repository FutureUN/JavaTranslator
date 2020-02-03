import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.util.ArrayList;

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

        ArrayList<String> comments = new ArrayList<>();
        for ( Token tok : tokens.getTokens()) {
        //    if (tok.getChannel() == 1 && tok.getText().indexOf('/') >= 0) traducinador.AddComment(tok.getText());
           // System.out.println(tok.getText());
        }

        // All comments are stored in 'comments' array list

      //  for (String cmt : comments) System.out.println(cmt);
        traducinador.visit(tree);
    }
}