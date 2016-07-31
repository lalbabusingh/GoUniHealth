package com.health.gounihealth.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
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

    private EditText edtMobileNo, edtEmailId, edtPassword,edtConfirmPassword;
    private TextInputLayout inputLayoutMobileNo, inputLayoutEmailId, inputLayoutPassword,inputLayoutConfirmPassword;
    private Toolbar toolbar;
    private TextView txtAgree;
    private Button btnRegistration,btnLogin;
    public static RegistrationActivity mRegistrationActivity;

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
       // edtMobileNo.setCompoundDrawables(null, null, ContextCompat.getDrawable(RegistrationActivity.this,R.mipmap.line), null);
        edtEmailId = (EditText) findViewById(R.id.edtEmailId);
        edtEmailId.setTypeface(CommonMethods.getLatoLightFont(this));
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        edtPassword.setTypeface(CommonMethods.getLatoLightFont(this));
        edtConfirmPassword = (EditText) findViewById(R.id.edtConfirmPassword);
        edtConfirmPassword.setTypeface(CommonMethods.getLatoLightFont(this));
        txtAgree = (TextView) findViewById(R.id.txtAgree);

        btnRegistration = (Button)findViewById(R.id.btnRegister);
        btnRegistration.setTypeface(CommonMethods.getLatoHeavyFont(this));
        btnLogin = (Button)findViewById(R.id.btnLogin);
        btnLogin.setTypeface(CommonMethods.getLatoHeavyFont(this));

        mRegistrationActivity = this;

        inputLayoutMobileNo = (TextInputLayout) findViewById(R.id.inputLayoutMobileNo);
        inputLayoutEmailId = (TextInputLayout) findViewById(R.id.inputLayoutEmailId);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.inputLayoutPassword);
        inputLayoutConfirmPassword = (TextInputLayout) findViewById(R.id.inputLayoutConfirmPassword);

        edtMobileNo.addTextChangedListener(new MyTextWatcher(edtMobileNo));
        edtEmailId.addTextChangedListener(new MyTextWatcher(edtEmailId));
        edtPassword.addTextChangedListener(new MyTextWatcher(edtPassword));
        edtConfirmPassword.addTextChangedListener(new MyTextWatcher(edtConfirmPassword));

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
       // collapsingToolbar.setTitle(getString(R.string.labelResister));
        collapsingToolbar.setCollapsedTitleTypeface(CommonMethods.getLatoHeavyFont(this));
        collapsingToolbar.setCollapsedTitleTextColor(ContextCompat.getColor(this, R.color.White));
       // collapsingToolbar.setTitleEnabled(false);

       /* AppBarLayout appBarLayout = (AppBarLayout)findViewById(R.id.appBarLayout);
        appBarLayout.setExpanded(false);*/

    }

   /* private void collapsingToolBar(){
        final Toolbar tool = (Toolbar)findViewById(R.id.toolbar);
        CollapsingToolbarLayout c = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);
        AppBarLayout appbar = (AppBarLayout)findViewById(R.id.appBarLayout);
        tool.setTitle("");
        setSupportActionBar(tool);
        c.setTitleEnabled(false);
        appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isVisible = true;
            int scrollRange = -1;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    tool.setTitle("Title");
                    isVisible = true;
                } else if(isVisible) {
                    tool.setTitle("");
                    isVisible = false;
                }
            }
        });
    }*/

    private void setSpanString() {
        SpannableString spannableString = new SpannableString(txtAgree.getText().toString());
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Toast.makeText(RegistrationActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void updateDrawState(TextPaint textPaint) {
                textPaint.setColor(ContextCompat.getColor(RegistrationActivity.this, R.color.rectangleBG));
                textPaint.setUnderlineText(true);
            }
        };
        spannableString.setSpan(span, 33, 50, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        txtAgree.setText(spannableString);
        txtAgree.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void onRegisterClick(View view) {
       /* Intent intent = new Intent(RegistrationActivity.this, AddMedicalInsurance.class);
        // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);*/
        if (CommonMethods.isConnected(this)) {
            if (validateMobileNo() && validateEmail() && validatePassword() && validateConfirmPassword()) {
                Intent intent = new Intent(RegistrationActivity.this, MyProfileActivity.class);
                intent.putExtra("MOBILE",edtMobileNo.getText().toString().trim());
                intent.putExtra("EMAIL",edtEmailId.getText().toString().trim());
                intent.putExtra("PASSWORD",edtPassword.getText().toString().trim());
                intent.putExtra("CONFIRMPASSWORD",edtConfirmPassword.getText().toString().trim());
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
        }if(edtMobileNo.getText().toString().trim().length() < 10){
            inputLayoutMobileNo.setError(getString(R.string.validMobile));
            requestFocus(edtMobileNo);
            return false;
        }else {
            inputLayoutMobileNo.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateEmail() {
        String email = edtEmailId.getText().toString().trim();
        if (email.isEmpty()) {
            inputLayoutEmailId.setError(getString(R.string.emptyEmail));
            requestFocus(edtEmailId);
            return false;
        }if(!isValidEmail(email)) {
            inputLayoutEmailId.setError(getString(R.string.validEmail));
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
        }if(edtPassword.getText().toString().trim().length() < 6){
            inputLayoutPassword.setError(getString(R.string.validationPassword));
            requestFocus(edtPassword);
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateConfirmPassword() {
        if (edtConfirmPassword.getText().toString().trim().isEmpty()
                || !edtPassword.getText().toString().trim().equals(edtConfirmPassword.getText().toString().trim())) {
            inputLayoutConfirmPassword.setError(getString(R.string.validateConfirmPassword));
            requestFocus(edtConfirmPassword);
            return false;
        }else {
            inputLayoutConfirmPassword.setErrorEnabled(false);
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
                case R.id.edtConfirmPassword:
                    validateConfirmPassword();
                    break;
            }
        }
    }
    public static RegistrationActivity getInstance(){
        return mRegistrationActivity;

    }
}
