package it.unitn.disi.joney;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Random;

public class ResetPasswordActivity extends AppCompatActivity {

    Button btnSendPassword, btnLogin;
    EditText etEmail;

    DatabaseHandler db = new DatabaseHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        //fixing view when keyboard appear
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        btnSendPassword = (Button) findViewById(R.id.btn_send_email);
        btnLogin = (Button) findViewById(R.id.btn_login);
        etEmail = (EditText) findViewById(R.id.email);
        btnSendPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                if(email.length() == 0)
                {
                    Toast.makeText(getApplicationContext(), "Please write your email", Toast.LENGTH_SHORT).show();
                }
                else if (!isValidEmail(email))
                {
                    Toast.makeText(getApplicationContext(), "Not a correct email address", Toast.LENGTH_SHORT).show();
                }
                else {
                    User user = db.getUserByEmail(email);
                    if(user != null) {
                        String password = "joney2019";
                        db.updatePassword(email,Constants.md5(password));
                        Toast.makeText(getApplicationContext(), "New password: joney2019", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "There is no account with this email", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        //Login Button opens back the Login Activity
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intLogin = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                startActivity(intLogin);
            }
        });
    }

    //Check Email validity
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

}
