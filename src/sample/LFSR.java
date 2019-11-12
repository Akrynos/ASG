package sample;

import java.util.ArrayList;

public class LFSR {
    private int NumberOfBits;
    private int[] Taps;
    private boolean[] Bits;
    private String subLFSR0, subLFSR1;
    subLFSR sub0, sub1;
    private FeedbackType Feedback = FeedbackType.ONE2MANY;
    private int SeqLength = -1;
    private boolean Extended = false;
    private StringBuilder text, sLFSR0, sLFSR1, result;
    private static final int ACCEPTABLE_RUN_TIME = 5000;
    private long TimeOut;
    private boolean TimeOutFlag;

    private boolean[] StrobeBitClone;
    private boolean StrobeExtendedRestoreFlag;
    private boolean StrobeExtendedOnceFlag;
    //**************************************************************************
    //Constructors
    //**************************************************************************

    public LFSR(int NumberOfBits, int NumberOfBitsSub0, int NumberOfBitsSub1, int[] Taps, int[] Taps0, int[] Taps1, FeedbackType Feedback, boolean isExtended) {
        SeqLength = -1;
        sub0 = new subLFSR(NumberOfBitsSub0, Taps0, Feedback, false);
        sub1 = new subLFSR(NumberOfBitsSub1, Taps1, Feedback, false);
        if (Feedback == FeedbackType.MANY2ONE) {
            subLFSR1 = sub1.getBitsForward();
            subLFSR0 = sub0.getBitsForward();
        } else {
            subLFSR1 = sub1.getBitsBackward();
            subLFSR0 = sub0.getBitsBackward();
        }
        text = new StringBuilder();
        sLFSR0 = new StringBuilder();
        sLFSR1 = new StringBuilder();
        result = new StringBuilder();
        setFeedbackType(Feedback);
        setExtended(isExtended);
        System.out.println("Constructor: " + NumberOfBits);
        if (!setNumberOfBits(NumberOfBits) || !setTaps(Taps))
            throw new IllegalArgumentException("Failed to initialize an LFSR class.");
        resetTimeOutFlag();
        resetLFSR();
    }

    public LFSR(int NumberOfBits, int NumberOfBitsSub0, int NumberOfBitsSub1, int[] Taps, int[] Taps0, int[] Taps1, FeedbackType Feedback) {
        this(NumberOfBits, NumberOfBitsSub0, NumberOfBitsSub1, Taps, Taps1, Taps0, Feedback, false);
    }

    public LFSR(int NumberOfBits, FeedbackType Feedback) {
        this(NumberOfBits, 4, 6, new int[0], new int[0], new int[0], Feedback, false);
    }

    public LFSR(int NumberOfBits, int NumberOfBitsSub0, int NumberOfBitsSub1, FeedbackType Feedback) {
        this(NumberOfBits, NumberOfBitsSub0, NumberOfBitsSub1, new int[0], new int[0], new int[0], Feedback, false);
    }

    public final void setFeedbackType(FeedbackType val) {
        Feedback = val;
    }

    /**
     * setNumberOfBits
     * Sets the number of bits the LFSR should have. Checking is done to
     * insure a value greater than zero. Initializes the current value of
     * the LFSR to all zero if XNOR gate type and all one if XOR gate
     * type. The Bit array stored by the LFSR is one larger than requested
     * to facilitate the clock strobing operation.
     */
    public final boolean setNumberOfBits(int val) {
        SeqLength = -1;
        if (val <= 1)
            return false;
        boolean initialValue = true;
        NumberOfBits = val;
        //System.out.println(NumberOfBits);
        Bits = new boolean[NumberOfBits + 1];
        for (int i = 0; i < NumberOfBits; i++)
            Bits[i] = initialValue;
        return true;
    }

