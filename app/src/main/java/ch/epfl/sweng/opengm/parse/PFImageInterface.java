package ch.epfl.sweng.opengm.parse;

import android.graphics.Bitmap;

/**
 * This interface is an abstraction that should be used each time your object contains an image
 * that can be downloaded from the server in background
 */
public interface PFImageInterface {

    void setImage(Bitmap bitmap);

}
