package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private ChoiceBox feedbackType;
    @FXML
    private TextField testFile, tap10, tap20, tap30, tap40, tap11, tap21, tap31, tap41, tap12, tap22, tap32, tap42, writeFile, bytesLfsrMain, bytesLfsr0, bytesLfsr1, upper, lower, seqLength, keyFile, textFile, outputFile;
    @FXML
    private CheckBox autoTaps, ifFile, FullSeq, ignore;
    @FXML
    private Label TestError, time, keyError, zeroes, ones, singleTestRes, series11, series12, series13, series14, series15, series16, seriesTestRes, longTestRes, shortest, longest, pokerS, pokerX, pokerRes;
    @FXML
    private TextArea lfsr1Res, lfsr0Res, lfsrRes, result;
    private LFSR lfsr;
    private int[] tap0, tap1, tap2;
    int seqLth, ask = 0;
    String fileName;
    File file, key, text, out;
    FileWriter fw;
    FileReader fr;
    BufferedReader br;

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

        if (!seqLength.getText().isEmpty()) {
            seqLth = Integer.parseInt(seqLength.getText());
        } else seqLth = 10000;

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
            if (tap1.length > i) taps[i + 8].setText(Integer.toString(tap1[i]));
            if (tap2.length > i) taps[i + 4].setText(Integer.toString(tap2[i]));
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
            stop = seqLth;
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

    private void WriteToFile(String s) {
        out = new File(outputFile.getText());
        fw = null;
        try {
            fw = new FileWriter(out);
            fw.write(s);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String textToBinary(String s) {
        byte[] bytes = s.getBytes();
        StringBuilder binary = new StringBuilder();
        for (byte b : bytes) {
            int val = b;
            for (int i = 0; i < 8; i++) {
                binary.append((val & 128) == 0 ? 0 : 1);
                val <<= 1;
            }
        }
        return binary.toString();
    }

    private static String binaryToText(String string) {
        StringBuilder sb = new StringBuilder();
        char[] chars = string.replaceAll("\\s", "").toCharArray();
        int[] mapping = {1, 2, 4, 8, 16, 32, 64, 128};

        for (int j = 0; j < chars.length; j += 8) {
            int idx = 0;
            int sum = 0;
            for (int i = 7; i >= 0; i--) {
                if (chars[i + j] == '1') {
                    sum += mapping[idx];
                }
                idx++;
            }
            //System.out.println(sum);//debug
            sb.append(Character.toChars(sum));
        }
        return sb.toString();
    }

    private String fileContent(File file) {
        StringBuilder s = new StringBuilder();
        String line;
        fr = null;
        br = null;
        try {
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            while ((line = br.readLine()) != null) {
                s.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return s.toString();
        }
    }

    public void Cipher() {
        StringBuilder sb = new StringBuilder();
        String K = fileContent(new File(keyFile.getText()));
        String T = fileContent(new File(textFile.getText()));
        String binText = textToBinary(T);
        //System.out.println(binText.length());
        if (K.length() > binText.length()) K = K.substring(0, binText.length());
        if (keyLength(K.length(), binText.length())) {
            for (int i = 0; i < binText.length() - 1; ) {
                for (int j = 0; j < K.length() && i < binText.length(); j++, i++) {
                    sb.append(binText.charAt(i) ^ K.charAt(j));
                }
            }
            System.out.println(binText);
            WriteToFile(sb.toString());
        }
    }

    public void Decipher() {
        StringBuilder sb = new StringBuilder();
        String K = fileContent(new File(keyFile.getText()));
        String binText = fileContent(new File(textFile.getText()));
        //System.out.println(binText);
        if (K.length() > binText.length()) K = K.substring(0, binText.length());
        if (keyLength(K.length(), binText.length())) {
            for (int i = 0; i < binText.length(); ) {
                for (int j = 0; j < K.length() && i < binText.length(); j++, i++) {
                    sb.append(binText.charAt(i) ^ K.charAt(j));
                }
            }
            WriteToFile(binaryToText(sb.toString()));
        }
    }

    public boolean keyLength(int key, int file) {
        if (ignore.isSelected()) {
            ask = 0;
            return true;
        } else if (key != file && ask == 0) {
            keyError.setText("Key length error (mismatch length: key/text), if you want to proceed press the same button again or select checkbox to ignore keylength. If you dont want to proceed generate new key with the same length as text");
            ask++;
            return false;
        } else if (key != file && ask > 0) {
            return true;
        } else if (key == file) return true;
        else return false;
    }

    void singleTest(String s) {
        Integer z = 0, o = 0;
        if (s.length() < 20000) TestError.setText("Key is too short");
        for (int i = 0; i < 20000; i++) {
            if (s.charAt(i) == '0') z++;
            else o++;
        }
        zeroes.setText(z.toString());
        ones.setText(o.toString());
        if ((z > 9725 && z < 10275) && (o > 9725 && o < 10275))
            singleTestRes.setText("Passed");
        else singleTestRes.setText("Failed");
    }

    void seriesTest(String s) {
        int[] S = {0, 0, 0, 0, 0, 0, 0};
        Integer s1, s2, s3, s4, s5, s6, tempLen = 0, size = 0;
        int lst = 0, sst = 999;
        for (int i = 0; i < 19999; i++) {
            if (s.charAt(i) == s.charAt(i + 1)) tempLen++;
            else {
                tempLen++;
                if (sst > tempLen) sst = tempLen;
                if (lst < tempLen) lst = tempLen;
                size += tempLen;
                if (tempLen >= 6) S[5]++;
                else S[tempLen - 1]++;
                //System.out.print(tempLen + " ");
                tempLen = 0;
            }
        }
        seriesLengthTest(lst, sst);
       // System.out.println(size);
        s1 = S[0];
        s2 = S[1];
        s3 = S[2];
        s4 = S[3];
        s5 = S[4];
        s6 = S[5];
        //for (int i = 0; i <= 5; i++) System.out.println(Integer.toString(S[i]));
        series11.setText(s1.toString());
        series12.setText(s2.toString());
        series13.setText(s3.toString());
        series14.setText(s4.toString());
        series15.setText(s5.toString());
        series16.setText(s6.toString());
        if ((s1 >= 2315 && s1 <= 2685) && (s2 >= 1114 && s2 <= 1368) && (s3 >= 527 && s3 <= 723) && (s4 >= 240 && s4 <= 238) && (s5 >= 103 && s5 <= 209) && (s6 >= 103 && s6 <= 209))
            seriesTestRes.setText("Passed");
        else seriesTestRes.setText("Failed");
    }

    void seriesLengthTest(int l, int s) {
        longest.setText(Integer.toString(l));
        shortest.setText(Integer.toString(s));
        if (l >= 26) longTestRes.setText("Failed");
        else longTestRes.setText("Passed");
    }

    void pokerTest(String s) {
        int[] S = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        long dane=0;
        String temp;
        for (int i = 0; i < 5000; i += 4) {
            temp = s.substring(i, i + 4);
            //System.out.println(temp + " " + Integer.parseInt(temp, 2));
           S[Integer.parseInt(temp, 2)]++;
        }
        temp="";
       for(int i=0; i<15; i++){
           dane+=S[i]*S[i]-5000;
           temp+=i+": "+S[i]+" | ";
           if(i==15) temp+=i+": "+S[i];
       }
       double x = (16.00/5000.00)*dane;
        System.out.println(x+" "+dane);
       pokerX.setText(Double.toString(x));
        pokerS.setText(temp);
        if(x>2.16 && x<46.17) pokerRes.setText("Passed");
        else pokerRes.setText("Failed");
    }

    void launchAllTests(String s) {
        singleTest(s);
        seriesTest(s);
        pokerTest(s);
    }

    public void testingResult() {
        String key = lfsr.getResult();
        launchAllTests(key);
    }

    public void testingFile() {
        String key = fileContent(new File(testFile.getText()));
        launchAllTests(key);
    }
}
