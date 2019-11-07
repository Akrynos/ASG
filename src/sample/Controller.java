package sample;

public class Controller {

//    LFSR lfsr = new LFSR();
//    private void BitsTextAreaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BitsTextAreaActionPerformed
//        try {
//            lfsr.setNumberOfBits(Integer.parseInt(BitsTextArea.getText()));
//        }
//        catch (NumberFormatException f) {
//            lfsr.setNumberOfBits(1);
//        }
//        UpdateTapsList();
//        UpdateSeqLength();
//    }//GEN-LAST:event_BitsTextAreaActionPerformed
//
//    //**********************************************
//    //Update Functions
//    //**********************************************
//
//    private void UpdateTapsList() {
//        lfsr.resetTimeOutFlag();
//        javax.swing.DefaultListModel model = new javax.swing.DefaultListModel();
//        if (AutoRadio.isSelected())
//            lfsr.setTaps(lfsr.getOptimizedTaps());
//        else {
//            int[] adjArray = TapsList.getSelectedIndices();
//            for(int i = 0; i < adjArray.length; i++)
//                adjArray[i] = adjArray[i] + 1;
//            lfsr.setTaps(adjArray);
//        }
//        for(int i = 1; i <= lfsr.getNumberOfBits(); i++)
//            model.addElement(i);
//        TapsList.clearSelection();
//        TapsList.setModel(model);
//        int[] adjArray = lfsr.getTaps().clone();
//        for(int i = 0; i < adjArray.length; i++)
//            adjArray[i] = adjArray[i] - 1;
//        TapsList.setSelectedIndices(adjArray);
//    }
//
//    private void UpdateSeqList() {
//        SeqTextArea.setText("");
//        lfsr.resetTimeOutFlag();
//        int start, stop;
//        if (FullSeqCheckBox.isSelected()) {
//            start = 0;
//            stop = lfsr.getSequenceLength();
//        } else {
//            start = getStart();
//            stop = getStop();
//        }
//        SeqTextArea.append(lfsr.getBitSequence(start, stop, BitForward.isSelected()));
//        if (lfsr.getTimeOutFlag())
//            SeqTextArea.append("***Timeout Warning Occured***");
//        SeqTextArea.setCaretPosition(0);
//        lfsr.resetLFSR();
//    }
//
//    private void UpdateLFSRSettings() {
//        if (XorRadio.isSelected())
//            lfsr.setGateType(GateType.XOR);
//        else
//            lfsr.setGateType(GateType.XNOR);
//        if (ManyToOneRadio.isSelected())
//            lfsr.setFeedbackType(FeedbackType.MANY2ONE);
//        else
//            lfsr.setFeedbackType(FeedbackType.ONE2MANY);
//        if (nRadioButton.isSelected())
//            lfsr.setExtended(true);
//        else
//            lfsr.setExtended(false);
//        try {lfsr.setNumberOfBits(Integer.parseInt(BitsTextArea.getText()));}
//        catch (NumberFormatException e) { lfsr.setNumberOfBits(1);}
//        lfsr.resetLFSR();
//    }
//
//    private void UpdateCode() {
//        boolean IncludeReset = false;
//        boolean IncludeFlag = false;
//        if (IncludeResetCheckBox.isSelected())
//            IncludeReset = true;
//        if (IncludeFlagCheckBox.isSelected())
//            IncludeFlag = true;
//
//    }
//
//    private void UpdateSeqLength() {
//        lfsr.getSequenceLength();
//        if (lfsr.getTimeOutFlag())
//            SeqLengthTextArea.setText("Time Out");
//        else
//            SeqLengthTextArea.setText(Integer.toString(lfsr.getSequenceLength()));
//    }
//
//    //**********************************************
//    //Get Functions
//    //**********************************************
//
//    private int getStop() {
//        int stop;
//        try {
//            stop = Integer.parseInt(UpperTextArea.getText());
//            if (stop>lfsr.getSequenceLength()-1)
//                stop = lfsr.getSequenceLength()-1;
//        }
//        catch (NumberFormatException e) { stop = lfsr.getSequenceLength()-1; }
//        return stop;
//    }
//
//    private int getStart() {
//        int start;
//        try {
//            start = Integer.parseInt(LowerTextArea.getText());
//            if (start<1)
//                start = 0;
//        }
//        catch (NumberFormatException e) { start = 0; }
//        return start;
//    }

}
