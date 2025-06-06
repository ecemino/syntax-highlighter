import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int pos = 0;
    private String lastError = "No syntax errors.";

    public Parser(List<Token> tokens){
        this.tokens = tokens;
    }

    public String getLastError() {
        return lastError;
    }

    private boolean error(String msg){
        Token token = peek();
        lastError = "Syntax error at " + (token != null ? token.toString() : "end of input") + ": " + msg;
        System.out.println(lastError);  // Optional: keep console logging too
        return false;
    }

    private Token peek(){
        return pos < tokens.size() ? tokens.get(pos) : null;
    }

    private Token advance(){
        return pos < tokens.size() ? tokens.get(pos++) : null;
    }

    private boolean matchValue(String expectedValue){
        skipWhiteSpace();
        Token token = peek();
        if(token != null && token.getValue().equals(expectedValue)){
            advance();
            return true;
        }
        return false;
    }

    private boolean matchType(String expectedType){
        skipWhiteSpace();
        Token token = peek();
        if(token != null && token.getType().equals(expectedType)){
            advance();
            return true;
        }
        return false;
    }

    public boolean parseFunc(){
        if(matchType("KEYWORD") && matchType("IDENTIFIER") && matchValue("(")){
            parseArgList();
            if(!matchValue(")")) return  error("1expected ')'");
            if(!parseCompStmt()) return error("expected compound statement block");
            return true;
        }
        return false;
    }

    private void parseArgList(){
        if(peek() != null && peek().getType().equals("KEYWORD")){
            parseArg();
            while(matchValue(",")){
                parseArg();
            }
        }
    }

    private void parseArg(){
        if(!matchType("KEYWORD") || !matchType("IDENTIFIER")){
            error("invalid argument");
        }
    }


    private boolean parseFactor(){

        if(matchType("NUMBER") || matchType("IDENTIFIER")){
            return true;
        }else if(matchValue("(")){
            boolean expr = parseExpr();
            if(!matchValue(")")) return error("2expected ')'");
            return expr;
        }
        return error("expected number, identifier, or parenthesized expression");
    }

    private boolean parseTerm(){
        if(!parseFactor())return false;
        while(peek() != null && peek().getValue().equals("*") || peek().getValue().equals("/")){
            advance();
            if(!parseFactor()) return false;
        }
        return true;
    }

    private boolean parseExpr(){
        if(!parseTerm()) return false;
        while(peek() != null && (peek().getValue().equals("+")) || (peek().getValue().equals("-"))){
            advance();
            if(!parseTerm()) return false;
        }
        return true;
    }

    private boolean parseStmt(){
        if(matchValue("if")){
            if(!matchValue("(")) return error("expected '(' after 'if'");
            if(!parseCondition()) return false;
            if(!matchValue(")")) return error("3expected ')'");
            if(!parseStmt()) return false;
            if(matchValue("else")) return parseStmt();

            return true;
        }else if (matchValue("while")){
            if(!matchValue("(")) return error("expected '(' after 'while'");
            if(!parseCondition()) return false;
            if(!matchValue(")")) return error("4expected ')'");
            if(!parseStmt()) return false;
            return true;

        }else if(matchValue("{")){
            pos--;
            return parseCompStmt();
        }else if(matchValue("int")){
            return parseDec();

        }else if(matchType("IDENTIFIER")){
            if(!matchValue("=")) return error("expected '=' after identifier");
            if(!parseExpr()) return error("invalid expression");
            if(!matchValue(";")) return error("expected ';' after expression");
            return true;
        }
        return error("invalid statement");
    }

    private boolean parseCompStmt(){
        if(!matchValue("{")) return false;
        parseStmtList();
        if(!matchValue("}")) return error("expected '}'");
        return true;
    }

    private void parseStmtList(){
        while(isStartOfStmt()){
            if(!parseStmt()) break;
        }
    }

    private boolean isStartOfStmt(){
        Token token = peek();
        if(token == null) return false;
        String val = token.getValue();
        return val.equals("if") || val.equals("while") || val.equals("{") ||
                token.getType().equals("IDENTIFIER") ||
                (token.getType().equals("KEYWORD") && (val.equals("int")));
    }

    private boolean parseDec(){
        if(!matchType("IDENTIFIER")) return error("expected identifier in declaration");
        while (matchValue(",")){
            if(!matchType("IDENTIFIER")) return error("expected identifier after ','");
        }
        if(!matchValue("=")) return error("expected '=' after identifier");
        if(!matchType("NUMBER")) return error("expected number after '='");

        if(!matchValue(";")) return error("expected ';' at the end of declaration");
        return true;
    }

    private boolean parseCondition(){
        if(!parseTerm()) return false;

        while(peek() != null && (peek().getValue().equals("<")) || (peek().getValue().equals(">")) || (peek().getValue().equals("==")) || (peek().getValue().equals("!="))
                                                                || (peek().getValue().equals("<=")) || (peek().getValue().equals(">="))){
            advance();
            if(!parseTerm()) return false;
        }
        return true;

    }


    private void skipWhiteSpace(){
        while(peek() != null && peek().getType().equals("WHITESPACE")){
            advance();
        }
    }
}
