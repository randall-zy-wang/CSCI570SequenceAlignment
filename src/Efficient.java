import java.io.IOException;

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

    // Main Method
    public static void main(String[] args) throws IOException {
        Basic.StringGenerator generator = new Basic().new StringGenerator("datapoints/in2.txt");
        String string1 = generator.getStrings()[0];
        String string2 = generator.getStrings()[1];

        System.out.println(string1 + " " + string2);
    }
}