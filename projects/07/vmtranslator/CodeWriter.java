import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileWriter;

public class CodeWriter {
    private PrintWriter codeWriter;
    private String fileName;
    private File outputFile;
    private int loopCount = 0;

    public CodeWriter(File file){
        // Creates a PrintWriter object called codeWriter from the output file 
        codeWriter = null;
        outputFile = new File(file.getAbsolutePath().split(".vm")[0] + ".asm");
        try{
            codeWriter = new PrintWriter(new FileWriter(outputFile, false));
            fileName = file.getName();
        } catch (IOException error){
            error.printStackTrace();
        }
    }

    public void initializeWriter(File file){
        try {
            if (codeWriter != null) {
                codeWriter.close(); 
            }
            codeWriter = new PrintWriter(new FileWriter(file, false)); 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setFileName(String name){
        outputFile = new File(name.split("\\.vm")[0] + ".asm");
        initializeWriter(outputFile);
        fileName = outputFile.getName();
    }

    public void writeTest(String line){
        codeWriter.println(line);
    }

    public void close(){
        if (codeWriter != null){
            codeWriter.close();
        }
    }

    public void writePushPop(CommandType commandType, String arg1, int arg2){
        if (commandType == CommandType.C_PUSH){
            codeWriter.printf("// push %s %d\n", arg1, arg2);
            switch (arg1){
                case "constant":
                    codeWriter.println("@" + arg2);
                    codeWriter.println("D=A");
                    pushDToStack();
                    break;
                case "local":
                    loadSegment("LCL", arg2);
                    codeWriter.println("D=M");
                    pushDToStack();
                    break;
                case "argument":
                    loadSegment("ARG", arg2);
                    codeWriter.println("D=M");
                    pushDToStack();
                    break;
                case "this":
                    loadSegment("THIS", arg2);
                    codeWriter.println("D=M");
                    pushDToStack();
                    break;
                case "that":
                    loadSegment("THAT", arg2);
                    codeWriter.println("D=M");
                    pushDToStack();
                    break;
                case "temp":
                    codeWriter.println("@" + (arg2 + 5));
                    codeWriter.println("D=M");
                    pushDToStack();
                    break;
                case "pointer":
                    codeWriter.println("@" + (arg2 + 3));
                    codeWriter.println("D=M");
                    pushDToStack();
                    break;
                case "static":
                    codeWriter.println("@" + fileName.split("\\.")[0] + "." + arg2);
                    codeWriter.println("D=M");
                    pushDToStack();
                    break;
            }

        } else if (commandType == CommandType.C_POP){
            /* In each case, this is essentially what its doing. I just make a separate function to avoid redundancy.
             * pop (SEGMENT NAME) (INT N)
             * ---
             * loadRegister(SEGMENT NAME, INT N)
             * 
             * store the address of the location we are popping to in temp RAM[13]
             * D=A
             * @13
             * M=D
             * 
             * pop the stack to D
             * decrement
             * A=M
             * D=M
             * 
             * get the address we are popping to and pop D there
             * @13
             * A=M
             * M=D
             */
            codeWriter.printf("// pop %s %d\n", arg1, arg2);
            switch (arg1){
                case "local":
                    loadSegment("LCL", arg2);
                    storeAndPop(); 
                    break;
                case "argument":
                    loadSegment("ARG", arg2);
                    storeAndPop(); 
                    break;
                case "this":
                    loadSegment("THIS", arg2);
                    storeAndPop(); 
                    break;
                case "that":
                    loadSegment("THAT", arg2);
                    storeAndPop(); 
                    break;
                case "temp":
                    codeWriter.println("@" + (arg2 + 5));
                    storeAndPop(); 
                    break;
                case "pointer":
                    codeWriter.println("@" + (arg2 + 3));
                    storeAndPop(); 
                    break;
                case "static":
                    codeWriter.println("@" + fileName.split("\\.")[0] + "." + arg2);
                    storeAndPop();
                    break;
            }
        }

    }

    public void writeArithmetic(String command){
        codeWriter.println("// " + command);
        switch (command){
            case "add":
                popStackToD();
                decrementSP();
                codeWriter.println("A=M");
                codeWriter.println("M=D+M");
                incrementSP();
                break;
            case "sub":
                popStackToD();
                decrementSP();
                codeWriter.println("A=M");
                codeWriter.println("M=M-D");
                incrementSP();
                break;
            case "neg":
                decrementSP();
                codeWriter.println("A=M");
                codeWriter.println("M=-M");
                incrementSP();
                break;
            case "eq":
                writeCompareLogic("JEQ");
                break;
            case "lt":
                writeCompareLogic("JLT");
                break;
            case "gt": 
                writeCompareLogic("JGT");
                break;
            case "and":
                popStackToD();
                decrementSP();
                codeWriter.println("A=M");
                codeWriter.println("M=D&M");
                incrementSP();
                break;
            case "or":
                popStackToD();
                decrementSP();
                codeWriter.println("A=M");
                codeWriter.println("M=D|M");
                incrementSP();
                break;
            case "not":
                popStackToD();
                codeWriter.println("M=!D");
                incrementSP();
                break;
        }
    }

    public void writeCompareLogic(String jump){
        popStackToD();
        decrementSP();
        codeWriter.println("A=M");
        codeWriter.println("D=D-M");
        codeWriter.println("@LOOP" + loopCount);
        codeWriter.println("D;" + jump);

        codeWriter.println("D=0");
        codeWriter.println("@ENDLOOP" + loopCount);
        codeWriter.println("0;JMP");

        codeWriter.println("(LOOP" + loopCount + ")");
        codeWriter.println("D=-1");
        codeWriter.println("@ENDLOOP" + loopCount);
        codeWriter.println("0;JMP");

        codeWriter.println("(ENDLOOP" + loopCount + ")");
        pushDToStack();
        incrementSP();
        loopCount++;
    }

    private void storeAndPop(){
        codeWriter.println("D=A");
        codeWriter.println("@13");
        codeWriter.println("M=D");
        popStackToD();
        codeWriter.println("@13");
        codeWriter.println("A=M");
        codeWriter.println("M=D");
    }

    private void popStackToD(){
        decrementSP();
        codeWriter.println("A=M");
        codeWriter.println("D=M");
    }

    private void pushDToStack(){
        codeWriter.println("@SP");
        codeWriter.println("A=M");
        codeWriter.println("M=D");
        incrementSP();
    }
    private void loadSegment(String segment, int n){
        codeWriter.println("@" + segment);
        codeWriter.println("D=M");
        codeWriter.println("@" + n);
        codeWriter.println("A=D+A");
    }

    private void incrementSP(){
        codeWriter.println("@SP");
        codeWriter.println("M=M+1");
    }

    private void decrementSP(){
        codeWriter.println("@SP");
        codeWriter.println("M=M-1");
    }

}
