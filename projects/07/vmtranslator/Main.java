import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.File;

public class Main {
    private static CodeWriter codeWriter;

    public static void main(String[] args){
        
        if (args.length != 1) {
            System.out.println("No file path provided or multiple file paths provided.");
            return;
        }
        
        File inputFile = new File(args[0]);
        
        // Creates a CodeWriter object and translates the vm file or files if the input is a directory.
        codeWriter = new CodeWriter(inputFile);
        if (inputFile.isDirectory()){
            iterateFiles(inputFile.listFiles());
        } else if (inputFile.getName().endsWith(".vm")) {
            translate(inputFile);
        }
        codeWriter.close();
    }

    // Iterates over every file in the given directory and translates the vm files.
    public static void iterateFiles(File[] inputDirectory){
        for (File file: inputDirectory){
            if (file.isDirectory()){
                iterateFiles(file.listFiles());
            } else if (file.getName().endsWith(".vm")){
                translate(file);
            }
        }
    }

    public static void translate(File inputFile){

        // Creates a parser
        Scanner fileScanner = null;
        try{
            fileScanner = new Scanner(inputFile);
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }
        Parser parser = new Parser(fileScanner);
        codeWriter.setFileName(inputFile.getAbsolutePath());

        // Iterates over every line and translates the vm code
        while (parser.hasNextLine()){
            parser.advance();
            
            CommandType commandType = parser.commandType();
            if (commandType == CommandType.C_POP || commandType == CommandType.C_PUSH){
                codeWriter.writePushPop(commandType, parser.arg1(), parser.arg2());
            } else if (commandType == CommandType.C_ARITHMETIC){
                codeWriter.writeArithmetic(parser.arg1());
            }
        }
        codeWriter.close();
    }

    
}
