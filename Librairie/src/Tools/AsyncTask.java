package Tools;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import javafx.application.Platform;

public abstract class AsyncTask<Params, Progress, Result> {
    protected AsyncTask() {
    }

    protected void onPreExecute() {
    }

    protected void onProgressUpdate(Progress... progress) {
    }

    public final void publishProgress(Progress... values) {
        Platform.runLater(() -> this.onProgressUpdate(values));
    }

    public final AsyncTask<Params, Progress, Result> execute(Params... params) {
        // Invoke pre execute
        try {
            runAndWait(this::onPreExecute);
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Invoke doInBackground
        CompletableFuture<Result> cf = CompletableFuture.supplyAsync(() -> doInBackground(params));

        // Invoke post execute
        Runnable r = () -> {
            try {
                onPostExecute(cf.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        };

        if (Platform.isFxApplicationThread())
            r.run();
        else
            Platform.runLater(r);
        return this;
    }

    private static void runAndWait(Runnable action) throws InterruptedException {
        if (action == null) throw new NullPointerException("action");

        if (Platform.isFxApplicationThread()) {
            action.run();
            return;
        }

        final CountDownLatch doneLatch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                action.run();
            } finally {
                doneLatch.countDown();
            }
        });
        doneLatch.await();
    }

    protected void onPostExecute(Result result) {
    }

    protected abstract Result doInBackground(Params... params);
}