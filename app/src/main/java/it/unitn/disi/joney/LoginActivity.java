package it.unitn.disi.joney;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin, btnSignup;
    TextView tvForgotPassword;
    DatabaseHandler db = new DatabaseHandler(this);
    Context mContext;

    //Facebook Login
    CallbackManager cbm = CallbackManager.Factory.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mContext = this;
        boolean isLoggedIn = prefs.getBoolean(Constants.PREF_REMEMBER_ME, false); // get value of last login status
        int currentUserId = prefs.getInt(Constants.PREF_CURRENT_USER_ID, Constants.NO_USER_LOGGED_IN);

        //login user and redirect to home
        if(isLoggedIn && currentUserId != Constants.NO_USER_LOGGED_IN) {
            Log.i("Already logged in ", isLoggedIn + " " + currentUserId);
            Intent intHome = new Intent(LoginActivity.this, MyJobsActivity.class);
            startActivity(intHome);
            finish();
        }

        //otherwise show login form

        btnLogin = (Button) findViewById(R.id.btn_login);
        btnSignup = (Button) findViewById(R.id.btn_signup);
        View wrapper = (View) findViewById(R.id.forgot_password_wrapper);
        tvForgotPassword = (TextView) wrapper.findViewById(R.id.forgot_password);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String email, password;
                EditText etEmail, etPassword;
                CheckBox cbRememberMe;

                etEmail = (EditText) findViewById(R.id.email);
                email = etEmail.getText().toString();
                etPassword = (EditText) findViewById(R.id.password);
                password = etPassword.getText().toString();
                cbRememberMe = (CheckBox) findViewById(R.id.cb_remember_me);

                if(email.length() == 0 || password.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please fill in the fields first", Toast.LENGTH_SHORT).show();
                } else {
                    User user = db.getUserByEmail(email);
                    if(user != null) {
                        password = Constants.md5(password);
                        if (password.equals(user.getPassword())) {
                            if(cbRememberMe.isChecked()) {
                                prefs.edit().putBoolean(Constants.PREF_REMEMBER_ME, true).commit();
                            } else {
                                prefs.edit().putBoolean(Constants.PREF_REMEMBER_ME, false).commit();
                            }
                            prefs.edit().putInt(Constants.PREF_CURRENT_USER_ID, user.getId()).commit();
                            Toast.makeText(getApplicationContext(), "Successfully logged in!", Toast.LENGTH_SHORT).show();
                            Intent intHome = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intHome);
                            finish();
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
                Intent intSignup = new Intent(LoginActivity.this,SignupActivity.class);
                startActivity(intSignup);
            }
        });

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intResetPassword = new Intent(LoginActivity.this,ResetPasswordActivity.class);
                startActivity(intResetPassword);
            }
        });

        //Facebook Login
        LoginManager.getInstance().registerCallback(cbm,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        Toast.makeText(getApplicationContext(), "Successfully logged in with FB!", Toast.LENGTH_SHORT).show();
                        String accessToken = loginResult.getAccessToken().getToken();

                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Toast.makeText(mContext, "Going", Toast.LENGTH_SHORT).show();
                                try {
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
                        Toast.makeText(getApplicationContext(), "Unsuccessfully logged in!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Toast.makeText(getApplicationContext(), "Porco dio!", Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(), exception.getMessage().toString(), Toast.LENGTH_SHORT).show();
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
}
