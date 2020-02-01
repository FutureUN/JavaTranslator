import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.io.*;
import java.util.*;

public class NUBtrad<T> extends JavaParserBaseVisitor {
    @Override
    public T visitTypeDeclaration(JavaParser.TypeDeclarationContext ctx) {
        if (ctx.classDeclaration() != null){
            System.out.println(visitClassDeclaration(ctx.classDeclaration()));
            return (T) null;
        }
        return (T) null;
        //TODO enumDeclaration interfaceDeclaration annotationTypeDeclaration
    }
    @Override
    public T visitClassDeclaration(JavaParser.ClassDeclarationContext ctx){
        return (T)("class " + ctx.IDENTIFIER().toString()+ "{\n" + visitClassBody(ctx.classBody()) +  "\n}" );
        // TODO typeParameters , typeType, TypeList
    }
    @Override
    public  T visitClassBody(JavaParser.ClassBodyContext ctx){
        String traduc = "";
        for ( int i = 0 ; i < ctx.classBodyDeclaration().size(); i ++){
            traduc += (String)(visitClassBodyDeclaration(ctx.classBodyDeclaration(i))) + "\n";
        }
        return (T)traduc;
    }
    @Override
    public T visitClassBodyDeclaration(JavaParser.ClassBodyDeclarationContext ctx) {
        JavaParser.MemberDeclarationContext tmp = ctx.memberDeclaration();
        String modifier = "";  // Solo usaremos STATIC

        for ( int i =0; i < ctx.modifier().size() ; i ++){
            if(ctx.modifier(i).classOrInterfaceModifier().STATIC() != null){
                modifier = "static ";
            }
        }
        String traduc  =  modifier +  (String)(visitMemberDeclaration(ctx.memberDeclaration())) ;
        return (T) traduc;
        //TODO STATIC Y ;
    }


    @Override
    public T visitMemberDeclaration(JavaParser.MemberDeclarationContext ctx){
        JavaParser.MethodDeclarationContext tmp = ctx.methodDeclaration();
        if (ctx.methodDeclaration() != null){
            return (T) visitMethodDeclaration(ctx.methodDeclaration());
        }
        // TODO genericMethoddec, fieldDecl, contrucDecl, geneConsDecl, interDecla, annoTypeDecla, classDecla, enumDecla
        return (T) null;
    }
    @Override
    public T visitMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
        //TODO TypeTypeOrVoid , [], thows

