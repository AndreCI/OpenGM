package ch.epfl.sweng.opengm.parse;

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
}
