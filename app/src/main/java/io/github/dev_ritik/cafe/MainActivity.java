package io.github.dev_ritik.cafe;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.budiyev.android.codescanner.CodeScanner;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.google.zxing.WriterException;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

//import com.budiyev.android.codescanner.CodeScanner;

public class MainActivity extends AppCompatActivity {

    public static int ACCOUNT_ACTIVITY_REQUEST_CODE = 1;
    ProfileTracker profileTracker;
    ImageView accountButton;
    private CodeScanner mCodeScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        accountButton = (ImageView) findViewById(R.id.account_button);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 50);
        else {

            // set click listener on account button
            accountButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, AccountActivity.class);
                    startActivityForResult(intent, ACCOUNT_ACTIVITY_REQUEST_CODE);
                }
            });

            // register a receiver for the onCurrentProfileChanged event
            profileTracker = new ProfileTracker() {
                @Override
                protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                    if (currentProfile != null) {
                        displayProfilePic(currentProfile);
                    }
                }
            };

            // show profile pic on account button
            Profile currentProfile = Profile.getCurrentProfile();
            if (currentProfile != null) {
                displayProfilePic(currentProfile);
            } else {
                // Fetch the profile, which will trigger the onCurrentProfileChanged receiver
                Profile.fetchProfileForCurrentAccessToken();
            }


//            CodeScannerView scannerView = findViewById(R.id.scanner_view);
//            mCodeScanner = new CodeScanner(this, scannerView);
//            mCodeScanner.setDecodeCallback(new DecodeCallback() {
//                @Override
//                public void
//                onDecoded(@NonNull final Result result) {
//                    MainActivity.this.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(MainActivity.this, result.getText(), Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//            });
//            scannerView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    mCodeScanner.startPreview();
//                }
//            });
//        }


            ImageView qrImage = findViewById(R.id.qrImage);
            QRGEncoder qrgEncoder = new QRGEncoder("ritik", null, QRGContents.Type.TEXT, 450);
            try {
                // Getting QR-Code as Bitmap
                // Setting Bitmap to ImageView
                qrImage.setImageBitmap(qrgEncoder.encodeAsBitmap());
            } catch (WriterException e) {
                Log.i("point 115", e.toString());
            }
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode,
                                    final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACCOUNT_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            // if the user logged out in AccountActivity, show the login screen
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // unregister the profile tracker receiver
        profileTracker.stopTracking();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
//        mCodeScanner.releaseResources();
        super.onPause();
    }

    private void displayProfilePic(Profile profile) {
        // helper method to load the profile pic in a circular imageview
        Uri uri = profile.getProfilePictureUri(28, 28);
        Transformation transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(30)
                .oval(false)
                .build();
        Picasso.get()
                .load(uri)
                .placeholder(R.drawable.icon_profile_empty)
                .transform(transformation)
                .into(accountButton);
    }


}

