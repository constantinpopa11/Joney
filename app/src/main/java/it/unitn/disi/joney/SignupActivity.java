package it.unitn.disi.joney;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignupActivity extends AppCompatActivity {

    DatabaseHandler db = new DatabaseHandler(this);

    Button btnSignup;
    TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //fixing view when keyboard appear
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        btnSignup = (Button) findViewById(R.id.btn_signup);
        tvLogin = (TextView) findViewById(R.id.tv_login);

        //Signup Button create a new entry in the database
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName, lastName, email, password, repeatPassword;
                EditText etFirstName, etLastName, etEmail, etPassword, etRepeatPassword;
                CheckBox cbTos;

                etFirstName = (EditText) findViewById(R.id.first_name);
                firstName = etFirstName.getText().toString();
                etLastName = (EditText) findViewById(R.id.last_name);
                lastName = etLastName.getText().toString();
                etEmail = (EditText) findViewById(R.id.email);
                email = etEmail.getText().toString();
                etPassword = (EditText) findViewById(R.id.password);
                password = etPassword.getText().toString();
                etRepeatPassword = (EditText) findViewById(R.id.repeat_password);
                repeatPassword = etRepeatPassword.getText().toString();
                cbTos = (CheckBox) findViewById(R.id.tos);

                if(firstName.length() == 0 || lastName.length() == 0 || email.length() == 0 || password.length() == 0 || repeatPassword.length() == 0)
                    Toast.makeText(getApplicationContext(), "You must fill every field!", Toast.LENGTH_SHORT).show();
                else {
                    if (isValidEmail(email)) {
                        if (isValidPassword(password)) {
                            if (password.equals(repeatPassword)) {
                                if(cbTos.isChecked()) {
                                    password = Constants.md5(password);

                                    User user = new User(email, firstName, lastName, password);
                                    db.addUser(user);

                                    Intent intLogin = new Intent(SignupActivity.this, LoginActivity.class);
                                    startActivity(intLogin);
                                    finish();
                                }
                                else
                                    Toast.makeText(getApplicationContext(), "You must accept the Terms of Service!", Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(getApplicationContext(), "Passwords are not the same!", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(getApplicationContext(), "Password must be at least 8 character long and must contain at least 1 number", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(getApplicationContext(), "Not valid Email Address!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //Login textView opens back the Login Activity
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intLogin = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intLogin);
            }
        });
    }

    //Check Email validity
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static boolean isValidPassword(String p)    {
        boolean valid = false;

        if(p.length() > 7)
            for(int i = 0;i < p.length();i++)
                if(Character.isDigit(p.charAt(i)))
                    valid = true;
        return valid;
    }

    @Override
    public void onBackPressed() {
        //do nothing
    }
}
