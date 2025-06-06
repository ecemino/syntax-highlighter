import javax.swing.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        String source = """
                int main(int a){ 
                if(a <5){
                a = a+1;}}
           
            """;

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();
        for (Token token : tokens) {
            System.out.println(token);
        }

        System.out.println("\n--- Parsing ---");
        Parser parser = new Parser(tokens);
        boolean success = parser.parseFunc();
        System.out.println(success ? "Parsing successful!" : "Parsing failed.");

        SwingUtilities.invokeLater(() -> new Highlighter().start());
    }
}