    /**
     * setTaps
     * Sets the given taps to active in the LFSR. Taps can be in any order and
     * should not contain zero. The empty array sets the taps to optimal.
     */
    public final boolean setTaps(int[] NewTaps) {
        SeqLength = -1;
        if (NewTaps.length == 0)
            NewTaps = getOptimizedTaps();
        java.util.Arrays.sort(NewTaps);
        if (NewTaps[0] == 0)
            return false;
        Taps = NewTaps;
        return true;
    }

    public void setTaps0(int[] a) {
        sub0.setTaps(a);
    }

    public void setTaps1(int[] a) {
        sub1.setTaps(a);
    }

    /**
     * setCurrentBits
     * Sets the current value of the LFSR to that given. This function is
     * dangerous as the it does not reset the LFSR. The array should be the same
     * or one shorted that the value of NumberOfBits.
     */
    public boolean setCurrentBits(boolean[] NewBits) {
        if ((NewBits.length == NumberOfBits - 1) || (NewBits.length == NumberOfBits)) {
            System.arraycopy(NewBits, 0, Bits, 0, NewBits.length);
            return true;
        }
        return false;
    }

    /**
     * setExtended
     * Sets the extended flag high or low for the LFSR.
     */
    public final void setExtended(boolean val) {
        Extended = val;
    }

    /**
     * resetTimeOutFlag
     * resets the time out flag to zero. The flag may be set by certain
     * operations that could take a long time.
     */
    public final void resetTimeOutFlag() {
        TimeOutFlag = false;
    }

    /**
     * resetLFSR
     * resets the the value of the LFSR and all flags that are set by the LFSR
     * except time out. Sequence length is saved. The flag is used by
     * getBitSequence and CalculateSequenceLength.
     */
    public final void resetLFSR() {
        int SeqLenClone = SeqLength;
        setNumberOfBits(NumberOfBits);
        SeqLength = SeqLenClone;
        StrobeExtendedRestoreFlag = false;
        StrobeExtendedOnceFlag = false;
    }

    //**************************************************************************
    // Get Functions
    //**************************************************************************

    /**
     * isExtended
     * gets the value of the Extended setting of the LFSR
     */
    public boolean isExtended() {
        return Extended;
    }

    /**
     * getNumberOfBits
     * gets the size of the LFSR. This is not the length of the Bits array,
     * the array is NumberOfBits+1 in size as one additional bit is needed
     * for operations.
     */
    public int getNumberOfBits() {
        return NumberOfBits;
    }

    /**
     * getTaps
     * gets the array of active taps of the LFSR
     */
    public int[] getTaps() {
        return Taps;
    }

    public int[] getTaps1() {
        return sub1.getTaps();
    }

    public int[] getTaps0() {
        return sub0.getTaps();
    }

    public int[] getOpTaps1() {
        return sub1.getOptimizedTaps();
    }

    public int[] getOpTaps0() {
        return sub0.getOptimizedTaps();
    }

    public String getBitsForward() {
        String currentBits = BitsToString();
        if (Feedback == FeedbackType.MANY2ONE)
            return currentBits;
        else
            return new StringBuilder(currentBits).reverse().toString();
    }

    public String getBitsBackward() {
        String currentBits = BitsToString();
        if (Feedback == FeedbackType.ONE2MANY)
            return currentBits;
        else
            return new StringBuilder(currentBits).reverse().toString();
    }

    public String getSubLFSR0() {
        return sLFSR0.toString();
    }

    public String getSubLFSR1() {
        return sLFSR1.toString();
    }

    public String getResult() {
        return result.toString();
    }

    public int getSequenceLength() {
        if (SeqLength == -1)
            return CalculateSequenceLength();
        return SeqLength;
    }

    public int[] getOptimizedTaps(int val) {
        int[][] x = OptimalTaps.getOptimalTaps(val);
        return x[0];
    }

    public int[] getOptimizedTaps() {
        int[][] x = OptimalTaps.getOptimalTaps(NumberOfBits);
        return x[0];
    }

