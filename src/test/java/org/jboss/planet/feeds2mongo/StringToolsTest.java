package org.jboss.planet.feeds2mongo;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test for {@link StringTools}
 */
public class StringToolsTest {

    @Test
    public void testConvertTitleToCode() {
        Assert.assertEquals("one_two", StringTools.title2Code("One Two"));
        Assert.assertEquals("one_two", StringTools.title2Code("    One    Two   "));
        Assert.assertEquals("one_two", StringTools.title2Code("One_Two"));
        Assert.assertEquals("one_two", StringTools.title2Code("_One Two_"));
        Assert.assertEquals("one_two", StringTools.title2Code("One  ___ Two"));
    }

}
