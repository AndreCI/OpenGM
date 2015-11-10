package ch.epfl.sweng.opengm.identification.phoneNumber;

public class CountryCode implements Comparable<CountryCode> {

    private final String mCountry;
    private final String mCode;

    public CountryCode(String s) {
        String[] extracted = s.split(";");
        mCode = "+" + extracted[0];
        mCountry = extracted[1];
    }

    public String getCountry() {
        return mCountry;
    }

    public String getCode() {
        return mCode;
    }

    @Override
    public int compareTo(CountryCode another) {
        return mCountry.compareTo(another.mCountry);
    }

    @Override
    public String toString() {
        return mCountry + " : " + mCode;
    }
}
