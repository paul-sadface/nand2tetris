public class Processor {

    // returns the variable from an A instruction. E.g. ("    @LOOP" -> "LOOP") or ("@R0 -> 0")
    public static String getAVar(String line){
        String noWhitespace = line.replaceAll("\\s", "");
        String result = noWhitespace.substring(1, noWhitespace.length());

        if (result.charAt(0) == 'R' && Character.isDigit(result.charAt(1))){
            result = result.substring(1, noWhitespace.length() - 1);
        }
        return result;
    }

    // processes A instruction and return the corrent 2 bytes.
    public static String processA(String line){
        String stringN = getAVar(line);
        int n = Integer.parseInt(stringN);
        String binaryValue = Integer.toBinaryString(n);
        while (binaryValue.length() < 16){
            binaryValue = "0" + binaryValue;
        }
        return binaryValue;
    }

    // processes C instruction and return the corrent 2 bytes.
    public static String processC(String line){
        String result = "111";
        line = line.replaceAll("\\s", "");
        int equalsIndex = line.indexOf("=");
        String comp = line.substring(equalsIndex + 1, line.length());
        String dest = line.substring(0, equalsIndex);
        result = result + Comparisons.comp(comp) + Comparisons.dest(dest) + "000";
        return result;

    }

    // processes jump instruction and return the corrent 2 bytes.
    public static String processJ(String line){
        String result = "111";
        line = line.replaceAll("\\s", "");
        int semicolonIndex = line.indexOf(";");
        String comp = line.substring(0, semicolonIndex);
        String jump = line.substring(semicolonIndex + 1, line.length());
        result = result + Comparisons.comp(comp) + "000" + Comparisons.jump(jump);
        return result;
    }

    
}
