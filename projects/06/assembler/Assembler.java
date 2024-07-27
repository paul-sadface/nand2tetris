import java.io.IOException;
import java.util.Scanner;



public class Assembler{
    public static String f;
    public static void main(String[] args) throws IOException{
        Scanner scanner = new Scanner(System.in);
        System.out.println("file name");
        f = scanner.nextLine();
        String fileName = System.getProperty("user.dir") + "/projects/06/" + f.toLowerCase() + "/" + f + ".asm";
        scanner.close();

        Parser parser = new Parser(fileName);
        if (!parser.isReadable(fileName)){
            System.out.println("File is unreadable, try again.");
        }
        parser.firstPass();
        parser.secondPass();

        
        
        
    }
}