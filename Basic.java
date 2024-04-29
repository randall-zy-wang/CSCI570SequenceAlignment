import java.io.*;
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
    private static String dp(String string1, String string2) {
        // TODO: implement basic dp
        String result = string1 + " " + string2;

        return result;
    }

    // Main Method
    public static void main(String[] args) throws Exception {
        String inputFilePath = "datapoints/in2.txt";
        String outputFilePath = "output.txt";

        if (args.length > 0) {
            inputFilePath = args[0];
        } 
        if (args.length > 1) {
            outputFilePath = args[1];
        }

        try {
            // generate standard input
            StringGenerator generator = new Basic().new StringGenerator(inputFilePath);
            String string1 = generator.getStrings()[0];
            String string2 = generator.getStrings()[1];

            // calculate
            String output = dp(string1, string2);
            
            // output result to a file
            File file = new File(outputFilePath);
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter writer = new FileWriter(file, true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            bufferedWriter.write(output);
            
            bufferedWriter.close();
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


