package com.tiriig.whatsdeleted.utility

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GeneralExtensionsTest {

    @Test
    fun groupName_stripsSenderAndMessageCount() {
        assertEquals("Family", "Family: Ali".groupName())
        assertEquals("Family", "Family (3 messages): Ali".groupName())
        assertEquals("Family", "Family (3 messages)".groupName())
        assertEquals("Family", "Family".groupName())
    }

    @Test
    fun isValidTitle_rejectsBundledMessageCounts() {
        assertFalse("3 new messages".isValidTitle())
        assertFalse("1 message".isValidTitle())
        assertTrue("see you at 3".isValidTitle())
    }

    @Test
    fun isDeletionNotice_matchesWhatsAppAndSignal() {
        assertTrue("This message was deleted".isDeletionNotice())
        assertTrue("This message was deleted.".isDeletionNotice())
        assertFalse("This message was deleted by admin".isDeletionNotice())
    }
}
