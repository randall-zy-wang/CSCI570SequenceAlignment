import java.io.*;
import java.time.Duration;
import java.time.Instant;
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

    public static String[]alignments = new String[2];
    public static float timeInMs = 0;
    public static float memInKb = 0;

    // Other methods
    private static int charToIndex(char c){
        switch (c) {
            case 'A':
                return 0;
            case 'C':
                return 1;
            case 'G':
                return 2;
            case 'T':
                return 3;

            default:
                throw new IllegalArgumentException("Invalid character: " + c);
        }
    }

    private static int dp(String string1, String string2) {
        // String result = string1 + " " + string2;
        int n = string1.length(); int m = string2.length();
        int[][]dp = new int[n + 1][m + 1];
        
        // initialize dp table
        for(int i = 0; i <= n; i++){ 
            dp[i][0] = i * GAP_PENALTY; // string2 is empty 
        }

        for(int j = 0; j <= m; j++){ 
            dp[0][j] = j * GAP_PENALTY; // string1 is empty 
        }

        // fill dp table
        for(int i = 1; i <= n; i++){
            for(int j = 1; j <= m; j++){
                int matchCost = dp[i - 1][j - 1] + MISMATCH_COST[charToIndex(string1.charAt(i-1))][charToIndex(string2.charAt(j-1))];
                int gapCost1 = dp[i - 1][j] + GAP_PENALTY; // put a gap in string 2; a letter in s1 matches a gap in s2
                int gapCost2 = dp[i][j - 1] + GAP_PENALTY;
                
                dp[i][j] = Math.min(matchCost, Math.min(gapCost1, gapCost2));
            }
        }

        int optCost = dp[n][m];

        // System.out.println(optCost);

        // Top-down pass
        constructAlignment(dp, string1, string2, n, m);

        return optCost;
    }

    private static void constructAlignment(int[][]dp, String s1, String s2, int n, int m){
        StringBuilder alignment1 = new StringBuilder();
        StringBuilder alignment2 = new StringBuilder();

        // start from dp[n][m]
        int i = n; int j = m;
        while(i > 0 || j > 0){            
            if(i > 0 && j > 0 && dp[i][j] == dp[i-1][j-1] + MISMATCH_COST[charToIndex(s1.charAt(i-1))][charToIndex(s2.charAt(j-1))]){ // diagonal
                alignment1.append(s1.charAt(i-1));
                alignment2.append(s2.charAt(j-1));
                i--;
                j--;
            }else if(i > 0 && dp[i][j] == dp[i-1][j] + GAP_PENALTY){ // going up 
                alignment1.append(s1.charAt(i-1));
                alignment2.append("_");
                i--;
            }else if(j > 0 && dp[i][j] == dp[i][j-1] + GAP_PENALTY){ // going left
                alignment1.append("_");
                alignment2.append(s2.charAt(j-1));
                j--;
            }
            
        }

        alignment1.reverse();
        alignment2.reverse();

        alignments[0] = alignment1.toString(); 
        alignments[1] = alignment2.toString();

        // System.out.println(alignment1.toString());
        // System.out.println(alignment2.toString());
    }

    // Main Method
    public static void main(String[] args) throws Exception {
        // String inputFilePath = "datapoints/in2.txt";
        String inputFilePath = "SampleTestCases/input4.txt";
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
            

            Runtime runtime = Runtime.getRuntime();
            runtime.gc();
            // memory used before algorithm
            long memoryBefore = runtime.totalMemory() - runtime.freeMemory();

            // basic dp start time
            long startTime = System.nanoTime(); 


            // calculate optimal cost & alignments using dp
            int optCost = dp(string1, string2);
            

            // end time
            long endTime = System.nanoTime();
            long elapsedTime = endTime - startTime;
            timeInMs = elapsedTime / 1000000.0f;
            
            // memory used after
            long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
            memInKb = memoryAfter - memoryBefore;
            
        
            // output result to a file
            File file = new File(outputFilePath);
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter writer = new FileWriter(file, true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            // write optimal cost
            bufferedWriter.write(Integer.toString(optCost));
            bufferedWriter.newLine();
            // write alignments
            bufferedWriter.write(alignments[0]);
            bufferedWriter.newLine();
            bufferedWriter.write(alignments[1]);
            bufferedWriter.newLine();
            // write time in Ms
            bufferedWriter.write(String.valueOf(timeInMs));
            bufferedWriter.newLine();
            // write memory in Kb
            bufferedWriter.write(String.valueOf(memInKb));
            bufferedWriter.newLine();

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
                currentString = currentString.insert(index + 1, currentString.toString());
                System.out.println("Step " + (insertionIndices.indexOf(index) + 1) + ": " + currentString);
            }
            return currentString.toString();
        }
    
        public String[] getStrings() {
            return output;
        }
    }
    
}


