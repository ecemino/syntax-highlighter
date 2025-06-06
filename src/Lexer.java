import java.util.*;

public class Lexer {
    private final String input;
    private int pos = 0;
    private final List<Token> tokens = new ArrayList<>();


    private static final Set<String> keywords = Set.of("int", "if", "else", "for", "while");
    private static final Set<String> operators = Set.of("+", "-", "*", "/", "=", "==", "!=", "<", "<=", ">", ">=");
    private static final Set<Character> symbols = Set.of('(', ')', '{', '}', ';', ',');

    public Lexer (String input) {
        this.input = input;
    }

    public List<Token> tokenize(){
        while (pos <input.length()){
            char current = input.charAt(pos);

            if(isWhiteSpace(current)){
                tokens.add(readWhiteSpace());
            }else if (Character.isLetter(current)){
                tokens.add(readIdentifierOrKeyword());
            }else if (Character.isDigit(current)){
                tokens.add(readNumber());
            }else if(symbols.contains(current)){
                tokens.add(new Token("SYMBOL", Character.toString(current),pos,1));
                pos++;
            }else if(isOpStart(current)){
                tokens.add(readOperator());
            }else{
                System.out.println("Syntax error: " + current);
                pos++;
            }
        }

        return tokens;
    }

    private boolean isWhiteSpace (char c) {
        return c == ' ' || c == '\t' || c == '\n' || c == '\r';
    }

    private Token readIdentifierOrKeyword(){
        int start = pos;
        StringBuilder sb = new StringBuilder();
        while(pos < input.length() && Character.isLetterOrDigit(input.charAt(pos))){
            sb.append(input.charAt(pos));
            pos++;
        }

        String word = sb.toString(); //overriden by custom func (toString)


        if(keywords.contains(word))
            return new Token ("KEYWORD", word,start, pos - start );
        else
            return new Token("IDENTIFIER", word,start, pos - start);
    }

    private Token readNumber(){
        int start = pos;
        StringBuilder sb = new StringBuilder();
        while (pos < input.length() && Character.isDigit(input.charAt(pos))){
            sb.append(input.charAt(pos));
            pos++;
        }

        return new Token("NUMBER", sb.toString(), start, pos - start);
    }

    private Token readOperator(){
        int start = pos;
        StringBuilder sb= new StringBuilder();
        sb.append(input.charAt(pos));
        pos++;

        if(pos < input.length()){
            char next = input.charAt(pos);
            String potential = sb.toString() + next;
            if(operators.contains(potential)) {
                sb.append(next);
                pos++;
            }
        }

        return new Token("OPERATOR", sb.toString(), start, pos - start);
    }

    private Token readWhiteSpace(){
        int start = pos;

        while(pos < input.length() && isWhiteSpace(input.charAt(pos))){
           pos++;
        }
        String whitespace = input.substring(start,pos);
        return new Token("WHITESPACE",whitespace,start, pos - start);
    }

    private boolean isOpStart(char c){
        for(String op : operators) {
            if (op.charAt(0) == c) return true;
        }
        return false;
    }}
