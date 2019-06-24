package it.unitn.disi.joney;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import static it.unitn.disi.joney.ImageUploadUtils.saveImage;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin, btnSignup;
    TextView tvForgotPassword;
    EditText etEmail;
    DatabaseHandler db = new DatabaseHandler(this);
    Context mContext;
    SharedPreferences prefs;

    //Facebook Login
    CallbackManager cbm = CallbackManager.Factory.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mContext = this;
        boolean isLoggedIn = prefs.getBoolean(Constants.PREF_REMEMBER_ME, false); // get value of last login status
        int currentUserId = prefs.getInt(Constants.PREF_CURRENT_USER_ID, Constants.NO_USER_LOGGED_IN);

        //login user and redirect to home
        if (isLoggedIn && currentUserId != Constants.NO_USER_LOGGED_IN) {
            Log.i("Already logged in ", isLoggedIn + " " + currentUserId);
            Intent intHome = new Intent(LoginActivity.this, HomeActivity.class);
            intHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intHome);
        }

        //otherwise show login form

        btnLogin = (Button) findViewById(R.id.btn_login);
        btnSignup = (Button) findViewById(R.id.btn_signup);
        View wrapper = (View) findViewById(R.id.forgot_password_wrapper);
        tvForgotPassword = (TextView) wrapper.findViewById(R.id.forgot_password);

        etEmail = (EditText) findViewById(R.id.email);
        //if coming from signup put email directly
        if(getIntent().hasExtra("email"))
            etEmail.setText(getIntent().getStringExtra("email"));
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String email, password;
                EditText etPassword;
                CheckBox cbRememberMe;


                email = etEmail.getText().toString();
                etPassword = (EditText) findViewById(R.id.password);
                password = etPassword.getText().toString();
                cbRememberMe = (CheckBox) findViewById(R.id.cb_remember_me);

                if (email.length() == 0 || password.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please fill in the fields first", Toast.LENGTH_SHORT).show();
                } else {
                    User user = db.getUserByEmail(email);
                    if (user != null) {
                        password = Constants.md5(password);
                        if (password.equals(user.getPassword())) {
                            if (cbRememberMe.isChecked()) {
                                prefs.edit().putBoolean(Constants.PREF_REMEMBER_ME, true).commit();
                            } else {
                                prefs.edit().putBoolean(Constants.PREF_REMEMBER_ME, false).commit();
                            }
                            prefs.edit().putInt(Constants.PREF_CURRENT_USER_ID, user.getId()).commit();
                            //Toast.makeText(getApplicationContext(), "Successfully logged in!", Toast.LENGTH_SHORT).show();
                            Intent intHome = new Intent(LoginActivity.this, HomeActivity.class);
                            intHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intHome);
                        } else
                            Toast.makeText(getApplicationContext(), "Wrong password!", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(getApplicationContext(), "Account does not exist!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intSignup = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intSignup);
            }
        });

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intResetPassword = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                startActivity(intResetPassword);
            }
        });

        //Facebook Login
        LoginManager.getInstance().registerCallback(cbm,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        //Toast.makeText(getApplicationContext(), "Successfully logged in with FB!", Toast.LENGTH_SHORT).show();
                        String accessToken = loginResult.getAccessToken().getToken();
                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                               // Toast.makeText(mContext, "Going", Toast.LENGTH_SHORT).show();
                                /*try {
                                    if (object.has("first_name"))
                                        Toast.makeText(getApplicationContext(), object.getString("first_name"), Toast.LENGTH_SHORT).show();
                                    if (object.has("last_name"))
                                        Toast.makeText(getApplicationContext(), object.getString("last_name"), Toast.LENGTH_SHORT).show();
                                    if (object.has("email"))
                                        Toast.makeText(getApplicationContext(), object.get("email").toString(), Toast.LENGTH_SHORT).show();
                                } catch (Exception e)
                                {
                                    Log.d("Error",e.getMessage().toString());
                                    Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT).show();

                                }*/
                                User user = null;
                                if (object.has("email")) {
                                    try {
                                        user = db.getUserByEmail(object.getString("email"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else if (object.has("id")) {
                                    try {
                                        user = db.getUserByEmail(object.getString("id"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                //if account exist
                                if (user != null) {
                                    prefs.edit().putBoolean(Constants.PREF_REMEMBER_ME, true).commit();
                                    prefs.edit().putInt(Constants.PREF_CURRENT_USER_ID, user.getId()).commit();
                                    //Toast.makeText(getApplicationContext(), "Successfully logged in with Facebook!", Toast.LENGTH_SHORT).show();
                                    Intent intHome = new Intent(LoginActivity.this, HomeActivity.class);
                                    intHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intHome);
                                } else {
                                    String email = null, firstName = null, lastName = null, id = null, profile_pic = null;
                                    try {
                                        if (object.has("first_name"))
                                            firstName = object.getString("first_name");
                                        if (object.has("first_name"))
                                            lastName = object.getString("last_name");
                                        if (object.has("email"))
                                            email = object.getString("email");
                                        else if (object.has("id"))
                                            email = object.getString("id");
                                        id = object.getString("id");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    //if info are missing let him create an account normally
                                    if(email == null || firstName == null || lastName == null) {
                                        Toast.makeText(getApplicationContext(), "Information are missing, create an account!", Toast.LENGTH_SHORT).show();
                                        Intent intSignup = new Intent(LoginActivity.this, SignupActivity.class);
                                        startActivity(intSignup);
                                    } else {
                                        user = new User(email, firstName, lastName, generateRandomPassword());
                                        db.addUser(user);
                                        user = db.getUserByEmail(email);
                                        prefs.edit().putBoolean(Constants.PREF_REMEMBER_ME, true).commit();
                                        prefs.edit().putInt(Constants.PREF_CURRENT_USER_ID, user.getId()).commit();
                                        //Toast.makeText(getApplicationContext(), "Successfully logged in with Facebook!", Toast.LENGTH_SHORT).show();
                                        if(id != null) {
                                            profile_pic = "https://graph.facebook.com/" + id + "/picture?type=large";
                                            //Log.i("profile_pic", profile_pic + "");

                                            if (Utils.isStoragePermissionGranted(getApplicationContext()))
                                                new saveProfileImage(user.getId()).execute(profile_pic);
                                        }
                                        //Toast.makeText(getApplicationContext(),String.valueOf(user.getId()),Toast.LENGTH_SHORT).show();

                                        Intent intHome = new Intent(LoginActivity.this, HomeActivity.class);
                                        intHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intHome);
                                    }

                                }
                            }
                        });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,first_name,last_name,email");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        //Toast.makeText(getApplicationContext(), "Unsuccessfully logged in!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        //Toast.makeText(getApplicationContext(), exception.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        cbm.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        //do nothing
    }

    public String generateRandomPassword() {
        String randPass = UUID.randomUUID().toString();
        //Toast.makeText(getApplicationContext(), "Password: " + randPass, Toast.LENGTH_SHORT).show();
        return randPass;

    }

    private class saveProfileImage extends AsyncTask<String, Void, Bitmap> {
        int currentUserId;
        public saveProfileImage(int currentUserId) {
            this.currentUserId = currentUserId;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            String path = saveImage(getApplicationContext(), result, Constants.PATH_USER_PROFILE_IMAGES);
           // Toast.makeText(getApplicationContext(),"PATH:" + path,Toast.LENGTH_SHORT).show();
            UserProfileImage userProfileImage = new UserProfileImage(path, currentUserId);
            db.addUserProfileImage(userProfileImage);
        }
    }
}
