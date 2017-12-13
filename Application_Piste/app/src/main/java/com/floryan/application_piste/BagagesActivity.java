package com.floryan.application_piste;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import LUGAP.RequeteLUGAP;
import LUGAP.TypeRequeteLUGAP;
import Tools.Procedural;

public class BagagesActivity extends AppCompatActivity {
    private Socket s;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private String vol;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bagages);

        s = LoginActivity.getS();
        ois = LoginActivity.getOis();
        oos = LoginActivity.getOos();
    }

    private class RequestBagages extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                oos.writeObject(new RequeteLUGAP(TypeRequeteLUGAP.Request_Bagages_Vol,vol, Procedural.IpPort(s)));

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
