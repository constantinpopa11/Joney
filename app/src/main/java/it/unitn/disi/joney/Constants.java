package it.unitn.disi.joney;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Constants {
    private static Constants instance;

    // Global variables
    public static final String PREF_REMEMBER_ME = "is_user_logged_in";
    public static final String PREF_CURRENT_USER_ID = "current_user_id";
    public static final int NO_USER_LOGGED_IN = -1;

    public static final int INVALID_ITEM_VALUE = -1;
    public static final String NO_SPINNER_SELCTION = "No selection";

    public static final int POSTED_JOB_TAB = 0;
    public static final int PENDING_JOB_TAB = 1;
    public static final int COMPLETED_JOB_TAB = 2;

    public static final int GALLERY = 0;
    public static final int CAMERA = 1;

    public static final String PATH_TICKET_IMAGES = "/Joney/ticket_image/";
    public static final String PATH_JOB_IMAGES = "/Joney/post_job_image/";


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

    public double getDistance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(lat1 * Math.PI / 180.0)
                * Math.sin(lat2 * Math.PI / 180.0)
                + Math.cos(lat1 * Math.PI / 180.0)
                * Math.cos(lat2 * Math.PI / 180.0)
                * Math.cos(theta * Math.PI / 180.0);
        dist = Math.acos(dist);
        dist = dist * 180.0 / Math.PI;
        dist = dist * 60 * 1.1515;
        return (dist);
    }
}
