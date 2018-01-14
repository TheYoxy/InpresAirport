package Tools.Affichage;

import java.io.OutputStream;
import java.util.LinkedList;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class TextAreaOutputStream extends OutputStream {
    private final JTextArea TextArea;
    private final LinkedList<Byte> Input;

    public TextAreaOutputStream(final JTextArea textArea) {
        this.TextArea = textArea;
        this.Input = new LinkedList<>();
    }

    @Override
    public void write(int i) {
        if (i == '\r')
            return;

        if (i == '\n') {
            Input.add((byte) i);
            byte[] b = new byte[Input.size()];
            for (int j = 0; j < Input.size(); j++)
                b[j] = Input.get(j);
            final String text = new String(b);
            SwingUtilities.invokeLater(() -> TextArea.append(text));
            Input.clear();
        } else {
            Input.add((byte) i);
        }
    }
}
