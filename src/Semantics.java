import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Semantics extends pdrawBaseVisitor<String> {
   private HashMap<String, Symbol> vars = new HashMap<String, Symbol>();
   private HashMap<String, Symbol> temp_vars = new HashMap<String, Symbol>();

   // DONE
   @Override public String visitProgram(pdrawParser.ProgramContext ctx) {
      return visitChildren(ctx);
   }


   // DONE
   @Override public String visitComment(pdrawParser.CommentContext ctx) {
      return visitChildren(ctx);
   }

   @Override
   public String visitImport(pdrawParser.ImportContext ctx) {
      String res = null;
      return visitChildren(ctx);
      // return res;
   }

   // DONE
   @Override public String visitDecla(pdrawParser.DeclaContext ctx) {
      return visitChildren(ctx);
   }

   // DONE
   @Override public String visitAttr(pdrawParser.AttrContext ctx) {
      return visitChildren(ctx);
   }


   // DONE GONCALO
   @Override
   public String visitPenCommand(pdrawParser.PenCommandContext ctx) {
      if ((vars.containsKey(ctx.ID().getText()) && ((vars.get(ctx.ID().getText()).getType()).equals("pen") || (vars.get(ctx.ID().getText()).getType()).equals("array-pen")))) {
         return "stat";
      } else {
         if (!vars.containsKey(ctx.ID().getText())) {
            System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Variable " + ctx.ID().getText() + " is not defined");
            System.exit(1);
         } else if (!(vars.get(ctx.ID().getText()).getType().equals("pen"))) {
            if (ctx.op.getText().equals("raise")) {
               System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Cannot perform Command 'raise' with '"
                     + vars.get(ctx.ID().getText()).getType().split(";")[0] + "' type");
               System.exit(1);
            } else if (ctx.op.getText().equals("lower")) {
               System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Cannot perform Command 'lower' with '"
                     + vars.get(ctx.ID().getText()).getType().split(";")[0] + "' type");
               System.exit(1);
            }
         }
      }
      return "";
   }

   // DONE GONCALO
   @Override
   public String visitDoAction(pdrawParser.DoActionContext ctx) {
      if (vars.containsKey(ctx.ID().getText()) && vars.get(ctx.ID().getText()) != null && vars.get(ctx.ID().getText()).getType().equals("pen")) {
         visit(ctx.action());
         return "stat";
      }
      else if (temp_vars.containsKey(ctx.ID().getText()) && temp_vars.get(ctx.ID().getText()) != null && temp_vars.get(ctx.ID().getText()).getType().equals("pen"))  {
         visit(ctx.action());
         return "stat";
      }
      else {
         if (!(vars.containsKey(ctx.ID().getText()) || temp_vars.containsKey(ctx.ID().getText()))) {
            System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Variable " + ctx.ID().getText() + " is not defined");
            System.exit(1);
         }else if (temp_vars.containsKey(ctx.ID().getText()) && temp_vars.get(ctx.ID().getText()) == null){
            System.err.println(
               "[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Cannot perform action with '" + temp_vars.get(ctx.ID().getText()) + "' type");
            System.exit(1);
         }else if (!(temp_vars.containsKey(ctx.ID().getText()) && temp_vars.get(ctx.ID().getText()).getType().equals("pen"))){
            System.err.println(
               "[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Cannot perform action with '" + temp_vars.get(ctx.ID().getText()).getType() + "' type");
            System.exit(1);
         }else if (vars.containsKey(ctx.ID().getText()) && vars.get(ctx.ID().getText()) == null){
            System.err.println(
               "[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Cannot perform action with '" + vars.get(ctx.ID().getText()) + "' type");
            System.exit(1);
         } else if (!(vars.containsKey(ctx.ID().getText()) && vars.get(ctx.ID().getText()).getType().equals("pen"))) {
            System.err.println(
               "[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Cannot perform action with '" + vars.get(ctx.ID().getText()).getType() + "' type");
            System.exit(1);
         }
      }
      return "";
   }

      // DONE GONCALO
      @Override public String visitIfElifElseStat(pdrawParser.IfElifElseStatContext ctx) {
         if (visit(ctx.expr()).split(";")[0].equals("boolean")) {
            for (int i = 0; i<ctx.stat().size();i++){
               if (!visit(ctx.stat(i)).equals("stat")){
                  System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ":Invalid Statement");
                  System.exit(1);
               }
            }
            
            for (int i = 0; i<ctx.elif().size();i++){
               visit(ctx.elif(i));
            }
            
            if (ctx.els() != null){
               visit(ctx.els());
            }
         }
         else{
            System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": If condition should be boolean and not type '" + visit(ctx.expr()).split(";")[0] + "'.");
            System.exit(1);
         }
         return "stat";
      }

   // DONE BUTUC
   @Override public String visitForLoop(pdrawParser.ForLoopContext ctx) {
      if (ctx.r == null){
         if (!visit(ctx.expr()).split(";")[0].contains("array-")){
            System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Loop can only iterate over array type.");
            System.exit(1);
         }
         temp_vars.put(ctx.ID().getText(), new Symbol(ctx.ID().getText(), visit(ctx.expr()).split(";")[0].split("-")[1]));
         
         for (int i = 0; i < ctx.stat().size(); i++){
            if (!visit(ctx.stat(i)).equals("stat")){
               System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Invalid Statement.");
               System.exit(1);
            }
         }
         temp_vars.remove(ctx.ID().getText());
         return "stat";
      }
      else if (ctx.r.getText().equals("range")){
         if (!visit(ctx.expr()).split(";")[0].equals("num")){
            System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": In range loop can only iterate over range in num type.");
            System.exit(1);
         }
         temp_vars.put(ctx.ID().getText(), new Symbol(ctx.ID().getText(), visit(ctx.expr())));
         for (int i = 0; i < ctx.stat().size(); i++){
            if (!visit(ctx.stat(i)).equals("stat")){
               System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Invalid Statement.");
               System.exit(1);
            }
         }
         temp_vars.remove(ctx.ID().getText());
         return "stat";
      }
      return "";
   }

   //DONE GONCALO
   @Override public String visitWhileLoop(pdrawParser.WhileLoopContext ctx) {
      if (visit(ctx.expr()).equals("boolean")) {
         int cont=0;
         for (int i = 0; i<ctx.stat().size();i++){
            if (!visit(ctx.stat(i)).equals("stat")){
               System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ":Invalid Statement");
               System.exit(1);
            }
         }
      }
      else{
         System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": While condition should be boolean");
         System.exit(1);
      }
      return "stat";
   }

   // DONE BUTUC
   @Override public String visitFunction(pdrawParser.FunctionContext ctx) {
      // declaration error.
      if (vars.containsKey(ctx.ID().getText())){
         System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Function '" + ctx.ID().getText() + "' is already declared.");
         System.exit(1);
      }

      String arg_string = "";
      String[] content;
      ArrayList<String> args = new ArrayList<>();
      // Args assemments
      for (int i = 0; i<ctx.arg().size(); i++){
         content = visit(ctx.arg(i)).split(";");
         arg_string += content[0] + " ";
         args.add(content[1]);
      }

      // Checks if there isnt another function declaration within.
      for (int i = 0; i<ctx.stat().size(); i++){
         if (visit(ctx.stat(i)).equals("function")){
            System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Cannot declare function inside of another function.");
         }
      }

      if (ctx.expr() != null){
         if (arg_string.equals("")){
            vars.put(ctx.ID().getText(), new Symbol(ctx.ID().getText(), visit(ctx.expr()).split(";")[0] + ";null" + ";function") ); 
         }
         else {
            vars.put(ctx.ID().getText(), new Symbol(ctx.ID().getText(), visit(ctx.expr()).split(";")[0] + ";" + arg_string +  ";function")); 
         }
      }
      else {
         if (arg_string.equals("")){
            vars.put(ctx.ID().getText(), new Symbol(ctx.ID().getText(), "void" + ";null"+ ";function"));
         }
         else {
            vars.put(ctx.ID().getText(), new Symbol(ctx.ID().getText(),  "void;" + arg_string + ";function"));
         }
      }

      for (int i = 0; i<args.size(); i++){
         temp_vars.remove(args.get(i));
      }

      return "function;" + arg_string ;
   }

   // DONE
   @Override public String visitPrint(pdrawParser.PrintContext ctx) {
      if (visit(ctx.expr()).split(";").length == 2){
         if (visit(ctx.expr()).split(";")[1].equals("ID")) {
            if (!(vars.containsKey(ctx.expr().getText()) || temp_vars.containsKey(ctx.expr().getText()))) {
               if (!vars.containsKey(ctx.expr().getText())){
                  System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Variable " + ctx.expr().getText() + " is not defined");
                  System.exit(1);
               } else if (!temp_vars.containsKey(ctx.expr().getText())){
                  System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Variable " + ctx.expr().getText() + " is not defined");
                  System.exit(1);
               }
            }
         }
      }
      else if (visit(ctx.expr()).split(";")[0] == "null"){
         System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Variable " + ctx.expr().getText() + " is not defined");
         System.exit(1);
      }
      return "stat";
   }
   //DONE GONCALO
   @Override
   public String visitArrayAdd(pdrawParser.ArrayAddContext ctx) {
      if (vars.containsKey(ctx.ID().getText())) {
         if (vars.get(ctx.ID().getText()).getType().split("-")[0].equals("array")) {
            String type = "";
            for (int i=1; i<vars.get(ctx.ID().getText()).getType().split("-").length; i++){
               if (i != vars.get(ctx.ID().getText()).getType().split("-").length - 1){
                  type += vars.get(ctx.ID().getText()).getType().split("-")[i] + "-";
               }
               else {
                  type += vars.get(ctx.ID().getText()).getType().split("-")[i];
               }
            }

            if (visit(ctx.expr()).split(";")[0].contains("array") && visit(ctx.expr()).split(";")[0].split("-")[1].equals(type)) {
               return "stat";
            }
            else if (visit(ctx.expr()).split(";")[0].equals(type)){
               return "stat";
            }
            else{
               System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ":Cannot add '"+visit(ctx.expr()).split(";")[0]+"' value to "+vars.get(ctx.ID().getText()).getType()+" array");
               System.exit(1);
            }
         }
         else{
            System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Variable " + ctx.ID().getText() + " is not type array");
            System.exit(1);
         }

      }
      else{
         if (!vars.containsKey(ctx.ID().getText())) {
            System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Variable " + ctx.ID().getText() + " is not defined");
         }
         else if(vars.get(ctx.ID().getText()).getType().split("-")[0].equals("array")){
            System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Variable " + ctx.ID().getText() + " is not type array");
            System.exit(1);
         }
      }
      return "";
   }

   //DONE GONCALO
   @Override
   public String visitArrayRemove(pdrawParser.ArrayRemoveContext ctx) {
      if (vars.containsKey(ctx.ID().getText())) {
         if (vars.get(ctx.ID().getText()).getType().split("-")[0].equals("array")) {
            if (visit(ctx.expr()).split(";")[0].equals("num")){
               if (!visit(ctx.expr()).split(";")[1].equals("int")){
                  System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Passed index must be of type int.");
                  System.exit(1);
               }
            }
            else {
               System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Passed index must be of type num.");
               System.exit(1);
            }
         }
         else {
            System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Variable must be of type array.");
            System.exit(1);
         }
      }
      else {
         System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Variable is not defined.");
         System.exit(1);
      }
      return "";
   }

   // DONE BUTUC
   @Override public String visitIDID(pdrawParser.IDIDContext ctx) {
      for (int i = 0; i<ctx.ID().size(); i++){
         if (!vars.containsKey(ctx.ID(i).getText())){
            System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + " " + ctx.ID(i).getText() + " is not defined.");
            System.exit(1);
         }
      }
      if (!(vars.get(ctx.ID(0).getText()).getType().equals("pen") || vars.get(ctx.ID(0).getText()).getType().equals("array-pen"))){
         System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": statement only works with first variable of type pen or array-pen.");
         System.exit(1);
      }

      if (!(vars.get(ctx.ID(1).getText()).getType().equals("action"))){
         System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": statment only works with second variable of type action.");
         System.exit(1);
      }
      return "stat";
   }

   // DONE
   @Override
   public String visitGoExpr(pdrawParser.GoExprContext ctx) {
      if (!visit(ctx.expr()).split(";")[visit(ctx.expr()).split(";").length - 1].equals("function")){
         System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Invalid statement.");
         System.exit(1);
      }
      return "";
   }


   // DONE BUTUC
   @Override public String visitArg(pdrawParser.ArgContext ctx) {
      if (temp_vars.containsKey(ctx.ID().getText())){
         System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Cannot declare more than 1 argument with the same name.");
         System.exit(1);
      }
      else {
         temp_vars.put(ctx.ID().getText(), new Symbol(ctx.ID().getText(), ctx.TYPE().getText()));
      }
      return ctx.TYPE().getText() + ";" + ctx.ID().getText();
   }

   // DONE BUTUC
   @Override
   public String visitPenMoveTo(pdrawParser.PenMoveToContext ctx) {
      if (visit(ctx.expr(0)).split(";")[0].equals("num") && visit(ctx.expr(1)).split(";")[0].equals("num")) {
         return "action";
      } else if (!visit(ctx.expr(0)).split(";")[0].equals("num")) {
         System.err.println(
               "[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": value of x coordinate should be a num and not a '" + visit(ctx.expr(0)) + "' value.");
         System.exit(1);
      } else {
         System.err.println(
               "[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": value of y coordinate should be a num and not a '" + visit(ctx.expr(1)) + "' value.");
         System.exit(1);
      }
      return "";
   }

   // DONE BUTUC
   @Override
   public String visitPenMoveBy(pdrawParser.PenMoveByContext ctx) {
      if (ctx.expr().size() == 1) {
         if ((visit(ctx.expr(0)).split(";")[0]).equals("num")) {
            return "action";
         } else {
            System.err.println(
                  "[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Can't pass type '" + visit(ctx.expr(0)) + "' value as argument of value 'num'.");
            System.exit(1);
         }
      } else if (ctx.expr().size() == 2) {
         if ((visit(ctx.expr(0)).split(";")[0]).equals("num") && visit(ctx.expr(1)).split(";")[0].equals("degree")) {
            return "action";
         } else if (!(visit(ctx.expr(0)).split(";")[0]).equals("num")) {
            System.err.println(
                  "[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Can't pass type '" + visit(ctx.expr(0)) + "' value as argument of value 'num'.");
            System.exit(1);
         } else if (!visit(ctx.expr(1)).split(";")[0].equals("degree")) {
            System.err.println(
                  "[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Can't pass type '" + visit(ctx.expr(1)) + "' value as argument of value 'degree'.");
            System.exit(1);
         }
      }
      return "";
   }

   // DONE BUTUC
   @Override
   public String visitPenRotateBY(pdrawParser.PenRotateBYContext ctx) {
      if (visit(ctx.expr()).split(";")[0].equals("degree")) {
         return "action";
      } else {
         System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Can only pass 'degree' type value in rotate operation.");
         System.exit(1);
      }
      return "";
   }

   // DONE BUTUC
   @Override public String visitPenChangeTo(pdrawParser.PenChangeToContext ctx) {
      for (int i = 0; i < ctx.chg().size(); i++){
         switch (ctx.chg(i).prop.getText()){
            case "-heading":
               if (! visit(ctx.chg(i).expr()).split(";")[0].equals("degree")){
                  System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Can only pass type 'degree' to '-heading' flag.");
                  System.exit(1);
               }
               break;
            case "-status":
               if (! visit(ctx.chg(i).expr()).split(";")[0].equals("status")){
                  System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Can only pass type 'status' to '-status' flag.");
                  System.exit(1);
               }
               break;
            
            case "-posx":
               if (! visit(ctx.chg(i).expr()).split(";")[0].equals("num")){
                  System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Can only pass type 'num' to '-posx' flag.");
                  System.exit(1);
               }
               break;

            case "-posy":
               if (! visit(ctx.chg(i).expr()).split(";")[0].equals("num")){
                  System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Can only pass type 'num' to '-posy' flag.");
                  System.exit(1);
               }
               break;
            
            case "-colour":
               if (! visit(ctx.chg(i).expr()).split(";")[0].equals("colour")){
                  System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Can only pass type 'colour' to '-colour' flag.");
                  System.exit(1);
               }
               break;
            
            case "-thickness":
               if (! visit(ctx.chg(i).expr()).split(";")[0].equals("num")){
                  System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Can only pass type 'num' to '-thickness' flag.");
                  System.exit(1);
               }
               break;
            case "-pattern":
               if (! visit(ctx.chg(i).expr()).split(";")[0].equals("pattern")){
                  System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Can only pass type 'pattern' to '-pattern' flag.");
                  System.exit(1);
               }
               break;  
         }
      }
      return "action";
   }

   //DONE GONCALO
   @Override
   public String visitElif(pdrawParser.ElifContext ctx) {
      if (visit(ctx.expr()).equals("boolean")) {
         for (int i = 0; i<ctx.stat().size(); i++){
            if (!visit(ctx.stat(i)).equals("stat")){
               System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Invalid statement introduced");
               System.exit(1);
            }
         }
      }
      else {
         System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": If condition should be boolean and not type '" + visit(ctx.expr()).split(";")[0] + "'.");
         System.exit(1);
      }
      return "";
   }

   //DONE GONCALO
   @Override
   public String visitEls(pdrawParser.ElsContext ctx) {
      int cont=0;
      for (int i = 0;i<ctx.stat().size();i++) {
         if (!visit(ctx.stat(i)).equals("stat")) {
            System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Invalid statement introduced");
            System.exit(1);
         }
         else {
            cont++;
         }
      }
      if (cont==ctx.stat().size()) {
         return "els";
      }
      return "";
   }

   // DONE
   @Override
   public String visitDeclaration(pdrawParser.DeclarationContext ctx) {
      if (!vars.containsKey(ctx.ID().getText())) { // if not declarated
         if (!((ctx.expr() == null) || ctx.expr().getText().equals("null") || visit(ctx.expr()).equals("null"))) {
            String typeVar = ctx.TYPE().getText();
            if ((visit(ctx.expr()).split(";")[0]).equals(typeVar)) {// tipos iguais
               vars.put(ctx.ID().getText(), new Symbol(ctx.ID().getText(), visit(ctx.expr())));
            } else {
                  System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Cannot assign '" + visit(ctx.expr()).split(";")[0] + "' value to '"
                        + ctx.TYPE().getText() + "' variable.");
                  System.exit(1);
               }
            } else {
            vars.put(ctx.ID().getText(), new Symbol(ctx.ID().getText(), ctx.TYPE().getText()));
         }
      } else {
         System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Variable '" + ctx.ID().getText() + "' already declared.");
         System.exit(1);
      }
      return "stat";
   }

   // DONE BUTUC
   @Override public String visitChg(pdrawParser.ChgContext ctx) {
      String res = null;
      return visitChildren(ctx);
   }


   // DONE BUTUC
   @Override
   public String visitAttribution(pdrawParser.AttributionContext ctx) {
      if (!(vars.containsKey(ctx.ID().getText()) || temp_vars.containsKey(ctx.ID().getText()))) { // ID variable is not defined
         System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Variable " + ctx.ID().getText() + " is not defined");
         System.exit(1);
      }
      else if (vars.containsKey(ctx.ID().getText()) && vars.get(ctx.ID().getText()) == null){
         vars.put(ctx.ID().getText(), new Symbol(ctx.ID().getText(), visit(ctx.expr())));
         return "stat";
      }
      else {
         if (vars.containsKey(ctx.ID().getText()) && !(vars.get(ctx.ID().getText()).getType().split(";")[0].equals(visit(ctx.expr()).split(";")[0]))){
            System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Cannot assign '" + visit(ctx.expr()).split(";")[0] + "' value to variable of type '" + vars.get(ctx.ID().getText()).getType().split(";")[0] + "'.");
            System.exit(1);
         }
         else if (temp_vars.containsKey(ctx.ID().getText()) && !(temp_vars.get(ctx.ID().getText()).getType().equals(visit(ctx.expr()).split(";")[0]))){
            System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Cannot assign '" + visit(ctx.expr()).split(";")[0] + "' value to variable of type '" + temp_vars.get(ctx.ID().getText()).getType() + "'.");
            System.exit(1);
         }
      }
      vars.put(ctx.ID().getText(), new Symbol(ctx.ID().getText(), visit(ctx.expr())));
      return "stat";
   }

   // DONE BUTUC
   @Override
   public String visitExprPattern(pdrawParser.ExprPatternContext ctx) {
      return "pattern";
   }


   // DONE GONCALO
   @Override
   public String visitExprEquals(pdrawParser.ExprEqualsContext ctx) {
      if (!visit(ctx.expr(0)).split(";")[0].equals(visit(ctx.expr(1)).split(";")[0])) {
         System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Can't compare '" + visit(ctx.expr(0)).split(";")[0] + "' value with '"
               + visit(ctx.expr(1)).split(";")[0] + "' value.");
         System.exit(1);
      }
      return "boolean";
   }

   // DONE
   @Override
   public String visitExprString(pdrawParser.ExprStringContext ctx) {
      return "string";
   }
   // DONE BUTUC
   @Override public String visitExprLength(pdrawParser.ExprLengthContext ctx) {
      if (!vars.containsKey(ctx.ID().getText())){
         System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": " + ctx.ID() + " is not defined.");
      }
      else {
         if (!(((vars.get(ctx.ID().getText())).getType().contains("array-")) || (vars.get(ctx.ID().getText())).getType().equals("string"))){
            System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Cannot calculate length of '" + vars.get(ctx.ID().getText()).getType() + "' value." );
            System.exit(1);
         }
         
      }
      return "num;int";
   }
   // DONE
   @Override
   public String visitExprBoolean(pdrawParser.ExprBooleanContext ctx) {
      return "boolean";
   }

   // DONE
   @Override
   public String visitExprParenthesis(pdrawParser.ExprParenthesisContext ctx) {
      return visit(ctx.expr());
   }

   // DONE GONCALO
   @Override
   public String visitExprNot(pdrawParser.ExprNotContext ctx) {
      if (!visit(ctx.expr()).equals("boolean")) {
         System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Cannot use 'Not(!)' with '" + visit(ctx.expr()).split(";")[0] + "' value.");
         System.exit(1);
      }
      return "boolean";
   }

   @Override public String visitExprInput(pdrawParser.ExprInputContext ctx) {
      if (ctx.op.getText().equals("input num")){
         return "num;int";
      }
      else if (ctx.op.getText().equals("input")){
         return "string";
      }
      return "";
   }
   // DONE BUTUC
   @Override
   public String visitExprColour(pdrawParser.ExprColourContext ctx) {
      return "colour";
   }

   // DONE GONCALO
   @Override
   public String visitExprAndOR(pdrawParser.ExprAndORContext ctx) {
      if (visit(ctx.expr(0)).equals("boolean") && visit(ctx.expr(1)).equals("boolean")) {
         return "boolean";
      } else {
         if (ctx.op.getText().equals("and")) {
            System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Cannot use 'and' with '" + visit(ctx.expr(0)).split(";")[0]
                  + "' value and '" + visit(ctx.expr(1)).split(";")[0] + "' value.");
            System.exit(1);
         } else if (ctx.op.getText().equals("or")) {
            System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Cannot use 'or' with '" + visit(ctx.expr(0)).split(";")[0]
                  + "' value and '" + visit(ctx.expr(1)).split(";")[0] + "' value.");
            System.exit(1);
         }
      }
      return "";
   }

   // DONE GONCALO
   @Override
   public String visitExprArray(pdrawParser.ExprArrayContext ctx) {
      List<pdrawParser.ExprContext> content = ctx.expr();
      Iterator<pdrawParser.ExprContext> it = content.iterator();
      String type;
      if (!it.hasNext()) {
         return "null";
      }
      type = visit(it.next()).split(";")[0];
      while (it.hasNext()) {
         if (!(visit(it.next()).split(";")[0]).equals(type)) {
            System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": All array elements should be of the same type -->" + type);
            System.exit(1);
         }
      }
      return "array-" + type;
   }

   // DONE BUTUC
   @Override
   public String visitExprMultDivMod(pdrawParser.ExprMultDivModContext ctx) {
      if ((visit(ctx.expr(0)).split(";")[0]).equals("num") && (visit(ctx.expr(1)).split(";")[0]).equals("num")) {
         if (ctx.op.getText().equals("/")) {
            if ((visit(ctx.expr(0)).split(";")[1]).equals("int") && (visit(ctx.expr(1)).split(";")[1]).equals("int")) {
               Integer int1 = Integer.parseInt(visit(ctx.expr(0)).split(";")[2]);
               Integer int2 = Integer.parseInt(visit(ctx.expr(1)).split(";")[2]);
               if (((int1 / int2) % 1) == 0) {
                  return "num;int;" + (int1 / int2);
               } else {
                  return "num;float;" + (int1 / int2);
               }
            } else {
               Double float1 = Double.parseDouble(visit(ctx.expr(0)).split(";")[2]);
               Double float2 = Double.parseDouble(visit(ctx.expr(1)).split(";")[2]);
               return "num;float;" + (float1/float2);
            }
         } else if (ctx.op.getText().equals("%")) {
            if ((visit(ctx.expr(0)).split(";")[1]).equals("int") && (visit(ctx.expr(1)).split(";")[1]).equals("int")) {
               Integer int1 = Integer.parseInt(visit(ctx.expr(0)).split(";")[2]);
               Integer int2 = Integer.parseInt(visit(ctx.expr(1)).split(";")[2]);
               return "num;int;" + (int1%int2);
            } else {
               Double float1 = Double.parseDouble(visit(ctx.expr(0)).split(";")[2]);
               Double float2 = Double.parseDouble(visit(ctx.expr(1)).split(";")[2]);
               return "num;float;" + (float1%float2);
            }
         } else if (ctx.op.getText().equals("*")){
            if ((visit(ctx.expr(0)).split(";")[1]).equals("int") && (visit(ctx.expr(1)).split(";")[1]).equals("int")){
               Integer int1 = Integer.parseInt(visit(ctx.expr(0)).split(";")[2]);
               Integer int2 = Integer.parseInt(visit(ctx.expr(1)).split(";")[2]);
               return "num;int;" + (int1*int2);
            }
            else {
               Double float1 = Double.parseDouble(visit(ctx.expr(0)).split(";")[2]);
               Double float2 = Double.parseDouble(visit(ctx.expr(1)).split(";")[2]);
               return "num;float;" + (float1*float2);
            }
         }

      } else if (visit(ctx.expr(0)).split(";")[0].equals("degree") && visit(ctx.expr(1)).split(";")[0].equals("num")) {
         return "degree";
      } else if (visit(ctx.expr(1)).split(";")[0].equals("degree") && visit(ctx.expr(0)).split(";")[0].equals("num")) {
         if (ctx.op.getText().equals("%")){
            System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Cannot calculate modulus of num by degree.");
            System.exit(1);
         }
         else if (ctx.op.getText().equals("/")){
            System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Cannot calculate division of num by degree.");
            System.exit(1);
         }
         return "degree";

      } else if (ctx.op.getText().equals("*")) {
         if ((visit(ctx.expr(0)).equals("string") && (visit(ctx.expr(1)).split(";")[0]).equals("num"))) {
            if ((visit(ctx.expr(1)).split(";")[1]).equals("float")) {
               System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Cannot multiply string by num subtype float");
               System.exit(1);
            }
            return "string";
         } else if ((visit(ctx.expr(1)).equals("string") && (visit(ctx.expr(0)).split(";")[0]).equals("num"))) {
            if ((visit(ctx.expr(0)).split(";")[1]).equals("float")) {
               System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Cannot multiply string by num subtype float");
               System.exit(1);
            }
            return "string";
         } else {
            System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Cannot multiply '" + (visit(ctx.expr(0)).split(";")[0])
                  + "' value with '" + (visit(ctx.expr(0)).split(";")[0]) + "' value.");
            System.exit(1);
         }
      } else if (ctx.op.getText().equals("/")) {
         System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Cannot divide '" + (visit(ctx.expr(0)).split(";")[0]) + "' value with '"
               + (visit(ctx.expr(0)).split(";")[0]) + "' value.");
         System.exit(1);
      }
      return "";
   }

   // DONE BUTUC
   @Override public String visitExprGet(pdrawParser.ExprGetContext ctx) {
      if (!vars.containsKey(ctx.ID().getText())){
         System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": " + ctx.ID().getText() + " is not defined.");
         System.exit(1);
      }
      else {
         if (!vars.get(ctx.ID().getText()).getType().contains("array-")){
            System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": get only works with array type values");
            System.exit(1);
         }
         else {
            if (visit(ctx.expr()).split(";")[0].equals("num") && (visit(ctx.expr()).split(";")[1].equals("int"))){
               return vars.get(ctx.ID().getText()).getType().split("-")[1];
            }
            
            else {
               System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": get only works with integer indexing.");
               System.exit(1);
            }
         }
      }
      return "";
   }

   // DONE
   @Override
   public String visitExprDegree(pdrawParser.ExprDegreeContext ctx) {
      return "degree";
   }

   // DONE BUTUC
   @Override
   public String visitExprMinus(pdrawParser.ExprMinusContext ctx) {
      if (visitChildren(ctx).split(";")[0].equals("num") || visitChildren(ctx).equals("degree")) {
         return visitChildren(ctx);
      } else {
         System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Cannot assign unary operator to '" + visitChildren(ctx) + "'.");
         System.exit(1);
      }
      return "";
   }

   // DONE BUTUC
   @Override
   public String visitExprPen(pdrawParser.ExprPenContext ctx) {
      int count = 0;
      if (ctx.STATUS().size() > 1){
            System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Cannot assign more than 1 'status' attribution.");
            System.exit(1);
      }

      if (ctx.DEGREE().size() > 1){
            System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Cannot assign more than 1 'heading' attribution.");
            System.exit(1);
      }

      if (ctx.COLOUR().size() > 1){
            System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Cannot assign more than 1 'colour' attribution.");
            System.exit(1);
      }

      if (ctx.PATTERN().size() > 1){
         System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Cannot assign more than 1 'pattern' attribution.");
         System.exit(1);
      }

      if (ctx.posx != null){
         count++;
      }
      if (ctx.posy != null){
         count++;
      }
      if (ctx.thickness != null){
         count++;
      }

      if (count != ctx.expr().size()){
         System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Cannot assign more than 1 'posx', 'posy' or 'thickness' attribution.");
         System.exit(1);
      }
      else {
         for (int i = 0; i<ctx.expr().size(); i++){
            if (!visit(ctx.expr(i)).split(";")[0].equals("num")){
               System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Cannot assign '" + visit(ctx.expr(i)).split(";")[0] + "' value to either posx, posy or thickness.");
               System.exit(1);
            }
         }
      }
      return "pen";
   }

   //DONE GONCALO
   @Override
   public String visitExprAction(pdrawParser.ExprActionContext ctx) {
      return visit(ctx.action());
   }


   // BUTUC DONE
   @Override public String visitExprFunction(pdrawParser.ExprFunctionContext ctx) {
      if (!vars.containsKey(ctx.ID().getText())){
         System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": variable " + ctx.ID().getText() + " is not declared." );
         System.exit(1);
      }
      else {
         if (!vars.get(ctx.ID().getText()).getType().split(";")[vars.get(ctx.ID().getText()).getType().split(";").length - 1].equals("function")){
            System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": variable " + ctx.ID().getText() + " must be of type function.");
            System.exit(1);
         }
         else {
            if (vars.get(ctx.ID().getText()).getType().split(";").length != 1){
               String[] params = vars.get(ctx.ID().getText()).getType().split(";")[1].split(" ");
               if (!params[0].equals("null")){
                  if (params.length != ctx.expr().size()){
                     System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": number of arguments inputed don't match the number of function parameters.");
                     System.exit(1);
                  }
                  for (int i = 0; i<params.length; i++){
                     if(!params[i].equals(visit(ctx.expr(i)).split(";")[0])){
                        System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": arguments inputed don't match function parameters.");
                        System.exit(1);
                     }
                  }
               }
            }
         }
      }
      return vars.get(ctx.ID().getText()).getType();
   }


   // DONE BUTUC
   @Override
   public String visitExprNumber(pdrawParser.ExprNumberContext ctx) {
      try {
         Integer.parseInt(ctx.getText());
         return "num;int;" + ctx.getText();
      } catch (NumberFormatException e) {
         return "num;float;" + ctx.getText();
      }
   }
   // DONE BUTUC
   @Override
   public String visitExprStatus(pdrawParser.ExprStatusContext ctx) {
      return "status";
   }

   // DONE BUTUC
   @Override
   public String visitExprID(pdrawParser.ExprIDContext ctx) {
      if ((vars.get(ctx.ID().getText()) == null) && (temp_vars.get(ctx.ID().getText()) == null)){
         return "null";
      }
      else {
         if (temp_vars.containsKey((ctx.ID().getText()))){
            return temp_vars.get(ctx.ID().getText()).getType() + ";ID";
         } else if (vars.containsKey(ctx.ID().getText())){
            return vars.get(ctx.ID().getText()).getType() + ";ID";
         }
      }
      return "";
   }

   // DONE BUTUC
   @Override
   public String visitExprPlusMinus(pdrawParser.ExprPlusMinusContext ctx) {
      if ((visit(ctx.expr(0)).split(";")[0]).equals("num") && visit(ctx.expr(0)).split(";")[0].equals(visit(ctx.expr(1)).split(";")[0])) {
         if (visit(ctx.expr(0)).split(";").length > 2 && visit(ctx.expr(1)).split(";").length > 2 ){
            try {
               Integer int1 = Integer.parseInt(visit(ctx.expr(0)).split(";")[2]);
               Integer int2 = Integer.parseInt(visit(ctx.expr(1)).split(";")[2]);
               return "num;int;" + (int1 + int2);
            } catch (NumberFormatException e) {
               return "num;float;"
                     + (Double.parseDouble(visit(ctx.expr(0)).split(";")[2]) + Double.parseDouble(visit(ctx.expr(1)).split(";")[2]));
            }
         }
      } else if (visit(ctx.expr(0)).split(";")[0].equals("degree") && visit(ctx.expr(0)).split(";")[0].equals(visit(ctx.expr(1)).split(";")[0])) {
         return "degree";
      } else if (visit(ctx.expr(0)).equals("string") && visit(ctx.expr(0)).equals(visit(ctx.expr(1)))) {
         if (ctx.op.getText().equals("-")){
            System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Cannot subtract strings.");
            System.exit(1);
         }
         return "string";
      } else {
         if (ctx.op.getText().equals("+")) {
            System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Cannot add '" + visit(ctx.expr(0)).split(";")[0] + "' value with '"
                  + visit(ctx.expr(1)).split(";")[0] + "' value.");
            System.exit(1);
         } else if (ctx.op.getText().equals("-")) {
            System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Cannot subtract '" + visit(ctx.expr(0)).split(";")[0] + "' value with '"
                  + visit(ctx.expr(1)).split(";")[0] + "' value.");
            System.exit(1);
         }
      }
      return "";
   }

   // DONE BUTUC
   @Override
   public String visitExprExponent(pdrawParser.ExprExponentContext ctx) {
      if ((visit(ctx.expr(0)).split(";")[0]).equals("num") && (visit(ctx.expr(1)).split(";")[0]).equals("num")) {
         return "num";
      }
      else if (((visit(ctx.expr(0)).split(";")[0]).equals("degree"))) {
         System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Cannot do exponent operation with '" + visit(ctx.expr(0)) + "' value.");
         System.exit(1);
      } else if (!((visit(ctx.expr(0)).split(";")[0]).equals("num") || visit(ctx.expr(0)).equals("degree"))) {
         System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Cannot do exponent operation with '" + visit(ctx.expr(0)) + "' value.");
         System.exit(1);
      } else if (!((visit(ctx.expr(1)).split(";")[0]).equals("num"))) {
         if (visit(ctx.expr(1)).equals("degree")) {
            System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Cannot exponentiate num by degree");
            System.exit(1);
         } else {
            System.err.println("[line " + ctx.start.getLine() + "]" + "[Semantic Error]" + ": Cannot do exponent operation with '" + visit(ctx.expr(1)) + "' value.");
            System.exit(1);
         }
      }
      return "";
   }
}