import static org.junit.Assert.assertEquals;
import org.junit.Test;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;

// Some simple tests
public class Tests {
    Parser testParser = new Parser();

    @Test
    public void testComments0(){
        String ins = "    //this is a comment";
        assertEquals(true, testParser.isComment(ins));
    }
    
    @Test
    public void testComments1(){
        String ins = "D=M+1";
        assertEquals(false, testParser.isComment(ins));
    }

    @Test
    public void testInstructionType0(){
        String ins = "   @R0";
        assertEquals(true, testParser.isAInstruction(ins));
    }

    @Test
    public void testInstructionType1(){
        String ins = "    D=D+A";
        assertEquals(true, testParser.isCInstruction(ins));
    }

    @Test
    public void testInstructionType2(){
        String ins = "   (LOOP)";
        assertEquals(true, testParser.isLInstruction(ins));
    }

    @Test
    public void testInstructionType3(){
        String ins = "   (LOOP)";
        assertEquals("L", testParser.instructionType(ins));
    }
    
    @Test
    public void testProcessorGetAVar(){
        String line = "     @LOOP";
        assertEquals("LOOP", Processor.getAVar(line));
    }

    @Test
    public void testProcessA(){
        String line = "@1234";
        assertEquals("0000010011010010", Processor.processA(line));
    }

    @Test
    public void testProcessC0(){
        String line = "D=A";
        String result = "1110110000010000";
        assertEquals(Processor.processC(line), result);
    }

    @Test
    public void testProcessC1(){
        String line = "D=D+A";
        String result = "1110000010010000";
        assertEquals(Processor.processC(line), result);
    }

    @Test
    public void testProcessJ0(){
        String line = "    D;JGT";
        String result = "1110001100000001";
        assertEquals(Processor.processJ(line), result);
    }

    @Test
    public void testA(){
        String line = "256";
        boolean res = !Character.isDigit(line.charAt(0));
        assertEquals(false, res);
    }

    @Test
    public void testB(){
        String line = "@256";
        assertEquals("256", Processor.getAVar(line));
    }

    public static void testAssemberComparePong() throws IOException{
        String original = System.getProperty("user.dir") + "/projects/06/pong/Pong.hack";
        String mine = System.getProperty("user.dir") + "/projects/06/pong/PongP.hack";

        File fo = new File(original);
        File fm = new File(mine);

        BufferedReader o = new BufferedReader(new FileReader(fo));
        BufferedReader m = new BufferedReader(new FileReader(fm));

        String lineO = o.readLine();
        String lineM = m.readLine();
        int count = 0;
        while (lineO != null && lineM != null){
            if (!lineO.equals(lineM)){
                System.out.println("Original: " + lineO);
                System.out.println("Mine: " + lineM);
                System.out.println("Line num: " + count);
            }
            lineO = o.readLine();
            lineM = m.readLine();
            count ++;
        }
        o.close();
        m.close();
    }
    
}
