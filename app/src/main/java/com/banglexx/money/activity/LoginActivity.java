package com.banglexx.money.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.banglexx.money.R;
import com.banglexx.money.databinding.ActivityLoginBinding;
import com.banglexx.money.model.LoginResponse;
import com.banglexx.money.retrofit.ApiEndpoint;
import com.banglexx.money.retrofit.ApiService;
import com.banglexx.money.retrofit.ErrorUtil;
import com.banglexx.money.sharedpreferences.PreferencesManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding binding;
    private PreferencesManager pref;
    private final ApiEndpoint api = ApiService.endpoint();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        pref = new PreferencesManager(this);

//        setupTest();
        setupListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        showProgress(false);
    }

    private void setupTest(){
        binding.editEmail.setText("irsyad@gmail.com");
        binding.editPassword.setText("password");
    }

    private void setupListener(){
        binding.buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRequired()) {
                    showProgress(true);
                    api.login(
                            binding.editEmail.getText().toString(),
                            binding.editPassword.getText().toString()
                    ).enqueue(new Callback<LoginResponse>() {
                        @Override
                        public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                            showProgress(false);
                            if (response.isSuccessful()) {
                                saveLogin( response.body().getData() );
                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                finish();
                            } else {
                                showMessage(ErrorUtil.getMessage(response));
                            }
                        }
                        @Override
                        public void onFailure(Call<LoginResponse> call, Throwable t) {
                            showProgress(false);
                        }
                    });
                } else {
                   showMessage("Isi data dengan benar");
                }
            }
        });
        binding.textRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });
    }

    private Boolean isRequired() {
        return (binding.editEmail.getText() != null && binding.editPassword.getText() != null);
    }

    private void showMessage(String message) {
        Toast.makeText( LoginActivity.this, message, Toast.LENGTH_SHORT ).show();
    }

    private void showProgress(Boolean progress) {
        if (progress) {
            binding.progress.setVisibility(View.VISIBLE);
            binding.buttonLogin.setEnabled(false);
        } else {
            binding.progress.setVisibility(View.GONE);
            binding.buttonLogin.setEnabled(true);
        }
    }

    private void saveLogin(LoginResponse.Data data){
        pref.put("pref_is_login", true);
        pref.put("pref_user_id", data.getId());
        pref.put("pref_user_name", data.getName());
        pref.put("pref_user_email", data.getEmail());
        pref.put("pref_user_date", data.getDate());
        if (pref.getInt("pref_user_avatar") == 0 ) pref.put("pref_user_avatar", R.drawable.avatar2);
    }
}