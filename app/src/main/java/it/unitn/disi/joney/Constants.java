package it.unitn.disi.joney;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;

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

    public static final int UPLOAD_FROM_GALLERY = 0;
    public static final int UPLOAD_FROM_CAMERA = 1;
    public static final int REMOVE_PICTURE = 2;
    public static final int CROP_PIC = 3;

    public static final int RECEIVED_LOCATION = 69;

    public static final String OLD_IMG_VIEW_INDEX = "old_img_view_index";

    public static final int MAX_JOB_PICTURE_NUMBER = 4;
    public static final int MAX_TICKET_PICTURE_NUMBER = 4;

    public static final String PATH_TICKET_IMAGES = "/Joney/ticket_images/";
    public static final String PATH_TICKET_FULL_IMAGES = "/Joney/ticket_images/full_image/";
    public static final String PATH_JOB_IMAGES = "/Joney/post_job_images/";
    public static final String PATH_JOB_FULL_IMAGES = "/Joney/post_job_images/full_image/";
    public static final String PATH_USER_PROFILE_IMAGES = "/Joney/user_profile_images/";
    public static final String PATH_USER_PROFILE_FULL_IMAGES = "/Joney/user_profile_images/full_image/";

    public static final String MAPBOX_ACCESS_TOKEN = "pk.eyJ1IjoibG92YXoiLCJhIjoiY2p3bTdqMGszMDJtOTN6cGY0YjNoZmc0eCJ9.d8C4iygw-U5rGxRlHiPDzw";

    //hashing function
    public static String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }



}
