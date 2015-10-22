package ch.epfl.sweng.opengm.identification;

import java.util.regex.Pattern;

class InputUtils {

    private final static int PASSWORD_LENGTH = 6;
    private final static Pattern emailPattern =
            Pattern.compile("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$");


    public static boolean isEmailValid(String email) {
        return emailPattern.matcher(email).matches();
    }

    public static boolean isPasswordInvalid(String password) {
        // Rules :
        // 1 = length greater than 6
        // 2 = at least one lowercase and one uppercase
        // 3 = at least one number
        // 4 = contains letters and characters
        int length = password.length();
        int nOfIntegers = 0;
        for (Character c : password.toCharArray()) {
            nOfIntegers += (c >= '0' && c <= '9') ? 1 : 0;
        }
        // TODO : change this after testing
        return length <= PASSWORD_LENGTH || password.toLowerCase().equals(password) || nOfIntegers <= 0 || nOfIntegers >= length;
        //return false;
    }

}
