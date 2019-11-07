package sample;

public class LFSR {
    private int NumberOfBits;
    private int[] Taps;
    private boolean[] Bits;

    private FeedbackType Feedback = FeedbackType.ONE2MANY;
    private int SeqLength = -1;
    private boolean Extended = false;

    private static final int ACCEPTABLE_RUN_TIME = 5000;
    private long TimeOut;
    private boolean TimeOutFlag;

    private boolean[] StrobeBitClone;
    private boolean StrobeExtendedRestoreFlag;
    private boolean StrobeExtendedOnceFlag;

    //**************************************************************************
    //Constructors
    //**************************************************************************

    public LFSR(int NumberOfBits, int[] Taps, FeedbackType Feedback, boolean isExtended) {
        SeqLength = -1;
        setFeedbackType(Feedback);
        setExtended(isExtended);
        if (!setNumberOfBits(NumberOfBits) || !setTaps(Taps))
            throw new IllegalArgumentException("Failed to initialize an LFSR class.");
        resetTimeOutFlag();
        resetLFSR();
    }

    public LFSR() {
        this(4, new int[0], FeedbackType.MANY2ONE, false);
    }

    public LFSR(int NumberOfBits, int[] Taps) {
        this(NumberOfBits, Taps, FeedbackType.MANY2ONE, false);
    }

    public LFSR(int NumberOfBits, int[] Taps, FeedbackType Feedback) {
        this(NumberOfBits, Taps, Feedback, false);
    }

    public LFSR(int NumberOfBits, FeedbackType Feedback, boolean isExtended) {
        this(NumberOfBits, new int[0], Feedback, isExtended);
    }

    public LFSR(int NumberOfBits, FeedbackType Feedback) {
        this(NumberOfBits, new int[0], Feedback, false);
    }

    public LFSR(int NumberOfBits) {
        this(NumberOfBits, new int[0], FeedbackType.MANY2ONE, false);
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

    /**
     * getBitsForward
     * gets a string of "1" and "0" that is th value of the LFSR with the most
     * significant bit (MSB) on the left and least significant bit (LSB) on the
     * right.
     *
     * @return string of the value stored in the LFSR in binary
     */
    public String getBitsForward() {
        String currentBits = BitsToString();
        if (Feedback == FeedbackType.MANY2ONE)
            return currentBits;
        else
            return new StringBuilder(currentBits).reverse().toString();
    }

    /**
     * getBitsBackward
     * gets a string of "1" and "0" that is th value of the LFSR with the most
     * significant bit (MSB) on the right and least significant bit (LSB) on the
     * left.
     */
    public String getBitsBackward() {
        String currentBits = BitsToString();
        if (Feedback == FeedbackType.ONE2MANY)
            return currentBits;
        else
            return new StringBuilder(currentBits).reverse().toString();
    }

    /**
     * getSequenceLength
     * gets the length of the sequence generated by the current LFSR settings. If
     * that value has not yet been calculated is will be by calling
     */
    public int getSequenceLength() {
        if (SeqLength == -1)
            return CalculateSequenceLength();
        return SeqLength;
    }

    /**
     * getOptimizedTaps
     * gets the first (shortest) solution for optimal taps for a LFSR of size
     * val. These values are stored in the class OptimalTaps and are precomputed.
     */
    public int[] getOptimizedTaps(int val) {
        int[][] x = OptimalTaps.getOptimalTaps(val);
        return x[0];
    }

    /**
     * getOptimizedTaps
     * gets the first (shortest) solution for optimal taps for a LFSR of size
     * val. These values are stored in the class OptimalTaps and are precomputed.
     */
    public int[] getOptimizedTaps() {
        return getOptimizedTaps(NumberOfBits);
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
     * sequence delimited by the system line separator. The sequences can be
     * displayed forward or backward. The method can cause a timeout flag.
     */
    public String getBitSequence(int start, int stop, boolean bitDirection) {
        initTimeOut(ACCEPTABLE_RUN_TIME);
        for (int i = 0; i < start; i++) {
            strobeClock();
            if (isTimeOut())
                return "timeout";
        }
        StringBuilder text = new StringBuilder();
        for (int i = start; i <= stop; i++) {
            text.append(i)
                    .append("\t");
            if (bitDirection)
                text.append(getBitsForward());
            else
                text.append(getBitsBackward());
            text.append(System.getProperty("line.separator"));
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
                Bits[i] = BitsClone[i - 1] ^ BitsClone[NumberOfBits - 1];
                j++;
            } else
                Bits[i] = BitsClone[i - 1];
        }
    }

    private void strobeClockXORM2O() {
        Bits[NumberOfBits] = false;
        for (int i = 0; i < Taps.length; i++)
            Bits[NumberOfBits] ^= Bits[NumberOfBits - Taps[i]];
        for (int i = 0; i < NumberOfBits; i++)
            Bits[i] = Bits[i + 1];
    }

    private void strobeClockXNORO2M() {
        boolean[] BitsClone = Bits.clone();
        int j = 0;
        Bits[0] = BitsClone[NumberOfBits - 1];
        for (int i = 1; i < NumberOfBits; i++) {
            if ((Taps[j] == i) && (Taps[j] != NumberOfBits)) {
                Bits[i] = !(BitsClone[i - 1] ^ BitsClone[NumberOfBits - 1]);
                j++;
            } else
                Bits[i] = BitsClone[i - 1];
        }
    }

    private void strobeClockXNORM2O() {
        Bits[NumberOfBits] = true;
        for (int i = 0; i < Taps.length; i++)
            Bits[NumberOfBits] = !(Bits[NumberOfBits] ^ Bits[NumberOfBits - Taps[i]]);
        for (int i = 0; i < NumberOfBits; i++)
            Bits[i] = Bits[i + 1];
    }

    /**
     * strobeClock
     * strobes the clock once. Advances the LFSR given its current settings.
     * The results can be observed with a display function.
     */
    public void strobeClock() {
        if (Extended && preStrobeExtended())
            return;
        if (Feedback == FeedbackType.ONE2MANY)
            strobeClockXORO2M();
        else
            strobeClockXORM2O();
    }

    /**
     * strobeClock
     * strobes the clock strobes many times. Advances the LFSR given its current
     * settings. The results can be observed with a display function.
     */
    public void strobeClock(int strobes) {
        for (int i = 0; i < strobes; i++)
            strobeClock();
    }

    /**
     * CalculateSequenceLength
     * Calculates the sequence length of the LFSR with current settings. Unlike
     * getSequenceLength, this method does not check the previously calculated
     * and saved value.
     */
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

    private boolean areAllFalse(boolean[] array) {
        for (boolean b : array) if (b) return false;
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
