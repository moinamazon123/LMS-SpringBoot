package com.maps.yolearn.util.mail;

public class MaskUtil {

    /**
     * @param number The number in plain format
     * @param mask The  mask pattern.
     *    Use # to include the digit from the position.
     *    Use x to mask the digit at that position.
     *    Any other char will be inserted.
     *
     * @return The masked card number
     */
    public static String maskNumber(String number, String mask) {

        int index = 0;
        StringBuilder masked = new StringBuilder();
        for (int i = 0; i < mask.length(); i++) {
            char c = mask.charAt(i);
            if (c == '#') {
                masked.append(number.charAt(index));
                index++;
            } else if (c == 'x') {
                masked.append(c);
                index++;
            } else {
                masked.append(c);
            }
        }
        return masked.toString();
    }

    public static void main(String[] args) {
        System.out.println(maskNumber("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJGRUNvWEpiYVFKNnNrdmlGZE51Y0RBIiwiZXhwIjoxNjU0ODY3MDE3NjM5LCJpYXQiOjE2NTQ4NjcwMTJ9.2b_OkTka6EuM1Xvfa30xXOPVUJHFWIRq2OGIgtorIag","xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx####"));

    }
}
