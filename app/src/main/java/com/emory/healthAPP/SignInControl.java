package com.emory.healthAPP;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

/**
 * Sign up screen control class
 *
 * @author Zixiang Xu
 * @version 1.0
 */
public class SignInControl extends AppCompatActivity {

    // User data storage
    private HashMap<String, String[]> patientData;
    private HashMap<String, HashSet<String>> patientAudioList;
    // User data storage
    private HashMap<String, String[]> doctorData;

    // Activity resources
    private EditText userID, password;
    private CardView userIDMask, passwordMask;
    private RelativeLayout bottom_set, buttonSignInMask, buttonSwitchMask, base;
    private ImageView emoryLogo01, emoryLogo02;
    private Button buttonSignIn, buttonSwitch;
    private AnimatorSet animatorSet;
    private DataSecurity security;
    private Random rand;
    private ConvPDS PDS;

    // temp animation-used variable
    private float topPosition;
    private float midPosition;

    private String currUserID, currPassword, currUserHashCode;
    private String patientAssignedDoctorID;
    private boolean isDoctor;

    private boolean isSignOutBack;

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("save/emory_health/isSignOutBack", isSignOutBack);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            isSignOutBack = savedInstanceState.getBoolean("save/emory_health/isSignOutBack");
            savedInstanceState.remove("save/emory_health/isSignOutBack");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults == null || grantResults.length == 0) {
                Toast.makeText(getApplicationContext(), "Crucial permissions are denied", Toast.LENGTH_SHORT).show();
            } else {
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(getApplicationContext(), "READ_EXTERNAL_STORAGE permission is denied", Toast.LENGTH_SHORT).show();
                }
                if (grantResults[1] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(getApplicationContext(), "WRITE_EXTERNAL_STORAGE permission is denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * Create the activity of sign up page.
     *
     * @param savedInstanceState current state of current manifest
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Inherit sign out info from main page
        inherit();

        // Initialize activity views
        isDoctor = true;
        userID = findViewById(R.id.SignInControl_username);
        password = findViewById(R.id.SignInControl_password);
        userIDMask = findViewById(R.id.SignInControl_username_mask);
        passwordMask = findViewById(R.id.SignInControl_password_mask);
        bottom_set = findViewById(R.id.SignInControl_bottom_set);
        buttonSignIn = findViewById(R.id.SignInControl_button01);
        buttonSwitch = findViewById(R.id.SignInControl_button02);
        buttonSignInMask = findViewById(R.id.SignInControl_button01_mask);
        buttonSwitchMask = findViewById(R.id.SignInControl_button02_mask);
        emoryLogo01 = findViewById(R.id.SignInControl_emory_logo_01);
        emoryLogo02 = findViewById(R.id.SignInControl_emory_logo_02);
        base = findViewById(R.id.SignInControl_no_overlapped);
        initViews();

        // Initialize log-in userData
        try {
            security = new DataSecurity();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        patientData = new HashMap<>();
        patientAudioList = new HashMap<>();
        doctorData = new HashMap<>();
        try {
            readUserData();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

        // Click the [Sign Up] button
        // Check validity of log-in info and continue to main screen
        buttonSignIn.setOnClickListener(signIn -> {


            String currUserID = userID.getText().toString();
            String currPassword = password.getText().toString();
            boolean admin = (isDoctor) && adminCheck(currUserID, currPassword);
            boolean match = false;
            Iterator<Map.Entry<String, String[]>> userDataItr;
            if (isDoctor)
                userDataItr = doctorData.entrySet().iterator();
            else
                userDataItr = patientData.entrySet().iterator();
            if (!admin) {
                while (userDataItr.hasNext()) {
                    Map.Entry<String, String[]> userDataPair = userDataItr.next();
                    if (userDataPair.getKey().equals(currUserID) &&
                            userDataPair.getValue()[0].equals(currPassword)) {
                        this.currUserID = currUserID;
                        this.currPassword = currPassword;
                        this.currUserHashCode = userDataPair.getValue()[1];
                        if (!isDoctor) {
                            this.patientAssignedDoctorID = userDataPair.getValue()[2];
                        }
                        match = true;
                        break;
                    }
                }
            }


            // If a match is found, user confirmed
            if (admin || match) {
                if (admin)
                    adminWrite();
                else
                    endAnimation();

                // If a match is not found, alert
            } else {
                String title = (isDoctor) ? "Wrong doctor ID or password!" : "Wrong user ID or password!";
                warningMsg("Please try again.", title);
            }
        });

        // Click the [switch] button
        buttonSwitch.setOnClickListener(switchState -> {
            /*
            String[] randomDoctor = randomCreateUser (
                    "DOC_xxxxxxxx", "Dxxxxxxxx", "xxxxxxxx",
                    4, 1, 0, 8);
            currUserID = randomDoctor[0];
            currPassword = randomDoctor[1];
            currUserHashCode = randomDoctor[2];
            endAnimation();
            */

            switchAnimation01();
        });

        Handler handler = new Handler();
        if (isSignOutBack)
            handler.postDelayed(this::startAnimation02, 1500);
        else
            handler.postDelayed(this::startAnimation01, 2000);
    }

    private void inherit() {
        Intent intent = getIntent();
        isSignOutBack = intent.getBooleanExtra("com.emory.healthAPP.isSignOutBack", false);
    }

    /**
     * Read user data from the local data storage.
     *
     * @throws java.io.IOException Exception when file is not found / cannot be created
     */
    private void readUserData() throws java.io.IOException {

        // Create the File tag for the data file
        File patientFile = new File(getFilesDir(), "data01.emory");
        File doctorFile = new File(getFilesDir(), "data02.emory");

        // Determine if the data file exists.
        if (!patientFile.exists() && !patientFile.createNewFile()) {

            // Error: 0x4AC971
            System.err.println("Error 0x4AC971: create patient log-in file failed!");
            System.exit(-1);
        }
        if (!doctorFile.exists() && !doctorFile.createNewFile()) {

            // Error: 0x4AC972
            System.err.println("Error 0x4AC972: create doctor log-in file failed!");
            System.exit(-1);
        }

        // Create scanner for the input file
        Scanner patientDataIn = new Scanner(patientFile);
        Scanner doctorDataIn = new Scanner(doctorFile);

        // Initialize the index and temporary userdata storage
        int index = 0;
        String[] data = new String[4];


        // Read patient log-in data from the data file line by line
        while (patientDataIn.hasNextLine()) {

            // data[0] = username
            // data[1] = password
            // data[2] = hashCode
            // data[3] = doctorID
            System.err.print("data" + index + ": ");
            data[index++] = patientDataIn.nextLine();
            System.err.println(data[index - 1]);
            if (index == 4) {
                index = 0;
                try {
                    String patientHashCode = security.decrypt(data[2]);
                    this.patientData.put(security.decrypt(data[0]),
                            new String[]{security.decrypt(data[1]), security.decrypt(data[2]), security.decrypt(data[3])});
                    int numAudio = Integer.parseInt(security.decrypt(patientDataIn.nextLine()));
                    HashSet<String> patientAudioList = new HashSet<>();
                    for (int i = 0; i < numAudio; i++) {
                        patientAudioList.add(security.decrypt(patientDataIn.nextLine()));
                    }
                    this.patientAudioList.put(patientHashCode, patientAudioList);
                } catch (Exception e) {

                    // Error: 0xBE136E
                    System.err.println("Error 0xBE136E: cannot read/decrypt patient data from log-in input!");
                    System.exit(-1);
                }
            }
        }

        // Read doctor log-in userdata from the data file line by line
        index = 0;
        data = new String[3];
        while (doctorDataIn.hasNextLine()) {

            // data[0] = username
            // data[1] = password
            // data[2] = hashcode
            data[index++] = doctorDataIn.nextLine();
            if (index == 3) {
                index = 0;
                try {
                    this.doctorData.put(security.decrypt(data[0]), new String[]{security.decrypt(data[1]), security.decrypt(data[2])});
                } catch (Exception e) {

                    // Error: 0xBE136F
                    System.err.println("Error 0xBE136F: cannot read/decrypt doctor data from log-in input!");
                    System.exit(-1);
                }
            }
        }

        // Close the scanner after finishing the work
        patientDataIn.close();
        doctorDataIn.close();
    }

    // Admin Check & Controls
    private boolean adminCheck(String str1, String str2) {
        String[] admin_users = new String[] {
                "7a8c63b7b7c4cf31768e62af92633cbf623085d2742517ee",
                "7a8c63b7b7c4cf3146bcdfdaf4f3012f92647b09bef1a579",
                "7a8c63b7b7c4cf31bbb1a6e84a1e822cdd131b088bc32d68",
                "7a8c63b7b7c4cf318145e85c3503c51f193e89d61abac9b7",
                "7a8c63b7b7c4cf31faf96d72ee27880fc1993f72cb63775f"
        };
        String[] admin_passwords = new String[] {
                "6f2cf3bc02bb0070",
                "9c7414fe466e6019",
                "16d81c0ac667bad0",
                "bc60fa0c5580f0c1",
                "86a9af6e83e0bdc77612da38d8a773fe"
        };
        try {
            str1 = security.encrypt(str1);
            str2 = security.encrypt(str2);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        for (int i = 0; i < admin_users.length; i++) {
            if (admin_users[i].equals(str1) && admin_passwords[i].equals(str2)) {
                return true;
            }
        }
        return false;
    }
    private char randomChar(boolean password) {
        String specialChar = ".,_!#@";
        int max = (password) ? 4 : 3, min = 1;
        int choice = rand.nextInt((max - min) + 1) + min;
        switch(choice) {
            case 1:
                // Lower Case English Letter
                max = 122; min = 97;
                break;
            case 2:
                // Upper Case English Letter
                max = 90; min = 65;
                break;
            case 3:
                // Digits
                max = 57; min = 48;
                break;
            case 4:
                return specialChar.charAt(rand.nextInt(specialChar.length()));
        }
        return (char) (rand.nextInt((max - min) + 1) + min);
    }
    private boolean containsUserHashCode(String hashCode) {
        for (Map.Entry<String, String[]> userDataPair : doctorData.entrySet())
            if (userDataPair.getValue()[1].equals(hashCode))
                return true;
        return false;
    }
    private String[] randomCreateUser (String str1, String str2, String str3,
                                       int pre1, int pre2, int pre3, int xLength) {
        rand = new Random();
        StringBuilder currUserIDBuilder = new StringBuilder (str1);
        StringBuilder  currPasswordBuilder = new StringBuilder (str2);
        StringBuilder  currUserHashCodeBuilder = new StringBuilder (str3);
        String currUserID;
        String currPassword;
        String currUserHashCode;
        do {
            for (int i = 0; i < xLength; i++) {
                currUserIDBuilder.setCharAt(i + pre1, randomChar(false));
                currPasswordBuilder.setCharAt(i + pre2, randomChar(true));
                currUserHashCodeBuilder.setCharAt(i + pre3, randomChar(true));
            }
            currUserID = currUserIDBuilder.toString();
            currPassword = currPasswordBuilder.toString();
            currUserHashCode = currUserHashCodeBuilder.toString();
        } while (doctorData.containsKey(currUserID) || containsUserHashCode(currUserHashCode));
        return new String[] {currUserID, currPassword, currUserHashCode};
    }
    private void adminWrite() {

        // Set up new doctorID & new password
        String[] randomDoctor = randomCreateUser (
                "DOC_xxxxxxxx", "Dxxxxxxxx", "xxxxxxxx",
                4, 1, 0, 8);
        String currUserID = randomDoctor[0];
        String currPassword = randomDoctor[1];
        String currUserHashCode = randomDoctor[2];

        // Write new doctor data into local
        File file = new File(getFilesDir(), "data02.emory");
        if (!file.exists()) {
            try {
                boolean createSuccess = file.createNewFile();
                if (!createSuccess) {
                    System.err.println("Create file failed");
                    System.exit(-1);
                }
            } catch (IOException e) {
                e.printStackTrace();
                warningMsg("Please check if your disk is full or no permission allowed.",
                        "Data file cannot be created!"
                );
                System.exit(-1);
            }
        }
        if (!file.canWrite() && !file.setWritable(true)) {
            System.err.println("file cannot be written!");
            System.exit(-1);
        }
        try {
            FileWriter writer = new FileWriter(file, true);
            try {
                writer.write(security.encrypt(currUserID) + "\n");
                writer.write(security.encrypt(currPassword) + "\n");
                writer.write(security.encrypt(currUserHashCode) + "\n");
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
            }
            writer.flush();
            writer.close();
            doctorData.put(currUserID, new String[] {currPassword, currUserHashCode});
            this.currUserID = currUserID;
            this.currPassword = currPassword;
            this.currUserHashCode = currUserHashCode;
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(String.format("Welcome!\nHere is your new Doctor ID and password!\n\n" +
                "Doctor ID: %s\nPassword: %s", currUserID, currPassword));
        builder.setTitle("Admin check successful!");
        builder.setPositiveButton("OK", (dialog, id) -> {
            endAnimation();
        });
        builder.show();
    }

    // Warning messages
    private void warningMsg(final String msg, final String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg);
        builder.setTitle(title);
        builder.setPositiveButton("OK", (dialog, id) -> {
        });
        builder.show();
    }

    private void immersive() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams windowLayoutParams = getWindow().getAttributes();
            windowLayoutParams.layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(windowLayoutParams);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            int statusBarHeight = 0;
            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0)
                statusBarHeight = getResources().getDimensionPixelSize(resourceId);
            //--------------------------------------------------------------------------------------

            base.setPadding(0, statusBarHeight, 0, 0);
        }
    }

    // Initialize views
    private void initViews() {

        // no status-bar and navigation bar
        immersive();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        PDS = new ConvPDS(
                this.getResources().getDisplayMetrics().density,
                this.getResources().getDisplayMetrics().scaledDensity,
                this.getResources().getDisplayMetrics().heightPixels,
                this.getResources().getDisplayMetrics().widthPixels);
        float width = PDS.getWidth();
        float height = PDS.getHeight();
        topPosition = 0.2f * PDS.getHeight();
        midPosition = PDS.f_dp2px(height * 0.4f);

        RelativeLayout.LayoutParams relativeLayoutParams;
        RelativeLayout.MarginLayoutParams relativeMarginLayoutParams;

        relativeLayoutParams = new RelativeLayout.LayoutParams(
                PDS.i_dp2px(PDS.getWidth() * 0.7f),
                PDS.i_dp2px(PDS.getHeight() * 0.2f));
        emoryLogo01.setLayoutParams(relativeLayoutParams);
        emoryLogo01.setTranslationX(PDS.f_dp2px(width * 0.15f));
        emoryLogo01.setTranslationY(midPosition);

        relativeLayoutParams = new RelativeLayout.LayoutParams(
                PDS.i_dp2px(width * 0.6f),
                PDS.i_dp2px(height * (4056.0f) / (6185.0f)));
        emoryLogo02.setLayoutParams(relativeLayoutParams);
        emoryLogo02.setTranslationX(PDS.f_dp2px(width * 0.2f));
        emoryLogo02.setTranslationY(PDS.f_dp2px(height * (2129.0f) / (12370.0f)));
        if (isSignOutBack) {
            emoryLogo01.setAlpha(1.0f);
            emoryLogo02.setAlpha(0.5f);
        }

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

        relativeMarginLayoutParams = (RelativeLayout.MarginLayoutParams) userIDMask.getLayoutParams();
        relativeMarginLayoutParams.topMargin = PDS.i_dp2px(bottom_set_height * 0.25f);
        userIDMask.setLayoutParams(relativeMarginLayoutParams);
        userIDMask.setRadius(PDS.f_dp2px(0.076f * height));
        relativeMarginLayoutParams = (RelativeLayout.MarginLayoutParams) userID.getLayoutParams();
        relativeMarginLayoutParams.leftMargin = PDS.i_dp2px(bottom_set_width * 0.05f);
        relativeMarginLayoutParams.rightMargin = PDS.i_dp2px(bottom_set_width * 0.05f);
        userID.setLayoutParams(relativeMarginLayoutParams);
        userID.setMinimumWidth(PDS.i_dp2px(0.9f * bottom_set_width));

        relativeMarginLayoutParams = (RelativeLayout.MarginLayoutParams) passwordMask.getLayoutParams();
        relativeMarginLayoutParams.topMargin = PDS.i_dp2px(bottom_set_height * 0.03f);
        passwordMask.setLayoutParams(relativeMarginLayoutParams);
        passwordMask.setRadius(PDS.f_dp2px(0.076f * height));
        relativeMarginLayoutParams = (RelativeLayout.MarginLayoutParams) password.getLayoutParams();
        relativeMarginLayoutParams.leftMargin = PDS.i_dp2px(bottom_set_width * 0.05f);
        relativeMarginLayoutParams.rightMargin = PDS.i_dp2px(bottom_set_width * 0.05f);
        password.setLayoutParams(relativeMarginLayoutParams);
        password.setMinimumWidth(PDS.i_dp2px(0.9f * bottom_set_width));

        relativeMarginLayoutParams = (RelativeLayout.MarginLayoutParams) buttonSignInMask.getLayoutParams();
        relativeMarginLayoutParams.topMargin = PDS.i_dp2px(bottom_set_height * 0.07f);
        buttonSignInMask.setLayoutParams(relativeMarginLayoutParams);
        relativeMarginLayoutParams = (RelativeLayout.MarginLayoutParams) buttonSwitchMask.getLayoutParams();
        relativeMarginLayoutParams.topMargin = PDS.i_dp2px(bottom_set_height * 0.03f);
        buttonSwitchMask.setLayoutParams(relativeMarginLayoutParams);
    }

    // Animations
    private void startAnimation01() {
        LinkedList<Animator> animators = new LinkedList<>();
        ObjectAnimator animator;
        animator = ObjectAnimator.ofFloat(emoryLogo01, View.ALPHA, 0.0f, 1.0f);
        animators.add(animator);
        animator = ObjectAnimator.ofFloat(emoryLogo02, View.ALPHA, 0.0f, 0.5f);
        animators.add(animator);
        animatorSet = new AnimatorSet();
        animatorSet.setDuration(1000);
        animatorSet.playTogether(animators);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                emoryLogo01.setAlpha(1.0f);
                emoryLogo02.setAlpha(0.5f);
                startAnimation02();
            }
        });
        animatorSet.start();
    }
    private void startAnimation02() {
        LinkedList<Animator> animators = new LinkedList<>();
        ObjectAnimator animator;
        animator = ObjectAnimator.ofFloat(emoryLogo01, View.TRANSLATION_Y, midPosition, topPosition);
        animators.add(animator);
        animator = ObjectAnimator.ofFloat(userIDMask, View.ALPHA, 0.0f, 1.0f);
        animators.add(animator);
        animator = ObjectAnimator.ofFloat(passwordMask, View.ALPHA, 0.0f, 1.0f);
        animators.add(animator);
        animator = ObjectAnimator.ofFloat(buttonSignIn, View.ALPHA, 0.0f, 1.0f);
        animators.add(animator);
        animator = ObjectAnimator.ofFloat(buttonSwitch, View.ALPHA, 0.0f, 1.0f);
        animators.add(animator);
        animatorSet = new AnimatorSet();
        animatorSet.setDuration(1500);
        animatorSet.playTogether(animators);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                emoryLogo01.setTranslationY(topPosition);
                userIDMask.setAlpha(1.0f);
                passwordMask.setAlpha(1.0f);
                buttonSignIn.setAlpha(1.0f);
                buttonSwitch.setAlpha(1.0f);
                userID.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                userID.setEnabled(true);
                password.setEnabled(true);
                buttonSignIn.setEnabled(true);
                buttonSwitch.setEnabled(true);
            }
        });
        animatorSet.start();
    }
    private void switchAnimation01() {
        userID.setEnabled(false);
        password.setEnabled(false);
        buttonSignIn.setEnabled(false);
        buttonSwitch.setEnabled(false);
        userID.setInputType(InputType.TYPE_NULL);
        password.setInputType(InputType.TYPE_NULL);
        isDoctor = !isDoctor;

        LinkedList<Animator> animators = new LinkedList<>();
        ObjectAnimator animator;
        animator = ObjectAnimator.ofFloat(userIDMask, View.ALPHA, 1.0f, 0.0f);
        animators.add(animator);
        animator = ObjectAnimator.ofFloat(passwordMask, View.ALPHA, 1.0f, 0.0f);
        animators.add(animator);
        animator = ObjectAnimator.ofFloat(buttonSignIn, View.ALPHA, 1.0f, 0.0f);
        animators.add(animator);
        animator = ObjectAnimator.ofFloat(buttonSwitch, View.ALPHA, 1.0f, 0.0f);
        animators.add(animator);
        animatorSet = new AnimatorSet();
        animatorSet.setDuration(200);
        animatorSet.playTogether(animators);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                userIDMask.setAlpha(0.0f);
                passwordMask.setAlpha(0.0f);
                buttonSignIn.setAlpha(0.0f);
                buttonSwitch.setAlpha(0.0f);
                switchAnimation02();
            }
        });
        animatorSet.start();
    }
    private void switchAnimation02() {
        if (isDoctor) {
            userID.setHint(R.string.text_usernameDoctor);
            buttonSwitch.setText(R.string.text_signInSwitchPatient);
        } else {
            userID.setHint(R.string.text_usernamePatient);
            buttonSwitch.setText(R.string.text_signInSwitchDoctor);
        }

        LinkedList<Animator> animators = new LinkedList<>();
        ObjectAnimator animator;
        animator = ObjectAnimator.ofFloat(userIDMask, View.ALPHA, 0.0f, 1.0f);
        animators.add(animator);
        animator = ObjectAnimator.ofFloat(passwordMask, View.ALPHA, 0.0f, 1.0f);
        animators.add(animator);
        animator = ObjectAnimator.ofFloat(buttonSignIn, View.ALPHA, 0.0f, 1.0f);
        animators.add(animator);
        animator = ObjectAnimator.ofFloat(buttonSwitch, View.ALPHA, 0.0f, 1.0f);
        animators.add(animator);
        animatorSet = new AnimatorSet();
        animatorSet.setDuration(200);
        animatorSet.playTogether(animators);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                userIDMask.setAlpha(1.0f);
                passwordMask.setAlpha(1.0f);
                buttonSignIn.setAlpha(1.0f);
                buttonSwitch.setAlpha(1.0f);
                userID.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                userID.setEnabled(true);
                password.setEnabled(true);
                buttonSignIn.setEnabled(true);
                buttonSwitch.setEnabled(true);
            }
        });
        animatorSet.start();
    }
    private void endAnimation() {
        userID.setEnabled(false);
        password.setEnabled(false);
        buttonSignIn.setEnabled(false);
        buttonSwitch.setEnabled(false);
        userID.setInputType(InputType.TYPE_NULL);
        password.setInputType(InputType.TYPE_NULL);
        LinkedList<Animator> animators = new LinkedList<>();
        ObjectAnimator animator;
        animator = ObjectAnimator.ofFloat(emoryLogo01, View.TRANSLATION_Y, topPosition, midPosition);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animators.add(animator);
        animator = ObjectAnimator.ofFloat(userIDMask, View.ALPHA, 1.0f, 0.0f);
        animators.add(animator);
        animator = ObjectAnimator.ofFloat(passwordMask, View.ALPHA, 1.0f, 0.0f);
        animators.add(animator);
        animator = ObjectAnimator.ofFloat(buttonSignIn, View.ALPHA, 1.0f, 0.0f);
        animators.add(animator);
        animator = ObjectAnimator.ofFloat(buttonSwitch, View.ALPHA, 1.0f, 0.0f);
        animators.add(animator);
        animatorSet = new AnimatorSet();
        animatorSet.setDuration(750);
        animatorSet.playTogether(animators);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                userIDMask.setAlpha(0.0f);
                passwordMask.setAlpha(0.0f);
                buttonSignIn.setAlpha(0.0f);
                buttonSwitch.setAlpha(0.0f);
                emoryLogo01.setTranslationY(midPosition);
                Intent intent = new Intent(SignInControl.this, MainMenu.class);
                intent.putExtra("com.emory.healthAPP.currUserID", currUserID);
                intent.putExtra("com.emory.healthAPP.currPassword", currPassword);
                intent.putExtra("com.emory.healthAPP.currUserHashCode", currUserHashCode);
                intent.putExtra("com.emory.healthAPP.isDoctor", isDoctor);
                intent.putExtra("com.emory.healthAPP.patientAssignedDoctorID", patientAssignedDoctorID);
                intent.putExtra("com.emory.healthAPP.patientData", patientData);
                intent.putExtra("com.emory.healthAPP.patientAudioList", patientAudioList);
                intent.putExtra("com.emory.healthAPP.doctorData", doctorData);
                finish();
                startActivity(intent);
            }
        });
        animatorSet.start();
    }
}
