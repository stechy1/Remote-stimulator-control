package cz.zcu.fav.remotestimulatorcontrol.util;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;

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

    @Test
    public void intFromBytesTest() throws Exception {
        final byte[] src = {0x00, 0x00, 0x10, 0x15};
        final int result = BitUtils.intFromBytes(src, 0);
        assertEquals("Chyba, číslo nebylo správně rozpoznáno", 4117, result);
    }

    @Test
    public void intToBytesTest() throws Exception {
        final int value = 4117;
        final byte[] dest = new byte[4];
        BitUtils.intToBytes(value, dest, 0);
        final byte[] expected = {0x00, 0x00, 0x10, 0x15};
        assertArrayEquals("Chyba, číslo nebylo správně překonvertováno", expected, dest);
    }

    @Test
    public void bytesToStringTest() throws Exception {
        final byte[] bytes = {0x15, 0x20, 0x36};
        final String expected = "15 20 36";
        assertEquals("Chyba, textový výpis se neshoduje", expected, BitUtils.byteArrayToHex(bytes));
    }
}
