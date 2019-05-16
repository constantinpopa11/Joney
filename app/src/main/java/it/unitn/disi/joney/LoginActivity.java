package it.unitn.disi.joney;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin, btnSignup;
    TextView tvForgotPassword;

    SQLiteDatabase dbJoney;

    //Facebook Login
    CallbackManager cbm = CallbackManager.Factory.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        dbJoney = openOrCreateDatabase(Constants.DB_NAME,MODE_PRIVATE,null);

        //fixing view when keyboard appear
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        createDatabase();

        btnLogin = (Button) findViewById(R.id.btn_login);
        btnSignup = (Button) findViewById(R.id.btn_signup);
        tvForgotPassword = (TextView) findViewById(R.id.forgot_password);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, password, dbPassword;
                EditText etEmail, etPassword;

                etEmail = (EditText) findViewById(R.id.email);
                email = etEmail.getText().toString();
                etPassword = (EditText) findViewById(R.id.password);
                password = etPassword.getText().toString();

                Cursor resultSet = dbJoney.rawQuery(
                        Queries.FETCH_PASSWORD_BY_EMAIL,
                        new String[] {email}
                );

                resultSet.moveToFirst();
                if(resultSet.getCount() != 0)
                {
                    dbPassword = resultSet.getString(0);
                    password = Constants.md5(password);
                    if (password.equals(dbPassword))
                        Toast.makeText(getApplicationContext(), "Successfully logged in!", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getApplicationContext(), "Wrong password!", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(getApplicationContext(), "Account does not exist!", Toast.LENGTH_SHORT).show();
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
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        cbm.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void createDatabase()
    {
        dbJoney = openOrCreateDatabase(Constants.DB_NAME,MODE_PRIVATE,null);
        dbJoney.execSQL(Queries.CREATE_TABLE_USERS);
        dbJoney.execSQL(Queries.CREATE_TABLE_JOB_CATEGORIES);
        dbJoney.execSQL(Queries.CREATE_TABLE_JOBS);
        dbJoney.execSQL(Queries.CREATE_TABLE_JOB_IMAGES);
        dbJoney.execSQL(Queries.CREATE_TABLE_JOB_CANDIDATES);
        dbJoney.execSQL(Queries.CREATE_TABLE_FEEDBACKS);
        dbJoney.execSQL(Queries.CREATE_TABLE_TICKETS);
        dbJoney.execSQL(Queries.CREATE_TABLE_TICKET_IMAGES);

        Toast.makeText(getApplicationContext(), "Database created!", Toast.LENGTH_SHORT).show();
    }


}
