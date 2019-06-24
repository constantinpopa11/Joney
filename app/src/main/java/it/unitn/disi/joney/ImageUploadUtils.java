package it.unitn.disi.joney;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ImageUploadUtils {

    static boolean isPicEmpty;
    //Pick image from gallery || camera
    public static void showPictureOptionDialog(final Context context, final Activity activity, final int clickedImgIndex, final String path) {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(context);
        pictureDialog.setTitle("Select Action");
        isPicEmpty = true;
        List<String> pictureDialogOptions = new ArrayList<>();

        pictureDialogOptions.add("Select photo from gallery");
        pictureDialogOptions.add("Capture photo from camera");

        if (clickedImgIndex > -1) {
            pictureDialogOptions.add("Remove picture");
            isPicEmpty = false;
        }
        if (AccessToken.getCurrentAccessToken() != null && path.equals(Constants.PATH_USER_PROFILE_IMAGES))
            pictureDialogOptions.add("Use Facebook profile image");

        pictureDialog.setItems(pictureDialogOptions.toArray(new String[pictureDialogOptions.size()]),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                //choose pic from gallery
                                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                ((PictureUploadListener) activity).onChangePicture(clickedImgIndex);
                                activity.startActivityForResult(galleryIntent, Constants.UPLOAD_FROM_GALLERY);
                                break;
                            case 1:
                                //take photo from camera
                                File wallpaperDirectory = new File(
                                        Environment.getExternalStorageDirectory().toString() + path);
                                if (!wallpaperDirectory.exists()) {
                                    wallpaperDirectory.mkdirs();
                                }
                                File tempImg = new File(Environment.getExternalStorageDirectory(), path + "temp.jpg");
                                tempImg.delete();
                                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                ((PictureUploadListener) activity).onChangePicture(clickedImgIndex);
                                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                                StrictMode.setVmPolicy(builder.build());
                                cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempImg));
                                activity.startActivityForResult(cameraIntent, Constants.UPLOAD_FROM_CAMERA);
                                break;
                            case 2:
                                // if facebook user
                                if (AccessToken.getCurrentAccessToken() != null) {
                                    if(isPicEmpty) {
                                        Intent userIntent = new Intent(context, UserProfileActivity.class);
                                        userIntent.putExtra("FromFacebook", true);
                                        Log.d("Facebook", "ok");
                                        activity.startActivity(userIntent);
                                        //activity.finish();
                                        break;
                                    } else {
                                        //remove pic
                                        ((PictureUploadListener) activity).onRemovePicture(clickedImgIndex);
                                    }
                                } else {
                                    //remove pic
                                    ((PictureUploadListener) activity).onRemovePicture(clickedImgIndex);
                                }
                                break;
                            case 3:
                                //load from facebook
                                Intent userIntent = new Intent(context, UserProfileActivity.class);
                                userIntent.putExtra("FromFacebook", true);
                                Log.d("Facebook", "ok");
                                activity.startActivity(userIntent);
                                //activity.finish();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }


    public static String saveImage(Context context, Bitmap bitmap, String savePath) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory().toString() + savePath);
        //Toast.makeText(getApplicationContext(), Environment.getExternalStorageDirectory().toString() + "/post_job_image/", Toast.LENGTH_SHORT).show();
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
            //Toast.makeText(context, "Failed to create directory. Grant storage permission.", Toast.LENGTH_LONG).show();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(context,
                    new String[]{f.getPath()},
                    new String[]{"image/jpg"}, null);
            fo.close();
            //Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
            //Toast.makeText(context, "Failed to save image. Grant storage permission.", Toast.LENGTH_LONG).show();
        }
        return "";
    }
}