        return (T)( ctx.IDENTIFIER().toString() + visitFormalParameters(ctx.formalParameters()) + visitMethodBody(ctx.methodBody()));
    }
    @Override
    public T visitFormalParameters(JavaParser.FormalParametersContext ctx) {
        if (ctx.formalParameterList() == null)
            return (T) ("()");
        else
            return (T)("(" + visitFormalParameterList(ctx.formalParameterList())+")");
    }
    @Override
    public T visitFormalParameterList(JavaParser.FormalParameterListContext ctx){
        String traduc = "";
        for(int i = 0 ; i < ctx.formalParameter().size() ; i++) {
            traduc += (String) visitFormalParameter(ctx.formalParameter(i)) +  ",";
        }
        if (ctx.lastFormalParameter() != null){
            traduc = (String) visitLastFormalParameter(ctx.lastFormalParameter());
        }else{
            return (T)(traduc.substring(0,traduc.length()-1));
        }
        return (T) (traduc);
        //TODO lastFormalParameter
    }
    @Override
    public T visitFormalParameter(JavaParser.FormalParameterContext ctx){
        return (T)(visitVariableDeclaratorId(ctx.variableDeclaratorId()));
        //TODO variable modifier
    }
    @Override
    public T visitMethodBody (JavaParser.MethodBodyContext ctx){
        return (T) (visitBlock(ctx.block()));
        //TODO ;
    }
    @Override
    public T visitBlock(JavaParser.BlockContext ctx){
        String trad = "";
        for (int i = 0 ; i < ctx.blockStatement().size(); i++)
            trad += (String) visitBlockStatement((ctx.blockStatement(i)));
        return (T) ("{ \n" + trad + "} \n");
    }
    @Override
    public T visitBlockStatement( JavaParser.BlockStatementContext ctx){
        if (ctx.localVariableDeclaration() != null){
            return (T) (visitLocalVariableDeclaration(ctx.localVariableDeclaration()) + "; \n");
        }
        if (ctx.statement() != null){
            return (T) (visitStatement(ctx.statement()));
        }
        return (T) null;
        //TODO statement , localTypeDecl
    }

    @Override
    public T visitStatement(JavaParser.StatementContext ctx){
        if(ctx.RETURN() != null ){
            return (T) "return" ;
        }
        return (T)null;
    }

    @Override
    public T visitLocalVariableDeclaration(JavaParser.LocalVariableDeclarationContext ctx){
        //TODO variableModifier
        return (T)(visitVariableDeclarators(ctx.variableDeclarators()));
        // QUEDE ACA
    }
    @Override
    public T visitVariableDeclarators(JavaParser.VariableDeclaratorsContext ctx){
        String trad = "";
        for ( int i = 0 ; i < ctx.variableDeclarator().size(); i++)
            trad += (String) visitVariableDeclarator(ctx.variableDeclarator(i)) + ",";
        return (T) (trad.substring(0,trad.length()-1));
    }
    @Override
    public T visitVariableDeclarator(JavaParser.VariableDeclaratorContext ctx){
        String trad = "";
        if (ctx.variableInitializer() != null)
            trad = "=" + (String) (visitVariableInitializer(ctx.variableInitializer()));
        return (T) (visitVariableDeclaratorId(ctx.variableDeclaratorId()) + trad);
    }
    @Override
    public T visitVariableDeclaratorId(JavaParser.VariableDeclaratorIdContext ctx){
        return (T)(String)(ctx.IDENTIFIER().toString());
        //TODO [][]* (matrices)
    }
    @Override
    public T visitVariableInitializer (JavaParser.VariableInitializerContext ctx){
        if (ctx.arrayInitializer() != null)
            return (T) (visitArrayInitializer(ctx.arrayInitializer()));
        else
            return (T) (visitExpression(ctx.expression()));
    }
    @Override
    public T visitArrayInitializer (JavaParser.ArrayInitializerContext ctx){
        String trad = "";
        for ( int i = 0 ; i < ctx.variableInitializer().size(); i++){
            trad +=(String) visitVariableInitializer(ctx.variableInitializer(i)) + ",";
        }
        return (T) ( "[" + trad + "]");
        // Hay algo raro de una coma despues de una variableInitializer
    }
    @Override
    public T visitExpression(JavaParser.ExpressionContext ctx){
        if (ctx.primary() != null){
            return (T)(visitPrimary(ctx.primary()));
        }
        return (T) null;
        //TODO TODO EL RESTO JAJA
    }
    @Override
    public T visitPrimary(JavaParser.PrimaryContext ctx){
        if ( ctx.LPAREN() != null)
            return (T)visitExpression(ctx.expression());
        if (ctx.literal() != null)
            return (T)visitLiteral(ctx.literal());
        if(ctx.IDENTIFIER() != null)
            return (T)(ctx.IDENTIFIER().toString());
        return (T) null;
        // TODO FALTA TODO EL RESTO JAJA
    }
    @Override
    public T visitLiteral (JavaParser.LiteralContext ctx){
        if (ctx.integerLiteral() != null)
            return (T) "Soy un integer";
        if (ctx.floatLiteral() != null)
            return (T) "Soy un float";
        if (ctx.CHAR_LITERAL() != null)
            return (T) ctx.CHAR_LITERAL().toString();
        if (ctx.STRING_LITERAL() != null)
            return (T) ctx.STRING_LITERAL().toString();
        if (ctx.BOOL_LITERAL() != null)
            return (T) ctx.BOOL_LITERAL().toString();
        else
            return (T) ctx.NULL_LITERAL().toString();
    }
    @Override
    public T visitTypeType(JavaParser.TypeTypeContext ctx){
        if (ctx.classOrInterfaceType() != null)
            return (T)(visitClassOrInterfaceType(ctx.classOrInterfaceType()));
        else
            return (T)(visitPrimitiveType(ctx.primitiveType()));
    }
    @Override
    public T visitPrimitiveType (JavaParser.PrimitiveTypeContext ctx){
        return (T) "";
    }

}
