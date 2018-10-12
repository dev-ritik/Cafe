package io.github.dev_ritik.cafe;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.login.LoginManager;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.Locale;

public class AccountActivity extends AppCompatActivity {
    public static ChildEventListener mChildEventListener;//to listen the changes in db
    ProfileTracker profileTracker;
    ImageView profilePic;
    TextView id;
    TextView infoLabel;
    TextView info;
    CallbackManager callbackManager;
    DatabaseReference mDatabaseReference;
    private ArrayList<Transaction> transactions = new ArrayList<>();
    private TransactionsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        Log.i("point aa6", "on create");

        Intent intent = getIntent();
        String userId = intent.getExtras().getString("id");
        if (userId == null) {
            Toast.makeText(this, "some error occurred", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
        }
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("transactions").child(userId);

        profilePic = findViewById(R.id.profile_image);
        id = findViewById(R.id.id);
        infoLabel = findViewById(R.id.info_label);
        info = findViewById(R.id.info);

        fetchDataFromFirebase();

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        mAdapter = new TransactionsAdapter(transactions);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));


        // register a receiver for the onCurrentProfileChanged event
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                if (currentProfile != null) {
                    displayProfileInfo(currentProfile);
                }
            }
        };

        if (AccessToken.getCurrentAccessToken() != null) {
            // If there is an access token then Login Button was used
            // Check if the profile has already been fetched
            Profile currentProfile = Profile.getCurrentProfile();
            if (currentProfile != null) {
                displayProfileInfo(currentProfile);
            } else {
                // Fetch the profile, which will trigger the onCurrentProfileChanged receiver
                Profile.fetchProfileForCurrentAccessToken();
            }
        } else {
            // Otherwise, get Account Kit login information
            AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                @Override
                public void onSuccess(final Account account) {
                    // get Account Kit ID
                    String accountKitId = account.getId();
                    id.setText(accountKitId);

                    PhoneNumber phoneNumber = account.getPhoneNumber();
                    if (account.getPhoneNumber() != null) {
                        // if the phone number is available, display it
                        String formattedPhoneNumber = formatPhoneNumber(phoneNumber.toString());
                        info.setText(formattedPhoneNumber);
                        infoLabel.setText(R.string.phone_label);
                    } else {
                        // if the email address is available, display it
                        String emailString = account.getEmail();
                        info.setText(emailString);
                        infoLabel.setText(R.string.email_label);
                    }

                }

                @Override
                public void onError(final AccountKitError error) {
                    String toastMessage = error.getErrorType().getMessage();
                    Log.i("point A96", toastMessage);
                }
            });
        }
    }

    private void fetchDataFromFirebase() {
        if (mChildEventListener == null) {
            Log.i("point 142", "here");
            mChildEventListener = new ChildEventListener() {//working with db after authentication
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                    Log.i("point 144", "onchildadded");
//                    Log.i(dataSnapshot.getKey(), "standpoint 1");

                    Transaction transaction = dataSnapshot.getValue(Transaction.class);
                    Log.i("point 148", dataSnapshot.getKey() + "");
                    transactions.add(transaction);

                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                    // changed content of a child
                    Log.i("point 159", "onchild changed");
                    Transaction transaction = dataSnapshot.getValue(Transaction.class);
                    if (transaction != null)
                        for (int i = 0; i < transactions.size(); i++) {
                            Transaction t = transactions.get(i);
                            if (t.getCheckInTime().equals(transaction.getCheckInTime()) && t.getCheckOutTime() == null) {
                                t.setCheckOutTime(transaction.getCheckOutTime());
                                transactions.set(i, t);
                                mAdapter.notifyDataSetChanged();
                            }
                        }

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    // child deleted
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
                    //moved position of a child
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // error or permission denied
                }
            };
            mDatabaseReference.addChildEventListener(mChildEventListener);
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // unregister the profile tracker receiver
        profileTracker.stopTracking();
    }

    public void onLogout() {
        // logout of Account Kit
        detachDatabaseReadListener();

        transactions.clear();
//        mPostAdapter.clear();//clear adapter so that it doesn't holds any earlier data
        mAdapter.notifyItemRangeRemoved(0, mAdapter.getItemCount());

        AccountKit.logOut();
        // logout of Login Button
        LoginManager.getInstance().logOut();

        launchLoginActivity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.logout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.logout) {
            onLogout();
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    private void displayProfileInfo(Profile profile) {

        callbackManager = CallbackManager.Factory.create();

        // get Profile ID
        String profileId = profile.getId();
        id.setText(profileId);

        // display the Profile name
        String name = profile.getName();
        info.setText(name);
        infoLabel.setText(R.string.name_label);

        // display the profile picture
        Uri profilePicUri = profile.getProfilePictureUri(100, 100);
        displayProfilePic(profilePicUri);
    }

    private void launchLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private String formatPhoneNumber(String phoneNumber) {
        // helper method to format the phone number for display
        try {
            PhoneNumberUtil pnu = PhoneNumberUtil.getInstance();
            Phonenumber.PhoneNumber pn = pnu.parse(phoneNumber, Locale.getDefault().getCountry());
            phoneNumber = pnu.format(pn, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
        } catch (NumberParseException e) {
            e.printStackTrace();
        }
        return phoneNumber;
    }

    private void detachDatabaseReadListener() {
        if (mChildEventListener != null)
            mDatabaseReference.removeEventListener(mChildEventListener);
        mChildEventListener = null;
    }

    private void displayProfilePic(Uri uri) {
        // helper method to load the profile pic in a circular imageview
        Transformation transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(30)
                .oval(false)
                .build();
        Picasso.get()
                .load(uri)
                .transform(transformation)
                .into(profilePic);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("point aa64", "onstart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("point aa70", "onresume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("point aa76", "onpause");
        detachDatabaseReadListener();
        transactions.clear();
//        mPostAdapter.clear();//clear all data stored in adapter
        mAdapter.notifyItemRangeRemoved(0, mAdapter.getItemCount());
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.i("point aa285", "onstop ");

    }
}
