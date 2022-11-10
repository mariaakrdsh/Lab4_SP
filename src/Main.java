import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Main {


    public static List<FoundLexem> lexems = new ArrayList<FoundLexem>();
    public static String currToken = "undefined";
    public static String currLexem = "undefined";
    public static List<String> keywordList = Arrays.asList("let", "var",  "associatedtype", "class",
            "deinit", "enum", "extension", "fileprivate", "func", "import", "init", "inout", "internal",
            "operator", "private", "protocol", "public", "static", "struct", "subscript", "typealias",
            "break", "case", "continue", "default", "defer", "do", "else", "fallthrough", "for", "guard",
            "if", "repeat", "return", "switch", "where", "while", "as", "catch", "false", "is", "nil",
            "rethrows", "super", "self", "Self", "throw", "throws", "true", "try", "#available", "#column",
            "#else", "#elseif", "#endif", "#file", "#function", "#if", "#line", "#sourceLocation", "dynamic",
            "#imageLiteral", "#fileliteral", "#colorLiteral", "#selector", "associativity", "convenience",
            "didSet", "final", "get", "infix", "indirect", "lazy", "left", "mutating", "none", "nonmutating",
            "optional", "override", "postfix", "precedence", "prefix", "Protocol", "required", "right", "set",
            "Type", "unowned", "weak", "willSet");
    public static HashMap<String, String> regexDictionary = new HashMap<String, String>();
    public static FileWriter writer;
    static {
        try {
            writer = new FileWriter("output.txt", false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String GetSpaces(int length){
        String res = "";
        for (int i=0; i<length; i++){
            res += " ";
        }
        return res;
    }

    public static void main(String[] args) throws IOException {
        String text = Files.readString(Paths.get("input.txt")); //зчитуємо файл
        regexDictionary.put("NUMBER", "\\d+(\\.\\d+)?");
        regexDictionary.put("IDENTIFIER", "[A-Za-z_][A-Za-z_0-9.]*");
        regexDictionary.put("OPERATOR", "[+-/*//%=]");
        regexDictionary.put("STRING_CONSTANT", "(\".*\") | (\'.*\')");
        regexDictionary.put("BRACKET", "[/{/}/[/]/(/)]");
        regexDictionary.put("DELIMITER", "[,:;]");
        regexDictionary.put("ESCAPE_CHAR", "[\n\t\r]");
        regexDictionary.put("ERROR", "[А-Яа-я_][А-Яа-я_]*");
        String commentRegex = "#.*[\n\r\t]";
        String commentString = "(\".*\")";

        try{
            Pattern regexComment = Pattern.compile(commentRegex);
            Matcher regexCommentMatcher = regexComment.matcher(text);
            while (regexCommentMatcher.find()) {
                String  matchText = regexCommentMatcher.group();
                int matchIndex = regexCommentMatcher.start();
                int matchEnd = regexCommentMatcher.end();
                lexems.add(new FoundLexem(matchIndex, matchText.substring(0, matchText.length()), "COMMENT"));
                text = text.substring(0, matchIndex) + GetSpaces(matchText.length())+ text.substring(matchEnd + 1);
            }

            Pattern regexString = Pattern.compile(commentString);
            Matcher regexStringMatcher = regexString.matcher(text);
            while (regexStringMatcher.find()) {
                String  matchText = regexStringMatcher.group();
                int matchIndex = regexStringMatcher.start();
                int matchEnd = regexStringMatcher.end();
                lexems.add(new FoundLexem(matchIndex, matchText.substring(0, matchText.length()), "STRING_CONST"));
                text = text.substring(0, matchIndex) + GetSpaces(matchText.length())+ text.substring(matchEnd + 1);
            }

            for( String keyword: keywordList){
                boolean notBreak = true;
                while(notBreak){
                    int index = text.indexOf(keyword);
                    if (index != -1){
                        lexems.add(new FoundLexem(index, text.substring(index, index + keyword.length()), "KEYWORD"));
                        text = text.substring(0, index) + GetSpaces(keyword.length())+ text.substring(index + keyword.length());
                    } else {
                        notBreak = false;
                    }
                }
            }

            for (var l : regexDictionary.entrySet()){ //проходимось по всьому що лишилось
                String type = l.getKey();
                Pattern regex = Pattern.compile(l.getValue());
                Matcher regexMatcher = regex.matcher(text);
                while (regexMatcher.find()) {
                    String  matchText = regexMatcher.group();
                    int mathchIndex = regexMatcher.start();
                    lexems.add(new FoundLexem(mathchIndex, matchText, type));

                }
            }

        } catch (Exception l) {
            l.printStackTrace();
        }
        Collections.sort(lexems);
        for (FoundLexem lexem : lexems){ // вивод
            writer.write(lexem.Lexem + " - " + lexem.Type+ "\n");
        }
        System.out.println("Program is working correctly, you can see the result in output.txt file:)");
        writer.close();
    }
}