    /**
     * getTimeOutFlag
     * gets the value of the timeout flag. The timeout flag is hardcoded to be
     * set after 5 seconds. The flag is used by getBitSequence and
     * CalculateSequenceLength
     */
    public boolean getTimeOutFlag() {
        return TimeOutFlag;
    }

    /**
     * getBitSequence
     * gets the string of many bit sequences from depth start to stop with each
     * sequence delimited by the system line separator.
     */
    public String getBitSequence(int start, int stop, boolean bitDirection) {
        initTimeOut(ACCEPTABLE_RUN_TIME);
        String tempResult, correction;
        correction = new String();
        int s1=0, s0=0;
        int subLength, subDiff;
        for (int i = 0; i < start; i++) {
            strobeClock();
            if (isTimeOut())
                return "timeout";
        }

        boolean is0SmallerThan1;
        if (subLFSR0.length() <= subLFSR1.length()) {
            subLength = subLFSR0.length();
            subDiff = subLFSR1.length() - subLFSR0.length();
            is0SmallerThan1 = true;
        } else {
            subLength = subLFSR1.length();
            subDiff = subLFSR0.length() - subLFSR1.length();
            is0SmallerThan1 = false;
        }
        for (int eee = 0; eee < subDiff; eee++) {
            correction += "0";
        }

        for (int i = start; i <= stop; i++) {

            text.append(i);
            if (i <= 999 && i >= 0) {
                text.append(" \t ");
            }if (i > 999) {
                text.append("\t\t");
            }

            if (bitDirection) {
                tempResult = getBitsForward();
                for (int a = 0; a < tempResult.length(); a++) {
                    if (tempResult.charAt(a) == '1') {
                        subLFSR1 = sub1.getBitSequence(true);
                        sLFSR1.append(++s1 + ". \t" + subLFSR1 + System.getProperty("line.separator"));
                    } else {
                        subLFSR0 = sub0.getBitSequence(true);
                        sLFSR0.append(++s0 + ". \t" + subLFSR0 + System.getProperty("line.separator"));
                    }
                    if (subLFSR0.length() != subLFSR1.length()) {
                        if (is0SmallerThan1) {
                            subLFSR0 = correction + subLFSR0;
                            //System.out.println("Corrected LFSR0: " + subLFSR0);
                        } else {
                            subLFSR1 = correction + subLFSR1;
                            //System.out.println("Corrected LFSR1: " + subLFSR1);
                        }
                    }
                    if (subLFSR0.length() == subLFSR1.length()) {
                        //System.out.println("Length is the same, appending with ^ sign");
                        for (int b = 0; b < subLength; b++) {
                            result.append(subLFSR1.charAt(b) ^ subLFSR0.charAt(b));
                        }
                    } else System.out.println("Error, data length mismatch");
                }
            } else {
                tempResult = getBitsBackward();
                for (int a = 0; a < tempResult.length(); a++) {
                    if (tempResult.charAt(a) == '1') {
                        subLFSR1 = sub1.getBitSequence(false);
                        sLFSR1.append(++s1 + ". \t" + subLFSR1 + System.getProperty("line.separator"));
                    } else {
                        subLFSR0 = sub0.getBitSequence(false);
                        sLFSR0.append(++s0 + ". \t" + subLFSR0 + System.getProperty("line.separator"));
                    }
                    if (subLFSR0.length() != subLFSR1.length()) {
                        if (is0SmallerThan1) {
                            subLFSR0 = correction + subLFSR0;
                            //System.out.println("Corrected LFSR0: " + subLFSR0);
                        } else {
                            subLFSR1 = correction + subLFSR1;
                            //System.out.println("Corrected LFSR1: " + subLFSR1);
                        }
                    }
                    if (subLFSR0.length() == subLFSR1.length()) {
                        //System.out.println("Length is the same, appending with ^ sign");
                        for (int b = 0; b < subLength; b++) {
                            result.append(subLFSR1.charAt(b) ^ subLFSR0.charAt(b));
                        }
                    } else System.out.println("Error, data length mismatch");
                }
            }
            text.append(tempResult)
                    .append(System.getProperty("line.separator"));
            strobeClock();
            if (isTimeOut())
                break;
        }
        return text.toString();
    }

