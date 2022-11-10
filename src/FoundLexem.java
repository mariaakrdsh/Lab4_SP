public class FoundLexem implements Comparable<FoundLexem>{
    public int Pos;
    public String Lexem;
    public String Type;

    FoundLexem(int pos, String lexem, String type){
        this.Lexem = lexem;
        this.Pos = pos;
        this.Type = type;
    }
    @Override
    public int  compareTo(FoundLexem f){
        return this.Pos - f.Pos;
    }
}