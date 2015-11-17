package ch.epfl.sweng.opengm.identification.phoneNumber;

import java.util.ArrayList;
import java.util.List;

public class CountryCode implements Comparable<CountryCode> {

    private final String mCountry;
    private final String mCode;
    private final List<String> mIso;


    public CountryCode(String s) {
        String[] extracted = s.split(";");
        mCode = "+" + extracted[0];
        mCountry = extracted[1];
        mIso = new ArrayList<>();
        String[] iso = extracted[2].split("/");
        for (int i = 0; i < iso.length; i++)
            mIso.add(iso[i].trim());
    }

    public String getCountry() {
        return mCountry;
    }

    public String getCode() {
        return mCode;
    }

    public boolean containsIso(String iso) {
        return mIso.contains(iso.toUpperCase());
    }

    @Override
    public int compareTo(CountryCode another) {
        return mCountry.compareTo(another.mCountry);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CountryCode that = (CountryCode) o;

        if (mCountry != null ? !mCountry.equals(that.mCountry) : that.mCountry != null)
            return false;
        return mCode.equals(that.mCode);

    }

    @Override
    public int hashCode() {
        int result = mCountry != null ? mCountry.hashCode() : 0;
        result = 31 * result + mCode.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return mCountry + " : " + mCode;
    }
}
