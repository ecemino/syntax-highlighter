public class Token {
    public final String type;
    public final String value;
    public final int startPos;
    public final int len;

    public Token(String type, String value, int startPos, int len){
        this.type = type;
        this.value = value;
        this.startPos = startPos;
        this.len = len;
    }

    @Override
    public String toString(){
        return type + ": " + value;
    }

    public String getValue(){
        return value;
    }

    public String getType(){
        return type;
    }



}
