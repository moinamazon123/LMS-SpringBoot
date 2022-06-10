package com.maps.yolearn.util.lang;

import java.util.ArrayList;
import java.util.List;

/**
 * @author PREMNATH
 */
public class FirstCharLowerToUpper {

    public static String getString(String str) {

        List<String> sList = new ArrayList<>();

        String[] strArray = str.trim().split(" ");
        for (String string : strArray) {
            sList.add(makeFirstCharToUpper(string) + " ");
        }

        str = String.valueOf(sList);

        str = str.replace(" ,", "");
        str = str.replace("[", "");
        str = str.replace("]", "");
        String result = str.trim();

        return result;
    }

    public static String makeFirstCharToUpper(String str) {

        String s = str.toLowerCase();
        char[] c = s.toCharArray();

        try {

            /*Converting first character (which at index 0) to Upper case*/
            int ascii = c[0];//ArrayIndexOutOfBoundsException

            int asciiToUppercase = ascii + (-32);
            char upperChar = (char) asciiToUppercase;

            c[0] = upperChar;

            s = String.valueOf(c);
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        return s;
    }

}
