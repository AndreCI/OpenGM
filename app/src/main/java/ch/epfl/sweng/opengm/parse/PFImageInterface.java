package ch.epfl.sweng.opengm.parse;

import android.graphics.Bitmap;

/**
 * This interface is an abstraction that should be used each time your object contains an image
 * that can be downloaded from the server in background
 */
public interface PFImageInterface {

    /**
     * This method must be implemented by each class which implements this interface
     * so you can save the parameter in the local field of your class.
     *
     * @param bitmap The image that you get from a ParseFile and you update in background
     */
    void setImage(Bitmap bitmap);

}
