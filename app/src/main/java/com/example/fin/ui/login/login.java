package com.example.fin.ui.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fin.ui.main.MainActivity;
import com.example.fin.R;
import com.example.fin.ToastUtil;


public class login extends AppCompatActivity {
    private static String username = "admin";
    private static String password = "123qwe";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        EditText editText1 = (EditText) findViewById(R.id.user);//账号输入框
        EditText editText2 = (EditText) findViewById(R.id.password);//密码输入框
        Button login = (Button) findViewById(R.id.loading);//登录按钮
        init();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(editText1.getText().toString())){
                    ToastUtil.missToast(getApplicationContext());
                    ToastUtil.showToast(login.this,"请输入账号", Toast.LENGTH_LONG);
                }
                else if(TextUtils.isEmpty(editText2.getText().toString())){
                    ToastUtil.missToast(getApplicationContext());
                    ToastUtil.showToast(login.this,"请输入密码", Toast.LENGTH_LONG);
                }
                else if(editText1.getText().toString().equals(username) && editText2.getText().toString().equals(password)){
                    Intent intent = new Intent(login.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    ToastUtil.missToast(getApplicationContext());
                    ToastUtil.showToast(getApplicationContext(), "登录成功！", Toast.LENGTH_SHORT);
                }
                else {
                    ToastUtil.missToast(getApplicationContext());
                    ToastUtil.showToast(getApplicationContext(), "密码错误，请重新输入", Toast.LENGTH_SHORT);
                }
            }
        });
    }
    public void init() {
        SharedPreferences sp = getApplication().getSharedPreferences("com.example.fin_preferences", Context.MODE_PRIVATE);
        boolean login = sp.getBoolean("login", false);
        username = sp.getString("username", username);
        password = sp.getString("password", password);
        // 自动登录
        if (login) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}