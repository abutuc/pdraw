import java.io.FileInputStream;
import java.io.IOException;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class pdrawMain {
   public static void main(String[] args) {
      try {
         // create a CharStream that reads from standard input:
         CharStream input;
         try {
            if (!args[0].endsWith(".pdraw")) {
               System.err.println("Error: File extension must be '.pdraw'!");
               System.exit(1);
            }
            input = CharStreams.fromStream(new FileInputStream(args[0]));
         }
         catch(Exception e){
            input = CharStreams.fromStream(System.in);
         }
         // create a lexer that feeds off of input CharStream:
         pdrawLexer lexer = new pdrawLexer(input);
         // create a buffer of tokens pulled from the lexer:
         CommonTokenStream tokens = new CommonTokenStream(lexer);
         // create a parser that feeds off the tokens buffer:
         pdrawParser parser = new pdrawParser(tokens);
         // replace error listener:
         //parser.removeErrorListeners(); // remove ConsoleErrorListener
         //parser.addErrorListener(new ErrorHandlingListener());
         // begin parsing at program rule:
         ParseTree tree = parser.program();
         if (parser.getNumberOfSyntaxErrors() == 0) {
            // print LISP-style tree:
            // System.out.println(tree.toStringTree(parser));
            StringTemplate visitor0 = new StringTemplate();
            visitor0.visit(tree);
         }
      }
      catch(IOException e) {
         e.printStackTrace();
         System.exit(1);
      }
      catch(RecognitionException e) {
         e.printStackTrace();
         System.exit(1);
      }
   }
}
