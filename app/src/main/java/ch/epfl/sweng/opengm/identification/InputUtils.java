package ch.epfl.sweng.opengm.identification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

class InputUtils {

    private final static int PASSWORD_LENGTH = 6;
    private final static Pattern emailPattern =
            Pattern.compile("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$");
    private final static int MIN_GROUP_NAME_LENGTH = 3;
    private final static int MAX_GROUP_NAME_LENGTH = 10;

    public enum ErrorCodes {
        GROUP_NAME_TOO_SHORT(200),
        GROUP_NAME_TOO_LONG(201),
        GROUP_NAME_CONTAINS_ILLEGAL_CHARACTERS(202),
        GROUP_NAME_BEGINS_WITH_SPACE(203);

        private final int errorCode;
        ErrorCodes(int i) {
            errorCode = i;
        }

        public int getErrorCode(){
            return errorCode;
        }
    }

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

    public static int isGroupValid(String name){
        String allowedChars = "abcdefghijklmnoprqstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890 ";
        int toReturn = 0;

        if(name.charAt(0) == ' '){
            toReturn = ErrorCodes.GROUP_NAME_BEGINS_WITH_SPACE.getErrorCode();
        } else if(name.length() < MIN_GROUP_NAME_LENGTH){
            toReturn = ErrorCodes.GROUP_NAME_TOO_SHORT.getErrorCode();
        } else if(name.length() > MAX_GROUP_NAME_LENGTH){
            toReturn = ErrorCodes.GROUP_NAME_TOO_LONG.getErrorCode();
        } else {
            for(int i = 0; i < name.length(); i++){
                if(allowedChars.indexOf(name.charAt(i)) < 0){
                    toReturn = ErrorCodes.GROUP_NAME_CONTAINS_ILLEGAL_CHARACTERS.errorCode;
                }
            }
        }

        return toReturn;
    }

}
