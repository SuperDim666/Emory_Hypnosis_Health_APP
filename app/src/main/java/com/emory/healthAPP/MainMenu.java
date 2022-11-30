package com.emory.healthAPP;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.InputType;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class MainMenu extends AppCompatActivity {

    private HashMap<String, String[]> patientData;
    private HashMap<String, String[]> doctorData;

    private final int REQUEST_CODE_EXTERNAL_FILE = 1099;
    private RelativeLayout ground, ground_base, base, base02, baseFinal;
    private ImageView emoryLogo01, emoryLogo02;
    private String currUserID, currPassword, currUserHashCode, path;
    private boolean isDoctor;
    private ConvPDS PDS;
    private DataSecurity security;
    private DataSecurity securityOriginal;
    private Random rand;
    private AnimatorSet animatorSet;
    private int localSearchChoice;
    private DoctorPage doctorPage;
    private PatientPage patientPage;


    private ActivityResultLauncher<Intent> localFileOpenLauncher;
    private AlertDialog.Builder builder;
    private Uri currURI;

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("save/emory_health/currUserID", currUserID);
        outState.putString("save/emory_health/currPassword", currPassword);
        outState.putString("save/emory_health/currUserHashCode", currUserHashCode);
        outState.putBoolean("save/emory_health/isDoctor", isDoctor);
        if (path != null) {
            outState.putString("save/emory_health/path", path);
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            currUserID = savedInstanceState.getString("save/emory_health/currUserID");
            currPassword = savedInstanceState.getString("save/emory_health/currPassword");
            currUserHashCode = savedInstanceState.getString("save/emory_health/currUserHashCode");
            isDoctor = savedInstanceState.getBoolean("save/emory_health/isDoctor");
            savedInstanceState.remove("save/emory_health/currUserID");
            savedInstanceState.remove("save/emory_health/currPassword");
            savedInstanceState.remove("save/emory_health/isDoctor");
            if (savedInstanceState.containsKey("save/emory_health/path")) {
                path = savedInstanceState.getString("save/emory_health/path");
                savedInstanceState.remove("save/emory_health/path");
            }
        }
    }

    protected void start() {
        setContentView(R.layout.activity_menu_services);
        builder = new AlertDialog.Builder(this);
        ground = findViewById(R.id.MainPage_Ground);
        ground_base = findViewById(R.id.MainPage_Background_ground);
        emoryLogo01 = findViewById(R.id.MainMenu_emory_logo_01);
        emoryLogo02 = findViewById(R.id.MainMenu_emory_logo_02);
        base = findViewById(R.id.MainPage_Base);
        base02 = findViewById(R.id.MainPage_Base02);
        baseFinal = findViewById(R.id.MainPage_BaseFinal);

        rand = new Random();

        if (currUserID == null) inherit();
        try {
            // user's hash code to en/decrypt user's own data to increase user's data security
            security = new DataSecurity(currUserHashCode);
            securityOriginal = new DataSecurity();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        PDS = new ConvPDS(
                this.getResources().getDisplayMetrics().density,
                this.getResources().getDisplayMetrics().scaledDensity,
                this.getResources().getDisplayMetrics().heightPixels,
                this.getResources().getDisplayMetrics().widthPixels);
        // no status-bar and navigation bar
        immersive();

        float midPosition = PDS.f_dp2px(PDS.getHeight() * 0.4f);
        RelativeLayout.LayoutParams relativeLayoutParams;
        relativeLayoutParams = new RelativeLayout.LayoutParams(
                PDS.i_dp2px(PDS.getWidth() * 0.6f),
                PDS.i_dp2px(PDS.getHeight() * (4056.0f) / (6185.0f)));
        emoryLogo02.setLayoutParams(relativeLayoutParams);
        emoryLogo02.setTranslationX(PDS.f_dp2px(PDS.getWidth() * 0.2f));
        emoryLogo02.setTranslationY(PDS.f_dp2px(PDS.getHeight() * (2129.0f) / (12370.0f)));
        emoryLogo02.setAlpha(0.5f);
        emoryLogo02.bringToFront();

        relativeLayoutParams = new RelativeLayout.LayoutParams(
                PDS.i_dp2px(PDS.getWidth() * 0.7f),
                PDS.i_dp2px(PDS.getHeight() * 0.2f));
        emoryLogo01.setLayoutParams(relativeLayoutParams);
        emoryLogo01.setTranslationX(PDS.f_dp2px(PDS.getWidth() * 0.15f));
        emoryLogo01.setTranslationY(midPosition);
        emoryLogo01.setAlpha(1.0f);
        emoryLogo01.bringToFront();

        // Begin to set pages differently (dependent on user type)
        if (isDoctor) {
            doctorPage = new DoctorPage();
            doctorPage.doctorPage();
        } else {
            patientPage = new PatientPage();
            patientPage.patientPage();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currUserID = null;
        if (savedInstanceState != null) {
            currUserID = savedInstanceState.getString("save/emory_health/currUserID");
            currPassword = savedInstanceState.getString("save/emory_health/currPassword");
            isDoctor = savedInstanceState.getBoolean("save/emory_health/isDoctor");
            savedInstanceState.remove("save/emory_health/currUserID");
            savedInstanceState.remove("save/emory_health/currPassword");
            savedInstanceState.remove("save/emory_health/isDoctor");
            if (savedInstanceState.containsKey("save/emory_health/path")) {
                path = savedInstanceState.getString("save/emory_health/path");
                savedInstanceState.remove("save/emory_health/path");
            }
        }
        start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_EXTERNAL_FILE)
            if (grantResults.length == 0)
                Toast.makeText(getApplicationContext(), "Crucial permission is denied", Toast.LENGTH_SHORT).show();
            else
                if (grantResults[0] == PackageManager.PERMISSION_DENIED)
                    Toast.makeText(getApplicationContext(), "READ_EXTERNAL_STORAGE permission is denied", Toast.LENGTH_SHORT).show();
                else
                    doctorPage.audio_upload01();
    }
    private char randomChar(int choices) {
        String specialChar = ".,_!#@";
        int max = choices, min = 1;
        int choice = rand.nextInt((max - min) + 1) + min;
        switch (choice) {
            case 1:
                // Digits
                max = 57;
                min = 48;
                break;
            case 2:
                // Lower Case English Letter
                max = 122;
                min = 97;
                break;
            case 3:
                // Upper Case English Letter
                max = 90;
                min = 65;
                break;
            case 4:
                return specialChar.charAt(rand.nextInt(specialChar.length()));
        }
        return (char) (rand.nextInt((max - min) + 1) + min);
    }
    private boolean containsUserHashCode(String hashCode, boolean doctor) {
        if (doctor)
            for (Map.Entry<String, String[]> doctorPair : doctorData.entrySet())
                if (doctorPair.getValue()[1].equals(hashCode))
                    return true;
        else
            for (Map.Entry<String, String[]> patientPair : patientData.entrySet())
                if (patientPair.getValue()[1].equals(hashCode))
                    return true;
        return false;
    }
    private String[] randomCreateUser (String str1, String str2, String str3,
                                       int pre1, int pre2, int pre3, int xLength, boolean doctor) {
        rand = new Random();
        StringBuilder currUserIDBuilder = new StringBuilder (str1);
        StringBuilder  currPasswordBuilder = new StringBuilder (str2);
        StringBuilder  currUserHashCodeBuilder = new StringBuilder (str3);
        String currUserID;
        String currPassword;
        String currUserHashCode;
        boolean check;
        do {
            for (int i = 0; i < xLength; i++) {
                currUserIDBuilder.setCharAt(i + pre1, randomChar(3));
                currPasswordBuilder.setCharAt(i + pre2, randomChar(4));
                currUserHashCodeBuilder.setCharAt(i + pre3, randomChar(4));
            }
            currUserID = currUserIDBuilder.toString();
            currPassword = currPasswordBuilder.toString();
            currUserHashCode = currUserHashCodeBuilder.toString();
            if (doctor) {
                check = doctorData.containsKey(currUserID) || containsUserHashCode(currUserHashCode, doctor);
            } else {
                check = patientData.containsKey(currUserID) || containsUserHashCode(currUserHashCode, doctor);
            }
        } while (check);
        return new String[] {currUserID, currPassword, currUserHashCode};
    }
    private void inherit() {
        Intent intent = getIntent();
        currUserID = intent.getStringExtra("com.emory.healthAPP.currUserID");
        currPassword = intent.getStringExtra("com.emory.healthAPP.currPassword");
        currUserHashCode = intent.getStringExtra("com.emory.healthAPP.currUserHashCode");
        patientData = (HashMap<String, String[]>) intent.getSerializableExtra("com.emory.healthAPP.patientData");
        doctorData = (HashMap<String, String[]>) intent.getSerializableExtra("com.emory.healthAPP.doctorData");
        currUserHashCode = intent.getStringExtra("com.emory.healthAPP.currUserHashCode");
        isDoctor = intent.getBooleanExtra("com.emory.healthAPP.isDoctor", false);
        if (currPassword == null || currUserID == null || patientData == null || doctorData == null) {
            System.err.println("Error 0x91DAAC: cannot derive sign-in data from sign-in page.");
            System.exit(-1);
        }
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
            ground_base.setPadding(0, statusBarHeight, 0, 0);
            base.setPadding(0, statusBarHeight, 0, 0);
            base02.setPadding(0, statusBarHeight, 0, 0);
            baseFinal.setPadding(0, statusBarHeight, 0, 0);
        }
    }
    private void warningMsg(final String msg, final String title) {
        builder.setMessage(msg);
        builder.setTitle(title);
        builder.setPositiveButton("OK", (dialog, id) -> {});
        builder.show();
    }

    private boolean fileExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }
    private String getPathFromExtSD(String[] pathData) {
        final String type = pathData[0];
        final String relativePath = "/" + pathData[1];
        String fullPath = "";

        // on my Sony devices (4.4.4 & 5.1.1), `type` is a dynamic string
        // something like "71F8-2C0A", some kind of unique id per storage
        // don't know any API that can get the root path of that storage based on its id.
        //
        // so no "primary" type, but let the check here for other devices
        if ("primary".equalsIgnoreCase(type)) {
            fullPath = Environment.getExternalStorageDirectory() + relativePath;
            if (fileExists(fullPath)) {
                return fullPath;
            }
        }

        // Environment.isExternalStorageRemovable() is `true` for external and internal storage
        // so we cannot relay on it.
        //
        // instead, for each possible path, check if file exists
        // we'll start with secondary storage as this could be our (physically) removable sd card
        fullPath = System.getenv("SECONDARY_STORAGE") + relativePath;
        if (fileExists(fullPath)) {
            return fullPath;
        }

        fullPath = System.getenv("EXTERNAL_STORAGE") + relativePath;
        fileExists(fullPath);

        return fullPath;
    }
    public boolean copyFileLocal(File src, String destPath) {;
        if ((src == null) || (destPath== null))
            return false;
        File dest= new File(destPath);
        if (dest.exists()) {
            dest.delete();
        }
        try {
            dest.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileChannel srcChannel;
        FileChannel dstChannel;
        try {
            srcChannel = new FileInputStream(src).getChannel();
            dstChannel = new FileOutputStream(dest).getChannel();
            srcChannel.transferTo(0, srcChannel.size(), dstChannel);
            srcChannel.close();
            dstChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    private String copyFileToInternalStorage(Uri uri,String newDirName) {
        Uri returnUri = uri;

        Cursor returnCursor = MainMenu.this.getContentResolver().query(returnUri, new String[]{
                OpenableColumns.DISPLAY_NAME,OpenableColumns.SIZE
        }, null, null, null);
        returnCursor.close();
        /*
         * Get the column indexes of the data in the Cursor,
         *     * move to the first row in the Cursor, get the data,
         *     * and display it.
         * */
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String name = (returnCursor.getString(nameIndex));
        String size = (Long.toString(returnCursor.getLong(sizeIndex)));
        returnCursor.close();
        File output;
        if(!newDirName.equals("")) {
            File dir = new File(MainMenu.this.getFilesDir() + "/" + newDirName);
            if (!dir.exists() && !dir.mkdir()) {
                System.err.println("Error 0x0001B: create assigned directory failed.");
                System.exit(-1);
            }
            output = new File(MainMenu.this.getFilesDir() + "/" + newDirName + "/" + name);
        }
        else{
            output = new File(MainMenu.this.getFilesDir() + "/" + name);
        }
        try {
            InputStream inputStream = MainMenu.this.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(output);
            int read;
            int bufferSize = 1024;
            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }

            inputStream.close();
            outputStream.close();

        }
        catch (Exception e) {

            Log.e("Exception", e.getMessage());
        }

        return output.getPath();
    }
    private String getFilePathForWhatsApp(Uri uri) {
        return copyFileToInternalStorage(uri, "whatsapp");
    }
    private String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }
    private boolean isExternalStorageDocument(Uri uri) {return "com.android.externalstorage.documents".equals(uri.getAuthority());}
    private boolean isDownloadsDocument(Uri uri) {return "com.android.providers.downloads.documents".equals(uri.getAuthority());}
    private boolean isMediaDocument(Uri uri) {return "com.android.providers.media.documents".equals(uri.getAuthority());}
    private boolean isGooglePhotosUri(Uri uri) {return "com.google.android.apps.photos.content".equals(uri.getAuthority());}
    public boolean isWhatsAppFile(Uri uri) {return "com.whatsapp.provider.media".equals(uri.getAuthority());}
    private boolean isGoogleDriveUri(Uri uri) { return "com.google.android.apps.docs.storage".equals(uri.getAuthority()) || "com.google.android.apps.docs.storage.legacy".equals(uri.getAuthority());}
    private String getDriveFilePath(Uri uri) {
        Uri returnUri = uri;
        Cursor returnCursor = MainMenu.this.getContentResolver().query(returnUri, null, null, null, null);
        /*
         * Get the column indexes of the data in the Cursor,
         *     * move to the first row in the Cursor, get the data,
         *     * and display it.
         * */
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String name = (returnCursor.getString(nameIndex));
        String size = (Long.toString(returnCursor.getLong(sizeIndex)));
        returnCursor.close();
        File file = new File(MainMenu.this.getCacheDir(), name);
        try {
            InputStream inputStream = MainMenu.this.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);
            int read = 0;
            int maxBufferSize = 1 * 1024 * 1024;
            int bytesAvailable = inputStream.available();

            //int bufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);

            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }
            Log.e("File Size", "Size " + file.length());
            inputStream.close();
            outputStream.close();
            Log.e("File Path", "Path " + file.getPath());
            Log.e("File Size", "Size " + file.length());
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
        return file.getPath();
    }
    @SuppressLint("NewApi")
    public String getPath(final Uri uri) {
        // check here to KITKAT or new version
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        String selection;
        String[] selectionArgs;
        // DocumentProvider
        if (isKitKat) {
            // ExternalStorageProvider

            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                String fullPath = getPathFromExtSD(split);
                if (!fullPath.equals("")) {
                    return fullPath;
                } else {
                    return null;
                }
            }

            // DownloadsProvider
            if (isDownloadsDocument(uri)) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    final String id;
                    try (Cursor cursor = MainMenu.this.getContentResolver().query(uri, new String[]{MediaStore.MediaColumns.DISPLAY_NAME}, null, null, null)) {
                        if (cursor != null && cursor.moveToFirst()) {
                            String fileName = cursor.getString(0);
                            String path = Environment.getExternalStorageDirectory().toString() + "/Download/" + fileName;
                            if (!TextUtils.isEmpty(path)) {
                                return path;
                            }
                        }
                    }
                    id = DocumentsContract.getDocumentId(uri);
                    if (!TextUtils.isEmpty(id)) {
                        if (id.startsWith("raw:")) {
                            return id.replaceFirst("raw:", "");
                        }
                        String[] contentUriPrefixesToTry = new String[]{
                                "content://downloads/public_downloads",
                                "content://downloads/my_downloads"
                        };
                        for (String contentUriPrefix : contentUriPrefixesToTry) {
                            try {
                                final Uri contentUri = ContentUris.withAppendedId(Uri.parse(contentUriPrefix), Long.parseLong(id));
                                return getDataColumn(MainMenu.this, contentUri, null, null);
                            } catch (NumberFormatException e) {
                                //In Android 8 and Android P the id is not a number
                                return uri.getPath().replaceFirst("^/document/raw:", "").replaceFirst("^raw:", "");
                            }
                        }
                    }
                } else {
                    final String id = DocumentsContract.getDocumentId(uri);

                    if (id.startsWith("raw:")) {
                        return id.replaceFirst("raw:", "");
                    }
                    try {
                        currURI = ContentUris.withAppendedId(
                                Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    if (currURI != null)
                        return getDataColumn(MainMenu.this, currURI, null, null);
                }
            }


            // MediaProvider
            if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;

                if ("image".equals(type))
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                else if ("video".equals(type))
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                else if ("audio".equals(type))
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                selection = "_id=?";
                selectionArgs = new String[]{split[1]};


                return getDataColumn(MainMenu.this, contentUri, selection,
                        selectionArgs);
            }
            if (isGoogleDriveUri(uri))
                return getDriveFilePath(uri);

            if (isWhatsAppFile(uri))
                return getFilePathForWhatsApp(uri);


            if ("content".equalsIgnoreCase(uri.getScheme())) {
                if (isGooglePhotosUri(uri))
                    return uri.getLastPathSegment();
                if (isGoogleDriveUri(uri))
                    return getDriveFilePath(uri);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                    // return getFilePathFromURI(context,uri);
                    return copyFileToInternalStorage(uri, "userfiles");
                    // return getRealPathFromURI(context,uri);
                } else
                    return getDataColumn(MainMenu.this, uri, null, null);
            }
            if ("file".equalsIgnoreCase(uri.getScheme())) return uri.getPath();
        } else {
            if (isWhatsAppFile(uri))
                return getFilePathForWhatsApp(uri);
            if ("content".equalsIgnoreCase(uri.getScheme())) {
                String[] projection = {
                        MediaStore.Images.Media.DATA
                };
                Cursor cursor = null;
                try {
                    cursor = MainMenu.this.getContentResolver()
                            .query(uri, projection, null, null, null);
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    if (cursor.moveToFirst()) {
                        cursor.close();
                        return cursor.getString(column_index);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (cursor != null) cursor.close();
            }
        }
        return null;
    }

    private class DoctorPage {

        private final int AUDIO_PAGE = 1;
        private final int ACCOUNT_PAGE = 2;
        private final int TUTORIAL_PAGE = 3;

        //------------------------------------------------------------------------------------------

        private RelativeLayout.LayoutParams relativeLayoutParams;
        private RelativeLayout.MarginLayoutParams relativeMarginParams;
        private ConstraintLayout.LayoutParams constraintLayoutParams;
        private ConstraintLayout.MarginLayoutParams constraintMarginParams;
        private ScrollView.LayoutParams scrollLayoutParams;
        private LinearLayout.LayoutParams linearLayoutParams;

        //------------------------------------------------------------------------------------------
        // Audio page
        private ImageView menuButton01, menuButton02, menuButton03;
        private Button audio_uploadButton;
        private ScrollView audio_scrollView;
        private LinearLayout audio_recordList;
        private ConstraintLayout menuMask;
        private TextView menuText01, menuText02, menuText03;
        private HashMap<String, String[]> audioMap; // map filename -> (title, description)
        private LinkedList<Button> audio_recordList_ButtonList;
        // Media Player settings
        private MediaPlayer currMedia;     // current media being played
        private ImageView backgroundShadow;
        private RelativeLayout audioBoard;
        private TextView audioBoardTitle;
        private SeekBar seekBar;            // Progress bar
        private Timer timer;                // Timer
        private boolean initialized, isBarChanging, isPlaying;
        private ConstraintLayout audioBeginEndMask;
        private TextView audioBegin, audioEnd;
        private Button playButton, descriptButton, deleteButton, returnButton;
        private String strTitle, currDescription;

        //------------------------------------------------------------------------------------------
        // Account page
        private RelativeLayout showIDLayout;
        private TextView showIDView;
        private HashMap<String, String> patientMapping;
        private ScrollView account_scrollView;
        private LinearLayout account_buttonList;
        private Button changeUserIDButton, changePasswordButton, registerButton, showListButton;
        // Change
        private ConstraintLayout accountChangeMask;
        private CardView accountCardView;
        private TextInputLayout accountInnerCardView;
        private EditText accountCardViewText;
        private RelativeLayout accountChangePadding01;
        private Button changeConfirmButton, changeBackButton;
        private int accountChangeRequestCode;
        // Patient List
        private ConstraintLayout patientListBase;
        private ScrollView patientListScroll;
        private LinearLayout patientListLayout;
        private RelativeLayout patientListButtonMask;
        private Button patientListButton;

        private int currPageNum;


        private void doctorPage() {
            System.err.println("1");
            pathCheck();
            currPageNum = 0;
            initialized = false;
            audioMap = null;
            // views of all pages
            localFileOpenLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();
                            currURI = data.getData();

                            if ("file".equalsIgnoreCase(currURI.getScheme())) {
                                // Open file with third-party software
                                path = currURI.getPath();
                                return;
                            }
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                                path = getPath(currURI);
                            } else {
                                path = null;
                                String[] proj = {MediaStore.Images.Media.DATA};
                                Cursor cursor = getContentResolver().query(currURI, proj, null, null, null);
                                if (null != cursor && cursor.moveToFirst()) {
                                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                                    path = cursor.getString(column_index);
                                    cursor.close();
                                }
                            }
                            audio_upload02();
                        }
                    });
            ContextThemeWrapper newContext = new ContextThemeWrapper(MainMenu.this, R.style.MenuMask);
            menuMask = new ConstraintLayout(newContext);
            menuButton01 = new ImageView(MainMenu.this);
            menuButton02 = new ImageView(MainMenu.this);
            menuButton03 = new ImageView(MainMenu.this);
            menuText01 = new TextView(MainMenu.this);
            menuText02 = new TextView(MainMenu.this);
            menuText03 = new TextView(MainMenu.this);
            menuMask.setId(View.generateViewId());
            menuButton01.setId(View.generateViewId());
            menuButton02.setId(View.generateViewId());
            menuButton03.setId(View.generateViewId());
            menuText01.setId(View.generateViewId());
            menuText02.setId(View.generateViewId());
            menuText03.setId(View.generateViewId());

            System.err.println("2");

            //------------------------------------------------------------------------------------
            // Constructed menu of page
            relativeLayoutParams = new RelativeLayout.LayoutParams(
                    PDS.i_dp2px(PDS.getWidth() - PDS.getHeight() * 0.02f),
                    PDS.i_dp2px(PDS.getHeight() * (1.0f / 8.0f + 0.02f))
            );
            relativeLayoutParams.topMargin = PDS.i_dp2px(PDS.getHeight() * (7.0f / 8.0f - 0.03f));
            relativeLayoutParams.leftMargin = PDS.i_dp2px(PDS.getHeight() * 0.01f);
            menuMask.setLayoutParams(relativeLayoutParams);
            menuMask.setAlpha(0.0f);
            //menuMask.getBackground().setAlpha(77);  // set alpha to 30%
            MainMenu.this.base.addView(menuMask);
            System.err.println("3");
            // set width, heights, and margins of menu buttons
            int radius = PDS.i_dp2px(PDS.getHeight() * 0.085f);
            menuButton01.setLayoutParams(new ConstraintLayout.LayoutParams(radius, radius));
            menuButton02.setLayoutParams(new ConstraintLayout.LayoutParams(radius, radius));
            menuButton03.setLayoutParams(new ConstraintLayout.LayoutParams(radius, radius));
            System.err.println("4");
            // clear button background
            menuButton01.setBackground(null);
            menuButton02.setBackground(null);
            menuButton03.setBackground(null);
            System.err.println("5");
            // set menu buttons as non-clickable
            menuButton01.setClickable(false);
            menuButton02.setClickable(false);
            menuButton03.setClickable(false);
            menuButton01.setEnabled(false);
            menuButton02.setEnabled(false);
            menuButton03.setEnabled(false);
            menuButton01.setOnClickListener(audio -> initAudioPage());
            menuButton02.setOnClickListener(account -> initAccountPage());
            menuButton03.setOnClickListener(tutorial -> initTutorialPage());
            System.err.println("6");

            // set width & height of menu button texts
            menuText01.setLayoutParams(new ConstraintLayout.LayoutParams(radius, ConstraintLayout.LayoutParams.WRAP_CONTENT));
            menuText02.setLayoutParams(new ConstraintLayout.LayoutParams(radius, ConstraintLayout.LayoutParams.WRAP_CONTENT));
            menuText03.setLayoutParams(new ConstraintLayout.LayoutParams(radius, ConstraintLayout.LayoutParams.WRAP_CONTENT));
            System.err.println("7");
            // set texts of menu buttons
            menuText01.setText(R.string.text_MainMenu_audio);
            menuText02.setText(R.string.text_MainMenu_account);
            menuText03.setText(R.string.text_MainMenu_tutorial);
            System.err.println("8");
            // set font & bold of menu button texts
            menuText01.setTypeface(ResourcesCompat.getFont(MainMenu.this, R.font.font_formal), Typeface.BOLD);
            menuText02.setTypeface(ResourcesCompat.getFont(MainMenu.this, R.font.font_formal), Typeface.BOLD);
            menuText03.setTypeface(ResourcesCompat.getFont(MainMenu.this, R.font.font_formal), Typeface.BOLD);
            System.err.println("9");
            // set alignment of texts
            menuText01.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            menuText02.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            menuText03.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            menuText01.setGravity(Gravity.CENTER);
            menuText02.setGravity(Gravity.CENTER);
            menuText03.setGravity(Gravity.CENTER);
            System.err.println("10");
            // set size of texts
            float textSize = PDS.dp2sp_ff(PDS.getHeight() * 0.085f * 0.25f);
            menuText01.setTextSize(textSize);
            menuText02.setTextSize(textSize);
            menuText03.setTextSize(textSize);

            // add buttons & their texts to menu
            menuMask.addView(menuButton01);
            menuMask.addView(menuButton02);
            menuMask.addView(menuButton03);
            menuMask.addView(menuText01);
            menuMask.addView(menuText02);
            menuMask.addView(menuText03);
            System.err.println("11");

            // connect constraints of buttons and their texts
            int topMargin = PDS.i_dp2px(PDS.getHeight() * (1.0f / 8.0f + 0.02f - 0.085f - 0.085f * 0.25f) / 2.0f);
            int startMargin = PDS.i_dp2px((PDS.getWidth() - PDS.getHeight() * (0.02f + 0.085f * 3.0f)) / 4.0f);
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(menuMask);
            System.err.println("11.1");
            constraintSet.connect(menuButton01.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, topMargin);
            constraintSet.connect(menuButton01.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, startMargin);
            constraintSet.connect(menuButton02.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, topMargin);
            constraintSet.connect(menuButton02.getId(), ConstraintSet.START, menuButton01.getId(), ConstraintSet.END, startMargin);
            constraintSet.connect(menuButton03.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, topMargin);
            constraintSet.connect(menuButton03.getId(), ConstraintSet.START, menuButton02.getId(), ConstraintSet.END, startMargin);
            System.err.println("12");
            //-------------------
            constraintSet.connect(menuText01.getId(), ConstraintSet.TOP, menuButton01.getId(), ConstraintSet.BOTTOM, 0);
            constraintSet.connect(menuText01.getId(), ConstraintSet.START, menuButton01.getId(), ConstraintSet.START, 0);
            constraintSet.connect(menuText02.getId(), ConstraintSet.TOP, menuButton02.getId(), ConstraintSet.BOTTOM, 0);
            constraintSet.connect(menuText02.getId(), ConstraintSet.START, menuButton02.getId(), ConstraintSet.START, 0);
            constraintSet.connect(menuText03.getId(), ConstraintSet.TOP, menuButton03.getId(), ConstraintSet.BOTTOM, 0);
            constraintSet.connect(menuText03.getId(), ConstraintSet.START, menuButton03.getId(), ConstraintSet.START, 0);
            constraintSet.applyTo(menuMask);
            System.err.println("13");


            // -------------------------------------------------------------------------------------
            // initializations of audio page items
            newContext = new ContextThemeWrapper(MainMenu.this, R.style.AudioUploadMask);
            audio_uploadButton = new Button(newContext, null, R.style.AudioUploadMask);
            audio_scrollView = new ScrollView(MainMenu.this);
            audio_recordList = new LinearLayout(MainMenu.this);
            audio_uploadButton.setId(View.generateViewId());
            audio_scrollView.setId(View.generateViewId());
            audio_recordList.setId(View.generateViewId());
            // set each elements
            relativeLayoutParams = new RelativeLayout.LayoutParams(
                    PDS.i_dp2px(PDS.getWidth() - PDS.getHeight() * 0.03f),
                    PDS.i_dp2px(PDS.getHeight() * 0.07f)
            );
            relativeLayoutParams.topMargin = PDS.i_dp2px(PDS.getHeight() * 0.015f);
            relativeLayoutParams.leftMargin = PDS.i_dp2px(PDS.getHeight() * 0.015f);
            relativeLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            audio_uploadButton.setLayoutParams(relativeLayoutParams);
            audio_uploadButton.setClickable(false);
            audio_uploadButton.setEnabled(false);
            audio_uploadButton.setText(R.string.text_MainMenu_upload);
            audio_uploadButton.setTextColor(MainMenu.this.getResources().getColor(R.color.white));
            audio_uploadButton.setOnClickListener(switchState -> {
                if (ContextCompat.checkSelfPermission(MainMenu.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainMenu.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        builder.setMessage("The APP need permission of accessing external storage to upload audios from the external path.")
                                .setTitle("READ_EXTERNAL_STORAGE is required for uploading audios.");
                        builder.setPositiveButton("OK", (dialog, id) -> {});
                        builder.show();
                    } else {
                        ActivityCompat.requestPermissions(MainMenu.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_EXTERNAL_FILE);
                    }
                } else {
                    audio_upload01();
                }
            });
            relativeLayoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    PDS.i_dp2px(PDS.getHeight() * (7.0f / 8.0f - 0.14f))
            );
            relativeLayoutParams.topMargin = PDS.i_dp2px(PDS.getHeight() * 0.1f);
            audio_scrollView.setLayoutParams(relativeLayoutParams);
            audio_scrollView.setEnabled(false);

            linearLayoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            );
            audio_recordList.setLayoutParams(linearLayoutParams);
            audio_recordList.setOrientation(LinearLayout.VERTICAL);
            audio_recordList.setEnabled(false);
            audio_scrollView.addView(audio_recordList);
            System.err.println("16");

            // -------------------------------------------------------------------------------------
            // initializations of account page items
            loadPatientList();
            showIDLayout = new RelativeLayout(MainMenu.this);
            showIDLayout.setId(View.generateViewId());
            showIDView = new TextView(MainMenu.this);
            showIDView.setId(View.generateViewId());
            account_scrollView = new ScrollView(MainMenu.this);
            account_buttonList = new LinearLayout(MainMenu.this);
            newContext = new ContextThemeWrapper(MainMenu.this, R.style.AccountButtonChangeUserID);
            changeUserIDButton = new Button(newContext, null, R.style.AccountButtonChangeUserID);
            newContext = new ContextThemeWrapper(MainMenu.this, R.style.AccountButtonChangePassword);
            changePasswordButton = new Button(newContext, null, R.style.AccountButtonChangePassword);
            newContext = new ContextThemeWrapper(MainMenu.this, R.style.AccountButtonRegisterNewPatient);
            registerButton = new Button(newContext, null, R.style.AccountButtonRegisterNewPatient);
            newContext = new ContextThemeWrapper(MainMenu.this, R.style.AccountButtonShowPatientList);
            showListButton = new Button(newContext, null, R.style.AccountButtonShowPatientList);
            changeUserIDButton.setId(View.generateViewId());
            changePasswordButton.setId(View.generateViewId());
            registerButton.setId(View.generateViewId());
            showListButton.setId(View.generateViewId());

            relativeLayoutParams = new RelativeLayout.LayoutParams(
                    PDS.i_dp2px(PDS.getWidth() - PDS.getHeight() * 0.03f),
                    PDS.i_dp2px(PDS.getHeight() * 0.07f)
            );
            relativeLayoutParams.topMargin = PDS.i_dp2px(PDS.getHeight() * 0.015f);
            relativeLayoutParams.leftMargin = PDS.i_dp2px(PDS.getHeight() * 0.015f);
            relativeLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            showIDLayout.setLayoutParams(relativeLayoutParams);
            relativeLayoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            relativeLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
            showIDView.setLayoutParams(relativeLayoutParams);
            showIDView.setTypeface(ResourcesCompat.getFont(MainMenu.this, R.font.font_burgela_bold), Typeface.NORMAL);
            showIDView.setText("Doctor ID:\n" + currUserID);
            showIDView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, PDS.getWidth() * 0.0486618004866f);
            showIDView.setTextColor(MainMenu.this.getResources().getColor(R.color.white));
            showIDLayout.addView(showIDView);

            relativeLayoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    PDS.i_dp2px(PDS.getHeight() * (7.0f / 8.0f - 0.14f))
            );
            relativeLayoutParams.topMargin = PDS.i_dp2px(PDS.getHeight() * 0.10f);
            account_scrollView.setId(View.generateViewId());
            account_scrollView.setLayoutParams(relativeLayoutParams);
            account_scrollView.setEnabled(true);
            linearLayoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            );
            account_buttonList.setId(View.generateViewId());
            account_buttonList.setLayoutParams(linearLayoutParams);
            account_buttonList.setOrientation(LinearLayout.VERTICAL);
            account_buttonList.setEnabled(true);
            account_scrollView.addView(account_buttonList);

            linearLayoutParams = new LinearLayout.LayoutParams(
                    PDS.i_dp2px(PDS.getWidth() * 0.934963503649635f),
                    PDS.i_dp2px(PDS.getHeight() * 0.07f)
            );
            linearLayoutParams.leftMargin = PDS.i_dp2px(PDS.getWidth() * 0.0325182481751825f);
            linearLayoutParams.topMargin = PDS.i_dp2px(PDS.getHeight() * 0.030303030303f);
            changeUserIDButton.setLayoutParams(linearLayoutParams);
            changeUserIDButton.setEnabled(true);
            changeUserIDButton.setClickable(true);
            changeUserIDButton.setText("Change Doctor ID");
            changeUserIDButton.setTextColor(MainMenu.this.getResources().getColor(R.color.white));
            changePasswordButton.setLayoutParams(linearLayoutParams);
            changePasswordButton.setEnabled(true);
            changePasswordButton.setClickable(true);
            changePasswordButton.setText("Change Password");
            changePasswordButton.setTextColor(MainMenu.this.getResources().getColor(R.color.white));
            registerButton.setLayoutParams(linearLayoutParams);
            registerButton.setEnabled(true);
            registerButton.setClickable(true);
            registerButton.setText("Register New Patient");
            registerButton.setTextColor(MainMenu.this.getResources().getColor(R.color.white));
            showListButton.setLayoutParams(linearLayoutParams);
            showListButton.setEnabled(true);
            showListButton.setClickable(true);
            showListButton.setText("Show your Patient List");
            showListButton.setTextColor(MainMenu.this.getResources().getColor(R.color.white));


            changeUserIDButton.setOnClickListener(changeUserID -> {
                accountChangeRequestCode = 1;
                accountChangePageInit();
            });
            changePasswordButton.setOnClickListener(changePassword -> {
                accountChangeRequestCode = 2;
                accountChangePageInit();
            });
            registerButton.setOnClickListener(registerPatient -> {
                accountChangeRequestCode = 3;
                accountChangePageInit();
            });
            showListButton.setOnClickListener(showPatientList -> patientListShow());

            account_buttonList.addView(changeUserIDButton);
            account_buttonList.addView(changePasswordButton);
            account_buttonList.addView(showListButton);
            account_buttonList.addView(registerButton);

            // default starting page
            initAudioPage();
        }
        private void pathCheck() {
            File folder; String path;
            path = MainMenu.this.getFilesDir().toString() + "/emory_health";
            folder = new File(path);
            if (!folder.exists() && !folder.mkdirs()) {
                System.err.println("Error 0xCCCBA1: data directory path check 01 failed.");
                System.exit(-1);
            }
            path = MainMenu.this.getFilesDir().toString() + "/emory_health/data";
            folder = new File(path);
            if (!folder.exists() && !folder.mkdirs()) {
                System.err.println("Error 0xCCCBA2: data directory path check 02 failed.");
                System.exit(-1);
            }
            try {
                path = MainMenu.this.getFilesDir().toString() + "/emory_health/data/" +
                        security.encrypt(currUserHashCode);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
            }
            folder = new File(path);
            if (!folder.exists() && !folder.mkdirs()) {
                System.err.println("Error 0xCCCBA3: data directory path check 03 failed.");
                System.exit(-1);
            }
            try {
                path = MainMenu.this.getFilesDir().toString() + "/emory_health/data/" +
                        security.encrypt(currUserHashCode) + "/audio_records";
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
            }
            folder = new File(path);
            if (!folder.exists() && !folder.mkdirs()) {
                System.err.println("Error 0xCCCBA4: data directory path check 04 failed.");
                System.exit(-1);
            }
        }

        private void initAudioPage() {
            if (currPageNum == 1) return;
            if (currPageNum != 0) destroyCurrPage();

            currPageNum = AUDIO_PAGE;  // set current page as audio page

            // change menu pattern
            menuButton01.setImageResource(R.drawable.cd_logo_02);
            menuButton02.setImageResource(R.drawable.account_logo_01);
            menuButton03.setImageResource(R.drawable.book_logo_01);
            menuText01.setTextColor(MainMenu.this.getResources().getColor(R.color.menu_cyan));
            menuText02.setTextColor(MainMenu.this.getResources().getColor(R.color.white));
            menuText03.setTextColor(MainMenu.this.getResources().getColor(R.color.white));

            // read all existing audios' filenames, title & description

            System.err.println("11111");
            if (audioMap == null) {
                System.err.println("22222");
                audioMap = new HashMap<>();
                audio_recordList_ButtonList = new LinkedList<>();
            }
            try {
                File folder = new File(MainMenu.this.getFilesDir().toString() +
                        "/emory_health/data/"+ security.encrypt(currUserHashCode) +"/audio_records");
                if (!folder.exists() && !folder.mkdirs()) {
                    System.err.println("Error 0x000C0: create audio record directory failed.");
                    System.exit(-1);
                }
                String currAudioDir = folder.toString();
                File audioFile = new File(currAudioDir, "audio_data.emory");
                if (audioFile.exists()) {
                    Scanner audioDataIn = new Scanner(audioFile);
                    int index = 0;
                    String[] data = new String[3];
                    while (audioDataIn.hasNextLine()) {

                        // data[0] = filename
                        // data[1] = title
                        // data[2] = description file name
                        data[index++] = audioDataIn.nextLine();
                        if (index == 3) {
                            index = 0;
                            try {
                                String audioFileName = security.decrypt(data[0]);
                                String audioTitle = security.decrypt(data[1]);
                                File descriptionFile = new File(currAudioDir, data[2]);
                                StringBuilder description = new StringBuilder();
                                if (descriptionFile.exists()) {
                                    Scanner descriptionIn = new Scanner(descriptionFile);
                                    while (descriptionIn.hasNextLine()) {
                                        description.append(security.decrypt(descriptionIn.nextLine())).append('\n');
                                    }
                                } else {
                                    try {
                                        boolean createSuccess = descriptionFile.createNewFile();
                                        if (!createSuccess) {
                                            System.err.println("Create file failed");
                                            System.exit(-1);
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        System.exit(-1);
                                    }
                                }
                                if (!audioMap.containsKey(audioFileName)) {
                                    ContextThemeWrapper newContext = new ContextThemeWrapper(MainMenu.this, R.style.AudioButtonMask);
                                    Button newAudioButton = new Button(newContext, null, R.style.AudioButtonMask);
                                    newAudioButton.setId(View.generateViewId());
                                    linearLayoutParams = new LinearLayout.LayoutParams(
                                            PDS.i_dp2px(PDS.getWidth() * 0.8f),
                                            PDS.i_dp2px(PDS.getHeight() * 0.12f)
                                    );
                                    linearLayoutParams.leftMargin = PDS.i_dp2px(PDS.getWidth() * 0.1f);
                                    linearLayoutParams.topMargin = PDS.i_dp2px(PDS.getHeight() * 0.02f);
                                    newAudioButton.setLayoutParams(linearLayoutParams);
                                    newAudioButton.setClickable(true);
                                    newAudioButton.setEnabled(true);
                                    newAudioButton.setText(audioTitle);
                                    newAudioButton.setTextColor(MainMenu.this.getResources().getColor(R.color.buttonAudioButtonTextColor));
                                    newAudioButton.setTag(R.id.audio_entry_filename, audioFileName);
                                    newAudioButton.setOnClickListener(playAudio -> audio_play((String) newAudioButton.getTag(R.id.audio_entry_filename)));
                                    audio_recordList_ButtonList.add(newAudioButton);
                                    audio_recordList.addView(newAudioButton);
                                }
                                this.audioMap.put(audioFileName, new String[]{audioTitle, description.toString()});
                            } catch (Exception e) {

                                // Error: 0xCD136E
                                System.err.println("Error 0xCD136E: cannot read/decrypt audio data from doctor's audio directory!");
                                System.exit(-1);
                            }
                        }
                    }
                } else {
                    try {
                        boolean createSuccess = audioFile.createNewFile();
                        if (!createSuccess) {
                            System.err.println("Create file failed");
                            System.exit(-1);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.exit(-1);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.err.println("444444");
            base.addView(audio_uploadButton);
            base.addView(audio_scrollView);
            System.err.println("17");

            // reopen
            if (initialized) {
                audio_uploadButton.setClickable(true);
                audio_uploadButton.setEnabled(true);
                audio_scrollView.setEnabled(true);
                audio_recordList.setEnabled(true);
                menuButton01.setClickable(true);
                menuButton02.setClickable(true);
                menuButton03.setClickable(true);
                menuButton01.setEnabled(true);
                menuButton02.setEnabled(true);
                menuButton03.setEnabled(true);
                setStatusAudioRecords(true);
            } else {
                initialized = true;
                Handler handler = new Handler();
                handler.postDelayed(this::logInTransitAnimation, 200);
            }
        }
        private void audio_upload01() {
            try {
                System.err.println("upload00");
                File folder = new File(MainMenu.this.getFilesDir().toString() +
                        "/emory_health/data/"+ security.encrypt(currUserHashCode) +"/audio_records");
                if (!folder.exists() && !folder.mkdirs()) {
                    System.err.println("Error 0xAAAAB0: create audio record directory failed");
                    System.exit(-1);
                }
                System.err.println("upload01");
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                // intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // allow choosing multiple files
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                System.err.println("upload02");
                localSearchChoice = 1;
                System.err.println("upload03");
                setResult(Activity.RESULT_OK, intent);
                localFileOpenLauncher.launch(intent);
                System.err.println("upload04");

            } catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }
        private void audio_upload02() {
            try {
                System.err.println("path: " + path);
                String filename = path.substring(path.lastIndexOf("/") + 1);
                System.err.println("filename: " + filename);
                String extension = filename.substring(filename.lastIndexOf("."));
                filename = security.encrypt(filename) + extension;
                String currAudioPath = MainMenu.this.getFilesDir().toString() +
                        "/emory_health/data/" + security.encrypt(currUserHashCode) +"/audio_records/" + filename;
                File src = new File(path);
                if (!src.exists()) {
                    builder.setMessage("Please assign an existing file to upload.")
                            .setTitle("File does not existed!");
                    builder.setPositiveButton("OK", (dialog, id) -> {
                    });
                    builder.show();
                    return;
                }
                if (!copyFileLocal(src, currAudioPath)) {
                    System.err.println("audio_init_flag: 01");
                    builder.setMessage("Please delete the audio file in your audio" +
                                    " directory before uploading a new one with same name.")
                            .setTitle("File with same name found in your audio directory!");
                    builder.setPositiveButton("OK", (dialog, id) -> {});
                    builder.show();
                    return;
                }
                writeCheckAudioData(filename);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }
        private void audio_play(String filename) {
            if (!audioMap.containsKey(filename)) {
                System.err.println("Error 0001EF: cannot find key(filename) in audio map!");
                System.exit(-1);
            }
            currMedia = new MediaPlayer();
            isPlaying = false;
            try {
                File audioSrc = new File(MainMenu.this.getFilesDir().toString() +
                        "/emory_health/data/" + security.encrypt(currUserHashCode) + "/audio_records", filename);
                System.err.println("MUSIC_PATH_DEBUG: " + audioSrc.getPath());
                currMedia.setDataSource(audioSrc.getPath());   // assign path of audio
                currMedia.prepare();  // prepare media
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
            }
            menuButton01.setClickable(false);
            menuButton01.setEnabled(false);
            menuButton02.setClickable(false);
            menuButton02.setEnabled(false);
            menuButton03.setClickable(false);
            menuButton03.setEnabled(false);
            audio_uploadButton.setClickable(false);
            audio_uploadButton.setEnabled(false);
            setStatusAudioRecords(false);
            String[] tmp = audioMap.get(filename);
            assert tmp != null;
            strTitle = tmp[0];
            currDescription = tmp[1];


            backgroundShadow = new ImageView(MainMenu.this);
            relativeLayoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
            );
            backgroundShadow.setId(View.generateViewId());
            backgroundShadow.setLayoutParams(relativeLayoutParams);
            backgroundShadow.setImageResource(R.drawable.shadow_background);
            backgroundShadow.setAlpha(0.0f);
            backgroundShadow.bringToFront();
            ground.addView(backgroundShadow);
            base02.setAlpha(0.0f);
            base02.bringToFront();


            audioBoard = new RelativeLayout(MainMenu.this);
            relativeLayoutParams = new RelativeLayout.LayoutParams(
                    PDS.i_dp2px(PDS.getWidth() - PDS.getHeight() * 0.03f),
                    PDS.i_dp2px(PDS.getHeight() * 0.3f)
            );
            relativeLayoutParams.topMargin = PDS.i_dp2px(PDS.getHeight() * 0.35f);
            relativeLayoutParams.leftMargin = PDS.i_dp2px(PDS.getHeight() * 0.015f);
            audioBoard.setId(View.generateViewId());
            audioBoard.setLayoutParams(relativeLayoutParams);
            audioBoard.setBackground(ContextCompat.getDrawable(MainMenu.this, R.drawable.main_page_blue_board));
            base02.addView(audioBoard);


            audioBoardTitle = new TextView(MainMenu.this);
            relativeLayoutParams = new RelativeLayout.LayoutParams(
                    PDS.i_dp2px(PDS.getHeight() * 0.345f),
                    PDS.i_dp2px(PDS.getHeight() * 0.09f)
            );
            relativeLayoutParams.topMargin = PDS.i_dp2px(PDS.getHeight() * 0.03f);
            relativeLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            audioBoardTitle.setId(View.generateViewId());
            audioBoardTitle.setLayoutParams(relativeLayoutParams);
            audioBoardTitle.setGravity(Gravity.CENTER);
            audioBoardTitle.setText(strTitle);
            audioBoardTitle.setTypeface(ResourcesCompat.getFont(MainMenu.this, R.font.font_burgela), Typeface.BOLD);
            audioBoardTitle.setTextColor(getResources().getColor(R.color.audioBoardTextColor));
            audioBoardTitle.setTextSize(PDS.dp2sp_ff(PDS.getHeight() * 0.037037f));
            audioBoard.addView(audioBoardTitle);


            seekBar = new SeekBar(MainMenu.this);
            seekBar.setId(View.generateViewId());
            relativeLayoutParams = new RelativeLayout.LayoutParams(
                    PDS.i_dp2px(PDS.getWidth() * 0.82379562f),
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            relativeLayoutParams.topMargin = PDS.i_dp2px(PDS.getHeight() * 0.14f);
            relativeLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            seekBar.setLayoutParams(relativeLayoutParams);
            seekBar.setEnabled(false);
            audioBoard.addView(seekBar);

            audioBeginEndMask = new ConstraintLayout(MainMenu.this);
            relativeLayoutParams = new RelativeLayout.LayoutParams(
                    PDS.i_dp2px(PDS.getWidth() * 0.82379562f),
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            relativeLayoutParams.topMargin = PDS.i_dp2px(PDS.getHeight() * 0.164983164f);
            relativeLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            audioBeginEndMask.setLayoutParams(relativeLayoutParams);
            audioBeginEndMask.setId(View.generateViewId());
            audioBeginEndMask.setEnabled(false);
            audioBoard.addView(audioBeginEndMask);


            audioBegin = new TextView(MainMenu.this);
            audioBegin.setId(View.generateViewId());
            relativeLayoutParams = new RelativeLayout.LayoutParams(
                    PDS.i_dp2px(PDS.getHeight() * 0.09f),
                    PDS.i_dp2px(PDS.getHeight() * 0.028058f)
            );
            audioBegin.setLayoutParams(relativeLayoutParams);
            audioBegin.setGravity(Gravity.CENTER);
            audioBoardTitle.setTypeface(ResourcesCompat.getFont(MainMenu.this, R.font.font_formal), Typeface.BOLD);
            audioBoardTitle.setTextColor(getResources().getColor(R.color.audioBoardTextColor));
            audioBegin.setTextSize(PDS.dp2sp_ff(PDS.getHeight() * 0.02f));

            audioEnd = new TextView(MainMenu.this);
            audioEnd.setId(View.generateViewId());
            relativeLayoutParams = new RelativeLayout.LayoutParams(
                    PDS.i_dp2px(PDS.getHeight() * 0.09f),
                    PDS.i_dp2px(PDS.getHeight() * 0.028058f)
            );
            audioEnd.setLayoutParams(relativeLayoutParams);
            audioEnd.setGravity(Gravity.CENTER);
            audioBoardTitle.setTypeface(ResourcesCompat.getFont(MainMenu.this, R.font.font_formal), Typeface.BOLD);
            audioBoardTitle.setTextColor(getResources().getColor(R.color.audioBoardTextColor));
            audioEnd.setTextSize(PDS.dp2sp_ff(PDS.getHeight() * 0.02f));
            audioBegin.setEnabled(false);
            audioEnd.setEnabled(false);
            audioBeginEndMask.addView(audioBegin);
            audioBeginEndMask.addView(audioEnd);

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(audioBeginEndMask);
            constraintSet.connect(audioBegin.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
            constraintSet.connect(audioBegin.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
            constraintSet.connect(audioEnd.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
            constraintSet.connect(audioEnd.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
            constraintSet.applyTo(audioBeginEndMask);


            float y = (PDS.getWidth() - PDS.getHeight() * 0.218f) / 5.0f;
            playButton = new Button(MainMenu.this);
            descriptButton = new Button(MainMenu.this);
            deleteButton = new Button(MainMenu.this);
            returnButton = new Button(MainMenu.this);

            relativeLayoutParams = new RelativeLayout.LayoutParams(PDS.i_dp2px(PDS.getHeight() * 0.047f), PDS.i_dp2px(PDS.getHeight() * 0.047f));
            relativeLayoutParams.topMargin = PDS.i_dp2px(PDS.getHeight() * 0.213f);
            relativeLayoutParams.leftMargin = PDS.i_dp2px(y);
            playButton.setLayoutParams(relativeLayoutParams);
            relativeLayoutParams = new RelativeLayout.LayoutParams(PDS.i_dp2px(PDS.getHeight() * 0.047f), PDS.i_dp2px(PDS.getHeight() * 0.047f));
            relativeLayoutParams.topMargin = PDS.i_dp2px(PDS.getHeight() * 0.213f);
            relativeLayoutParams.leftMargin = PDS.i_dp2px(y * 2.0f + PDS.getHeight() * 0.047f);
            descriptButton.setLayoutParams(relativeLayoutParams);
            relativeLayoutParams = new RelativeLayout.LayoutParams(PDS.i_dp2px(PDS.getHeight() * 0.047f), PDS.i_dp2px(PDS.getHeight() * 0.047f));
            relativeLayoutParams.topMargin = PDS.i_dp2px(PDS.getHeight() * 0.213f);
            relativeLayoutParams.leftMargin = PDS.i_dp2px(y * 3.0f + PDS.getHeight() * 0.094f);
            deleteButton.setLayoutParams(relativeLayoutParams);
            relativeLayoutParams = new RelativeLayout.LayoutParams(PDS.i_dp2px(PDS.getHeight() * 0.047f), PDS.i_dp2px(PDS.getHeight() * 0.047f));
            relativeLayoutParams.topMargin = PDS.i_dp2px(PDS.getHeight() * 0.213f);
            relativeLayoutParams.leftMargin = PDS.i_dp2px(y * 4.0f + PDS.getHeight() * 0.141f);
            returnButton.setLayoutParams(relativeLayoutParams);

            playButton.setBackground(ContextCompat.getDrawable(MainMenu.this, R.drawable.ic_audio_board_button_play));
            descriptButton.setBackground(ContextCompat.getDrawable(MainMenu.this, R.drawable.ic_audio_board_button_description));
            deleteButton.setBackground(ContextCompat.getDrawable(MainMenu.this, R.drawable.ic_audio_board_button_delete));
            returnButton.setBackground(ContextCompat.getDrawable(MainMenu.this, R.drawable.ic_audio_board_button_return));
            playButton.setEnabled(false);
            descriptButton.setEnabled(false);
            deleteButton.setEnabled(false);
            returnButton.setEnabled(false);
            playButton.setClickable(false);
            descriptButton.setClickable(false);
            deleteButton.setClickable(false);
            returnButton.setClickable(false);
            audioBoard.addView(playButton);
            audioBoard.addView(descriptButton);
            audioBoard.addView(deleteButton);
            audioBoard.addView(returnButton);

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    int duration2 = currMedia.getDuration() / 1000;              // total length of audio
                    int position = currMedia.getCurrentPosition();               // current pos of audio
                    audioBegin.setText(transferSecToStr(position / 1000));  // start time
                    audioEnd.setText(transferSecToStr(duration2));               // total time
                }

                // Signify that a touch drag gesture has started
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    isBarChanging = true;
                }

                // Get & send new progress to seek bar when hand releases
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    isBarChanging = false;
                    currMedia.seekTo(seekBar.getProgress());  // change position of seek bar
                    audioBegin.setText(transferSecToStr(currMedia.getCurrentPosition() / 1000));
                }
            });
            int duration2 = currMedia.getDuration() / 1000;
            int position = currMedia.getCurrentPosition();
            audioBegin.setText(transferSecToStr(position / 1000));
            audioEnd.setText(transferSecToStr(duration2));

            playButton.setOnClickListener(playOrPause -> audioPlayPauseControl());
            descriptButton.setOnClickListener(descriptionShow -> {
            });  // incomplete
            deleteButton.setOnClickListener(deleteCurrAudio -> {
            });    // incomplete
            returnButton.setOnClickListener(returnToAudioMain -> audioPlayReturn());
            audioMainToMediaAnimation();
        }
        private String transferSecToStr(int secs) {
            StringBuilder str = new StringBuilder("00:00:00");
            int hours = secs / 3600;
            secs -= hours * 3600;
            int minutes = secs / 60;
            secs -= minutes * 60;
            str.setCharAt(0, (char) ((hours / 10 % 10) + 48));
            str.setCharAt(1, (char) ((hours % 10) + 48));
            str.setCharAt(3, (char) ((minutes / 10) + 48));
            str.setCharAt(4, (char) ((minutes % 10) + 48));
            str.setCharAt(6, (char) ((secs / 10) + 48));
            str.setCharAt(7, (char) ((secs % 10) + 48));
            return str.toString();
        }
        private void audioPlayPauseControl() {
            if (isPlaying) {
                isPlaying = false;
                playButton.setBackground(ContextCompat.getDrawable(MainMenu.this, R.drawable.ic_audio_board_button_play));
                if (currMedia.isPlaying()) {
                    currMedia.pause();
                }
                if (timer!=null) {
                    timer.cancel();
                    timer = null;
                }
            } else {
                isPlaying = true;
                playButton.setBackground(ContextCompat.getDrawable(MainMenu.this, R.drawable.ic_audio_board_button_pause));
                if (!currMedia.isPlaying()) {
                    currMedia.start();
                    int duration = currMedia.getDuration();  // total time length of audio
                    seekBar.setMax(duration);                // set total time as max of seek bar
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (!isBarChanging) {
                                if (currMedia.getCurrentPosition() == seekBar.getMax()) {
                                    seekBar.setProgress(0);
                                    isPlaying = false;
                                    playButton.setBackground(ContextCompat.getDrawable(MainMenu.this, R.drawable.ic_audio_board_button_play));
                                    if (currMedia.isPlaying()) {
                                        currMedia.stop();
                                        currMedia.seekTo(seekBar.getProgress());
                                    }
                                    if (timer!=null) {
                                        timer.cancel();
                                        timer = null;
                                    }
                                } else
                                    seekBar.setProgress(currMedia.getCurrentPosition());
                            }
                        }
                    }, 0, 50);
                }
            }
        }
        private void audioPlayReturn() {
            if (!isBarChanging) {
                if (currMedia != null) {
                    if (currMedia.isPlaying())
                        return;
                    currMedia.stop();
                    currMedia.release();
                    currMedia = null;
                }
                mediaToAudioMainAnimation();
            }
        }
        private void setStatusAudioRecords(boolean isEnabled) {
            for (Button currBtn : audio_recordList_ButtonList) {
                currBtn.setEnabled(isEnabled);
                currBtn.setClickable(isEnabled);
            }
        }
        private void writeCheckAudioData(String fullName) {
            System.err.println("audio_init_flag: 02");
            StringBuilder nameBuilder = new StringBuilder("e");
            for (int i = 0; i < 16; i++) {
                nameBuilder.append(randomChar(2));
            }
            nameBuilder.append(".emory");
            String newDescriptionName = nameBuilder.toString();
            System.err.println("audio_init_flag: 02");
            nameBuilder = new StringBuilder("Doctor Audio ");
            for (int i = 0; i < 5; i++) {
                nameBuilder.append(randomChar(1));
            }
            String newTitle = nameBuilder.toString();
            System.err.println("audio_init_flag: 04");
            try {
                File file;
                file = new File(getFilesDir().toString() +
                        "/emory_health/data/" + security.encrypt(currUserHashCode) + "/audio_records",
                        newDescriptionName);
                System.err.println("audio_init_flag: 05");
                if (!file.exists()) {
                    try {
                        System.err.println("audio_init_flag: 06");
                        boolean createSuccess = file.createNewFile();
                        if (!createSuccess) {
                            System.err.println("Create file failed");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.exit(-1);
                    }
                }
                System.err.println("audio_init_flag: 07");
                file = new File(getFilesDir().toString() +
                        "/emory_health/data/" + security.encrypt(currUserHashCode) + "/audio_records",
                        "audio_data.emory");
                System.err.println("audio_init_flag: 08");
                if (!file.exists()) {
                    try {
                        System.err.println("audio_init_flag: 09");
                        boolean createSuccess = file.createNewFile();
                        if (!createSuccess) {
                            System.err.println("Create file failed");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.exit(-1);
                    }
                }
                System.err.println("audio_init_flag: 10");
                if (!file.canWrite() && !file.setWritable(true)) {
                    System.err.println("is read-only");
                    System.exit(-1);
                }
                System.err.println("audio_init_flag: 11");
                // Successfully write all data in file
                FileWriter writer = new FileWriter(file, true);
                try {
                    writer.write(security.encrypt(fullName) + "\n");
                    writer.write(security.encrypt(newTitle) + "\n");
                    writer.write(security.encrypt(newDescriptionName) + "\n");
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(-1);
                }
                writer.flush();
                writer.close();
                System.err.println("audio_init_flag: 12");
                ContextThemeWrapper newContext = new ContextThemeWrapper(MainMenu.this, R.style.AudioButtonMask);
                Button newAudioButton = new Button(newContext, null, R.style.AudioButtonMask);
                newAudioButton.setId(View.generateViewId());
                linearLayoutParams = new LinearLayout.LayoutParams(
                        PDS.i_dp2px(PDS.getWidth() * 0.8f),
                        PDS.i_dp2px(PDS.getHeight() * 0.12f)
                );
                linearLayoutParams.leftMargin = PDS.i_dp2px(PDS.getWidth() * 0.1f);
                linearLayoutParams.topMargin = PDS.i_dp2px(PDS.getHeight() * 0.02f);
                newAudioButton.setLayoutParams(linearLayoutParams);
                newAudioButton.setClickable(true);
                newAudioButton.setEnabled(true);
                newAudioButton.setText(newTitle);
                newAudioButton.setTextColor(MainMenu.this.getResources().getColor(R.color.buttonAudioButtonTextColor));
                newAudioButton.setTag(R.id.audio_entry_filename, fullName);
                newAudioButton.setOnClickListener(playAudio -> audio_play((String) newAudioButton.getTag(R.id.audio_entry_filename)));
                audio_recordList_ButtonList.add(newAudioButton);
                audioMap.put(fullName, new String[]{newTitle, newDescriptionName});
                audio_recordList.addView(newAudioButton);
                currURI = null;
                path = null;
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }



        private void initAccountPage() {
            if (currPageNum == 2) return;
            if (currPageNum != 0) destroyCurrPage();

            currPageNum = ACCOUNT_PAGE;  // set current page as account page

            // change menu pattern
            menuButton01.setImageResource(R.drawable.cd_logo_01);
            menuButton02.setImageResource(R.drawable.account_logo_02);
            menuButton03.setImageResource(R.drawable.book_logo_01);
            menuText01.setTextColor(MainMenu.this.getResources().getColor(R.color.white));
            menuText02.setTextColor(MainMenu.this.getResources().getColor(R.color.menu_cyan));
            menuText03.setTextColor(MainMenu.this.getResources().getColor(R.color.white));

            // read all existing audios' filenames, title & description
            base.addView(account_scrollView);
            base.addView(showIDLayout);

            menuButton01.setClickable(true);
            menuButton01.setEnabled(true);
            menuButton02.setClickable(true);
            menuButton02.setEnabled(true);
            menuButton03.setClickable(true);
            menuButton03.setEnabled(true);
        }
        private void loadPatientList() {
            try {
                String doctorPath = getFilesDir().toString() +
                        "/emory_health/data";
                File directoryCheck = new File (doctorPath, security.encrypt(currUserHashCode));
                if (!directoryCheck.exists() && !directoryCheck.mkdir()) {
                        System.err.println("Error 0xC00000: create patient list directory failed!");
                        System.exit(-1);
                }
                File doctorPatientList = new File(doctorPath + "/" + security.encrypt(currUserHashCode), "patient_list.emory");
                patientMapping = new HashMap<>();
                if (!doctorPatientList.exists()) {
                    if (!doctorPatientList.createNewFile()) {
                        System.err.println("Error 0xC00001: create patient list file failed!");
                        System.exit(-1);
                    }
                    return;
                }
                Scanner listDataIn = new Scanner(doctorPatientList);
                while (listDataIn.hasNextLine()) {
                    try {
                        String curr = security.decrypt(listDataIn.nextLine());
                        patientMapping.put(curr, patientData.get(curr)[1]);
                        System.err.println("JDF:" + curr + "\t" + patientData.get(curr)[1]);
                    } catch (Exception e) {
                        System.err.println("Error 0xC00003: cannot read/decrypt patient list data!");
                        System.exit(-1);
                    }
                }
            }catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }
        private void accountChangePageInit() {
            menuButton01.setEnabled(false);
            menuButton01.setClickable(false);
            menuButton02.setEnabled(false);
            menuButton02.setClickable(false);
            menuButton03.setEnabled(false);
            menuButton03.setClickable(false);
            changeUserIDButton.setClickable(false);
            changeUserIDButton.setEnabled(false);
            changePasswordButton.setClickable(false);
            changePasswordButton.setEnabled(false);
            registerButton.setClickable(false);
            registerButton.setEnabled(false);
            showListButton.setClickable(false);
            showListButton.setEnabled(false);

            backgroundShadow = new ImageView(MainMenu.this);
            relativeLayoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
            );
            backgroundShadow.setId(View.generateViewId());
            backgroundShadow.setLayoutParams(relativeLayoutParams);
            backgroundShadow.setImageResource(R.drawable.shadow_background);
            backgroundShadow.setAlpha(0.0f);
            backgroundShadow.bringToFront();
            ground.addView(backgroundShadow);
            base02.setAlpha(0.0f);
            base02.bringToFront();

            accountChangeMask = new ConstraintLayout(MainMenu.this);
            accountChangeMask.setId(View.generateViewId());
            relativeLayoutParams = new RelativeLayout.LayoutParams(
                    PDS.i_dp2px(PDS.getWidth() * 0.9349635036496f),
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            relativeLayoutParams.leftMargin = PDS.i_dp2px(PDS.getWidth() * 0.0325182481752f);
            relativeLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
            accountChangeMask.setLayoutParams(relativeLayoutParams);
            accountChangeMask.setBackground(ContextCompat.getDrawable(MainMenu.this, R.drawable.main_page_blue_board));
            accountChangeMask.setEnabled(false);
            base02.addView(accountChangeMask);

            int margin = PDS.i_dp2px(PDS.getWidth() * 0.0194647201946f);
            accountCardView = new CardView(MainMenu.this);
            accountCardView.setId(View.generateViewId());
            constraintLayoutParams = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
            );
            constraintLayoutParams.bottomMargin = margin;
            constraintLayoutParams.rightMargin = margin;
            accountCardView.setEnabled(false);
            accountCardView.setLayoutParams(constraintLayoutParams);
            accountCardView.setCardBackgroundColor(MainMenu.this.getResources().getColor(R.color.inputTextWhite));
            accountCardView.setRadius(PDS.f_dp2px(PDS.getWidth() * 0.0583941605839f));
            accountCardView.setElevation(PDS.f_dp2px(0));
            accountChangeMask.addView(accountCardView);

            accountInnerCardView = new TextInputLayout(MainMenu.this);
            accountInnerCardView.setId(View.generateViewId());
            constraintLayoutParams = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
            );
            accountInnerCardView.setLayoutParams(constraintLayoutParams);
            accountInnerCardView.setEnabled(false);
            accountInnerCardView.setHintEnabled(false);
            if (accountChangeRequestCode == 2)
                accountInnerCardView.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
            accountCardView.addView(accountInnerCardView);

            accountCardViewText = new EditText(MainMenu.this);
            accountCardViewText.setId(View.generateViewId());
            linearLayoutParams = new LinearLayout.LayoutParams(
                    PDS.i_dp2px(PDS.getWidth() * 0.886301703162982f),
                    PDS.i_dp2px(PDS.getHeight() * 0.056116722783389f)
            );
            linearLayoutParams.leftMargin = PDS.i_dp2px(PDS.getWidth() * 0.024330900243309f);
            linearLayoutParams.rightMargin = PDS.i_dp2px(PDS.getWidth() * 0.024330900243309f);
            accountCardViewText.setLayoutParams(linearLayoutParams);
            accountCardViewText.setCompoundDrawablePadding(PDS.i_dp2px(PDS.getWidth() * 0.02919708029197f));
            accountCardViewText.setGravity(Gravity.CENTER_VERTICAL);
            accountCardViewText.setBackgroundColor(Color.TRANSPARENT);
            if (accountChangeRequestCode == 1) {
                accountCardViewText.setHint("New Doctor ID");
            } else if (accountChangeRequestCode == 2) {
                accountCardViewText.setHint("New Password");
            } else {
                accountCardViewText.setHint("New Patient ID");
            }
            accountCardViewText.setInputType(InputType.TYPE_NULL);
            accountCardViewText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            accountCardViewText.setSingleLine(true);
            accountCardViewText.setHintTextColor(getResources().getColor(R.color.inputTextHintWhite));
            accountCardViewText.setEnabled(false);
            accountInnerCardView.addView(accountCardViewText);

            accountChangePadding01 = new RelativeLayout(MainMenu.this);
            accountChangePadding01.setId(View.generateViewId());
            constraintLayoutParams = new ConstraintLayout.LayoutParams(
                    PDS.i_dp2px(PDS.getWidth() * 0.9349635036496f),
                    PDS.i_dp2px(PDS.getHeight() * 0.0617283950617f)
            );
            constraintLayoutParams.topMargin = PDS.i_dp2px(PDS.getHeight() * 0.01122334455667789f);
            accountChangePadding01.setLayoutParams(constraintLayoutParams);
            accountChangeMask.addView(accountChangePadding01);

            ContextThemeWrapper newContext = new ContextThemeWrapper(MainMenu.this, R.style.Button1);
            changeConfirmButton = new Button(newContext, null, R.style.Button1);
            changeConfirmButton.setId(View.generateViewId());
            relativeLayoutParams = new RelativeLayout.LayoutParams(
                    PDS.i_dp2px(PDS.getWidth()  * 0.364963503649635f),
                    PDS.i_dp2px(PDS.getHeight() * 0.05611672278f)
            );
            relativeLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            changeConfirmButton.setLayoutParams(relativeLayoutParams);
            changeConfirmButton.setClickable(false);
            changeConfirmButton.setEnabled(false);
            changeConfirmButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, PDS.getWidth() * 0.039559912621876497777f);
            if (accountChangeRequestCode == 1 || accountChangeRequestCode == 2)
                changeConfirmButton.setText("CHANGE");
            else
                changeConfirmButton.setText("REGISTER");
            changeBackButton = new Button(MainMenu.this);
            changeBackButton.setId(View.generateViewId());
            relativeLayoutParams = new RelativeLayout.LayoutParams(
                    PDS.i_dp2px(PDS.getHeight() * 0.05611672278f),
                    PDS.i_dp2px(PDS.getHeight() * 0.05611672278f)
            );
            relativeLayoutParams.leftMargin = PDS.i_dp2px(PDS.getWidth() * 0.922798053527950088730551568f - PDS.getHeight() * 0.05611672278f);
            relativeLayoutParams.addRule(RelativeLayout.RIGHT_OF);
            changeBackButton.setAlpha(0.7f);
            changeBackButton.setLayoutParams(relativeLayoutParams);
            changeBackButton.setBackground(ContextCompat.getDrawable(MainMenu.this, R.drawable.ic_account_change_button_return));
            changeBackButton.setClickable(false);
            changeBackButton.setEnabled(false);
            accountChangePadding01.addView(changeConfirmButton);
            accountChangePadding01.addView(changeBackButton);


            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(accountChangeMask);
            constraintSet.connect(accountCardView.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, margin);
            constraintSet.connect(accountCardView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, margin);
            constraintSet.connect(accountChangePadding01.getId(), ConstraintSet.TOP, accountCardView.getId(), ConstraintSet.BOTTOM, PDS.i_dp2px(PDS.getHeight() * 0.01122334455667789f));
            constraintSet.connect(accountChangePadding01.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
            constraintSet.connect(accountChangePadding01.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
            constraintSet.applyTo(accountChangeMask);
            changeConfirmButton.setOnClickListener(changeApplied -> accountChangePage());
            changeBackButton.setOnClickListener(changeCancelled -> changeToAccountMainAnimation());

            accountMainToChangeAnimation();
        }
        private void accountChangePage() {
            String str = accountCardViewText.getText().toString();
            String hintObj;
            if (accountChangeRequestCode == 1) {
                hintObj = "Doctor ID";
            } else if (accountChangeRequestCode == 2) {
                hintObj = "Password";
            } else {
                hintObj = "Patient ID";
            }
            if (accountChangeRequestCode == 1 || accountChangeRequestCode == 3) {
                if (str.length() < 5 || str.length() > 18 || !checkUserIDValidity(str)) {
                    warningMsg("A valid user ID is:\n" +
                                    "1. 5-18 characters long\n"+
                                    "2. only allowed to contain upper/lower-case " +
                                    "English letters, 0-9 digits OR a special character: _",
                            "Invalid "+hintObj+"!");
                    return;
                }
            } else {
                if (str.length() < 5 || str.length() > 25 || !checkPasswordValidity(str)) {
                    warningMsg("A valid password is:\n" +
                                    "1. 5-25 characters long\n"+
                                    "2. only allowed to contain upper/lower-case " +
                                    "English letters, 0-9 digits OR special characters: .,_!#@",
                            "Invalid "+hintObj+"!");
                    return;
                }
            }
            if (accountChangeRequestCode == 1 && str.equals(currUserID)) {
                warningMsg("Please try another ID or keep the current one.",
                        "New doctor is same to the current one!");
                return;
            }
            if (accountChangeRequestCode == 2 && str.equals(currPassword)) {
                warningMsg("Please try another password or keep the current one.",
                        "New password is same to the current one!");
                return;
            }
            if (accountChangeRequestCode == 3 && patientMapping.containsKey(str)) {
                warningMsg("Looks like the new patient ID has already existed in your patient list.",
                        "The new patient has been found in your patient list!");
                return;
            }
            if ((accountChangeRequestCode == 1 && doctorData.containsKey(str)) ||
                    (accountChangeRequestCode == 3 && patientData.containsKey(str))) {
                String noun = (accountChangeRequestCode == 1) ? "doctor" : "patient";
                warningMsg("Looks like another " + noun + " has taken this " + hintObj + ".\nTry another one.",
                        "Duplicate " + hintObj + " is found!");
                return;
            }
            if (accountChangeRequestCode == 1 || accountChangeRequestCode == 2) {
                String[] data = doctorData.get(currUserID);
                if (accountChangeRequestCode == 1) {
                    doctorData.remove(currUserID);
                    doctorData.put(str, data);
                    currUserID = str;
                    showIDView.setText("Doctor ID:\n" + currUserID);
                } else {
                    data[0] = str;
                    doctorData.put(currUserID, data);
                    currPassword = str;
                }
                File file = new File(MainMenu.this.getFilesDir(), "data02.emory");
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
                                "Doctor data file cannot be created!");
                        System.exit(-1);
                    }
                }
                if (!file.canWrite() && !file.setWritable(true)) {
                    System.err.println("Doctor data file cannot be written!");
                    System.exit(-1);
                }
                try {
                    FileWriter writer = new FileWriter(file, false);
                    try {
                        for (Map.Entry<String, String[]> doctorDataPair : doctorData.entrySet()) {
                            writer.write(securityOriginal.encrypt(doctorDataPair.getKey()) + "\n");
                            for (String currData : doctorDataPair.getValue())
                                writer.write(securityOriginal.encrypt(currData) + "\n");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.exit(-1);
                    }
                    writer.flush();
                    writer.close();
                    builder.setMessage("Remember your new "+hintObj+":\n"+str);
                    builder.setTitle("Change " + hintObj +" Successful!");
                    builder.setPositiveButton("OK", (dialog, id)->changeToAccountMainAnimation());
                    builder.show();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(-1);
                }
            } else {
                String[] newPatientData = randomCreateUser("xxxxxxxx", "xxxxxxxx", "xxxxxxxx",
                        0,0,0,8,false);
                File file = new File(MainMenu.this.getFilesDir(), "data01.emory");
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
                                "Patient data file cannot be created!");
                        System.exit(-1);
                    }
                }
                if (!file.canWrite() && !file.setWritable(true)) {
                    System.err.println("Patient data file cannot be written!");
                    System.exit(-1);
                }
                try {
                    File file2 = null;
                    try {
                        file2 = new File(getFilesDir().toString() + "/emory_health/data/"
                                + security.encrypt(currUserHashCode) + "/patient_list.emory");
                        if (!file2.exists()) {
                            try {
                                boolean createSuccess = file2.createNewFile();
                                if (!createSuccess) {
                                    System.err.println("Create file failed");
                                    System.exit(-1);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                warningMsg("Please check if your disk is full or no permission allowed.",
                                        "Patient list file cannot be created!");
                                System.exit(-1);
                            }
                        }
                        if (!file2.canWrite() && !file2.setWritable(true)) {
                            System.err.println("Patient list file cannot be written!");
                            System.exit(-1);
                        }
                    } catch(Exception e) {
                        e.printStackTrace();
                        System.exit(-1);
                    }
                    FileWriter writer = new FileWriter(file, true);
                    FileWriter writer2 = new FileWriter(file2, true);
                    try {
                        writer.write(securityOriginal.encrypt(str) + "\n");
                        writer.write(securityOriginal.encrypt(newPatientData[1]) + "\n");
                        writer.write(securityOriginal.encrypt(newPatientData[2]) + "\n");
                        writer.write(securityOriginal.encrypt(currUserID) + "\n");
                        writer2.write(security.encrypt(str) + "\n");
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.exit(-1);
                    }
                    writer.flush();
                    writer.close();
                    writer2.flush();
                    writer2.close();
                    patientMapping.put(str, newPatientData[2]);
                    patientData.put(str, new String[]{newPatientData[1], newPatientData[2]});
                    builder.setMessage("Here is your new patient info:\n" +
                            "Patient ID: " + str + "\n" +
                            "Password:   " + newPatientData[1]);
                    builder.setTitle("Register New Patient Successful!");
                    builder.setPositiveButton("OK", (dialog, id)->changeToAccountMainAnimation());
                    builder.show();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(-1);
                }
            }
        }
        private boolean checkUserIDValidity(final String userID) {

            for (int i = 0; i < userID.length(); i++) {
                char c = userID.charAt(i);
                if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') ||
                        (c >= '0' && c <= '9') || c == '_'))
                    return false;
            }
            return true;
        }
        private boolean checkPasswordValidity(final String password) {
            for (int i = 0; i < password.length(); i++) {
                char c = password.charAt(i);
                if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') ||
                        (c >= '0' && c <= '9') || c == '_' || c == ',' || c == '.' || c == '!' ||
                        c == '#' || c == '@'))
                    return false;
            }
            return true;
        }
        private void patientListShow() {
            menuButton01.setEnabled(false);
            menuButton01.setClickable(false);
            menuButton02.setEnabled(false);
            menuButton02.setClickable(false);
            menuButton03.setEnabled(false);
            menuButton03.setClickable(false);
            changeUserIDButton.setClickable(false);
            changeUserIDButton.setEnabled(false);
            changePasswordButton.setClickable(false);
            changePasswordButton.setEnabled(false);
            registerButton.setClickable(false);
            registerButton.setEnabled(false);
            showListButton.setClickable(false);
            showListButton.setEnabled(false);

            backgroundShadow = new ImageView(MainMenu.this);
            relativeLayoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
            );
            backgroundShadow.setId(View.generateViewId());
            backgroundShadow.setLayoutParams(relativeLayoutParams);
            backgroundShadow.setImageResource(R.drawable.shadow_background);
            backgroundShadow.setAlpha(0.0f);
            backgroundShadow.bringToFront();
            ground.addView(backgroundShadow);
            base02.setAlpha(0.0f);
            base02.bringToFront();

            patientListBase = new ConstraintLayout(MainMenu.this);
            patientListBase.setId(View.generateViewId());
            relativeLayoutParams = new RelativeLayout.LayoutParams(
                    PDS.i_dp2px(PDS.getWidth() * 0.9349635036496f),
                    PDS.i_dp2px(PDS.getHeight() * 0.3928170594837f)
            );
            relativeLayoutParams.leftMargin = PDS.i_dp2px(PDS.getWidth() * 0.0325182481752f);
            relativeLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
            patientListBase.setLayoutParams(relativeLayoutParams);
            patientListBase.setBackground(ContextCompat.getDrawable(MainMenu.this, R.drawable.main_page_blue_board));
            base02.addView(patientListBase);

            patientListScroll = new ScrollView(MainMenu.this);
            patientListScroll.setId(View.generateViewId());
            constraintLayoutParams = new ConstraintLayout.LayoutParams(
                    PDS.i_dp2px(PDS.getWidth()  * 0.837639902676363990f),
                    PDS.i_dp2px(PDS.getHeight() * 0.280583613916947f)
            );
            patientListScroll.setLayoutParams(constraintLayoutParams);
            patientListScroll.setEnabled(false);
            patientListBase.addView(patientListScroll);

            patientListLayout = new LinearLayout(MainMenu.this);
            patientListLayout.setId(View.generateViewId());
            constraintLayoutParams = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
            );
            patientListLayout.setLayoutParams(constraintLayoutParams);
            patientListLayout.setOrientation(LinearLayout.VERTICAL);
            patientListScroll.addView(patientListLayout);

            for (Map.Entry<String, String> entries : patientMapping.entrySet()) {
                EditText editText = new EditText(MainMenu.this);
                editText.setId(View.generateViewId());
                linearLayoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                linearLayoutParams.bottomMargin = PDS.i_dp2px(PDS.getHeight() * 0.01122334455667789f);
                editText.setLayoutParams(linearLayoutParams);
                editText.setCompoundDrawablesWithIntrinsicBounds(
                        ContextCompat.getDrawable(MainMenu.this, R.drawable.ic_person_black), null, null, null);
                editText.setCompoundDrawablePadding(PDS.i_dp2px(PDS.getWidth() * 0.01216545012165f));
                editText.setTypeface(ResourcesCompat.getFont(MainMenu.this, R.font.font_burgela_italic), Typeface.NORMAL);
                editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, PDS.i_dp2px(PDS.getWidth() * 0.048661800f));
                editText.setTextColor(MainMenu.this.getResources().getColor(R.color.black));
                editText.setText(entries.getKey());
                editText.setPaintFlags(0);
                patientListLayout.addView(editText);
            }

            patientListButtonMask = new RelativeLayout(MainMenu.this);
            patientListButtonMask.setId(View.generateViewId());
            constraintLayoutParams = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
            );
            patientListButtonMask.setLayoutParams(constraintLayoutParams);
            patientListBase.addView(patientListButtonMask);

            ContextThemeWrapper newContext = new ContextThemeWrapper(MainMenu.this, R.style.Button1);
            patientListButton = new Button(newContext, null, R.style.Button1);
            patientListButton.setId(View.generateViewId());
            relativeLayoutParams = new RelativeLayout.LayoutParams(
                    PDS.i_dp2px(PDS.getWidth()  * 0.364963503649635f),
                    PDS.i_dp2px(PDS.getHeight() * 0.05611672278f)
            );
            relativeLayoutParams.leftMargin = PDS.i_dp2px(PDS.getWidth() * 0.2849999999999825f);
            patientListButton.setLayoutParams(relativeLayoutParams);
            patientListButton.setEnabled(false);
            patientListButton.setClickable(false);
            patientListButton.setText("OK");
            patientListButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, PDS.i_dp2px(PDS.getHeight() * 0.02966993446640737333275f));
            patientListButtonMask.addView(patientListButton);

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(patientListBase);
            constraintSet.connect(patientListScroll.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, PDS.i_dp2px(PDS.getWidth() * 0.048661800486618f));
            constraintSet.connect(patientListScroll.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, PDS.i_dp2px(PDS.getWidth() * 0.048661800486618f));
            constraintSet.connect(patientListButtonMask.getId(), ConstraintSet.TOP, patientListScroll.getId(), ConstraintSet.BOTTOM, PDS.i_dp2px(PDS.getWidth()  * 0.03649635036496f));
            constraintSet.connect(patientListButtonMask.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
            constraintSet.applyTo(patientListBase);
            patientListButton.setOnClickListener(patientListReturn -> listToAccountMainAnimation());


            accountMainToListAnimation();
        }





        private void initTutorialPage() {
            System.out.println("Enter initializing tutorial page");
        }

        private void destroyCurrPage() {
            menuButton01.setClickable(false);
            menuButton02.setClickable(false);
            menuButton03.setClickable(false);
            menuButton01.setEnabled(false);
            menuButton02.setEnabled(false);
            menuButton03.setEnabled(false);

            if (currPageNum == 1) {
                audio_uploadButton.setClickable(false);
                audio_uploadButton.setEnabled(false);
                audio_scrollView.setEnabled(false);
                audio_recordList.setEnabled(false);
                setStatusAudioRecords(false);
                audio_uploadButton.setText(R.string.text_MainMenu_upload);
                audio_uploadButton.setTextColor(MainMenu.this.getResources().getColor(R.color.white));
                if (audio_uploadButton.getParent() == base)
                    base.removeView(audio_uploadButton);
                if (audio_scrollView.getParent() == base)
                    base.removeView(audio_scrollView);
            } else if (currPageNum == 2) {
                base.removeView(account_scrollView);
                base.removeView(showIDLayout);
            }
        }



        private void logInTransitAnimation() {
            LinkedList<Animator> animators = new LinkedList<>();
            ObjectAnimator animator;
            animator = ObjectAnimator.ofFloat(emoryLogo01, View.ALPHA, 1.0f, 0.5f);
            animators.add(animator);
            animator = ObjectAnimator.ofFloat(emoryLogo02, View.ALPHA, 0.5f, 0.15f);
            animators.add(animator);
            animator = ObjectAnimator.ofFloat(menuMask, View.ALPHA, 0.0f, 1.0f);
            animators.add(animator);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(500);
            animatorSet.playTogether(animators);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    emoryLogo01.setAlpha(0.5f);
                    emoryLogo02.setAlpha(0.15f);
                    menuMask.setAlpha(1.0f);
                    menuButton01.setClickable(true);
                    menuButton02.setClickable(true);
                    menuButton03.setClickable(true);
                    menuButton01.setEnabled(true);
                    menuButton02.setEnabled(true);
                    menuButton03.setEnabled(true);
                    audio_uploadButton.setClickable(true);
                    audio_uploadButton.setEnabled(true);
                    audio_scrollView.setEnabled(true);
                    audio_recordList.setEnabled(true);
                }
            });
            animatorSet.start();
        }
        private void audioMainToMediaAnimation() {
            LinkedList<Animator> animators = new LinkedList<>();
            ObjectAnimator animator;
            animator = ObjectAnimator.ofFloat(base02, View.ALPHA, 0.0f, 1.0f);
            animators.add(animator);
            animator = ObjectAnimator.ofFloat(backgroundShadow, View.ALPHA, 0.0f, 1.0f);
            animators.add(animator);
            animatorSet = new AnimatorSet();
            animatorSet.setDuration(300);
            animatorSet.playTogether(animators);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    base02.setAlpha(1.0f);
                    backgroundShadow.setAlpha(1.0f);
                    playButton.setClickable(true);
                    playButton.setEnabled(true);
                    descriptButton.setClickable(true);
                    descriptButton.setEnabled(true);
                    deleteButton.setClickable(true);
                    deleteButton.setEnabled(true);
                    returnButton.setClickable(true);
                    returnButton.setEnabled(true);
                    seekBar.setEnabled(true);
                    audioBeginEndMask.setEnabled(true);
                    audioBegin.setEnabled(true);
                    audioEnd.setEnabled(true);
                }
            });
            animatorSet.start();
        }
        private void mediaToAudioMainAnimation() {
            playButton.setClickable(false);
            playButton.setEnabled(false);
            descriptButton.setClickable(false);
            descriptButton.setEnabled(false);
            deleteButton.setClickable(false);
            deleteButton.setEnabled(false);
            returnButton.setClickable(false);
            returnButton.setEnabled(false);
            seekBar.setEnabled(false);
            LinkedList<Animator> animators = new LinkedList<>();
            ObjectAnimator animator;
            animator = ObjectAnimator.ofFloat(base02, View.ALPHA, 1.0f, 0.0f);
            animators.add(animator);
            animator = ObjectAnimator.ofFloat(backgroundShadow, View.ALPHA, 1.0f, 0.0f);
            animators.add(animator);
            animatorSet = new AnimatorSet();
            animatorSet.setDuration(300);
            animatorSet.playTogether(animators);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    base02.setAlpha(0.0f);
                    backgroundShadow.setAlpha(0.0f);
                    if (playButton != null && playButton.getParent() == audioBoard) {
                        audioBoard.removeView(playButton);
                        playButton = null;
                    }
                    if (descriptButton != null && descriptButton.getParent() == audioBoard) {
                        audioBoard.removeView(descriptButton);
                        descriptButton = null;
                    }
                    if (deleteButton != null && deleteButton.getParent() == audioBoard) {
                        audioBoard.removeView(deleteButton);
                        deleteButton = null;
                    }
                    if (returnButton != null && returnButton.getParent() == audioBoard) {
                        audioBoard.removeView(returnButton);
                        returnButton = null;
                    }
                    if (seekBar != null && seekBar.getParent() == audioBoard) {
                        audioBoard.removeView(seekBar);
                        seekBar = null;
                    }
                    if (audioBegin != null && audioBegin.getParent() == audioBeginEndMask) {
                        audioBeginEndMask.removeView(audioBegin);
                        audioBegin = null;
                    }
                    if (audioEnd != null && audioEnd.getParent() == audioBeginEndMask) {
                        audioBeginEndMask.removeView(audioEnd);
                        audioEnd = null;
                    }
                    if (audioBeginEndMask != null && audioBeginEndMask.getParent() == audioBoard) {
                        audioBoard.removeView(audioBeginEndMask);
                        audioBeginEndMask = null;
                    }
                    if (audioBoardTitle != null && audioBoardTitle.getParent() == audioBoard) {
                        audioBoard.removeView(audioBoardTitle);
                        audioBoardTitle = null;
                    }
                    if (audioBoard != null && audioBoard.getParent() == base02) {
                        base02.removeView(audioBoard);
                        audioBoard = null;
                    }
                    if (backgroundShadow != null && backgroundShadow.getParent() == ground) {
                        ground.removeView(backgroundShadow);
                        backgroundShadow = null;
                    }
                    menuButton01.setClickable(true);
                    menuButton01.setEnabled(true);
                    menuButton02.setClickable(true);
                    menuButton02.setEnabled(true);
                    menuButton03.setClickable(true);
                    menuButton03.setEnabled(true);
                    audio_uploadButton.setClickable(true);
                    audio_uploadButton.setEnabled(true);
                    setStatusAudioRecords(true);
                }
            });
            animatorSet.start();
        }
        private void accountMainToChangeAnimation() {
            LinkedList<Animator> animators = new LinkedList<>();
            ObjectAnimator animator;
            animator = ObjectAnimator.ofFloat(base02, View.ALPHA, 0.0f, 1.0f);
            animators.add(animator);
            animator = ObjectAnimator.ofFloat(backgroundShadow, View.ALPHA, 0.0f, 1.0f);
            animators.add(animator);
            animatorSet = new AnimatorSet();
            animatorSet.setDuration(300);
            animatorSet.playTogether(animators);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    backgroundShadow.setAlpha(1.0f);
                    base02.setAlpha(1.0f);
                    accountChangeMask.setEnabled(true);
                    accountCardView.setEnabled(true);
                    accountInnerCardView.setEnabled(true);
                    if (accountChangeRequestCode == 1 || accountChangeRequestCode == 3)
                        accountCardViewText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    else
                        accountCardViewText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    accountCardViewText.setEnabled(true);
                    changeConfirmButton.setClickable(true);
                    changeConfirmButton.setEnabled(true);
                    changeBackButton.setClickable(true);
                    changeBackButton.setEnabled(true);
                }
            });
            animatorSet.start();
        }
        private void changeToAccountMainAnimation() {
            accountChangeMask.setEnabled(false);
            accountCardView.setEnabled(false);
            accountInnerCardView.setEnabled(false);
            accountCardViewText.setEnabled(false);
            accountCardViewText.setInputType(InputType.TYPE_NULL);
            changeConfirmButton.setClickable(false);
            changeConfirmButton.setEnabled(false);
            changeBackButton.setClickable(false);
            changeBackButton.setEnabled(false);
            LinkedList<Animator> animators = new LinkedList<>();
            ObjectAnimator animator;
            animator = ObjectAnimator.ofFloat(base02, View.ALPHA, 1.0f, 0.0f);
            animators.add(animator);
            animator = ObjectAnimator.ofFloat(backgroundShadow, View.ALPHA, 1.0f, 0.0f);
            animators.add(animator);
            animatorSet = new AnimatorSet();
            animatorSet.setDuration(300);
            animatorSet.playTogether(animators);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    backgroundShadow.setAlpha(0.0f);
                    base02.setAlpha(0.0f);
                    if (backgroundShadow.getParent() == ground) ground.removeView(backgroundShadow);
                    base02.removeAllViews();
                    accountChangeMask.removeAllViews();
                    accountCardView.removeAllViews();
                    accountInnerCardView.removeAllViews();
                    accountChangePadding01.removeAllViews();
                    menuButton01.setEnabled(true);
                    menuButton01.setClickable(true);
                    menuButton02.setEnabled(true);
                    menuButton02.setClickable(true);
                    menuButton03.setEnabled(true);
                    menuButton03.setClickable(true);
                    changeUserIDButton.setClickable(true);
                    changeUserIDButton.setEnabled(true);
                    changePasswordButton.setClickable(true);
                    changePasswordButton.setEnabled(true);
                    registerButton.setClickable(true);
                    registerButton.setEnabled(true);
                    showListButton.setClickable(true);
                    showListButton.setEnabled(true);
                }
            });
            animatorSet.start();
        }

        private void accountMainToListAnimation() {
            LinkedList<Animator> animators = new LinkedList<>();
            ObjectAnimator animator;
            animator = ObjectAnimator.ofFloat(base02, View.ALPHA, 0.0f, 1.0f);
            animators.add(animator);
            animator = ObjectAnimator.ofFloat(backgroundShadow, View.ALPHA, 0.0f, 1.0f);
            animators.add(animator);
            animatorSet = new AnimatorSet();
            animatorSet.setDuration(300);
            animatorSet.playTogether(animators);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    backgroundShadow.setAlpha(1.0f);
                    base02.setAlpha(1.0f);
                    patientListScroll.setEnabled(true);
                    patientListButton.setClickable(true);
                    patientListButton.setEnabled(true);
                }
            });
            animatorSet.start();
        }
        private void listToAccountMainAnimation() {
            patientListScroll.setEnabled(false);
            patientListButton.setClickable(false);
            patientListButton.setEnabled(false);
            LinkedList<Animator> animators = new LinkedList<>();
            ObjectAnimator animator;
            animator = ObjectAnimator.ofFloat(base02, View.ALPHA, 1.0f, 0.0f);
            animators.add(animator);
            animator = ObjectAnimator.ofFloat(backgroundShadow, View.ALPHA, 1.0f, 0.0f);
            animators.add(animator);
            animatorSet = new AnimatorSet();
            animatorSet.setDuration(300);
            animatorSet.playTogether(animators);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    backgroundShadow.setAlpha(0.0f);
                    base02.setAlpha(0.0f);
                    base02.removeAllViews();
                    ground.removeView(backgroundShadow);
                    patientListLayout.removeAllViews();
                    menuButton01.setEnabled(true);
                    menuButton01.setClickable(true);
                    menuButton02.setEnabled(true);
                    menuButton02.setClickable(true);
                    menuButton03.setEnabled(true);
                    menuButton03.setClickable(true);
                    changeUserIDButton.setClickable(true);
                    changeUserIDButton.setEnabled(true);
                    changePasswordButton.setClickable(true);
                    changePasswordButton.setEnabled(true);
                    registerButton.setClickable(true);
                    registerButton.setEnabled(true);
                    showListButton.setClickable(true);
                    showListButton.setEnabled(true);
                }
            });
            animatorSet.start();
        }
    }










    private class PatientPage {

        private void patientPage() {

        }
    }
}