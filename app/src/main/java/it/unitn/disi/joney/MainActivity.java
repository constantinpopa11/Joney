package it.unitn.disi.joney;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button btnLogin, btnSignup;
    TextView tvForgotPassword;

    SQLiteDatabase dbJoney;
    Constants c = Constants.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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

                String checkUser = "SELECT Password FROM USER WHERE Email='"+email+"';";
                SQLiteDatabase dbJoney = openOrCreateDatabase(c.getDbName(),MODE_PRIVATE,null);


                Cursor resultSet = dbJoney.rawQuery(checkUser,null);
                resultSet.moveToFirst();
                if(resultSet.getCount() != 0)
                {
                    dbPassword = resultSet.getString(0);
                    password = c.md5(password);
                    if (password.equals(dbPassword))
                        Toast.makeText(getApplicationContext(), "Login ok!", Toast.LENGTH_SHORT).show();
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
                Intent intSignup = new Intent(MainActivity.this,SignupActivity.class);
                startActivity(intSignup);
            }
        });

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intResetPassword = new Intent(MainActivity.this,ResetPasswordActivity.class);
                startActivity(intResetPassword);
            }
        });


    }

    public void createDatabase()
    {
        dbJoney = openOrCreateDatabase(c.getDbName(),MODE_PRIVATE,null);
        dbJoney.execSQL(c.getCreateTableUser());
        dbJoney.execSQL(c.getCreateTableJobCategory());
        dbJoney.execSQL(c.getCreateTableJob());
        dbJoney.execSQL(c.getCreateTableJobImage());
        dbJoney.execSQL(c.getCreateTableJobCandidate());
        dbJoney.execSQL(c.getCreateTableFeedback());
        dbJoney.execSQL(c.getCreateTableTicket());
        dbJoney.execSQL(c.getCreateTableTicketImage());

        Toast.makeText(getApplicationContext(), "Database created!", Toast.LENGTH_SHORT).show();
    }


}
