package Tools;

import javax.swing.*;
import java.io.IOException;
import java.io.OutputStream;

public class TextAreaOutputStream extends OutputStream {
    private final JTextArea TextArea;
    private final StringBuilder Sb;

    public TextAreaOutputStream(final JTextArea textArea) {
        this.TextArea = textArea;
        this.Sb = new StringBuilder();
    }

    @Override
    public void write(int i) throws IOException {
        //TODO fix pour l'utf-16
        if (i == '\r')
            return;

        if (i == '\n') {
            final String text = Sb.toString() + "\n";
            SwingUtilities.invokeLater(() -> TextArea.append(text));
            Sb.setLength(0);
        } else {
            Sb.append((char) i);
        }
    }

}
