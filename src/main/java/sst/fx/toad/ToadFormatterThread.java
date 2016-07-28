package sst.fx.toad;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.StringTokenizer;
import java.util.Vector;

import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;

public class ToadFormatterThread implements Runnable {

    TextArea inputText = null;
    TextArea outputText = null;
    ProgressBar progressBar = null;
    Button processButton = null;
    Button cancelButton = null;
    Text rows = null;
    Text columns = null;

    public ToadFormatterThread(TextArea inputText, TextArea outputText, ProgressBar progressBar, Button processButton, Button cancelButton, Text rows, Text columns) {
        super();
        this.inputText = inputText;
        this.outputText = outputText;
        this.progressBar = progressBar;
        this.processButton = processButton;
        this.cancelButton = cancelButton;
        this.rows = rows;
        this.columns = columns;
    }

    private Thread tfThread = null;

    public void start() {
        tfThread = new Thread(this);
        tfThread.start();
    }

    public void stop() {
        tfThread = null;
    }

    @Override
    public void run() {
        // setCursor(Cursor.WAIT);

        processButton.setDisable(true);
        cancelButton.setDisable(false);
        inputText.clear();
        outputText.clear();
        rows.setText("0");
        columns.setText("0");
        progressBar.setProgress(0);

        String s = "";// = textArea.getText();
        Toolkit kit = Toolkit.getDefaultToolkit();
        Clipboard cb = kit.getSystemClipboard();

        Transferable clipData = cb.getContents(cb);

        if (clipData != null) {
            try {
                if (clipData.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                    s = (String) (clipData.getTransferData(DataFlavor.stringFlavor));
                    inputText.setText(s);
                }
            } catch (Exception exp) {
                System.err.println("Problems getting data: " + exp);
            }
        }

        System.out.println("ActionPerformed...");

        StringTokenizer st = new StringTokenizer(s, "\n");

        Vector<Integer> vSize = new Vector<>();
        Vector<Vector<String>> completeSet = new Vector<>();

        while (st.hasMoreTokens() && null != tfThread) {
            String line = st.nextToken();
            Vector<String> v = new Vector<>();

            StringTokenizer st2 = new StringTokenizer(line, "\t", true);

            int pos = 0;
            boolean bPreviousWasATab = false;

            while (st2.hasMoreElements()) {
                String element = st2.nextToken();

                if (element.equals("\t")) {
                    if (bPreviousWasATab) {
                        element = " ";
                    }

                    else {
                        bPreviousWasATab = true;
                        continue;
                    }

                } else {
                    bPreviousWasATab = false;
                }

                if (pos + 1 <= vSize.size()) {
                    Integer i = (Integer) vSize.elementAt(pos);

                    if (i == null || i.intValue() < element.length()) {
                        i = new Integer(element.length());
                    }

                    vSize.setElementAt(i, pos);
                } else {
                    vSize.addElement(new Integer(element.length()));
                }

                v.addElement(element);

                pos++;
            }

            completeSet.addElement(v);
        }

        rows.setText("" + completeSet.size());
        columns.setText("" + vSize.size());

        System.out.println("Number of lines   : " + completeSet.size());
        System.out.println("Number of columns : " + vSize.size());

        String result = "";

        for (int i = 0; i < completeSet.size() && null != tfThread; i++) {
            double percent = (((double) i) / ((double) completeSet.size()));
            // System.out.println("Percent = " + percent);
            progressBar.setProgress(percent);
            progressBar.requestLayout();
            // progressBar.setStringPainted(true);
            // paint(getGraphics());

            if (i == 1) {
                for (int j = 0; j < vSize.size(); j++) {
                    Integer max = (Integer) vSize.elementAt(j);

                    for (int k = 0; k < max.intValue(); k++) {
                        result += "-";
                    }
                    result += " ";
                }
                result += "\n";
            }

            Vector<String> v = (Vector<String>) completeSet.elementAt(i);

            for (int j = 0; j < v.size(); j++) {
                Integer max = (Integer) vSize.elementAt(j);
                String element = (String) v.elementAt(j);
                result += element;

                for (int k = element.length(); k < max.intValue(); k++) {
                    result += " ";
                }
                result += " ";
            }
            result += "\n";
        }

        StringSelection data = new StringSelection(result);
        cb.setContents(data, data);

        kit.beep();

        outputText.setText(result);

        // textArea.setText(result);

        System.out.println("Done.");

        progressBar.setProgress(1);

        processButton.setDisable(false);
        cancelButton.setDisable(true);

        // setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

}
