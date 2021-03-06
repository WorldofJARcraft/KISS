package net.ddns.worldofjarcraft.kappa;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;

public class LaunchActivity extends AppCompatActivity {
    /**
     * Name der Datei, in der die Logindaten gespeichert werden.
     */
    public static final String login_name="LOGIN";
    /**
     * Unter diesem Namen wird der Benutzername gespeichert.
     */
    public static final String user_preference = "mail";
    /**
     * Unter diesem Namen wird das Passwort gespeichert.
     */
    public static final String user_password = "pw";
    PendingIntent pendingIntent;
    BroadcastReceiver br;
    AlarmManager am;


    private void checkUserResponse(String output){
        if(output.equals("true")){
            System.out.println("Daten korrekt!");
        }
        else if(output.isEmpty()){
            AlertDialog.Builder builder = new AlertDialog.Builder(LaunchActivity.this);
            builder.setTitle(R.string.ErrorUnerreichbar);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //do nothing
                }
            });
            builder.show();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(LaunchActivity.this);
            builder.setTitle(R.string.unauth_titel);
            builder.setMessage(R.string.unauth);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    setTheme(R.style.AppTheme);
                    startActivity(new Intent(LaunchActivity.this, LoginActivity.class));
                }
            });
            builder.show();
        }
    }
    Intent mServiceIntent;
    @Override
    protected void onStart(){
        super.onStart();
        mServiceIntent = new Intent(getApplicationContext(), MHDCheckerService.class);
        mServiceIntent.setData(Uri.EMPTY);
        SharedPreferences login = getSharedPreferences(login_name,MODE_PRIVATE);
        if((login.contains(user_preference)&&login.contains(user_password))||(data.mail!=null&&data.pw!=null)){
            System.out.println("Melde mich mit gespeiucherten Daten an!");
            data.mail=login.getString(user_preference,null);
            data.pw=login.getString(user_password,null);
            //prüfen, ob Daten gültig sind
            HTTP_Connection conn = new HTTP_Connection(Constants.Server_Adress+"/user/"+data.mail,2);
            conn.delegate = new AsyncResponse() {
                @Override
                public void processFinish(String output, String url) {
                    checkUserResponse(output);
                }
            };
            conn.execute("params");
        }

        else{
            System.out.println("Frage Nutzerdaten an!");
            setTheme(R.style.AppTheme);
            startActivity(new Intent(LaunchActivity.this,LoginActivity.class));
        }

    }
    @Override
    protected void onPostResume(){
        super.onPostResume();
        SharedPreferences login = getSharedPreferences(login_name,MODE_PRIVATE);
        if((login.contains(user_preference)&&login.contains(user_password))||(data.mail!=null&&data.pw!=null)){
            System.out.println("Melde mich mit gespeiucherten Daten an!");
            data.mail=login.getString(user_preference,null);
            data.pw=login.getString(user_password,null);
            //prüfen, ob Daten gültig sind
            HTTP_Connection conn = new HTTP_Connection(Constants.Server_Adress+"/user/"+data.mail,2);
            conn.delegate = new AsyncResponse() {
                @Override
                public void processFinish(String output, String url) {
                    checkUserResponse(output);
                }
            };
            conn.execute("params");
        }

        else{
            System.out.println("Frage Nutzerdaten an!");
            setTheme(R.style.AppTheme);
            startActivity(new Intent(LaunchActivity.this,LoginActivity.class));
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        setTheme(R.style.SplashTheme);
        final FloatingActionButton logoff = (FloatingActionButton) findViewById(R.id.logoffButton);
        logoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoff();
            }
        });
        final FloatingActionButton einkaufButton = (FloatingActionButton) findViewById(R.id.einkaufButton);
        einkaufButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                einkauf();
            }
        });
        final FloatingActionButton button = (FloatingActionButton) findViewById(R.id.schraenkeButton);
        FloatingActionButton butt = (FloatingActionButton) findViewById(R.id.startDaemon);
        ImageButton b = (ImageButton) findViewById(R.id.back_Launch);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back(view);
            }
        });
        butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LaunchActivity.this);
                builder.setMessage(R.string.ueberwachung_gestartet);
                builder.setTitle(R.string.ueberwachung_starten);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        startService(mServiceIntent);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }
                );
                builder.show().show();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                schraenke();
            }
        });
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //login-Daten privat auslesen und speichern
        SharedPreferences login = getSharedPreferences(login_name,MODE_PRIVATE);
        if((login.contains(user_preference)&&login.contains(user_password))||(data.mail!=null&&data.pw!=null)){
            System.out.println("Melde mich mit gespeiucherten Daten an!");
            data.mail=login.getString(user_preference,null);
            data.pw=login.getString(user_password,null);
            //prüfen, ob Daten gültig sind
            HTTP_Connection conn = new HTTP_Connection(Constants.Server_Adress+"/user/"+data.mail,2);
            conn.delegate = new AsyncResponse() {
                @Override
                public void processFinish(String output, String url) {
                    checkUserResponse(output);
                }
            };
            conn.execute("params");
        }
        else{
            System.out.println("Frage Nutzerdaten an!");
            setTheme(R.style.AppTheme);
            startActivity(new Intent(LaunchActivity.this,LoginActivity.class));
        }
        if(login.contains(autostart_name)){
            CheckBox auto = (CheckBox) findViewById(R.id.autostart_ueberwachung);
            auto.setChecked(login.getBoolean(autostart_name,false));
            if(login.getBoolean(autostart_name,false)){
                mServiceIntent = new Intent(getApplicationContext(), MHDCheckerService.class);
                mServiceIntent.setData(Uri.EMPTY);
                startService(mServiceIntent);
            }
        }
        //beendet Anzeige des Splash
        setTheme(R.style.AppTheme);
        System.out.println("Benutzername: "+data.mail +", Passwort "+data.pw);
    }

    public void logoff(){
        SharedPreferences login = getSharedPreferences(login_name,MODE_PRIVATE);
        SharedPreferences.Editor editor = login.edit();
        editor.remove(user_preference);
        editor.remove(user_password);
        editor.commit();
        data.mail=data.pw=null;
        System.out.println("Frage Nutzerdaten an!");
        setTheme(R.style.AppTheme);
        startActivity(new Intent(LaunchActivity.this,LoginActivity.class));
    }

    public void einkauf(){
        startActivity(new Intent(this,EinkaufActivity.class));
    }
    public void schraenke(){startActivity(new Intent(this, SchrankActivity.class));}

    public void back(View view){
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);

    }
    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);

    }
    public final static String autostart_name = "autostart";
    public void autostart_service(View view){
        if(view instanceof CheckBox){
            CheckBox box = (CheckBox) view;
            if(box.isChecked()){
                startService(mServiceIntent);
            }
                SharedPreferences login = getSharedPreferences(login_name,MODE_PRIVATE);
                SharedPreferences.Editor editor = login.edit();
                editor.putBoolean(autostart_name,box.isChecked());
                editor.commit();

        }
    }
}
