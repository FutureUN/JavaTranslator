import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.io.*;
import java.util.*;

public class NUBtrad<T> extends JavaParserBaseVisitor {
    @Override
    public T visitTypeDeclaration(JavaParser.TypeDeclarationContext ctx) {

        if (!ctx.classDeclaration().isEmpty()){
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
            traduc += (String)(visitClassBodyDeclaration(ctx.classBodyDeclaration(i)));
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
        if (!ctx.methodDeclaration().isEmpty()){
            return (T) visitMethodDeclaration(ctx.methodDeclaration());
        }
        // TODO genericMethoddec, fieldDecl, contrucDecl, geneConsDecl, interDecla, annoTypeDecla, classDecla, enumDecla
        return (T) null;
    }
    @Override
    public T visitMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
        //TODO TypeTypeOrVoid , [], thows
        return (T)( ctx.IDENTIFIER().toString() + visitFormalParameters(ctx.formalParameters()) + "{\n" + " methodBody" + "\n}");
    }
    @Override
    public T visitFormalParameters(JavaParser.FormalParametersContext ctx) {
        return (T)("(" + visitFormalParameterList(ctx.formalParameterList())+")");
    }
    @Override
    public T visitFormalParameterList(JavaParser.FormalParameterListContext ctx){
        if (ctx == null)
            return (T) ("");
        String traduc = "";
        for(int i = 0 ; i < ctx.formalParameter().size() ; i++) {
            traduc = (String) visitFormalParameter(ctx.formalParameter(i)) + ",";
        }
        return (T) ("");
    }

}
