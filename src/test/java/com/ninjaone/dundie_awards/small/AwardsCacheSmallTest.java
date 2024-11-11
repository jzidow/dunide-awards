package com.ninjaone.dundie_awards.small;

import com.ninjaone.dundie_awards.AwardsCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AwardsCacheSmallTest {

    private AwardsCache awardsCache;

    @BeforeEach
    void setUp() {
        awardsCache = new AwardsCache();
        awardsCache.setTotalAwards(0);  // Reset totalAwards to ensure a clean state for each test
    }

    @Test
    @DisplayName("setTotalAwards() set totalAwards to specified value")
    void testSetTotalAwards() {
        awardsCache.setTotalAwards(100);
        assertEquals(100, awardsCache.getTotalAwards(), "Total awards should be set to 100");
    }

    @Test
    @DisplayName("getTotalAwards() return the correct totalAwards value")
    void testGetTotalAwards() {
        awardsCache.setTotalAwards(50);
        assertEquals(50, awardsCache.getTotalAwards(), "Total awards should be 50");
    }

    @Test
    @DisplayName("addAwards()  increase totalAwards by specified amount")
    void testAddAwards() {
        awardsCache.setTotalAwards(10);
        awardsCache.addAwards(5);
        assertEquals(15, awardsCache.getTotalAwards(), "Total awards should be increased to 15");
    }

    @Test
    @DisplayName("subtractAwards() decrease totalAwards")
    void testSubtractAwards() {
        awardsCache.setTotalAwards(20);
        awardsCache.subtractAwards(5);
        assertEquals(15, awardsCache.getTotalAwards(), "Total awards should be decreased to 15");
    }
}
