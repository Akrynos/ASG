package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private ChoiceBox feedbackType;
    @FXML
    private TextField tap10, tap20, tap30, tap40, tap11, tap21, tap31, tap41, tap12, tap22, tap32, tap42, writeFile, bytesLfsrMain, bytesLfsr0, bytesLfsr1, upper, lower;
    @FXML
    private CheckBox autoTaps, ifFile, FullSeq;
    @FXML
    private Label time, seqLength;
    @FXML
    private TextArea lfsr1Res, lfsr0Res, lfsrRes, result;
    private LFSR lfsr;
    private int[] tap0, tap1, tap2;
    String fileName;
    File file;
    FileWriter fw;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void Generate() {
        if (!autoTaps.isSelected()) {
            getTaps();
        }
        if (ifFile.isSelected()) {
            fileName = writeFile.getText();
        }
        int[] bytes = new int[]{Integer.parseInt(bytesLfsrMain.getText()), Integer.parseInt(bytesLfsr0.getText()), Integer.parseInt(bytesLfsr1.getText())};
        System.out.println(bytes[0] + " " + bytes[1] + " " + bytes[2]);
        for (int i = 0; i < 3; i++) {
            if (bytes[i] <= 0 && bytes[i] > 100) {
                bytes[i] = 8;
            }
        }
        String feedBack = (String) feedbackType.getValue();
        switch (feedBack) {
            case "OneToMany":
                if (autoTaps.isSelected()) {
                    lfsr = new LFSR(bytes[0], bytes[1], bytes[2], FeedbackType.ONE2MANY);
                } else {
                    lfsr = new LFSR(bytes[0], bytes[1], bytes[2], tap0, tap1, tap2, FeedbackType.ONE2MANY);
                }
                break;
            case "ManyToOne":
                if (autoTaps.isSelected()) {
                    lfsr = new LFSR(bytes[0], bytes[1], bytes[2], FeedbackType.MANY2ONE);
                } else {
                    lfsr = new LFSR(bytes[0], bytes[1], bytes[2], tap0, tap1, tap2, FeedbackType.MANY2ONE);
                }
                break;
        }
        long generationTime = System.currentTimeMillis();
        UpdateTapsList();
        UpdateSeqLength();
        UpdateSeqList();
        time.setText(Long.toString(System.currentTimeMillis() - generationTime));
    }

    private void getTaps() {
        int[] a = new int[]{};
        if (!tap10.getText().isEmpty()) {
            //System.out.println("Not empty: " + tap10.getText());
            if (!tap20.getText().isEmpty()) {
                //System.out.println("Not empty: " + tap20.getText());
                if (!tap30.getText().isEmpty()) {
                    //System.out.println("Not empty: " + tap30.getText());
                    if (!tap40.getText().isEmpty()) {
                        //System.out.println("Not empty: " + tap40.getText());
                        tap0 = new int[]{Integer.parseInt(tap10.getText()), Integer.parseInt(tap20.getText()), Integer.parseInt(tap30.getText()), Integer.parseInt(tap40.getText())};
                    } else {
                        //System.out.println("Three");
                        tap0 = new int[]{Integer.parseInt(tap10.getText()), Integer.parseInt(tap20.getText())};
                    }
                } else {
                    //System.out.println("Two");
                    tap0 = new int[]{Integer.parseInt(tap10.getText()), Integer.parseInt(tap20.getText())};
                }
            } else {
                //System.out.println("One");
                tap0 = new int[]{Integer.parseInt(tap10.getText()), Integer.parseInt(tap10.getText()) + 1};
            }
        } else {
            tap0 = new int[0];
        }
        if (!tap11.getText().isEmpty()) {
            if (!tap21.getText().isEmpty()) {
                if (!tap31.getText().isEmpty()) {
                    if (!tap41.getText().isEmpty()) {
                        tap1 = new int[]{Integer.getInteger(tap11.getText()), Integer.parseInt(tap21.getText()), Integer.parseInt(tap31.getText()), Integer.parseInt(tap41.getText())};
                    } else
                        tap1 = new int[]{Integer.parseInt(tap11.getText()), Integer.parseInt(tap21.getText())};
                } else tap1 = new int[]{Integer.parseInt(tap11.getText()), Integer.parseInt(tap21.getText())};
            } else tap1 = new int[]{Integer.parseInt(tap11.getText()), Integer.parseInt(tap11.getText()) + 1};
        } else {
            tap1 = new int[0];
        }
        if (!tap12.getText().isEmpty()) {
            if (!tap22.getText().isEmpty()) {
                if (!tap32.getText().isEmpty()) {
                    if (!tap42.getText().isEmpty()) {
                        tap2 = new int[]{Integer.parseInt(tap12.getText()), Integer.parseInt(tap22.getText()), Integer.parseInt(tap32.getText()), Integer.parseInt(tap42.getText())};
                    } else
                        tap2 = new int[]{Integer.parseInt(tap12.getText()), Integer.parseInt(tap22.getText())};
                } else tap2 = new int[]{Integer.parseInt(tap12.getText()), Integer.parseInt(tap22.getText())};
            } else tap2 = new int[]{Integer.parseInt(tap12.getText()), Integer.parseInt(tap12.getText()) + 1};
        } else {
            tap2 = new int[0];
        }
    }

    //**********************************************
    //Update Functions
    //**********************************************

    private void UpdateTapsList() {
        TextField[] taps = new TextField[]{tap10, tap20, tap30, tap40, tap11, tap21, tap31, tap41, tap12, tap22, tap32, tap42};
        lfsr.resetTimeOutFlag();
        if (!autoTaps.isSelected()) {
            lfsr.setTaps(tap0);
            lfsr.setTaps0(tap1);
            lfsr.setTaps1(tap2);
        }
        tap0 = lfsr.getTaps();
//        for (int i = 0; i < tap0.length; i++) {
//            System.out.println("T: " + tap0[i]);
//        }
        tap1 = lfsr.getTaps0();
//        for (int i = 0; i < tap1.length; i++) {
//            System.out.println("T0: " + tap1[i]);
//        }

        tap2 = lfsr.getTaps1();
//        for (int i = 0; i < tap2.length; i++) {
//            System.out.println("T1: " + tap2[i]);
//        }

        for (int i = 0; i < 4; i++) {
            if (tap0.length > i) taps[i].setText(Integer.toString(tap0[i]));
            if (tap1.length > i) taps[i + 4].setText(Integer.toString(tap1[i]));
            if (tap2.length > i) taps[i + 8].setText(Integer.toString(tap2[i]));
        }
    }

    private void UpdateSeqList() {
        lfsr1Res.setText("");
        lfsrRes.setText("");
        lfsr0Res.setText("");
        result.setText("");
        lfsr.resetTimeOutFlag();
        int start, stop;
        if (FullSeq.isSelected()) {
            start = 0;
            stop = lfsr.getSequenceLength();
            System.out.println(stop);
        } else {
            start = getStart();
            stop = getStop();
        }
        lfsrRes.setText(lfsr.getBitSequence(start, stop, true));
        lfsr0Res.setText(lfsr.getSubLFSR0());
        lfsr1Res.setText(lfsr.getSubLFSR1());
        result.setText(lfsr.getResult());
        seqLength.setText(Integer.toString(lfsr.getResult().length()));
        if (ifFile.isSelected()) {
            file = new File(writeFile.getText());
            fw = null;
            try {
                fw = new FileWriter(file);
                fw.write(lfsr.getResult());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                //close resources
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (lfsr.getTimeOutFlag())
            result.setText("***Timeout Warning Occured***");
        lfsr.resetLFSR();
    }

    private void UpdateSeqLength() {
        lfsr.getSequenceLength();
        if (lfsr.getTimeOutFlag())
            seqLength.setText("Time Out");
        else
            seqLength.setText(Integer.toString(lfsr.getSequenceLength()));
    }

    //**********************************************
    //Get Functions
    //**********************************************

    private int getStop() {
        int stop;
        try {
            stop = Integer.parseInt(upper.getText());
            if (stop > lfsr.getSequenceLength() - 1)
                stop = lfsr.getSequenceLength() - 1;
        } catch (NumberFormatException e) {
            stop = lfsr.getSequenceLength() - 1;
        }
        return stop;
    }

    private int getStart() {
        int start;
        try {
            start = Integer.parseInt(lower.getText());
            if (start < 1)
                start = 0;
        } catch (NumberFormatException e) {
            start = 0;
        }
        return start;
    }

}
