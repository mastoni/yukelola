package id.yukelola.core.designsystem.adaptive

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class YukelolaAdaptiveTest {

    @Test
    fun testWindowWidthSizeClassEnum() {
        assertEquals(3, WindowWidthSizeClass.values().size)
        assertNotNull(WindowWidthSizeClass.valueOf("COMPACT"))
        assertNotNull(WindowWidthSizeClass.valueOf("MEDIUM"))
        assertNotNull(WindowWidthSizeClass.valueOf("EXPANDED"))
    }

    @Test
    fun testDevicePostureEnum() {
        assertEquals(4, DevicePosture.values().size)
        assertNotNull(DevicePosture.valueOf("PHONE_PORTRAIT"))
        assertNotNull(DevicePosture.valueOf("PHONE_LANDSCAPE"))
        assertNotNull(DevicePosture.valueOf("TABLET_PORTRAIT"))
        assertNotNull(DevicePosture.valueOf("TABLET_LANDSCAPE"))
    }
}
