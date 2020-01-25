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
        return (T)("class " + ctx.IDENTIFIER().toString()+ "{" + visitClassBody(ctx.classBody()) +  "}" );
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
        if (!ctx.modifier().isEmpty()){
            return (T)visitMemberDeclaration(ctx.memberDeclaration());
        }
        //TODO STATIC Y ;
        return (T) null;
    }
    @Override
    public T visitMemberDeclaration(JavaParser.MemberDeclarationContext ctx){
        if (!ctx.methodDeclaration().isEmpty()){
            return (T) visitMethodDeclaration(ctx.methodDeclaration());
        }
        // TODO genericMethoddec, fieldDecl, contrucDecl, geneConsDecl, interDecla, annoTypeDecla, classDecla, enumDecla
        return (T) null;
    }
    @Override
    public T visitMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
        return (T) ("soy la declaracion de un metodo");
    }


}
