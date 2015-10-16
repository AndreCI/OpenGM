package ch.epfl.sweng.opengm.parse;

import ch.epfl.sweng.opengm.utils.Alert;

class PFUtils {

    public static String objectToString(Object o) throws PFException {
        if (o == null) {
            throw new PFException("Object was null");
        }
        try {
            return (String) o;
        } catch (Exception e) {
            throw new PFException("Error while casting the value to a string");
        }
    }

    public static Object[] objectToArray(Object o) throws PFException {
        if (o == null) {
            throw new PFException("Object was null");
        }
        try {
            return (Object[]) o;
        } catch (Exception e) {
            throw new PFException("Error while casting the value to an array of object");
        }
    }

    public static boolean checkArguments(String arg, String name) {
        if (arg == null || arg.isEmpty()) {
            Alert.displayAlert(name + " is null or empty.");
            return false;
        }
        return true;
    }

    public static boolean checkNullArguments(String arg, String name) {
        if (arg == null) {
            Alert.displayAlert(name + " is null.");
            return false;
        }
        return true;
    }


}
