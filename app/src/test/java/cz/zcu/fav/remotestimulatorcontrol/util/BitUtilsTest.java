package cz.zcu.fav.remotestimulatorcontrol.util;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Testovací třída třídy {@link BitUtils}
 */
public class BitUtilsTest {

    private static final int SOME_FLAG = 1 << 5;

    private int original;

    @Before
    public void setUp() throws Exception {
        original = 0;
    }

    @Test(expected = IllegalAccessException.class)
    public void testConstructorNegative() throws Exception {
        BitUtils.class.newInstance();
    }

    @Test
    public void bitTest1() throws Exception {
        original = BitUtils.setBit(original, SOME_FLAG, true);

        assertTrue("Chyba, příznak se nenastavil", BitUtils.isBitSet(original, SOME_FLAG));
    }

    @Test
    public void bitTest2() throws Exception {
        original = SOME_FLAG;
        original = BitUtils.clearBit(original, SOME_FLAG);

        assertFalse("Chyba, přiznak se nezrušil", BitUtils.isBitSet(original, SOME_FLAG));
    }
}
