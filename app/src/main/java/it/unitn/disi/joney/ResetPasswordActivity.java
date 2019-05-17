package it.unitn.disi.joney;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class ResetPasswordActivity extends AppCompatActivity {

    Button btnSendPassword, btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        //fixing view when keyboard appear
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        btnSendPassword = (Button) findViewById(R.id.btn_send_email);
        btnLogin = (Button) findViewById(R.id.btn_login);

        btnSendPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Fuck yourself!", Toast.LENGTH_SHORT).show();
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
}
