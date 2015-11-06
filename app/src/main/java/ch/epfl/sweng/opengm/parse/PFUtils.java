package ch.epfl.sweng.opengm.parse;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

/**
 * This class contains some static methods that may be called for conversion, saving or checking purposes
 */
public final class PFUtils {

    /**
     * Converts a JSONArray into an array of String
     *
     * @param jsonArray A json array containing the information you want to extract
     * @return An array of strings with all the elements of the json array
     */
    public static String[] convertFromJSONArray(JSONArray jsonArray) {
        if (jsonArray == null) {
            return null;
        }
        String[] array = new String[jsonArray.length()];

        for (int i = 0; i < array.length; i++) {
            try {
                array[i] = objectToString(jsonArray.get(i));
            } catch (JSONException | PFException e) {
                // TODO : what to do?
            }
        }

        return array;
    }

    /**
     * Downloads in background the image of a Parse object and stores it at the first index of the array given in parameter
     *
     * @param object A Parse object which contains the image we want to get
     * @param entry  A string whose value is the entry which contains the image in our table
     * @param image  An array of image with only one case
     */
    public static void retrieveFileFromServer(ParseObject object, String entry, final Bitmap[] image) {
        if (image.length != 1) {
            // TOOD : empty array
        }
        ParseFile fileObject = (ParseFile) object.get(entry);
        if (fileObject != null) {
            fileObject.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if (e == null) {
                        image[0] = BitmapFactory.decodeByteArray(data, 0, data.length);
                    }
                }
            });
        }
    }

    /**
     * Casts the object into a string
     *
     * @param o An object that we want to cast
     * @return A string of the object
     * @throws PFException If the object in parameter is null if it could not be cast into a string
     */
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

    /**
     * Converts a list of PFEntities into a JSonarray whose elements are the id of the entities
     *
     * @param entitiesList A list that contains some PFEntity we want to get the ids
     * @return A JSONArray whose elements are the ids of the parameter
     */
    public static JSONArray listToArray(List<? extends PFEntity> entitiesList) {
        JSONArray array = new JSONArray();
        for (int i = 0; i < entitiesList.size(); i++) {
            array.put(entitiesList.get(i).getId());
        }
        return array;
    }

    /**
     * Checks if the argument is not null and not empty. If it is, displays an Toast with the message given in parameter
     *
     * @param arg            The string argument to be checked
     * @return True if the argument is correct, false otherwise
     */
    public static boolean checkArguments(String arg) {
        return !(arg == null || arg.isEmpty());
    }

    /**
     * Checks if the argument is not null. If it is, displays an Toast with the message given in parameter
     *
     * @param arg            The string argument to be checked
     * @return True if the argument is correct, false otherwise
     */
    public static boolean checkNullArguments(String arg) {
        return (arg != null);
    }


}
