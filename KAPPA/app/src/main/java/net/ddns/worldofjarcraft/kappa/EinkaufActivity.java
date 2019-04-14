package net.ddns.worldofjarcraft.kappa;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import net.ddns.worldofjarcraft.kappa.Model.Einkauf;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static android.os.Build.VERSION_CODES.M;
import static net.ddns.worldofjarcraft.kappa.R.id.reloadButton;

public class EinkaufActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_einkauf);
       FloatingActionButton fab = findViewById(R.id.neuerEinkauf);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                neuerEinkauf();
            }
        });
        ImageButton butt = findViewById(R.id.back_Einkauf);
        butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back(view);
            }
        });
        FloatingActionButton reloadButton = findViewById(R.id.reloadButton);
        reloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aktualisieren();
            }
        });
        aktualisieren();
    }
    List<Einkauf> einkäufe;
    public void aktualisieren(){
        String url = Constants.Server_Adress +"/einkauf";
        AsyncResponse response = new AsyncResponse() {
            @Override
            public void processFinish(String output, String url) {
                ProgressBar prog = (ProgressBar) findViewById(R.id.fortschritt);
                prog.setVisibility(View.GONE);
                LinearLayout layout = (LinearLayout) findViewById(R.id.einkaufsliste);
                layout.removeAllViews();
                Einkauf[] ret = new Gson().fromJson(output,Einkauf[].class);
                einkäufe = new ArrayList<>();
                if(ret==null) return;
                for(Einkauf einkauf:ret){
                    einkäufe.add(einkauf);
                    TextView v = new TextView(EinkaufActivity.this);
                    v.setText(einkauf.getLebensmittel());
                    v.setTextSize(24);
                    v.setTextColor(getResources().getColor(R.color.black));
                    v.setBackgroundResource(R.drawable.textviewstyle);
                    v.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            final View v = view;
                            AlertDialog.Builder builder = new AlertDialog.Builder(EinkaufActivity.this);
                            builder.setTitle(R.string.einkauf_loeschen_titel);
                            builder.setMessage(R.string.einkauf_loeschen);
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    delete(v);
                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                            builder.show();
                            return false;
                        }
                    });
                    Space s = new Space(EinkaufActivity.this);
                    s.setMinimumHeight(30);
                    /*LinearLayout hor = new LinearLayout(EinkaufActivity.this);
                    hor.setOrientation(LinearLayout.HORIZONTAL);
                    hor.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    ContextThemeWrapper newContext = new ContextThemeWrapper(EinkaufActivity.this, R.style.AppTheme);
                    Button button = new Button(newContext);
                    button.setText(R.string.delete_Einkauf);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            delete(view);
                        }
                    });
                    hor.addView(v);
                    hor.addView(button);
                    layout.addView(hor);*/
                    layout.addView(v);
                    layout.addView(s);
                }
            }
        };
        HTTP_Connection login = new HTTP_Connection(url,2);
        login.delegate = response;
        login.execute();
        ProgressBar prog = (ProgressBar) findViewById(R.id.fortschritt);
        prog.setVisibility(View.VISIBLE);
    }

    private void delete(View view) {
        LinearLayout layout = (LinearLayout) findViewById(R.id.einkaufsliste);
        int nummer = -1;
        int spaces = 0;
        for(int i=0;i<layout.getChildCount();i++){
            View v = layout.getChildAt(i);
                if(v==view){
                    nummer=i-spaces;
                }
            else if (v instanceof Space)
                spaces++;
        }
        if(nummer!=-1){
            Einkauf ziel = einkäufe.get(nummer);
            AsyncResponse response = new AsyncResponse() {
                @Override
                public void processFinish(String output, String url) {
                    if(output.equals("true")){
                        aktualisieren();
                    }
                    else
                        Toast.makeText(EinkaufActivity.this, "Eintrag konnte nicht gelöscht werden!", Toast.LENGTH_LONG).show();
                    aktualisieren();
                }
            };
            HTTP_Connection conn = new HTTP_Connection(Constants.Server_Adress+"/einkauf/"+ziel.getId()+"/delete");
            conn.delegate = response;
            conn.execute("params");
        }
    }

    public void neuerEinkauf(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.neuer_Einkauf);
        builder.setMessage(R.string.Einkauf_Beschreibung);
// Set up the input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!input.getText().toString().isEmpty()){
                String url = Constants.Server_Adress+"/einkauf/create";
                HashMap<String,String> map = new HashMap<>();
                map.put("Einkauf",input.getText().toString());
                    HTTP_Connection login = null;
                    try {
                        login = new HTTP_Connection(url,2,map,"GET");
                    } catch (UnsupportedEncodingException e) {
                        return;
                    }
                    AsyncResponse response = new AsyncResponse() {
                    @Override
                    public void processFinish(String output, String url) {
                        ProgressBar prog = (ProgressBar) findViewById(R.id.fortschritt);
                        prog.setVisibility(View.GONE);
                        if(!output.isEmpty()){
                            aktualisieren();
                        }
                        else{
                            Toast.makeText(EinkaufActivity.this,"Es ist ein Netzwerkfehler aufgetreten.",Toast.LENGTH_LONG).show();
                            prog.setVisibility(View.GONE);
                        }
                    }
                };
                login.delegate = response;
                login.execute();
                ProgressBar prog = (ProgressBar) findViewById(R.id.fortschritt);
                prog.setVisibility(View.VISIBLE);
                }
                else
                    Toast.makeText(EinkaufActivity.this,R.string.name_leer,Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void back(View view){
       this.finish();
    }
}
