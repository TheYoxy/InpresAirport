package Tools.Affichage;

import java.io.OutputStream;
import java.util.LinkedList;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class FTextAreaOutputStream extends OutputStream {
    private final TextArea TextArea;
    private final LinkedList<Byte> Input;

    public FTextAreaOutputStream(final TextArea textArea) {
        this.TextArea = textArea;
        this.Input = new LinkedList<>();
    }

    @Override
    public void write(int i) {
        if (i == '\r') return;

        if (i == '\n') {
            Input.add((byte) i);
            byte[] b = new byte[Input.size()];
            for (int j = 0; j < Input.size(); j++)
                b[j] = Input.get(j);
            final String text = new String(b);
            Platform.runLater(() -> TextArea.appendText(text));
            Input.clear();
        } else {
            Input.add((byte) i);
        }
    }
}
