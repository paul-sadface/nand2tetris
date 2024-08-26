import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.*;

public class Parser {
    public static int lineNum;
    public int numStaticVars;
    private File file;
    private SymbolTable symbolTable;
    private String tempFile;

    // So tests dont need a filename to work.
    public Parser (){
        lineNum = 0;
        symbolTable = new SymbolTable();
    }

    public Parser(String fileName){
        lineNum = 0;
        file = new File(fileName);
        symbolTable = new SymbolTable();
        numStaticVars = 0;
    }

    // returns true if the line is a comment. E.g. "//this is a comment"
    public boolean isComment(String line){
        for (int i=0; i<line.length() - 1; i++){
            if (line.charAt(i) == '/' && line.charAt(i) == '/'){
                return true;
            }
        }
        return false;
    }

    // returns true if the line is an A instruction. E.g. "@R9"
    public boolean isAInstruction(String line){
        for (int i=0; i<line.length(); i++){
            if (line.charAt(i) == '@'){
                return true;
            }
        }
        return false;
    }

    // returns true if the line is a C instruction. E.g. "D=M+1"
    public boolean isCInstruction(String line){
        for (int i=0; i<line.length(); i++){
            if (line.charAt(i) == '='){
                return true;
            }
            
        }
        return false;
    }

    // returns true if the line is a L instruction. E.g. "(LOOP)"
    public boolean isLInstruction(String line){
        boolean open = false;
        boolean closed = false;
        for (int i=0; i<line.length(); i++){
            if (line.charAt(i) == '('){
                open = true;
            }
            if (line.charAt(i) == ')'){
                closed = true;
            }  
        }
        if (open && closed){
            return true;
        } else {
            return false;
        }
    }

    // returns true if the line is a Jump instruction. E.g. "M;JEQ"
    public boolean isJInstruction(String line){
        if (line.indexOf(";J") != -1){
            return true;
        } else {
            return false;
        }
    }

    // returns the instruction type. If it is a comment or empty line, return null.
    public String instructionType(String line){
        if (isComment(line)){
            return null;
        } else if (isAInstruction(line)){
            return "A";
        } else if (isCInstruction(line)){
            return "C";
        } else if (isLInstruction(line)){
            return "L";
        } else if (isJInstruction(line)){
            return "J";
        } else {
            return null;
        }
    }

    // returns true if the file is readable.
    public boolean isReadable(String fileName){
        file = new File(fileName);
        if (file.exists()) {
            System.out.println("opening file....");
            System.out.println("File name: " + file.getName());
            System.out.println("path: " + file.getAbsolutePath());
            System.out.println("Writeable: " + file.canWrite());
            System.out.println("Readable " + file.canRead());
            System.out.println("File size in bytes " + file.length());
            return true;
        } else {
            return false;
        }
    }

    // the first pass of the file. It adds all symbols to the SymbolTable and then writes to the temp.asm file. In doing so, it removes the R in front
    // of the RAM address (E.g. "@R12"), ignores L instructions (E.g. "(END)") & comments, and finally replaces all instances of the symbols 
    // with their lineNum whichthe original L instruction was to loop.
    public void firstPass() throws IOException{
        try {
            // Reads the file and records symbols. E.g. "(LOOP)"
            BufferedReader firstReader = new BufferedReader(new FileReader(file));
            String line = firstReader.readLine();
            while (line != null){
                if (instructionType(line) != null){
                    if (instructionType(line).equals("L")){
                        int startIndex = line.indexOf('(') + 1;
                        int endIndex = line.lastIndexOf(')');

                        String symbol = line.substring(startIndex, endIndex);
                        symbolTable.add(symbol, lineNum);
                    } else {
                        lineNum ++;
                    }
                    
                }
                line = firstReader.readLine();
            }
            firstReader.close();

            // Writes to temp.asm as the first pass.
            BufferedReader replaceReader = new BufferedReader(new FileReader(file));
            tempFile = System.getProperty("user.dir") + "/projects/06/" + Assembler.f.toLowerCase() + "/temp.asm";
            System.out.println(tempFile);

            Path path = Paths.get(tempFile);
            Files.deleteIfExists(path);

            FileWriter tempWriter = new FileWriter(tempFile);

            line = replaceReader.readLine();
            while (line != null){
                String instructionType = instructionType(line);

                if (instructionType != null)
                    if (instructionType.equals("A")){
                        String aVar = Processor.getAVar(line);
                        if (symbolTable.isSymbol(aVar)){
                            line = "@" + symbolTable.getAddressBySymbol(aVar);
                        } else if (aVar.charAt(0) == 'R'){
                            line = "@" + aVar;
                        } else if (!Character.isDigit(aVar.charAt(0)) && !symbolTable.isSymbol(aVar)){ // handles static variables
                            symbolTable.add(aVar, numStaticVars + 16);
                            line = "@" + (numStaticVars + 16);
                            numStaticVars += 1;
                        } else {
                            line = "@" + aVar;
                        }
                        tempWriter.write(line + "\n");
                    } else if (instructionType.equals("C") || instructionType.equals("J")){
                        tempWriter.write(line.replaceAll("\\s", "") + "\n");
                    }
                line = replaceReader.readLine();
            }
            tempWriter.close();
            replaceReader.close();

            // just testing the SymbolTable.
            // System.out.println(symbolTable.toString());
        
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

    // reads from the temp file and writes the processed instructions to the ___P.hack file. P is there to differentiate from the provided assembler's translation.
    public void secondPass() throws IOException{
        try {
            BufferedReader tempReader = new BufferedReader(new FileReader(tempFile));
            String line = tempReader.readLine();

            String asmFilePath = file.getPath();
            int lastPeriodIndex = asmFilePath.lastIndexOf(".");
            String finalPath = asmFilePath.substring(0, lastPeriodIndex) + "P.hack";

            // deletes the ___p.hack file if it already exists
            Path path = Paths.get(finalPath);
            Files.deleteIfExists(path);

            FileWriter finalWriter = new FileWriter(finalPath);
            String result = "";
            while (line != null){
                if (instructionType(line).equals("A")){
                    result = Processor.processA(line);
                } else if (instructionType(line).equals("C")){
                    result = Processor.processC(line);
                } else if (instructionType(line).equals("J")){
                    result = Processor.processJ(line);
                }
                finalWriter.write(result + "\n");
                line = tempReader.readLine();
            }
            finalWriter.close();
            tempReader.close();
            File del = new File(tempFile);
            del.delete();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
