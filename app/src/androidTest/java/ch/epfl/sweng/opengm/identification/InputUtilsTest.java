package ch.epfl.sweng.opengm.identification;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class InputUtilsTest {

    @Test
    public void testAcceptGoodEmail(){
        assertTrue(InputUtils.isEmailValid("good@mail.com"));
    }

}
