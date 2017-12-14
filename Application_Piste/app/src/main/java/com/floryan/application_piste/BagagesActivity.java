package com.floryan.application_piste;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import LUGAP.ReponseLUGAP;
import LUGAP.RequeteLUGAP;
import LUGAP.TypeReponseLUGAP;
import LUGAP.TypeRequeteLUGAP;
import NetworkObject.Bagage;
import NetworkObject.BagageAdapter;
import NetworkObject.Table;
import NetworkObject.Vol;
import Tools.Procedural;

import static com.floryan.application_piste.MainActivity.VOL;

public class BagagesActivity extends AppCompatActivity {
    private Socket s;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private Vol vol;
    private Table t;
    private BagageAdapter controleurBagages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        t = null;

        s = LoginActivity.getS();
        ois = LoginActivity.getOis();
        oos = LoginActivity.getOos();

        vol = (Vol) getIntent().getSerializableExtra(VOL);

        try {
            if (!new RequestBagages().execute().get())
                finishActivity(-1);
            else {
                setContentView(R.layout.activity_bagages);
                Button b = findViewById(R.id.button2);
                b.setOnClickListener(view -> finish());
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (t != null)
            new ReponseBagages().execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class ReponseBagages extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                List<List<String>> lls = new LinkedList<>();
                for (int i = 0; i < controleurBagages.getCount(); i++) {
                    Bagage b = controleurBagages.getItem(i);
                    List<String> l = new LinkedList<>();
                    l.add(b.getNumeroBagage());
                    l.add(((Boolean) b.isChager()).toString());
                    lls.add(l);
                }
                oos.writeObject(new RequeteLUGAP(TypeRequeteLUGAP.Update_mobile, (Serializable) lls, Procedural.IpPort(s)));
                ReponseLUGAP r = (ReponseLUGAP) ois.readObject();
                return r.getCode() == TypeReponseLUGAP.OK;
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class RequestBagages extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                oos.writeObject(new RequeteLUGAP(TypeRequeteLUGAP.Request_Bagages_Vol, vol.getNumeroVol(), Procedural.IpPort(s)));
                ReponseLUGAP r = (ReponseLUGAP) ois.readObject();
                if (r.getCode() != TypeReponseLUGAP.OK) {
                    //todo exception
                } else {
                    t = (Table) r.getParam();
                    return true;
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                ListView lv = findViewById(R.id.ListBagages);
                controleurBagages = new BagageAdapter(BagagesActivity.this, Bagage.FromTable(t));
                lv.setAdapter(controleurBagages);
                lv.invalidate();
            } else {
                Toast.makeText(BagagesActivity.this, "Impossible de récupèrer les bagages", Toast.LENGTH_LONG).show();
                BagagesActivity.this.finishActivity(-1);
            }
        }
    }
}
