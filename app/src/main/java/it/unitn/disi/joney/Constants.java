package it.unitn.disi.joney;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Constants {
    private static Constants instance;

    // Global variables
    public static final String PREF_REMEMBER_ME = "is_user_logged_in";
    public static final String PREF_CURRENT_USER_ID = "current_user_id";
    public static final int NO_USER_LOGGED_IN = -1;

    public static final int INVALID_JOB_CATEGORY = -1;
    public static final String NO_JOB_CATEGORY_SELECTED = "No selection";

    //hashing function
    public static String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
