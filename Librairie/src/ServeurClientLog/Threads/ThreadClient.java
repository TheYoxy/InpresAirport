package ServeurClientLog.Threads;

import java.util.LinkedList;
import java.util.List;

import ServeurClientLog.Containers.Containeur;
import ServeurClientLog.Interfaces.Events.StateChanged;

public class ThreadClient extends Thread {
    private final Containeur<Runnable> TachesAExecuter;
    private Runnable Client;
    private List<StateChanged> stateChangedList;

    public ThreadClient(Containeur<Runnable> st, String name) {
        TachesAExecuter = st;
        this.setName(name);
        stateChangedList = new LinkedList<>();
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                Client = TachesAExecuter.get();
                fireState(true);
                Client.run();
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
