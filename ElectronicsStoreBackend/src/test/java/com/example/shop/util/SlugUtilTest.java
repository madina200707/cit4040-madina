package com.example.shop.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SlugUtilTest {
    @Test
    void makesUrlFriendlySlugs() {
        assertEquals("samsung-galaxy-s24", SlugUtil.toSlug("Samsung Galaxy S24"));
        assertEquals("tv-home-cinema", SlugUtil.toSlug("  TV & Home   Cinema! "));
        assertEquals("cafe", SlugUtil.toSlug("Café"));
    }
}
