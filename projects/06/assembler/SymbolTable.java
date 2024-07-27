import java.util.ArrayList;

// Represents the symbols including which line the L instruction occured. E.g. "(LOOP)"
public class SymbolTable {
    private ArrayList<String> indexes;
    private ArrayList<String> symbols;

    public SymbolTable(){
        indexes = new ArrayList<String>();
        symbols = new ArrayList<String>();

        // predefined symbols
        add("SP", 0);
        add("LCL", 1);
        add("ARG", 2);
        add("THIS", 3);
        add("THAT", 4);
        add("SCREEN", 16384);
        add("KBD", 24576);
    }

    // Returns true if the symbol is found in ArrayList symbols.
    public boolean isSymbol(String symbol){
        for (int i = 0; i < symbols.size(); i++){
            if (symbols.get(i).equals(symbol)){
                return true;
            }
        }
        return false;
    }

    // Adds an instance of a symbol occurence.
    public void add(String symbol, int lineNum){
        symbols.add(symbol);
        indexes.add(Integer.toString(lineNum));
    }

    // toString method to test if the SymbolTable was working
    @Override
    public String toString(){
        String result = "";
        for (int i = 0; i < indexes.size(); i++){
            result += symbols.get(i) + " -> " + indexes.get(i) + "\n";
        }
        return result;
    }

    // Returns the Address (line that it occured) by its name.
    public String getAddressBySymbol(String symbol){
        for (int i = 0; i < symbols.size(); i++){
            if (symbols.get(i).equals(symbol)){
                return indexes.get(i);
            }
        }
        return "";
    }

    public boolean isSymbolByIndex(String index){
        for (int i = 0; i < indexes.size(); i++){
            if (indexes.get(i).equals(index)){
                return true;
            }
        }
        return false;
    }
        
}
