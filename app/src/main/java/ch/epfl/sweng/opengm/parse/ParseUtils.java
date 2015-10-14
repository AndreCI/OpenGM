package ch.epfl.sweng.opengm.parse;

class ParseUtils {

    public static String objectToString(Object o) throws ServerException {
        if (o == null) {
            throw new ServerException("Object was null");
        }
        try {
            return (String) o;
        } catch (Exception e) {
            throw new ServerException("Error while casting the value to a string");
        }
    }

    public static Object[] objectToArray(Object o) throws ServerException {
        if (o == null) {
            throw new ServerException("Object was null");
        }
        try {
            return (Object[]) o;
        } catch (Exception e) {
            throw new ServerException("Error while casting the value to an array of object");
        }
    }
}
