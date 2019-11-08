package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private ChoiceBox feedbackType;
    @FXML
    private TextField tap10, tap20, tap30, tap40, tap11, tap21, tap31, tap41, tap12, tap22, tap32, tap42, writeFile, bytesLfsr1, bytesLfsr2, bytesLfsr3, upper, lower;
    @FXML
    private CheckBox autoTaps, ifFile, FullSeq;
    @FXML
    private Label time, seqLength;
    @FXML
    private TextArea lfsr1Res, lfsr2Res, lfsr3Res, result;
    private LFSR lfsr1, lfsr2, lfsr3;
    private int[] tap0, tap1, tap2;
    String fileName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void Generate() {
        if (!autoTaps.isSelected()) {
            tap0 = new int[]{Integer.parseInt(tap10.getText()), Integer.parseInt(tap20.getText()), Integer.parseInt(tap30.getText()), Integer.parseInt(tap40.getText())};
            tap1 = new int[]{Integer.parseInt(tap11.getText()), Integer.parseInt(tap21.getText()), Integer.parseInt(tap31.getText()), Integer.parseInt(tap41.getText())};
            tap2 = new int[]{Integer.parseInt(tap12.getText()), Integer.parseInt(tap22.getText()), Integer.parseInt(tap32.getText()), Integer.parseInt(tap42.getText())};
        }
        if (ifFile.isSelected()) {
            fileName = writeFile.getText();
        }
        int[] bytes = new int[]{Integer.parseInt(bytesLfsr1.getText()), Integer.parseInt(bytesLfsr2.getText()), Integer.parseInt(bytesLfsr3.getText())};
        System.out.println(bytes[0] + " " + bytes[1] + " " + bytes[2]);
        for (int i = 0; i < 3; i++) {
            if (bytes[i] <= 0 && bytes[i] > 100) {
                bytes[i] = 8;
            }
        }
        String feedBack = (String) feedbackType.getValue();

        if (feedBack != "OneToMany" || feedBack != "ManyToOne") feedBack = "OneToMany";
        switch (feedBack) {
            case "OneToMany":
                if (autoTaps.isSelected()) {
                    lfsr1 = new LFSR(bytes[0], FeedbackType.ONE2MANY);
                    lfsr2 = new LFSR(bytes[1], FeedbackType.ONE2MANY);
                    lfsr3 = new LFSR(bytes[2], FeedbackType.ONE2MANY);
                } else {
                    lfsr1 = new LFSR(bytes[0], tap0, FeedbackType.ONE2MANY);
                    lfsr2 = new LFSR(bytes[1], tap1, FeedbackType.ONE2MANY);
                    lfsr3 = new LFSR(bytes[2], tap2, FeedbackType.ONE2MANY);
                }
                break;
            case "ManyToOne":
                if (autoTaps.isSelected()) {
                    lfsr1 = new LFSR(bytes[0], FeedbackType.MANY2ONE);
                    lfsr2 = new LFSR(bytes[1], FeedbackType.MANY2ONE);
                    lfsr3 = new LFSR(bytes[2], FeedbackType.MANY2ONE);
                } else {
                    lfsr1 = new LFSR(bytes[0], tap0, FeedbackType.MANY2ONE);
                    lfsr2 = new LFSR(bytes[1], tap1, FeedbackType.MANY2ONE);
                    lfsr3 = new LFSR(bytes[2], tap2, FeedbackType.MANY2ONE);
                }
                break;
        }
        long generationTime = System.currentTimeMillis();
        UpdateTapsList();
        UpdateSeqLength();
        UpdateSeqList();
        time.setText(Long.toString(System.currentTimeMillis() - generationTime));
    }

    //**********************************************
    //Update Functions
    //**********************************************

    private void UpdateTapsList() {
        TextField[] taps = new TextField[]{tap10, tap20, tap30, tap40, tap11, tap21, tap31, tap41, tap12, tap22, tap32, tap42};
        lfsr1.resetTimeOutFlag();
        lfsr2.resetTimeOutFlag();
        lfsr3.resetTimeOutFlag();
        tap0 = lfsr1.getTaps();
        tap1 = lfsr2.getTaps();
        tap2 = lfsr3.getTaps();
        for (int i = 0; i < 4; i++) {
            if (tap0.length > i + 1) taps[i].setText(Integer.toString(tap0[i]));
            if (tap1.length > i + 1) taps[i + 4].setText(Integer.toString(tap1[i]));
            if (tap2.length > i + 1) taps[i + 8].setText(Integer.toString(tap2[i]));
        }
    }

    private void UpdateSeqList() {
        result.setText("");
        lfsr1.resetTimeOutFlag();
        lfsr2.resetTimeOutFlag();
        lfsr3.resetTimeOutFlag();
        int start, stop;
        if (FullSeq.isSelected()) {
            start = 0;
            stop = lfsr1.getSequenceLength();
            System.out.println(stop);
        } else {
            start = getStart();
            stop = getStop();
        }
        result.setText(lfsr1.getBitSequence(start, stop, true));
        if (lfsr1.getTimeOutFlag())
            result.setText("***Timeout Warning Occured***");
        lfsr1.resetLFSR();
    }

    private void UpdateSeqLength() {
        lfsr1.getSequenceLength();
        if (lfsr1.getTimeOutFlag())
            seqLength.setText("Time Out");
        else
            seqLength.setText(Integer.toString(lfsr1.getSequenceLength()));
    }

    //**********************************************
    //Get Functions
    //**********************************************

    private int getStop() {
        int stop;
        try {
            stop = Integer.parseInt(upper.getText());
            if (stop > lfsr1.getSequenceLength() - 1)
                stop = lfsr1.getSequenceLength() - 1;
        } catch (NumberFormatException e) {
            stop = lfsr1.getSequenceLength() - 1;
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
