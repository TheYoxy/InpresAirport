package com.floryan.application_piste;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.ParseException;
import java.util.List;

import LUGAP.ReponseLUGAP;
import LUGAP.RequeteLUGAP;
import LUGAP.TypeReponseLUGAP;
import LUGAP.TypeRequeteLUGAP;
import NetworkObject.Table;
import NetworkObject.Vol;
import Tools.Procedural;

public class MainActivity extends AppCompatActivity {
    public static final String VOL = "VOL";
    private Socket s;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private Table t;
    private List<Vol> i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        s = LoginActivity.getS();
        ois = LoginActivity.getOis();
        oos = LoginActivity.getOos();
        new GetVols().execute();

        Intent intent = getIntent();
        Toolbar tb = findViewById(R.id.VolsTB);
        tb.setSubtitle(intent.getStringExtra(LoginActivity.USERNAME));
        ListView lv = findViewById(R.id.ListVols);
        lv.setOnItemClickListener((adapterView, view, i, l1) -> {
            Intent intent1 = new Intent(getBaseContext(), BagagesActivity.class);
            intent1.putExtra(VOL, (Vol) lv.getItemAtPosition(i));
            int res = 0;
            startActivityForResult(intent1, res);
            if (res == -1)
                Toast.makeText(MainActivity.this, "Les bagages de ce vol sont déjà en cours de modification", Toast.LENGTH_LONG).show();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //        new Deconnexion().execute();
    }

    @SuppressLint("StaticFieldLeak")
    public class Deconnexion extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                oos.writeObject(new RequeteLUGAP(TypeRequeteLUGAP.Logout, Procedural.IpPort(s)));
                ReponseLUGAP r = (ReponseLUGAP) ois.readObject();
                if (r.getCode() != TypeReponseLUGAP.OK) {
                    //Todo Gestion d'exception
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class GetVols extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... objects) {
            try {
                oos.writeObject(new RequeteLUGAP(TypeRequeteLUGAP.Request_Vols, Procedural.IpPort(s)));
                ReponseLUGAP r = (ReponseLUGAP) ois.readObject();
                if (r.getCode() == TypeReponseLUGAP.OK) {
                    t = (Table) r.getParam();
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                ListView lv = findViewById(R.id.ListVols);
                try {
                    ArrayAdapter<Vol> controleurVol = new ArrayAdapter<>(MainActivity.this, R.layout.vol_list_layout, R.id.tv, Vol.fromTableList(t));
                    lv.setAdapter(controleurVol);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                lv.invalidate();
            }
        }
    }
}