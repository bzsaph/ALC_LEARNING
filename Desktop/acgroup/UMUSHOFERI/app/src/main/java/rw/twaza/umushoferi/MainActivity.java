package rw.twaza.umushoferi;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import dmax.dialog.SpotsDialog;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;
import rw.twaza.umushoferi.Common.Common;
import rw.twaza.umushoferi.mode.User;

public class MainActivity extends AppCompatActivity {

    //    button and text we will use in loin and submit
    private Button Signin;
    private Button Register;
    private Typeface tf1,tf2;
    private TextView heading;
    private  TextView twazaheading;
    private TextView txt_forgate_password;
    //     end of decalarig sty

//    decallaring fire base requers

    private FirebaseDatabase db;
    private FirebaseAuth Auth;
    private DatabaseReference users;
    //    end of decalaring firebase
    RelativeLayout rootLayout;




    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Signin = (Button) findViewById(R.id.btnSignIn);
//        Register = (Button) findViewById(R.id.btnRegister);

        txt_forgate_password=(TextView)findViewById(R.id.txt_forgoten_password);
        rootLayout = (RelativeLayout) findViewById(R.id.rootlayout);
        txt_forgate_password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                showDialogForgotpwd();
                return false;
            }
        });


//        calling styling of words
        tf2 = Typeface.createFromAsset(getAssets(), "101!kimmy'skowboyhat.ttf");
        tf1 = Typeface.createFromAsset(getAssets(), "Tyrannothesaurus.otf");

//        Signin.setTypeface(tf1);
//        Register.setTypeface(tf1);

//        end of calling styling phse

        Auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();

//        table ya user in fire base

        users = db.getReference(Common.user_driver_tbl);



        Signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowLoginDialog();
            }
        });
    }

    private void showDialogForgotpwd() {
        final AlertDialog.Builder alertdialog = new AlertDialog.Builder(MainActivity.this);
        alertdialog.setTitle("RESET you PASSWORD");
        alertdialog.setMessage("please enter your email address");
        LayoutInflater inflater = LayoutInflater.from(this);

        View sigin = inflater.inflate(R.layout.layout_forget_pwd,null);
        final MaterialEditText email = sigin.findViewById(R.id.txtlEmaila);
        alertdialog.setView(sigin);
        alertdialog.setPositiveButton("REST", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {
                final SpotsDialog waitingDialog = new SpotsDialog(MainActivity.this);
                waitingDialog.show();
                Auth.sendPasswordResetEmail(email.getText().toString().trim())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                dialogInterface.dismiss();
                                waitingDialog.dismiss();
                                Toast.makeText(MainActivity.this, "mwareba muri email yanyu mugahindura password", Toast.LENGTH_SHORT).show();;
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialogInterface.dismiss();
                        waitingDialog.dismiss();
                        Snackbar.make(rootLayout,"IYI EMAIL NTIBA MURI DATABASE YACU"+e.getMessage(),Snackbar.LENGTH_LONG)
                                .show();
                    }
                });

            }

        });

        alertdialog.setNegativeButton("CANCEl", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertdialog.show();

    }


    public void ShowLoginDialog() {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("SIGN IN");

        LayoutInflater inflater = LayoutInflater.from(this);

        View sigin = inflater.inflate(R.layout.layout_login,null);
        final EditText email = sigin.findViewById(R.id.txtlEmaila);
        final EditText password = sigin.findViewById(R.id.txtlPassword1);

        dialog.setView(sigin);
        dialog.setPositiveButton("SIGN IN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                if (TextUtils.isEmpty((email.getText().toString()))) {
                    Toast.makeText(MainActivity.this, "please enter your email", Toast.LENGTH_SHORT).show();

                    return;
                }
                if (TextUtils.isEmpty((password.getText().toString()))) {
                    Toast.makeText(MainActivity.this, "please enter your password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.getText().toString().length() < 3) {
                    Toast.makeText(MainActivity.this, "please enter your password more than six number", Toast.LENGTH_SHORT).show();
                    return;
                }

               final SpotsDialog waitingDialog = new SpotsDialog(MainActivity.this);
              waitingDialog.show();
                String ema=email.getText().toString().trim();
                String pas =password.getText().toString().trim();

            new LoginUser().execute(ema,pas);

            }
        });
        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        dialog.show();


    }



    public class LoginUser extends AsyncTask<String ,Void, String>{
        @Override
        protected String doInBackground(String... strings) {

            final String email = strings[0];
            final String passord= strings[1];
            final String emai=email+"@tapandgo.com";



            OkHttpClient okHttpClient = new OkHttpClient();
            RequestBody formBody = new FormBody.Builder()
                 .add("phone",email)
                    .add("password",passord)
                    .build();

            Request request = new Request.Builder()
                    .url(Common.LoGINURL)
                    .post(formBody)
                    .build();
            Response response = null;

            try {
                response = okHttpClient.newCall(request).execute();
                if (response.isSuccessful() ){

                       String password=emai;

                        Auth.signInWithEmailAndPassword(emai, password)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {

                                        FirebaseDatabase.getInstance().getReference(Common.user_driver_tbl)
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        Common.currentUser = dataSnapshot.getValue(User.class);
                                                        startActivity(new Intent(MainActivity.this, HomeDriver.class));
                                                        finish();
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                //                    sign up to fire base

                                final SpotsDialog waitingDialog = new SpotsDialog(MainActivity.this);
                                waitingDialog.show();
                                final String password=emai;
                                final String name=email;

                                Auth.createUserWithEmailAndPassword(emai,password)
                                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                            @Override
                                            public void onSuccess(AuthResult authResult) {
                                                User user = new User();
                                                user.setEmail(emai);
                                                user.setPassword(password);
                                                user.setFname(name);
                                                user.setLname(name);




                                                users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                        .setValue(user)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                waitingDialog.dismiss();
                                                                Intent main = new Intent(MainActivity.this, HomeDriver.class);
                                                                main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                startActivity(main);
                                                                finish();
                                                                Toast.makeText(MainActivity.this,"Regisataration successful",Toast.LENGTH_SHORT)
                                                                        .show();


                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                waitingDialog.dismiss();
                                                                Toast.makeText(MainActivity.this,"FaiLed",Toast.LENGTH_SHORT)
                                                                        .show();


                                                            }
                                                        });


                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e)

                                            {
                                                waitingDialog.dismiss();
                                                Toast.makeText(MainActivity.this,"MWABAZA UMUKOZI WA MARA ",Toast.LENGTH_SHORT)
                                                        .show();

                                            }
                                        });

                            }








//                    end of sign up


                        });

                    }



            }catch (Exception e){
                e.printStackTrace();
            }



            return null;
        }
    }

    {

    }
}