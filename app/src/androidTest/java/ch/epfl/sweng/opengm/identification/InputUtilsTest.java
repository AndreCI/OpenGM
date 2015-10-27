package ch.epfl.sweng.opengm.identification;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static ch.epfl.sweng.opengm.identification.InputUtils.INPUT_CORRECT;
import static ch.epfl.sweng.opengm.identification.InputUtils.INPUT_NOT_CASE_SENSITIVE;
import static ch.epfl.sweng.opengm.identification.InputUtils.INPUT_TOO_SHORT;
import static ch.epfl.sweng.opengm.identification.InputUtils.INPUT_WITHOUT_LETTER;
import static ch.epfl.sweng.opengm.identification.InputUtils.INPUT_WITHOUT_NUMBER;
import static ch.epfl.sweng.opengm.identification.InputUtils.isEmailValid;
import static ch.epfl.sweng.opengm.identification.InputUtils.isPasswordInvalid;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class InputUtilsTest {

    // Addresses taken from http://www.mkyong.com/regular-expressions/how-to-validate-email-address-with-regular-expression/

    @Test
    public void testAcceptGoodEmail(){
        List<String> validAddress = Arrays.asList("sweng@yahoo.com", "sweng-100@yahoo.com", "sweng.100@yahoo.com",
                "sweng111@sweng.com", "sweng-100@sweng.net", "sweng.100@sweng.com.au",
                "sweng@1.com", "sweng@gmail.com.com",
                "sweng+100@gmail.com", "sweng-100@yahoo-test.com");

        for (String email : validAddress) {
            assertTrue(isEmailValid(email));
        }

    }

    @Test
    public void testDeclineBadEmail() {
        assertFalse("must contains “@” symbol", isEmailValid("sweng"));
        assertFalse("tld can not start with dot .", isEmailValid("sweng@.com.my"));
        assertFalse(".a is not a valid tld, last tld must contains at least two characters", isEmailValid("sweng123@gmail.a"));
        assertFalse("tld can not start with dot .", isEmailValid("sweng123@.com"));
        assertFalse("tld can not start with dot .", isEmailValid("sweng123@.com.com"));
        assertFalse("email’s first character can not start with dot .", isEmailValid(".sweng@sweng.com"));
        assertFalse("email’s is only allow character, digit, underscore and dash", isEmailValid("sweng()*@gmail.com"));
        assertFalse("email’s tld is only allow character and digit", isEmailValid("sweng@%*.com"));
        assertFalse("double dots “.” are not allow", isEmailValid("sweng..2002@gmail.com"));
        assertFalse("email’s last character can not end with dot .", isEmailValid("sweng.@gmail.com"));
        assertFalse("double “@” is not allow", isEmailValid("sweng@sweng@gmail.com"));
        assertFalse("email’s tld which has two characters can not contains digit", isEmailValid("sweng@gmail.com.1a"));

    }

    @Test
    public void testDeclineEmailWithoutAt(){
        assertFalse(isEmailValid("goodmail.com"));
    }

    @Test
    public void testDeclineEmailWithoutDot(){
        assertFalse(isEmailValid("good@mailcom"));
    }

    @Test
    public void testDeclineEmailWithoutDomain(){
        assertFalse(isEmailValid("good@mail"));
    }

    @Test
    public void testDeclineBadChars(){
        assertFalse(isEmailValid("góód@maił.cóm"));
    }

    @Test
    public void testAcceptGoodPassword(){
        assertEquals(INPUT_CORRECT, isPasswordInvalid("GoodPassword1"));
    }

    @Test
    public void testDeclineShortPassword(){
        assertEquals(INPUT_TOO_SHORT, isPasswordInvalid("goodpa"));
    }

    @Test
    public void testDeclinePasswordWithoutNumber() {
        assertEquals(INPUT_WITHOUT_NUMBER, isPasswordInvalid("GoodPassword"));
    }

    @Test
    public void testDeclinePasswordNotBothCases() {
        assertEquals(INPUT_NOT_CASE_SENSITIVE, isPasswordInvalid("1goodpassword"));
    }

    @Test
    public void testDeclinePasswordOnlyNumbers(){
        assertEquals(INPUT_WITHOUT_LETTER, isPasswordInvalid("123456789"));
    }
}
