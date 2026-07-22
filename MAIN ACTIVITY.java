package com.example.aahar100;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
public class MainActivity extends AppCompatActivity {
    private CardView cardDonate, cardReceive, cardFoodMap, cardMyPin, cardHistory, profile, menu_setting, menu_logout;
    private FirebaseAuth authProfile;
    private ProgressBar progressBar;
    private FirebaseUser firebaseUser;
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cardDonate = findViewById(R.id.cardDonate);
        cardReceive = findViewById(R.id.cardReceive);
        cardFoodMap = findViewById(R.id.cardFoodMap);
        cardMyPin = findViewById(R.id.cardMyPin);
        cardHistory = findViewById(R.id.cardHistory);
        profile = findViewById(R.id.profile);
        menu_setting = findViewById(R.id.menu_setting);
        menu_logout = findViewById(R.id.menu_logout);
        progressBar = findViewById(R.id.progressBar);
        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();
        if (firebaseUser == null) {
            Toast.makeText(MainActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        } else {
            checkIfEmailVerified(firebaseUser);
        }
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
        menu_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, setting_activity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
        menu_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authProfile.signOut();
                Intent intent = new Intent(MainActivity.this, landing_page.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | 
Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        cardReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                checkingPinExistOrNotForReceive();
            }
        });
        cardDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                checkingPinExistOrNotForDonate();
            }
        });
        cardFoodMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, FoodMap.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
        cardHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, History.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
        cardMyPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("DonateIdMapping");
                usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            progressBar.setVisibility(View.GONE);
                            String a = dataSnapshot.child("donationId").getValue(String.class);
                            showAlertDialogToRemoveCurrentPin(a);
                        } else {
                            progressBar.setVisibility(View.GONE);
                            showAlertDialogPinDoesNotExist();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, "Error checking user existence", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
    private void checkingPinExistOrNotForDonate() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("DonateIdMapping");
        usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    progressBar.setVisibility(View.GONE);
                    showAlertDialogThree();
                } else {
                    progressBar.setVisibility(View.GONE);
                    Intent intent = new Intent(MainActivity.this, Donate.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Error checking user existence", Toast.LENGTH_LONG).show();
            }
        });
    }
    private void checkingPinExistOrNotForReceive() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("DonateIdMapping");
        usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    progressBar.setVisibility(View.GONE);
                    showAlertDialogThree();
                } else {
                    progressBar.setVisibility(View.GONE);
                    Intent intent = new Intent(MainActivity.this, Receive.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Error checking user existence", Toast.LENGTH_LONG).show();
            }
        });
    }
    private void checkIfEmailVerified(FirebaseUser firebaseUser) {
        if (!firebaseUser.isEmailVerified()) {
            showAlertDialog();
        }
    }
    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Email Not Verified");
        builder.setMessage("Please verify your email now. If you have already verified your email close the app and Restart 
the application again");
        builder.setCancelable(false);
        builder.setPositiveButton("Get Verified", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                
alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.light_blue));
                alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.button_bg1);
            }
        });
        alertDialog.show();
    }
    private void showAlertDialogToRemoveCurrentPin(String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Remove Current Pin");
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference databaseReferenceTwo = FirebaseDatabase.getInstance().getReference("DonateIdMapping");
                DatabaseReference databaseReferenceOne = FirebaseDatabase.getInstance().getReference("FoodMap");
                databaseReferenceTwo.child(firebaseUser.getUid()).removeValue().addOnSuccessListener(new 
OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: User data2 Deleted");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.getMessage());
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                databaseReferenceOne.child(id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: User data1 Deleted");
                        Intent intent = new Intent(MainActivity.this, Donate.class);
                        startActivity(intent);
                    }
                });
            }
        });
        builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressBar.setVisibility(View.GONE);
            }
        });
        builder.setMessage("Are you sure you want to remove your current Pin? \nYou can donate at a new location after 
removing your current Pin");
        builder.setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void showAlertDialogPinDoesNotExist() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Pin Not Exist");
        builder.setMessage("You do not have any pin currently. Please add your location to donate food.");
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressBar.setVisibility(View.GONE);
            }
        });
        builder.show();
    }
    private void showAlertDialogThree() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Pin Exists");
        builder.setMessage("You already have a pin assigned. If you want to donate at a new location, please remove your 
current pin first.");
        builder.setCancelable(false);
        builder.setPositiveButton("Remove Pin", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressBar.setVisibility(View.VISIBLE);
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("DonateIdMapping");
                usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            progressBar.setVisibility(View.GONE);
                            String a = dataSnapshot.child("donationId").getValue(String.class);
                            showAlertDialogToRemoveCurrentPin(a);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressBar.setVisibility(View.GONE);
            }
        });
        builder.show()
