package ServeurClientLog.Threads;

import java.util.LinkedList;
import java.util.List;

import ServeurClientLog.Interfaces.Events.StateChanged;
import ServeurClientLog.Objects.Containeur;

public class ThreadClient extends Thread {
    protected final Containeur<Runnable> TachesAExecuter;
    protected List<StateChanged> stateChangedList;

    public ThreadClient(Containeur<Runnable> st, String name) {
        TachesAExecuter = st;
        this.setName(name);
        stateChangedList = new LinkedList<>();
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                Runnable runnable = TachesAExecuter.get();
                fireState(true);
                runnable.run();
                fireState(false);
            } catch (InterruptedException e) {
                System.out.println(this.getName() + "> Interruption : " + e.getMessage());
            }
        }
    }

    private void fireState(boolean state) {
        for (StateChanged sc : stateChangedList) sc.stateChanged(state);
    }

    public void addStateChangedListener(StateChanged sc) {
        stateChangedList.add(sc);
    }

    public void removeStateChangedListener(StateChanged sc) {
        stateChangedList.remove(sc);
    }
}
