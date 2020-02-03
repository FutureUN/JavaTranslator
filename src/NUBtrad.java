import java.util.*;

public class NUBtrad<T> extends JavaParserBaseVisitor {

    static Integer depth = 1;
    public void SearchForRepeatedMethods(TreeSet<String> RepeatedMethodIds,
                                         Map <String, ArrayList<Integer>> MapMethodsIdx,
                                         Map <Integer, String> BodyDecHasDupMethod,
                                         JavaParser.ClassBodyContext ctx) {

        for (int i = 0; i<ctx.classBodyDeclaration().size(); i++) {

            JavaParser.ClassBodyDeclarationContext body_declaration =  ctx.classBodyDeclaration().get(i);
            if (body_declaration.memberDeclaration() == null) continue;
            JavaParser.MemberDeclarationContext member = body_declaration.memberDeclaration();

                if (member.methodDeclaration() != null || member.constructorDeclaration() != null) {
                    String ID = "";

                    if(member.constructorDeclaration() != null)
                        ID = "constructor";
                    else
                        ID = member.methodDeclaration().IDENTIFIER().getText();

                    if (!MapMethodsIdx.containsKey(ID)) MapMethodsIdx.put(ID, new ArrayList<>());

                    MapMethodsIdx.get(ID).add(i);

                    if (MapMethodsIdx.get(ID).size() > 1) {
                        if (!BodyDecHasDupMethod.containsKey(MapMethodsIdx.get(ID).get(0)))
                            BodyDecHasDupMethod.put(MapMethodsIdx.get(ID).get(0), ID);
                        BodyDecHasDupMethod.put(i, ID);
                        RepeatedMethodIds.add(ID);
                    }
                }
        }
    }

    public String MergeMethods(String IDMethod,
                             Map<String, ArrayList<Integer>> MapMethodsIdx,
                             JavaParser.ClassBodyContext ctx) {
        String ret = "";
        for (Integer idx : MapMethodsIdx.get(IDMethod) ) {
            String t1 = (String)(visitClassBodyDeclaration(ctx.classBodyDeclaration(idx)));
            String t2 = "";
            for (int i =0; i<t1.length(); i++) { if (t1.charAt(i) =='\n') t2 += "\n\t"; else t2 += t1.charAt(i);}
            ret += RepeatChar('\t', 2) + t2 +  "\n";
        }

        return ret;
    }
    public String RepeatChar(char c, int n) {
        String str = "";
        for (int i=0; i<n; i++) str+=c;
        return str;
    }




    @Override
    public T visitTypeDeclaration(JavaParser.TypeDeclarationContext ctx) {
        if (ctx.classDeclaration() != null){
            System.out.println(visitClassDeclaration(ctx.classDeclaration()));
            return (T) "";
        }
        return (T) "Different from class declaration is missing";
        //TODO enumDeclaration interfaceDeclaration annotationTypeDeclaration
    }
    @Override

