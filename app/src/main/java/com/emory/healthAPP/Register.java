package com.emory.healthAPP;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 * Sign up screen control class
 *
 * @author Zixiang Xu
 * @version 1.0
 */
public class Register extends AppCompatActivity {

    // User data storage
    private HashMap<String, String> userData;

    // Activity resources
    private EditText username, password, confirmedPassword;
    private CardView usernameMask, passwordMask, confirmedPasswordMask;
    private RelativeLayout bottom_set, buttonSignUpMask, buttonGoBackMask;
    private ImageView emoryLogo01, emoryLogo02;
    private Button buttonSignUp, buttonGoBack;
    private AnimatorSet animatorSet;
    private DataSecurity security;

    /**
     * Create the activity of sign up page.
     *
     * @param savedInstanceState current state of current manifest
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        // Inherit data from SignInControl.java
        inherit();

        // Initialize activity views
        username = findViewById(R.id.SignUpControl_username);
        password = findViewById(R.id.SignUpControl_password);
        confirmedPassword = findViewById(R.id.SignUpControl_confirmed);
        usernameMask = findViewById(R.id.SignUpControl_username_mask);
        passwordMask = findViewById(R.id.SignUpControl_password_mask);
        confirmedPasswordMask = findViewById(R.id.SignUpControl_confirmed_mask);
        bottom_set = findViewById(R.id.SignUpControl_bottom_set);
        buttonSignUp = findViewById(R.id.SignUpControl_button01);
        buttonGoBack = findViewById(R.id.SignUpControl_button02);
        buttonSignUpMask = findViewById(R.id.SignUpControl_button01_mask);
        buttonGoBackMask = findViewById(R.id.SignUpControl_button02_mask);
        emoryLogo01 = findViewById(R.id.SignUpControl_emory_logo_01);
        emoryLogo02 = findViewById(R.id.SignUpControl_emory_logo_02);
        initialize();

        // Initialize data
        try {
            security = new DataSecurity();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Click the [Sign Up] button
        buttonSignUp.setOnClickListener(signUp -> {

            // Getting an iterator for the local userdata hashmap
            Iterator<Map.Entry<String, String>> userDataItr = userData.entrySet().iterator();

            // Set a sentinel signal for match username
            boolean match = false;

            // Get the username, password, and confirm username
            String currentUsername = username.getText().toString();
            String currentPassword = password.getText().toString();
            String currentConfirmPassword = confirmedPassword.getText().toString();

            // If password != confirm username, retry the password
            if (!currentPassword.equals(currentConfirmPassword)) {
                builder.setMessage("Please enter the same password.").setTitle("Password mismatches!");
                builder.setPositiveButton("OK", (dialog, id) -> {
                });
                builder.show();
            } else {

                // Iterating through local userdata and check if the input data match one of them
                while (userDataItr.hasNext()) {

                    // Get each userdata key-value pair
                    Map.Entry<String, String> userDataPair = userDataItr.next();

                    // If the comparison matches, set the match signal as true and break the loop
                    if (userDataPair.getKey().equals(currentUsername)) {
                        match = true;
                        break;
                    }
                }

                if ((currentUsername.length() < 4) || (currentUsername.length() > 20)) {
                    warningMsg(builder,
                            "Username length should be >= 4 and <= 20.",
                            "Username length is invalid!"
                    );
                    return;
                }
                if ((currentPassword.length() < 5) || (currentPassword.length() > 25)) {
                    warningMsg(builder,
                            "Password length should be >= 5 and <= 25.",
                            "Password length is invalid!"
                    );
                    return;
                }
                int countSpecialChar = 0, countDigit = 0, countUpper = 0, countLower = 0;
                for (int i = 0; i < currentPassword.length(); i++) {
                    char c = currentPassword.charAt(i);
                    if (c >= '0' && c <= '9') {
                        countDigit++;
                    } else if (c >= 'a' && c <= 'z') {
                        countLower++;
                    } else if (c >= 'A' && c <= 'Z') {
                        countUpper++;
                    } else if (c == '_' || c == '?' || c == '.' || c == ','|| c =='!' || c =='#') {
                        countSpecialChar++;
                    } else {
                        warningMsg(builder,
                                "Password is required to contains: 1 digit, 1 upper-case and" +
                                        " 1 lower-case English letter, and special characters of _?.,!#",
                                "Invalid password character: " + c
                        );
                        return;
                    }
                }
                if (countSpecialChar < 1 || countDigit < 1 || countUpper < 1 || countLower < 1) {
                    warningMsg(builder,
                            "Password is required to contains: 1 digit, 1 upper-case and" +
                                    " 1 lower-case English letter, and special characters of _?.,!#",
                            "Invalid password format!"
                    );
                    return;
                }

                // If a match is found, alert
                if (match) {
                    warningMsg(builder,
                            "Please try another username OR log in.",
                            "Username have already existed!"
                    );
                } else {

                    // Store the new username and password into the local data
                    File file = new File(getFilesDir(), "userData.txt");

                    // Initialize the sentinel signal for checking the existence of the file
                    boolean existCheck = true;

                    // Determine if the data file exists.
                    if (!file.exists()) {

                        // Create the data file if the file does not exist
                        try {
                            boolean createSuccess = file.createNewFile();

                            // Print warning if createNewFile return false
                            if (!createSuccess) {
                                System.err.println("Create file failed");
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                            warningMsg(builder,
                                    "Please check if your disk is full or no permission allowed.",
                                    "Data file cannot be created!"
                            );
                            existCheck = false;
                        }
                    }

                    // If the file cannot be created, jump the rest procedure
                    if (existCheck) {

                        // Determine if the file is writable
                        if (!file.canWrite() && !file.setWritable(true)) {
                            // Data file is not writable
                            warningMsg(builder,
                                    "Please check if the file is occupied by other programs or is readonly.",
                                    "Data file not writable!"
                            );
                        } else {
                            try {

                                // Successfully write all data in file
                                FileWriter writer = new FileWriter(file, true);
                                try {
                                    writer.write(security.encrypt(currentUsername) + "\n");
                                    writer.write(security.encrypt(currentPassword) + "\n");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    System.exit(-1);
                                }
                                writer.flush();
                                writer.close();
                                userData.put(currentUsername, currentPassword);

                                // Set the congratulation dialog
                                builder.setMessage("Username: " + currentUsername + "\nPassword: " + currentPassword + "\n");
                                builder.setTitle("Your account has been created!");
                                builder.setPositiveButton("OK", (dialog, id) -> {
                                    dialog.dismiss();
                                    Intent k = new Intent(Register.this, SignInControl.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("com.emory.healthAPP.userData", userData);
                                    k.putExtras(bundle);
                                    endAnimation(k);
                                });
                                builder.show();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });

        // Click the [Back] button
        buttonGoBack.setOnClickListener(signUpBack -> {
            Intent k = new Intent(Register.this, SignInControl.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("com.emory.healthAPP.userData", userData);
            k.putExtras(bundle);
            endAnimation(k);
        });

        startAnimation();
    }

    private void warningMsg(AlertDialog.Builder builder,
                            final String msg, final String title) {
        builder.setMessage(msg);
        builder.setTitle(title);
        builder.setPositiveButton("OK", (dialog, id) -> {});
        builder.show();
    }

    private void inherit() {
        Intent intent = this.getIntent();
        userData =
                (HashMap<String, String>) intent.getSerializableExtra("com.emory.healthAPP.userData");
    }

    private void initialize() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams windowLayoutParams = getWindow().getAttributes();
            windowLayoutParams.layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(windowLayoutParams);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        ConvPDS PDS = new ConvPDS(
                this.getResources().getDisplayMetrics().density,
                this.getResources().getDisplayMetrics().scaledDensity,
                this.getResources().getDisplayMetrics().heightPixels,
                this.getResources().getDisplayMetrics().widthPixels);
        float width = PDS.getWidth();
        float height = PDS.getHeight();

        RelativeLayout.LayoutParams relativeLayoutParams;
        RelativeLayout.MarginLayoutParams relativeMarginLayoutParams;

        relativeLayoutParams = new RelativeLayout.LayoutParams(
                PDS.i_dp2px(PDS.getWidth() * 0.7f),
                PDS.i_dp2px(PDS.getHeight() * 0.2f));
        emoryLogo01.setLayoutParams(relativeLayoutParams);
        emoryLogo01.setTranslationX(PDS.f_dp2px(width * 0.15f));
        emoryLogo01.setTranslationY(0.2f * PDS.getHeight());
        emoryLogo01.setAlpha(1.0f);

        relativeLayoutParams = new RelativeLayout.LayoutParams(
                PDS.i_dp2px(width * 0.6f),
                PDS.i_dp2px(height * (4056.0f) / (6185.0f)));
        emoryLogo02.setLayoutParams(relativeLayoutParams);
        emoryLogo02.setTranslationX(PDS.f_dp2px(width * 0.2f));
        emoryLogo02.setTranslationY(PDS.f_dp2px(height * (2129.0f) / (12370.0f)));
        emoryLogo02.setAlpha(0.5f);

        //------------------------------------------------------
        // bottom set
        float bottom_set_width = width * 0.8f;
        float bottom_set_height = height * 0.45f;
        relativeLayoutParams = new RelativeLayout.LayoutParams(
                PDS.i_dp2px(bottom_set_width),
                PDS.i_dp2px(height));
        bottom_set.setLayoutParams(relativeLayoutParams);
        bottom_set.setTranslationX(PDS.f_dp2px(width * 0.1f));
        bottom_set.setTranslationY(PDS.f_dp2px(height * 0.35f));

        relativeMarginLayoutParams = (RelativeLayout.MarginLayoutParams) usernameMask.getLayoutParams();
        relativeMarginLayoutParams.topMargin = PDS.i_dp2px(bottom_set_height * 0.25f);
        usernameMask.setLayoutParams(relativeMarginLayoutParams);
        usernameMask.setRadius(PDS.f_dp2px(0.076f * height));
        relativeMarginLayoutParams = (RelativeLayout.MarginLayoutParams) username.getLayoutParams();
        relativeMarginLayoutParams.leftMargin = PDS.i_dp2px(bottom_set_width * 0.05f);
        relativeMarginLayoutParams.rightMargin = PDS.i_dp2px(bottom_set_width * 0.05f);
        username.setLayoutParams(relativeMarginLayoutParams);
        username.setMinimumWidth(PDS.i_dp2px(0.9f * bottom_set_width));

        relativeMarginLayoutParams = (RelativeLayout.MarginLayoutParams) passwordMask.getLayoutParams();
        relativeMarginLayoutParams.topMargin = PDS.i_dp2px(bottom_set_height * 0.03f);
        passwordMask.setLayoutParams(relativeMarginLayoutParams);
        passwordMask.setRadius(PDS.f_dp2px(0.076f * height));
        relativeMarginLayoutParams = (RelativeLayout.MarginLayoutParams) password.getLayoutParams();
        relativeMarginLayoutParams.leftMargin = PDS.i_dp2px(bottom_set_width * 0.05f);
        relativeMarginLayoutParams.rightMargin = PDS.i_dp2px(bottom_set_width * 0.05f);
        password.setLayoutParams(relativeMarginLayoutParams);
        password.setMinimumWidth(PDS.i_dp2px(0.9f * bottom_set_width));

        relativeMarginLayoutParams = (RelativeLayout.MarginLayoutParams) confirmedPasswordMask.getLayoutParams();
        relativeMarginLayoutParams.topMargin = PDS.i_dp2px(bottom_set_height * 0.03f);
        confirmedPasswordMask.setLayoutParams(relativeMarginLayoutParams);
        confirmedPasswordMask.setRadius(PDS.f_dp2px(0.076f * height));
        relativeMarginLayoutParams = (RelativeLayout.MarginLayoutParams) confirmedPassword.getLayoutParams();
        relativeMarginLayoutParams.leftMargin = PDS.i_dp2px(bottom_set_width * 0.05f);
        relativeMarginLayoutParams.rightMargin = PDS.i_dp2px(bottom_set_width * 0.05f);
        confirmedPassword.setLayoutParams(relativeMarginLayoutParams);
        confirmedPassword.setMinimumWidth(PDS.i_dp2px(0.9f * bottom_set_width));

        relativeMarginLayoutParams = (RelativeLayout.MarginLayoutParams) buttonSignUpMask.getLayoutParams();
        relativeMarginLayoutParams.topMargin = PDS.i_dp2px(bottom_set_height * 0.07f);
        buttonSignUpMask.setLayoutParams(relativeMarginLayoutParams);
        relativeMarginLayoutParams = (RelativeLayout.MarginLayoutParams) buttonGoBackMask.getLayoutParams();
        relativeMarginLayoutParams.topMargin = PDS.i_dp2px(bottom_set_height * 0.03f);
        buttonGoBackMask.setLayoutParams(relativeMarginLayoutParams);
    }

    // Animations
    private void startAnimation() {
        LinkedList<Animator> translationList = new LinkedList<>();
        ObjectAnimator alphaAnimation01 =
                ObjectAnimator.ofFloat(usernameMask, View.ALPHA, 0.0f, 1.0f);
        translationList.add(alphaAnimation01);
        alphaAnimation01 = ObjectAnimator.ofFloat(passwordMask, View.ALPHA, 0.0f, 1.0f);
        translationList.add(alphaAnimation01);
        alphaAnimation01 = ObjectAnimator.ofFloat(confirmedPasswordMask, View.ALPHA, 0.0f, 1.0f);
        translationList.add(alphaAnimation01);
        alphaAnimation01 = ObjectAnimator.ofFloat(buttonSignUp, View.ALPHA, 0.0f, 1.0f);
        translationList.add(alphaAnimation01);
        alphaAnimation01 = ObjectAnimator.ofFloat(buttonGoBack, View.ALPHA, 0.0f, 1.0f);
        translationList.add(alphaAnimation01);
        animatorSet = new AnimatorSet();
        animatorSet.setDuration(2000);
        animatorSet.playTogether(translationList);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                usernameMask.setAlpha(1.0f);
                passwordMask.setAlpha(1.0f);
                confirmedPasswordMask.setAlpha(1.0f);
                buttonSignUp.setAlpha(1.0f);
                username.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                username.setEnabled(true);
                password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                password.setEnabled(true);
                confirmedPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                confirmedPassword.setEnabled(true);
                buttonSignUp.setEnabled(true);
                buttonGoBack.setEnabled(true);
            }
        });
        animatorSet.start();
    }

    private void endAnimation(Intent end) {
        username.setInputType(InputType.TYPE_NULL);
        username.setEnabled(false);
        password.setInputType(InputType.TYPE_NULL);
        password.setEnabled(false);
        confirmedPassword.setInputType(InputType.TYPE_NULL);
        confirmedPassword.setEnabled(false);
        buttonSignUp.setEnabled(false);
        buttonGoBack.setEnabled(false);
        LinkedList<Animator> translationList = new LinkedList<>();
        ObjectAnimator alphaAnimation01 =
                ObjectAnimator.ofFloat(usernameMask, View.ALPHA, 1.0f, 0.0f);
        translationList.add(alphaAnimation01);
        alphaAnimation01 = ObjectAnimator.ofFloat(passwordMask, View.ALPHA, 1.0f, 0.0f);
        translationList.add(alphaAnimation01);
        alphaAnimation01 = ObjectAnimator.ofFloat(confirmedPasswordMask, View.ALPHA, 1.0f, 0.0f);
        translationList.add(alphaAnimation01);
        alphaAnimation01 = ObjectAnimator.ofFloat(buttonSignUp, View.ALPHA, 1.0f, 0.0f);
        translationList.add(alphaAnimation01);
        alphaAnimation01 = ObjectAnimator.ofFloat(buttonGoBack, View.ALPHA, 1.0f, 0.0f);
        translationList.add(alphaAnimation01);
        animatorSet = new AnimatorSet();
        animatorSet.setDuration(1000);
        animatorSet.playTogether(translationList);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                usernameMask.setAlpha(0.0f);
                passwordMask.setAlpha(0.0f);
                confirmedPasswordMask.setAlpha(0.0f);
                buttonSignUp.setAlpha(0.0f);
                end.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                end.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                end.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                finish();
                startActivity(end);
                overridePendingTransition(0, 0);
            }
        });
        animatorSet.start();
    }
}
