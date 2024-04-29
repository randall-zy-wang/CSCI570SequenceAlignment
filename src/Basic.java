import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Basic {

    // Global Constants
    public static final int GAP_PENALTY = 30;
    public static final int[][] MISMATCH_COST =
    {
            {0, 110, 48, 94},
            {110, 0, 118, 48},
            {48, 118, 0, 110},
            {94, 48, 110, 0}
    };

    // Other methods

    // Main Method
    public static void main(String[] args) throws Exception {
        try {
            StringGenerator generator = new Basic().new StringGenerator("datapoints/in1.txt");
            String string1 = generator.getStrings()[0];
            String string2 = generator.getStrings()[1];

            System.out.println(string1 + " " + string2);

        } catch (IOException e) {
            System.err.println("Error reading input file: " + e.getMessage());
        }
    }

    // Helper Classes
    public class StringGenerator {
        private String baseString;
        private List<Integer> insertionIndices;
        private String[] output;
    
        public StringGenerator(String filePath) throws IOException {
            parseInput(filePath);
            
        }
    
        private void parseInput(String filePath) throws IOException {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            baseString = reader.readLine();
            output = new String[2]; 
            String line;
            insertionIndices = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                if (line.matches("\\d+")) { // Check if the line contains only digits
                    insertionIndices.add(Integer.parseInt(line));
                } else {
                    break; // Exit the loop once we reach the base string for the second string
                }
            }
            output[0] = generateStrings();
            insertionIndices.clear();
    
            baseString = line; // Set the base string for the second string
            while ((line = reader.readLine()) != null) {
                insertionIndices.add(Integer.parseInt(line));
            }
            output[1] = generateStrings();
    
            reader.close();
        }
    
        public String generateStrings() {
            StringBuilder currentString = new StringBuilder(baseString);
            // System.out.println(baseString);
            for (int index : insertionIndices) {
                currentString = currentString.insert(index, currentString.toString());
                System.out.println("Step " + (insertionIndices.indexOf(index) + 1) + ": " + currentString);
            }
            return currentString.toString();
        }
    
        public String[] getStrings() {
            return output;
        }
    }
    
}


