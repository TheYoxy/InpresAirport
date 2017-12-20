package Tools;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

import javafx.application.Platform;

public abstract class AsyncTask<Params, Progress, Result> {
    protected AsyncTask() {
    }

    protected void onPreExecute() {
    }

    protected void onPostExecute(Result result) {
    }

    public final void publishProgress(Progress... values) {
        Platform.runLater(() -> this.onProgressUpdate(values));
    }

    protected void onProgressUpdate(Progress... progress) {
    }

    public final AsyncTask<Params, Progress, Result> execute(Params... params) {
        // Invoke pre execute
        try {
            runAndWait(this::onPreExecute);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Invoke doInBackground
        CompletableFuture<Result> cf = CompletableFuture.supplyAsync(() -> doInBackground(params));

        // Invoke post execute
        cf.thenAccept(this::onPostExecute);

        return this;
    }

    private static void runAndWait(Runnable action) throws InterruptedException {
        if (action == null)
            throw new NullPointerException("action");

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

    protected abstract Result doInBackground(Params... params);
}