package rjmdatabase.userinterface;

import java.io.FilterInputStream;
import java.io.IOException;
import java.util.Scanner;

public class ConsoleInterfaceUtil
{
    // Private to disallow instantiation
    private ConsoleInterfaceUtil() {}

    public static void println(String s)
    {
        System.out.println(s);
    }

    public static void println()
    {
        System.out.println();
    }

    public static String getLineOfInput()
    {
        // Make a wrapper for System.in, so that closing the Scanner doesn't close
        // the underlying input stream.
        FilterInputStream inWrapper = new FilterInputStream(System.in)
        {
            @Override
            public void close() throws IOException {
                // Don't close System.in!
            }
        };

        try(Scanner in = new Scanner(inWrapper))
        {
            String result = in.nextLine();
            if (result.equals(""))
                return null;
            return result;
        }
    }

    public static String getLineOfInputNoSpaces()
    {
        String input = getLineOfInput();
        if (input == null)
            return null;

        input = input.replaceAll("\\s", "");
        return input.equals("") ? null : input;
    }

    public static int getIntInput()
    {
        int result; String input; boolean success = false;
        while (true)
        {
            input = getLineOfInput();
            try
            {
                result = Integer.parseInt(input);
                return result;
            }
            catch (NumberFormatException e)
            {
                println("Please enter a valid number.");
            }
        }
    }

    public static boolean userRespondedYes(String prompt)
    {
        println(prompt);
        String response;
        do
        {
            println("Please enter either \"Y\" or \"N\".");
            response = getLineOfInputNoSpaces();
        }
        while(!response.equals("Y") && !response.equals("N"));
        return "Y".equals(response);
    }
}
