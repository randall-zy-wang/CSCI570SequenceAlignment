import java.io.*;

public class Efficient {

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
    private static String efficientDp(String string1, String string2) {
        // TODO: implement efficient dp
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
            Basic.StringGenerator generator = new Basic().new StringGenerator(inputFilePath);
            String string1 = generator.getStrings()[0];
            String string2 = generator.getStrings()[1];

            // calculate
            String output = efficientDp(string1, string2);
            
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


}


