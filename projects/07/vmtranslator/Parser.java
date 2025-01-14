
import java.util.Scanner;

public class Parser {
    private Scanner fileReader;
    private String currentLine;

    public Parser(Scanner scanner){
        fileReader = scanner;
    }

    public boolean hasNextLine(){
        return fileReader.hasNextLine();

    }

    public void advance(){
        if (hasNextLine()){
            currentLine = fileReader.nextLine();
        }

        // Handles comments at the end of a line
        int commentIndex = currentLine.indexOf('/');
        if (commentIndex > 0){
            currentLine = currentLine.substring(0, commentIndex);
        } else if (commentIndex == 0 || currentLine.replaceAll("\\s+","").equals("")){
            advance();
        }

        currentLine = currentLine.strip(); // strips whitespace on the front and end
    }

    public String testReturn(){
        return currentLine;
    }

    public CommandType commandType(){
        String command = currentLine.split(" ")[0];
        switch (command){
            case "push":
                return CommandType.C_PUSH;
            case "pop":
                return CommandType.C_POP;
            default:
                return CommandType.C_ARITHMETIC;
        }
    }

    public String arg1(){
        CommandType cType = commandType();
        
        if (cType == CommandType.C_PUSH || cType == CommandType.C_POP){
            return currentLine.split(" ")[1];
        } else if (cType == CommandType.C_ARITHMETIC){
            return currentLine;
        }
        return "";
    }

    public int arg2(){
        CommandType cType = commandType();
        if (cType == CommandType.C_POP || cType == CommandType.C_PUSH){
            return Integer.valueOf(currentLine.split(" ")[2]);
        }
        return 0;
    }
}
