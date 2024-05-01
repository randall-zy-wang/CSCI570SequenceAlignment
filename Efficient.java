import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class Efficient
{

    // Global Constants
    public static final int GAP_PENALTY = 30;
    public static final int[][] MISMATCH_COST =
    {
            {0, 110, 48, 94},
            {110, 0, 118, 48},
            {48, 118, 0, 110},
            {94, 48, 110, 0}
    };
    private static Map<String, Integer> dpCache = new HashMap<>();

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

    private static int dp(String s1, String s2, int[][] dp) {
        int n = s1.length();
        int m = s2.length();

        // Initialize the dp table
        for (int i = 0; i <= n; i++) {
            dp[i][0] = i * GAP_PENALTY;
        }
        for (int j = 0; j <= m; j++) {
            dp[0][j] = j * GAP_PENALTY;
        }

        // Fill the dp table
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int matchCost = dp[i - 1][j - 1] + MISMATCH_COST[charToIndex(s1.charAt(i - 1))][charToIndex(s2.charAt(j - 1))];
                int gapCost1 = dp[i - 1][j] + GAP_PENALTY;
                int gapCost2 = dp[i][j - 1] + GAP_PENALTY;

                dp[i][j] = Math.min(matchCost, Math.min(gapCost1, gapCost2));
            }
        }

        return dp[n][m];
    }




    private static void constructAlignment(int[][] dp, String s1, String s2, int n, int m, StringBuilder align1, StringBuilder align2) {
        int i = n;
        int j = m;

        while (i > 0 || j > 0) {
            if (i > 0 && j > 0 && dp[i][j] == dp[i - 1][j - 1] + MISMATCH_COST[charToIndex(s1.charAt(i - 1))][charToIndex(s2.charAt(j - 1))]) {
                align1.append(s1.charAt(i - 1));
                align2.append(s2.charAt(j - 1));
                i--;
                j--;
            } else if (i > 0 && dp[i][j] == dp[i - 1][j] + GAP_PENALTY) {
                align1.append(s1.charAt(i - 1));
                align2.append('_');
                i--;
            } else if (j > 0) {
                align1.append('_');
                align2.append(s2.charAt(j - 1));
                j--;
            }
        }

        align1.reverse();
        align2.reverse();
    }

    private static int dpDnC(String string1, String string2, StringBuilder align1, StringBuilder align2) {
        if (string1.length() <= 2 || string2.length() <= 2) {
            int[][] dp = new int[string1.length() + 1][string2.length() + 1];
            int cost = dp(string1, string2, dp);
            constructAlignment(dp, string1, string2, string1.length(), string2.length(), align1, align2);
            return cost;
        }

        int mid1 = string1.length() / 2;
        String left1 = string1.substring(0, mid1);
        String right1 = string1.substring(mid1);

        int bestSplit = findBestSplit(left1, right1, string2);

        StringBuilder leftAlign1 = new StringBuilder();
        StringBuilder leftAlign2 = new StringBuilder();
        StringBuilder rightAlign1 = new StringBuilder();
        StringBuilder rightAlign2 = new StringBuilder();

        int costLeft = dpDnC(left1, string2.substring(0, bestSplit), leftAlign1, leftAlign2);
        int costRight = dpDnC(right1, string2.substring(bestSplit), rightAlign1, rightAlign2);

        align1.append(leftAlign1).append(rightAlign1);
        align2.append(leftAlign2).append(rightAlign2);

        return costLeft + costRight;
    }

    private static int findBestSplit(String left1, String right1, String string2) {
        int minCost = Integer.MAX_VALUE;
        int bestSplit = 0;
        for (int mid2 = 0; mid2 <= string2.length(); mid2++) {
            String left2 = string2.substring(0, mid2);
            String right2 = string2.substring(mid2);

            int costLeft = getDpResult(left1, left2);
            int costRight = getDpResult(right1, right2);

            int totalCost = costLeft + costRight;
            if (totalCost < minCost) {
                minCost = totalCost;
                bestSplit = mid2;
            }
        }
        return bestSplit;
    }

    private static int getDpResult(String s1, String s2) {
        String key = s1 + "|" + s2;
        return dpCache.computeIfAbsent(key, k -> {
            int[][] dp = new int[s1.length() + 1][s2.length() + 1];
            return dp(s1, s2, dp);  // Compute the cost and store it in dpCache
        });
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
            StringGenerator generator = new Efficient().new StringGenerator(inputFilePath);
            String string1 = generator.getStrings()[0];
            String string2 = generator.getStrings()[1];
            StringBuilder align1 = new StringBuilder();
            StringBuilder align2 = new StringBuilder();
            

            Runtime runtime = Runtime.getRuntime();
            runtime.gc();
            // memory used before algorithm
            long memoryBefore = runtime.totalMemory() - runtime.freeMemory();

            // basic dp start time
            long startTime = System.nanoTime(); 


            // calculate optimal cost & alignments using dp
            int optCost = dpDnC(string1, string2, align1, align2);




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
            bufferedWriter.write(align1.toString());
            bufferedWriter.newLine();
            bufferedWriter.write(align2.toString());
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


