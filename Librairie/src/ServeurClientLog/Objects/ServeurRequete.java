package ServeurClientLog.Objects;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

public abstract class ServeurRequete implements Serializable {
    protected final void HeaderRunnable(Requete req, String from) {
        System.out.println("====================");
        System.out.println(Thread.currentThread().getName() + "> Traitement d'une requête de " + req.getType().toString() + " de " + from);
        System.out.println(Thread.currentThread().getName() + "> Message reçu: " + req);
    }

    protected final void Reponse(final ObjectOutputStream outputStream, Reponse rep) throws IOException {
        System.out.println(Thread.currentThread().getName() + "> Réponse: " + rep);
        outputStream.writeObject(rep);
        System.out.println("====================\n");
    }

    public abstract Runnable createRunnable(Socket client);
}