    //**************************************************************************
    // Core Operations
    //**************************************************************************

    private boolean preStrobeExtended() {
        boolean isXNOR = false;
        if (StrobeExtendedRestoreFlag) {
            Bits = StrobeBitClone.clone();
            StrobeExtendedRestoreFlag = false;
            StrobeExtendedOnceFlag = true;
        }
        if (!StrobeExtendedOnceFlag && ((isXNOR && XNMSB()) || (!isXNOR && XMSB()))) {
            StrobeExtendedRestoreFlag = true;
            StrobeBitClone = Bits.clone();
            for (int i = 0; i < NumberOfBits; i++)
                Bits[i] = isXNOR;
            return true;
        }
        return false;
    }

    private void strobeClockXORO2M() {
        boolean[] BitsClone = Bits.clone();
        int j = 0;
        Bits[0] = BitsClone[NumberOfBits - 1];
        for (int i = 1; i < NumberOfBits; i++) {
            if ((Taps[j] == i) && (Taps[j] != NumberOfBits)) {
                //System.out.println("Taps pos: " + j + " " + Taps[j]);
                Bits[i] = BitsClone[i - 1] ^ BitsClone[NumberOfBits - 1];
                j++;
            } else {
                Bits[i] = BitsClone[i - 1];
                //System.out.println("ELSE");
            }

        }
    }

    private void strobeClockXORM2O() {
        Bits[NumberOfBits] = false;
        for (int i = 0; i < Taps.length; i++)
            Bits[NumberOfBits] ^= Bits[NumberOfBits - Taps[i]];
        for (int i = 0; i < NumberOfBits; i++)
            Bits[i] = Bits[i + 1];
    }


    public void strobeClock() {
        if (Extended && preStrobeExtended())
            return;
        if (Feedback == FeedbackType.ONE2MANY)
            strobeClockXORO2M();
        else
            strobeClockXORM2O();
    }


    public int CalculateSequenceLength() {
        int length = 0;
        boolean[] BitsClone = Bits.clone();
        setNumberOfBits(NumberOfBits);
        initTimeOut(ACCEPTABLE_RUN_TIME);
        strobeClock();
        for (; ; ) {
            length++;
            strobeClock();
            if (isTimeOut())
                break;
            if (areAllTrue(Bits))
                break;
        }
        Bits = BitsClone;
        SeqLength = length + 1;
        resetLFSR();
        return SeqLength;
    }

    //**************************************************************************
    // Miscellaneous Private Functions
    //**************************************************************************

    private String BitsToString() {
        String x = java.util.Arrays.toString(Bits)
                .replace("true", "1").replace("false", "0")
                .replace(",", "").replace(" ", "");
        return x.substring(1, x.length() - 2);
    }

    private boolean areAllTrue(boolean[] array) {
        Bits[NumberOfBits] = true;
        for (boolean b : array) if (!b) return false;
        return true;
    }

    private void initTimeOut(long miliSecTimeOut) {
        TimeOut = System.currentTimeMillis() + miliSecTimeOut;
    }

    private boolean isTimeOut() {
        if (System.currentTimeMillis() > TimeOut) {
            TimeOutFlag = true;
            return true;
        }
        return false;
    }

    private boolean XMSB() {
        if (Bits[0]) {
            for (int i = 1; i < NumberOfBits; i++) {
                if (Bits[i])
                    return false;
            }
            return true;
        }
        return false;
    }

    private boolean XNMSB() {
        if (!Bits[0]) {
            for (int i = 1; i < NumberOfBits; i++) {
                if (!Bits[i])
                    return false;
            }
            return true;
        }
        return false;
    }

}
