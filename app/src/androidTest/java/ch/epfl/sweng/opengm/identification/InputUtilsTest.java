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

    @Test
    public void testDeclineEmailWithoutAddress(){
        assertTrue(!InputUtils.isEmailValid("@mail.com"));
    }

    @Test
    public void testDeclineEmailWithoutAt(){
        assertTrue(!InputUtils.isEmailValid("goodmail.com"));
    }

    @Test
    public void testDeclineEmailWithoutDot(){
        assertTrue(!InputUtils.isEmailValid("good@mailcom"));
    }

    @Test
    public void testDeclineEmailWithoutDomain(){
        assertTrue(!InputUtils.isEmailValid("good@mail"));
    }

    @Test
    public void testDeclineBadChars(){
        assertTrue(!InputUtils.isEmailValid("góód@maił.cóm"));
    }

    @Test
    public void testAcceptGoodPassword(){
        assertTrue(!InputUtils.isPasswordInvalid("GoodPassword1"));
    }

    @Test
    public void testDeclineShortPassword(){
        assertTrue(InputUtils.isPasswordInvalid("goodpa"));
    }

    @Test
    public void testDeclinePasswordWithoutNumber() {
        assertTrue(InputUtils.isPasswordInvalid("GoodPassword"));
    }

    @Test
    public void testDeclinePasswordNotBothCases() {
        assertTrue(InputUtils.isPasswordInvalid("goodpassword"));
    }

    @Test
    public void testDeclinePasswordOnlyNumbers(){
        assertTrue(InputUtils.isPasswordInvalid("1337"));
    }
}
