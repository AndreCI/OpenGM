package ch.epfl.sweng.opengm.identification;

import android.renderscript.ScriptGroup;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class InputUtilsTest {

    @Test
    public void testAcceptGoodEmail(){
        assertTrue(InputUtils.isEmailValid("good@mail.com"));
    }

    public void testDeclineEmailWithoutAddress(){
        assertTrue(!InputUtils.isEmailValid("@mail.com"));
    }

    public void testDeclineEmailWithoutAt(){
        assertTrue(!InputUtils.isEmailValid("goodmail.com"));
    }

    public void testDeclineEmailWithoutDot(){
        assertTrue(!InputUtils.isEmailValid("good@mailcom"));
    }

    public void testDeclineEmailWithoutDomain(){
        assertTrue(!InputUtils.isEmailValid("good@mail"));
    }

    public void testDeclineBadChars(){
        assertTrue(!InputUtils.isEmailValid("góód@maił.cóm"));
    }

    public void testAcceptGoodPassword(){
        assertTrue(!InputUtils.isPasswordInvalid("GoodPassword1"));
    }
}
