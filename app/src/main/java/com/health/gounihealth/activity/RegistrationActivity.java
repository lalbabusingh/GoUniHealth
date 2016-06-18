package com.health.gounihealth.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.health.gounihealth.R;
import com.health.gounihealth.utils.CommonMethods;

/**
 * Created by LAL on 6/7/2016.
 */
public class RegistrationActivity extends AppCompatActivity {

    private EditText edtMobileNo, edtEmailId, edtPassword;
    private TextInputLayout inputLayoutMobileNo, inputLayoutEmailId, inputLayoutPassword;
    private Toolbar toolbar;
    private TextView txtAgree;
    private Button btnRegistration,btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_layout);

        initialization();
        setSpanString();


    }

    private void initialization() {
        edtMobileNo = (EditText) findViewById(R.id.edtMobileNo);
        edtMobileNo.setTypeface(CommonMethods.getLatoLightFont(this));
        edtEmailId = (EditText) findViewById(R.id.edtEmailId);
        edtEmailId.setTypeface(CommonMethods.getLatoLightFont(this));
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        edtPassword.setTypeface(CommonMethods.getLatoLightFont(this));
        txtAgree = (TextView) findViewById(R.id.txtAgree);

        btnRegistration = (Button)findViewById(R.id.btnRegister);
        btnRegistration.setTypeface(CommonMethods.getLatoHeavyFont(this));
        btnLogin = (Button)findViewById(R.id.btnLogin);
        btnLogin.setTypeface(CommonMethods.getLatoHeavyFont(this));

        inputLayoutMobileNo = (TextInputLayout) findViewById(R.id.inputLayoutMobileNo);
        inputLayoutEmailId = (TextInputLayout) findViewById(R.id.inputLayoutEmailId);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.inputLayoutPassword);
        edtMobileNo.addTextChangedListener(new MyTextWatcher(edtMobileNo));
        edtEmailId.addTextChangedListener(new MyTextWatcher(edtEmailId));
        edtPassword.addTextChangedListener(new MyTextWatcher(edtPassword));

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(getString(R.string.labelResister));
        collapsingToolbar.setCollapsedTitleTypeface(CommonMethods.getLatoHeavyFont(this));
        collapsingToolbar.setCollapsedTitleTextColor(ContextCompat.getColor(this, R.color.White));

    }

    private void setSpanString() {
        SpannableString spannableString = new SpannableString(txtAgree.getText().toString());
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Toast.makeText(RegistrationActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
            }
        };
        spannableString.setSpan(span, 33, 50, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        txtAgree.setText(spannableString);
        txtAgree.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void onRegisterClick(View view) {
        if (CommonMethods.isConnected(this)) {
            if (validateMobileNo() && validateEmail() && validatePassword()) {
                Intent intent = new Intent(RegistrationActivity.this, MyProfileActivity.class);
                startActivity(intent);
                //finish();
            }
        } else {
            CommonMethods.showToastMessage(this, getString(R.string.noInternet));
        }

    }

    public void onLoginClick(View view) {
        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();

    }

    private boolean validateMobileNo() {
        if (edtMobileNo.getText().toString().trim().isEmpty()) {
            inputLayoutMobileNo.setError(getString(R.string.emptyMobile));
            requestFocus(edtMobileNo);
            return false;
        } else {
            inputLayoutMobileNo.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateEmail() {
        String email = edtEmailId.getText().toString().trim();
        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmailId.setError(getString(R.string.emptyEmail));
            requestFocus(edtEmailId);
            return false;
        } else {
            inputLayoutEmailId.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePassword() {
        if (edtPassword.getText().toString().trim().isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.emptyPassword));
            requestFocus(edtPassword);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }
        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {
        private View view;
        private MyTextWatcher(View view) {
            this.view = view;
        }
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.edtMobileNo:
                    validateMobileNo();
                    break;
                case R.id.edtEmailId:
                    validateEmail();
                    break;
                case R.id.edtPassword:
                    validatePassword();
                    break;
            }
        }
    }
    /**
     * Called to process touch screen events.
     *//*
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        View view = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);
        if (view instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];
            if (event.getAction() == MotionEvent.ACTION_UP
                    && (x < w.getLeft() || x >= w.getRight()
                    || y < w.getTop() || y > w.getBottom()) ) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }
        return ret;
    }*/
}