    public T visitClassDeclaration(JavaParser.ClassDeclarationContext ctx){
        return (T)("class " + ctx.IDENTIFIER().toString()+ "{\n" + visitClassBody(ctx.classBody()) +  "\n}" );
        // TODO typeParameters , typeType, TypeList

    }
    @Override
    public  T visitClassBody(JavaParser.ClassBodyContext ctx){
        int cnt_methods = 0;


        TreeSet<String> RepeatedMethodIds = new TreeSet<>();
        Map <String, ArrayList<Integer>> MapMethodsIdx = new HashMap<>();
        Map <Integer, String> BodyDecHasDupMethod = new HashMap<>();
        HashSet<Integer> MethodTranslated= new HashSet<>();
        SearchForRepeatedMethods(RepeatedMethodIds, MapMethodsIdx, BodyDecHasDupMethod, ctx);

        String traduc = "";
        for ( int i = 0 ; i < ctx.classBodyDeclaration().size(); i ++){
            if (MethodTranslated.contains(i)) continue;
            if(BodyDecHasDupMethod.containsKey(i)) {
                String ID = BodyDecHasDupMethod.get(i);
                traduc  += RepeatChar('\t',depth)+ ID + "(...args){\n" + RepeatChar('\t',ctx.depth()-3)
                        + "//TODO: Change inner method according to ammount of args received. See : https://www.oreilly.com/library/view/javascript-the-definitive/0596101996/re05.html \n";

                traduc += MergeMethods(ID, MapMethodsIdx, ctx) +
                        RepeatChar('\t', depth) + "}\n";

                for(Integer it : MapMethodsIdx.get(ID)) MethodTranslated.add(it);
            }
            else
                traduc += RepeatChar('\t',depth) + (String)(visitClassBodyDeclaration(ctx.classBodyDeclaration(i))) + "\n";
        }


        // TODO : Send map to method mergeMethods



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
            return (T) (visitMethodDeclaration(ctx.methodDeclaration()));
        }
        if (ctx.constructorDeclaration() != null){



            return (T) (visitConstructorDeclaration(ctx.constructorDeclaration()));
        }
        // TODO genericMethoddec, fieldDecl, contrucDecl, geneConsDecl, interDecla, annoTypeDecla, classDecla, enumDecla
        return (T) "TODO genericMethoddec, fieldDecl, contrucDecl, geneConsDecl, interDecla, annoTypeDecla, classDecla, enumDecla";
    }

    @Override
    public T visitConstructorDeclaration(JavaParser.ConstructorDeclarationContext ctx){
        return (T) ( "constructor" + visitFormalParameters(ctx.formalParameters()) + visitBlock(ctx.constructorBody));

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
        depth++;
        String trad = "";
        if (ctx == null) return (T) trad;
        for (int i = 0 ; i < ctx.blockStatement().size(); i++)
            trad += RepeatChar('\t',depth) +  (String) visitBlockStatement((ctx.blockStatement(i)));

        return (T) ("{ \n" + trad + "\n" + RepeatChar('\t',--depth) + "}\n");
    }
    @Override
    public T visitBlockStatement( JavaParser.BlockStatementContext ctx){
        if (ctx.localVariableDeclaration() != null){
            return (T) (visitLocalVariableDeclaration(ctx.localVariableDeclaration()) + "; \n");
        }
        if (ctx.statement() != null){
            return (T) (visitStatement(ctx.statement()));
        }
        return (T) "TODO localTypeDecl";
        //TODO localTypeDecl
    }

    @Override
    public T visitLocalVariableDeclaration(JavaParser.LocalVariableDeclarationContext ctx){
        //TODO variableModifier
        return (T)( visitTypeType(ctx.typeType()) + " " + (String)visitVariableDeclarators(ctx.variableDeclarators()));
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
        if (ctx.methodCall() != null ) return (T) visitMethodCall(ctx.methodCall());
        if (ctx.postfix != null) return (T) (visitExpression(ctx.expression(0)) + ctx.postfix.getText());
        if (ctx.prefix != null) return (T) (ctx.prefix.getText() + visitExpression(ctx.expression(0)));
        if (ctx.NEW() != null) return (T) (ctx.NEW().getText() + " " + visitCreator(ctx.creator()));
        if (ctx.typeType() != null) {
            String _number = "float int double short";
            if (  _number.contains(ctx.typeType().getText()) )
                return (T)  ("Number(" + visitExpression(ctx.expression(0)) + ")");
            if( ctx.typeType().getText().equals("String"))
                return (T)  ("String(" + visitExpression(ctx.expression(0)) + ")");

        }
        return (T) ("Bits level operations or ::  or lambda expressison\n ");
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
            return  (T)(ctx.THIS().toString());
        // TODO FALTA: THIS, SUPER , TYPTTYPTORVOID, NONWILDCARDTYPEARG
    }
    @Override
    public T visitStatement (JavaParser.StatementContext ctx){
        if (ctx.blockLabel != null )
            return (T) (visitBlock(ctx.blockLabel));
        if (ctx.statementExpression != null)
            return (T) (visitExpression(ctx.statementExpression)+ "; \n") ;
        if (ctx.identifierLabel != null)
            return (T) (ctx.IDENTIFIER().getText() + ":" + visitStatement(ctx.statement(0)));
        // IF
        if (ctx.IF() != null) {
            if(ctx.ELSE() != null){
                return (T) (ctx.IF().getText() + " " + visitParExpression(ctx.parExpression()) + " " + visitStatement(ctx.statement(0)) + "\n" +
                        RepeatChar('\t',depth) + ctx.ELSE().getText() + " " + visitStatement(ctx.statement(1))+ "\n");
            }
            return (T) (ctx.IF().getText() + " " + visitParExpression(ctx.parExpression()) + " " + visitStatement(ctx.statement(0)) + "\n");
        }
        // FOR
        if (ctx.FOR() != null){
            return (T) (ctx.FOR().getText() + "(" + visitForControl(ctx.forControl()) + ')' + visitStatement(ctx.statement(0)));
        }
        // DO
        if(ctx.DO() != null){
            return (T) (ctx.DO().getText() + visitStatement(ctx.statement(0)) + "while" + visitParExpression(ctx.parExpression()) + ";\n");
        }
        // WHILE
        if(ctx.WHILE() != null){
            return (T) (ctx.WHILE().getText() + visitParExpression(ctx.parExpression()) + visitStatement(ctx.statement(0)));
        }

        // RETURN
        if(ctx.RETURN() != null){
            String trad = " ";
            if ( ctx.expression() == null) trad = (String) visitExpression(ctx.expression(0));
            return (T) (ctx.RETURN().getText() + trad + ";");}
        // THROW
        if (ctx.THROW() != null)
            return (T) (ctx.THROW().getText() + " " + visitExpression(ctx.expression(0)) + ";") ;
        if(ctx.BREAK() != null){
            if (ctx.IDENTIFIER() != null )
                return (T) (ctx.BREAK().getText() + ctx.IDENTIFIER().getText()+ ";\n");
            return (T) (ctx.BREAK().getText() + ";\n");
        }
        if(ctx.CONTINUE() != null){
            if (ctx.IDENTIFIER() != null )
                return (T) (ctx.CONTINUE().getText() + ctx.IDENTIFIER().getText()+ ";\n");
            return (T) (ctx.CONTINUE().getText() + ";\n");
        }
        return (T) "Missing non StatatementExpression or identifier label statement \n";
        //TODO ASSERT, TRY, SWITCH, SYNC, SEMI
    }

    @Override
    public T visitParExpression(JavaParser.ParExpressionContext ctx){
        return (T)("(" + visitExpression(ctx.expression()) +  ")");

    }
    @Override public T visitForControl(JavaParser.ForControlContext ctx) {
        if (ctx.enhancedForControl() != null){
            return (T) visitEnhancedForControl(ctx.enhancedForControl());
        }else {
            return (T)  (visitForInit(ctx.forInit()) + "; " + visitExpression(ctx.expression())  + "; " +  visitExpressionList(ctx.expressionList())) ;
        }
    }
    @Override public T visitForInit(JavaParser.ForInitContext ctx) {
        if(ctx.localVariableDeclaration() != null){
            return (T) visitLocalVariableDeclaration(ctx.localVariableDeclaration());
        }
        if(ctx.expressionList() != null){
            return (T) visitExpressionList(ctx.expressionList());
        }
        return  (T)null;
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

    @Override public T visitClassOrInterfaceType(JavaParser.ClassOrInterfaceTypeContext ctx) {
        if (ctx.DOT().size() <= 0 )
            return (T) "let";
        return  (T) "TODO classOrInterfaceType";
    }

    @Override
    public T visitPrimitiveType (JavaParser.PrimitiveTypeContext ctx){
        return (T) "var";
    }
    @Override
    public T  visitCreator(JavaParser.CreatorContext ctx){
        if (ctx.createdName() != null){
            if (ctx.classCreatorRest() != null)
                return (T) ( (String)visitCreatedName(ctx.createdName()) + (String)visitClassCreatorRest(ctx.classCreatorRest()));

            // TODO arrayCreatorRest
        }
        // TODO nonWildTypeArguments
        return  (T)null;
    }

    @Override
    public T visitClassCreatorRest(JavaParser.ClassCreatorRestContext ctx) {
        if (ctx.classBody() != null){
            return (T) ( (String) visitArguments(ctx.arguments()) + (String)visitClassBody(ctx.classBody()) );
        }
        return (T) (visitArguments(ctx.arguments()) );
    }

    @Override
    public T visitCreatedName(JavaParser.CreatedNameContext ctx){
        if (ctx.IDENTIFIER() != null) {
            String name = ctx.IDENTIFIER(0).getText();
            if (name.equals("RuntimeException") )
                name = "Error";
            return (T) (name );  // TODO typeArgumentsOrDiamond
        }
        if( ctx.primitiveType() != null){
            return (T) visitPrimitiveType(ctx.primitiveType());
        }

        return (T)null;
    }

    @Override public T visitArguments(JavaParser.ArgumentsContext ctx) {
        if( ctx.expressionList()!= null)
            return (T) ("(" + visitExpressionList(ctx.expressionList()) + ")");
        return (T) "()";
    }

}
