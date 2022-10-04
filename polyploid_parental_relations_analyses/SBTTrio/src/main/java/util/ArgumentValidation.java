package util;

import java.io.File;

/**
 * Implements validating filepath and numeric arguments.
 */
public class ArgumentValidation {

    /**
     * Validate that a given string is a valid file path.
     *
     * @param argument string representation of the input file path
     */
    public static void validateFileArgument(String argument) {
        File file = new File(argument);
        if (!file.isFile()) {
            System.out.println(argument + " is not a valid file.");
            System.out.println("Exiting program.");
            System.exit(1);
        }
    }

    /**
     * Validate that a given string is a valid integer number.
     *
     * @param argument string representation of the input integer
     * @return numeric representation of the input integer
     */
    public static int validateIntArgument(String argument) {
        int integer = 0;
        try {
            integer = Integer.parseInt(argument);
        } catch(NumberFormatException numberFormatException) {
            System.out.println(numberFormatException.getMessage());
            System.out.println("Exiting program.");
            System.exit(1);
        }
        return integer;
    }

}
