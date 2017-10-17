package Tools;

import javax.swing.*;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;

public class TextAreaOutputStream extends OutputStream {
    private final JTextArea TextArea;
    private final LinkedList<Byte> Input;

    public TextAreaOutputStream(final JTextArea textArea) {
        this.TextArea = textArea;
        this.Input = new LinkedList<>();
    }

    @Override
    public void write(int i) throws IOException {

        if (i == '\n') {
            Input.add((byte) i);
            byte[] b = new byte[Input.size()];
            for (int j = 0; j < Input.size(); j++)
                b[j] = Input.get(j);
            final String text = new String(b, "UTF-8");
            SwingUtilities.invokeLater(() -> TextArea.append(text));
            Input.clear();
        } else {
            Input.add((byte) i);
        }
    }
}
