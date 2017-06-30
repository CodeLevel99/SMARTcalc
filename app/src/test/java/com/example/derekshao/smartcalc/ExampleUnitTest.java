package com.example.derekshao.smartcalc;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.example.derekshao.smartcalc.MainActivity;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    MainActivity mainActivity = new MainActivity();

    private final static String BedmasCheck = "9+3x5-5";

    @Test
    public void BEDMASCheck() {

        assertEquals(mainActivity.calculate(BedmasCheck), "19");
    }
}