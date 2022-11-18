import java.io.FileOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class StringTemplate extends pdrawBaseVisitor<String> {

   private HashMap<String, String> values = new HashMap<>();
   private HashMap<String, String> types = new HashMap<>();
   private OutputStreamWriter sw;
   private int loopCount = 0;

   public StringTemplate() throws IOException {
      FileOutputStream ext = new FileOutputStream("exec.py");
      sw = new OutputStreamWriter(ext);
      File functions = new File("functions.py");
      Scanner sc1 = new Scanner(functions);
      while (sc1.hasNextLine())
         sw.write(sc1.nextLine() + "\n");
      sc1.close();
   }

   @Override
   public String visitProgram(pdrawParser.ProgramContext ctx) {
      try {
         for (int i = 0; i < ctx.stat().size(); i++)
            sw.write(visit(ctx.stat(i)) + '\n');
         sw.write("\n\nturtle.done()");
         sw.close();
      } catch (IOException e) {
         e.printStackTrace();
      }
      return null;

   }

   @Override
   public String visitComment(pdrawParser.CommentContext ctx) {
      return "";
   }

   @Override
   public String visitImport(pdrawParser.ImportContext ctx) {
      File pens = new File(ctx.STRING().getText().replace("\"", ""));
      if (pens.exists())
         return convertID(ctx.ID().getText()) + "=pensMain.main('" + ctx.STRING().getText().replace("\"", "") + "')";
      else {
         System.err.println("IO Error: File " + ctx.STRING().getText() + " not found (No such file or directory)");
         System.exit(1);
      }
      return null;
   }

   @Override
   public String visitDoAction(pdrawParser.DoActionContext ctx) {
      String id = convertID(ctx.ID().getText());
      String s = visit(ctx.action());
      return action(id, s);
   }

   @Override
   public String visitPenCommand(pdrawParser.PenCommandContext ctx) {
      if (ctx.op.getText().equals("lower"))
         return convertID(ctx.ID().getText()) + "['status']='down'";
      else
      return convertID(ctx.ID().getText()) + "['status']='up'";
   }

   @Override
   public String visitPenMoveTo(pdrawParser.PenMoveToContext ctx) {
      return "move(#,'to'," + visit(ctx.expr(0)) + "," + visit(ctx.expr(1)) + ")";
   }

   @Override
   public String visitPenMoveBy(pdrawParser.PenMoveByContext ctx) {
      String s = "move(#,'by'," + visit(ctx.expr(0)) + ",";
      try {
         s += visit(ctx.expr(1));
      } catch (Exception e) {
         s += "#['heading']";
      }
      return s + ")";
   }

   @Override
   public String visitPenRotateBY(pdrawParser.PenRotateBYContext ctx) {
      return "['heading']+=" + visit(ctx.expr());
   }

   @Override
   public String visitPenChangeTo(pdrawParser.PenChangeToContext ctx) {
      String s = "";
      for (int i = 0; i < ctx.chg().size(); i++) {
         s += "#" + visit(ctx.chg(i)) + "\n";
      }
      return s;
   }

   @Override
   public String visitIfElifElseStat(pdrawParser.IfElifElseStatContext ctx) {
      loopCount += 1;
      String s = "if " + visit(ctx.expr()) + ":\n";
      for (int i = 0; i < ctx.stat().size(); i++) {
         for (int e = 0; e < loopCount; e++)
            s += "\t";
         s += visit(ctx.stat(i)) + "\n";
      }
      loopCount -= 1;
      try {
         for (int i = 0; i < ctx.elif().size(); i++)
            s += visit(ctx.elif(i)) + "\n";
      } catch (Exception e) {
      }
      try {
         s += visit(ctx.els()) + "\n";
      } catch (Exception e) {
      }
      return s;
   }

   @Override
   public String visitForLoop(pdrawParser.ForLoopContext ctx) {
      String s = "";
      loopCount += 1;
      try {
         s = "for " + convertID(ctx.ID().getText()) + " in " + ctx.r.getText() + "(" + visit(ctx.expr()) + "):\n";
         for (int i = 0; i < ctx.stat().size(); i++) {
            for (int e = 0; e < loopCount; e++)
               s += "\t";
            s += visit(ctx.stat(i)) + "\n";
         }
      } catch (Exception ex) {
         s = "for " + convertID(ctx.ID().getText()) + " in " + visit(ctx.expr()) + ":\n";
         for (int i = 0; i < ctx.stat().size(); i++) {
            for (int e = 0; e < loopCount; e++)
               s += "\t";
            s += visit(ctx.stat(i)) + "\n";
         }
      }
      loopCount -= 1;
      return s;
   }

   @Override
   public String visitWhileLoop(pdrawParser.WhileLoopContext ctx) {
      loopCount += 1;
      String s = "while " + visit(ctx.expr()) + ":\n";
      for (int i = 0; i < ctx.stat().size(); i++) {
         for (int e = 0; e < loopCount; e++)
            s += "\t";
         s += visit(ctx.stat(i)) + "\n";
      }
      loopCount -= 1;
      return s;
   }

   @Override
   public String visitFunction(pdrawParser.FunctionContext ctx) {
      String s = "def f_" + ctx.ID().getText() + " (";
      types.put("f_" + ctx.ID().getText(), "function");
      for (int i = 0; i < ctx.arg().size(); i++) {
         s += visit(ctx.arg(i));
      }
      s += "):\n";
      loopCount += 1;
      for (int i = 0; i < ctx.stat().size(); i++) {
         for (int e = 0; e < loopCount; e++)
            s += "\t";
         s += visit(ctx.stat(i)) + "\n";
      }
      try {
         s += "\treturn " + visit(ctx.expr()) + "\n";
      } catch (Exception e) {
      }
      loopCount -= 1;
      return s;
   }

   @Override
   public String visitPrint(pdrawParser.PrintContext ctx) {
      return "print(" + visit(ctx.expr()) + ")";
   }

   @Override
   public String visitArrayAdd(pdrawParser.ArrayAddContext ctx) {
      return convertID(ctx.ID().getText()) + ".append(" + visit(ctx.expr()) + ")";
   }

   @Override
   public String visitArrayRemove(pdrawParser.ArrayRemoveContext ctx) {
      return convertID(ctx.ID().getText()) + ".pop(" + visit(ctx.expr()) + ")";
   }

   @Override
   public String visitIDID(pdrawParser.IDIDContext ctx) {
      String id = convertID(ctx.ID(0).getText());
      String id2 = convertID(ctx.ID(1).getText());
      String s = values.get(id2);
      return action(id, s);
   }

   @Override
   public String visitGoExpr(pdrawParser.GoExprContext ctx) {
      return visit(ctx.expr());
   }

   @Override
   public String visitElif(pdrawParser.ElifContext ctx) {
      loopCount += 1;
      String s = "elif " + visit(ctx.expr()) + ":\n";
      for (int i = 0; i < ctx.stat().size(); i++) {
         for (int e = 0; e < loopCount; e++)
            s += "\t";
         s += visit(ctx.stat(i)) + "\n";
      }
      loopCount -= 1;
      return s;
   }

   @Override
   public String visitChg(pdrawParser.ChgContext ctx) {
      return "['" + ctx.prop.getText().split("-")[1] + "']=" + visit(ctx.expr());
   }

   @Override
   public String visitArg(pdrawParser.ArgContext ctx) {
      String s = convertID(ctx.ID().getText()) + ":";
      types.put(convertID(ctx.ID().getText()), ctx.TYPE().getText());
      switch (ctx.TYPE().getText()) {
         case ("num"):
            return s + "float";
         case ("string"):
            return s + "str";
         case ("pen"):
            return s + "dict";
         case ("boolean"):
            return s + "bool";
         case ("degree"):
            return s + "float";
         case ("colour"):
            return s + "string";
         case ("status"):
            return s + "string";
         case ("pattern"):
            return s + "string";
         default:
            return s + "list";
      }
   }

   @Override
   public String visitEls(pdrawParser.ElsContext ctx) {
      loopCount += 1;
      String s = "else:\n";
      for (int i = 0; i < ctx.stat().size(); i++) {
         for (int e = 0; e < loopCount; e++)
            s += "\t";
         s += visit(ctx.stat(i)) + "\n";
      }
      loopCount -= 1;
      return s;
   }

   @Override
   public String visitDeclaration(pdrawParser.DeclarationContext ctx) {
      String id = convertID(ctx.ID().getText());
      types.put(id, ctx.TYPE().getText());
      String s = id;
      try {
         values.put(s, visit(ctx.expr()));
         s += " = " + visit(ctx.expr());
      } catch (Exception e) {
         if (ctx.TYPE().getText().equals("pen"))
            return s
                  + "={ 'status':'up', 'posx':0, 'posy':0, 'heading':0, 'colour':'black', 'thickness':2, 'pattern':'solid'}";
         else if (ctx.TYPE().getText().startsWith("array"))
            return s + "=[]";
      }
      if (types.get(id).equals("action"))
         return "#" + s;
      else
         return s;
   }

   @Override
   public String visitAttribution(pdrawParser.AttributionContext ctx) {
      String s = convertID(ctx.ID().getText());
      values.put(s, visit(ctx.expr()));
      if (types.get(s).equals("action"))
         return "#" + s + " = " + visit(ctx.expr());
      else
         return s + " = " + visit(ctx.expr());
   }

   @Override
   public String visitExprPattern(pdrawParser.ExprPatternContext ctx) {
      return "'"+ctx.PATTERN().getText()+"'";
   }

   @Override
   public String visitExprEquals(pdrawParser.ExprEqualsContext ctx) {
      return visit(ctx.expr(0)) + "==" + visit(ctx.expr(1));
   }

   @Override
   public String visitExprString(pdrawParser.ExprStringContext ctx) {
      return ctx.STRING().getText();
   }

   @Override
   public String visitExprLength(pdrawParser.ExprLengthContext ctx) {
      return "len(" + convertID(ctx.ID().getText()) + ")";
   }

   @Override
   public String visitExprInput(pdrawParser.ExprInputContext ctx) {
      String s = "";
      if(ctx.op.getText().equals("input num"))
         s ="intOrFloat(float(input(#)))";
      else
         s = "input(#)";
      try {
         return s.replace("#",ctx.STRING().getText());
      }
      catch (Exception e) {
         return s.replace("#", "");
      }
   }

   @Override
   public String visitExprBoolean(pdrawParser.ExprBooleanContext ctx) {
      if (ctx.BOOLEAN().getText().equals("true"))
         return "True";
      else
         return "False";
   }

   @Override
   public String visitExprColour(pdrawParser.ExprColourContext ctx) {
      String s = ctx.COLOUR().getText();
      if (s.startsWith("rgb")) {
         String[] list = s.split(" ");
         return "(" + list[1] + "," + list[2] + "," + list[3] + ")";
      } else
         return "'" + s + "'";
   }

   @Override
   public String visitExprParenthesis(pdrawParser.ExprParenthesisContext ctx) {
      return "(" + visit(ctx.expr()) + ")";
   }

   @Override
   public String visitExprNot(pdrawParser.ExprNotContext ctx) {
      return "not " + visit(ctx.expr());
   }

   @Override
   public String visitExprAndOR(pdrawParser.ExprAndORContext ctx) {
      return visit(ctx.expr(0)) + " " + ctx.op.getText() + " " + visit(ctx.expr(1));
   }

   @Override
   public String visitExprArray(pdrawParser.ExprArrayContext ctx) {
      String s = "[";
      for (int i = 0; i < ctx.expr().size(); i++) {
         s += visit(ctx.expr(i)) + ",";
      }
      return s + "]";
   }

   @Override
   public String visitExprMultDivMod(pdrawParser.ExprMultDivModContext ctx) {
      return visit(ctx.expr(0)) + ctx.op.getText() + visit(ctx.expr(1));
   }

   @Override
   public String visitExprGet(pdrawParser.ExprGetContext ctx) {
      return convertID(ctx.ID().getText()) + "[" + visit(ctx.expr()) + "]";
   }

   @Override
   public String visitExprDegree(pdrawParser.ExprDegreeContext ctx) {
      return ctx.DEGREE().getText().split("º")[0];
   }

   @Override
   public String visitExprMinus(pdrawParser.ExprMinusContext ctx) {
      return "-" + visit(ctx.expr());
   }

   @Override
   public String visitExprPen(pdrawParser.ExprPenContext ctx) {
      String s = "{ 'status':";
      try {
         s += "'" + ctx.STATUS(0).getText() + "'";
      } catch (Exception e) {
         s += "'up'";
      }
      s += ", 'posx':";
      try {
         s += visit(ctx.posx);
      } catch (Exception e) {
         s += "0";
      }
      s += ", 'posy':";
      try {
         s += visit(ctx.posy);
      } catch (Exception e) {
         s += "0";
      }
      s += ", 'heading':";
      try {
         s += ctx.DEGREE(0).getText().split("º")[0];
      } catch (Exception e) {
         s += "0";
      }
      s += ", 'colour':";
      try {
         s += getColour(ctx.COLOUR(0).getText());
      } catch (Exception e) {
         s += "'black'";
      }
      s += ", 'thickness':";
      try {
         s += visit(ctx.thickness);
      } catch (Exception e) {
         s += "2";
      }
      s += ", 'pattern':";
      try {
         s += "'"+ctx.PATTERN(0).getText()+"'";
      } catch (Exception e) {
         s += "'solid'";
      }
      s += "}";
      return s;
   }

   @Override
   public String visitExprAction(pdrawParser.ExprActionContext ctx) {
      return visit(ctx.action());
   }

   @Override
   public String visitExprFunction(pdrawParser.ExprFunctionContext ctx) {
      String s = "f_" + ctx.ID().getText() + "(" + visit(ctx.expr(0));
      for (int i = 1; i < ctx.expr().size(); i++) {
         s += "," + visit(ctx.expr(i));
      }
      return s + ")";
   }

   @Override
   public String visitExprNumber(pdrawParser.ExprNumberContext ctx) {
      return ctx.NUMBER().getText();
   }

   @Override
   public String visitExprStatus(pdrawParser.ExprStatusContext ctx) {
      return "'" + ctx.STATUS().getText() + "'";
   }

   @Override
   public String visitExprID(pdrawParser.ExprIDContext ctx) {
      return convertID(ctx.ID().getText());
   }

   @Override
   public String visitExprPlusMinus(pdrawParser.ExprPlusMinusContext ctx) {
      return visit(ctx.expr(0)) + ctx.op.getText() + visit(ctx.expr(1));
   }

   @Override
   public String visitExprExponent(pdrawParser.ExprExponentContext ctx) {
      return visit(ctx.expr(0)) + "**" + visit(ctx.expr(1));
   }

   public String getColour(String s) {
      if (s.startsWith("rgb")) {
         String[] list = s.split(" ");
         return "(" + list[1] + "," + list[2] + "," + list[3] + ")";
      } else
         return "'" + s + "'";
   }

   public String convertID(String s) {
      return "var_" + s;
   }

   public String action(String id, String s) {
      if (types.get(id).startsWith("array")) {
         String rtn = "for fo_e in " + id + ":\n";
         if (s.startsWith("move(")) {
            String[] list = s.split("#");
            return rtn + "\t" + list[0] + "fo_e" + list[1];
         } else if (s.startsWith("#")) {
            return rtn + s.replace("#", "\tfo_e");
         } else
            return rtn + "\tfo_e" + s;
      } else {
         if (s.startsWith("move("))
            return s.replace("#", id);
         else if (s.startsWith("#")) {
            return s.replace("#", id);
         } else
            return id + s;
      }
   }
}