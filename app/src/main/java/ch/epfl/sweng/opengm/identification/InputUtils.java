package ch.epfl.sweng.opengm.identification;

import java.util.regex.Pattern;

public class InputUtils {

    public final static int INPUT_CORRECT = 0;

    public final static int INPUT_TOO_SHORT = 200;
    public final static int INPUT_TOO_LONG = 201;
    public final static int INPUT_NOT_CASE_SENSITIVE = 202;
    public final static int INPUT_WITHOUT_LETTER = 203;
    public final static int INPUT_WITHOUT_NUMBER = 204;
    public final static int INPUT_WITH_SYMBOL = 205;
    public final static int INPUT_BEGINS_WITH_SPACE = 206;


    private final static int PASSWORD_MIN_LENGTH = 6;
    private final static int PASSWORD_MAX_LENGTH = 30;
    private final static int GROUP_NAME_MIN_LENGTH = 3;
    private final static int GROUP_NAME_MAX_LENGTH = 30;

    private final static Pattern emailPattern =
            Pattern.compile("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$");

    public static boolean isEmailValid(String email) {
        return emailPattern.matcher(email).matches();
    }

    public static int isPasswordInvalid(String password) {
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
        if (length <= PASSWORD_MIN_LENGTH) {
            return INPUT_TOO_SHORT;
        } else if (length > PASSWORD_MAX_LENGTH) {
            return INPUT_TOO_LONG;
        }
        if (password.toLowerCase().equals(password)) {
            return INPUT_NOT_CASE_SENSITIVE;
        }
        if (nOfIntegers <= 0) {
            return INPUT_WITHOUT_NUMBER;
        } else if (nOfIntegers >= length) {
            return INPUT_WITHOUT_LETTER;
        }
        return INPUT_CORRECT;
    }

    public static int isGroupNameValid(String name) {
        if (name.charAt(0) == ' ') {
            return INPUT_BEGINS_WITH_SPACE;
        } else if (name.length() < GROUP_NAME_MIN_LENGTH) {
            return INPUT_TOO_SHORT;
        } else if (name.length() > GROUP_NAME_MAX_LENGTH) {
            return INPUT_TOO_LONG;
        } else {
            for (Character c : name.toCharArray()) {
                if (!((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9'))) {
                    return INPUT_WITH_SYMBOL;
                }
            }
        }

        return INPUT_CORRECT;
    }

}
