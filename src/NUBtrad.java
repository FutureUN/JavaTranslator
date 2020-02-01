import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.io.*;
import java.util.*;

public class NUBtrad<T> extends JavaParserBaseVisitor {
    public String RepeatChar(char c, int n) {
        String str = "";
        for (int i=0; i<n; i++) str+=c;
        return str;
    }


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
        return (T)("class " + ctx.depth() + " - " + ctx.IDENTIFIER().toString()+ "{\n" + visitClassBody(ctx.classBody()) +  "\n}" );
        // TODO typeParameters , typeType, TypeList
    }
    @Override
    public  T visitClassBody(JavaParser.ClassBodyContext ctx){
        String traduc = "";
        for ( int i = 0 ; i < ctx.classBodyDeclaration().size(); i ++){
            traduc += RepeatChar('\t',ctx.depth()-3) + (String)(visitClassBodyDeclaration(ctx.classBodyDeclaration(i))) + "\n";


        }
        return (T)traduc;
    }
    @Override
    public T visitClassBodyDeclaration(JavaParser.ClassBodyDeclarationContext ctx) {
        JavaParser.MemberDeclarationContext tmp = ctx.memberDeclaration();
        if (!ctx.modifier().isEmpty()){
            return (T)visitMemberDeclaration(ctx.memberDeclaration());
        }
        //TODO STATIC Y ;
        return (T) null;
    }
    @Override
    public T visitMemberDeclaration(JavaParser.MemberDeclarationContext ctx){
        JavaParser.MethodDeclarationContext tmp = ctx.methodDeclaration();
        if (ctx.methodDeclaration() != null){
            return (T) (visitMethodDeclaration(ctx.methodDeclaration()));
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
        if (ctx.block() == null)
            return (T) "";
        return (T) (visitBlock(ctx.block()));
        //TODO ;
    }
    @Override
    public T visitBlock(JavaParser.BlockContext ctx){
        String trad = "";
        for (int i = 0 ; i < ctx.blockStatement().size(); i++)
            trad += RepeatChar('\t',ctx.depth()-7) +  (String) visitBlockStatement((ctx.blockStatement(i)));
        return (T) ("{ \n" + trad + "\n" + RepeatChar('\t',ctx.depth()-8) + "}");
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
        //TODO localTypeDecl
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
        return (T) ( "[" + trad.substring(0,trad.length()-1) + "]");
        // Hay algo raro de una coma despues de una variableInitializer
    }
    @Override
    public T visitExpression(JavaParser.ExpressionContext ctx){
        if (ctx.primary() != null){
            return (T)(visitPrimary(ctx.primary()) );
        }
        if (ctx.LBRACK()!= null)
            return (T)( visitExpression(ctx.expression(0))+"[" + visitExpression(ctx.expression(1))+ "]");
        if (ctx.bop != null) {
            if (ctx.DOT() != null){
                if (ctx.IDENTIFIER() != null) return (T) (visitExpression(ctx.expression(0))+"." + ctx.IDENTIFIER().getText());
                if (ctx.methodCall() != null) return (T) (visitExpression(ctx.expression(0))+"." + visitMethodCall(ctx.methodCall()));
                return (T) ( "aun no ta echo");
                //TODO this, new , super , explicitGenericInv
            }
            String trad = "";
            if (ctx.QUESTION() != null)
                trad = (String) visitExpression(ctx.expression(2));
            return (T) (visitExpression(ctx.expression(0)) + ctx.bop.getText() + visitExpression(ctx.expression(1)) + trad);
        }
        return (T) ("caso prueba");
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
        // TODO FALTA: THIS, SUPER , TYPTTYPTORVOID, NONWILDCARDTYPEARG
    }
    @Override
    public T visitStatement (JavaParser.StatementContext ctx){
        if (ctx.statementExpression != null)
            return (T) (visitExpression(ctx.statementExpression)+ "; \n") ;
        if (ctx.identifierLabel != null)
            return (T) (ctx.IDENTIFIER().getText() );
        return (T) null;
        //TODO TODO EL RESTO JAJA
    }
    @Override
    public T visitMethodCall (JavaParser.MethodCallContext ctx){
        String trad = "()";
        if (ctx.expressionList() != null) trad = "(" + (String) visitExpressionList(ctx.expressionList()) + ")" ;
        if (ctx.IDENTIFIER() != null) return (T) (ctx.IDENTIFIER() + trad);
        else return (T) trad;  // TODO THIS, SUPER
    }
    @Override
    public T visitExpressionList (JavaParser.ExpressionListContext ctx){
        String trad = "" ;
        for ( int i = 0 ; i < ctx.expression().size() ; i++)
            trad += (String) visitExpression(ctx.expression(i)) + ",";
        return (T) trad.substring(0,trad.length()-1);
    }
    @Override
    public T visitLiteral (JavaParser.LiteralContext ctx){
        return (T) ctx.getText();
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
