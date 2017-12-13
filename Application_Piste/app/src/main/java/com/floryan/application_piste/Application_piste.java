package com.floryan.application_piste;

import android.app.Application;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import LUGAP.ReponseLUGAP;
import LUGAP.RequeteLUGAP;
import LUGAP.TypeReponseLUGAP;
import LUGAP.TypeRequeteLUGAP;
import Tools.Procedural;

public class Application_piste extends Application {
    public Application_piste() {
        super();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        Socket s = LoginActivity.getS();
        ObjectOutputStream oos = LoginActivity.getOos();
        ObjectInputStream ois = LoginActivity.getOis();

        if (s != null)
        {
            if (oos != null && ois != null) {
                try {
                    oos.writeObject(new RequeteLUGAP(TypeRequeteLUGAP.Logout,Procedural.IpPort(s)));
                    ReponseLUGAP r = (ReponseLUGAP) ois.readObject();
                    if (r.getCode() != TypeReponseLUGAP.OK)
                        return;

                    oos.writeObject(new RequeteLUGAP(TypeRequeteLUGAP.Disconnect, Procedural.IpPort(s)));
                    r = (ReponseLUGAP) ois.readObject();
                    if (r.getCode() != TypeReponseLUGAP.OK)
                        return;
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            try {
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
