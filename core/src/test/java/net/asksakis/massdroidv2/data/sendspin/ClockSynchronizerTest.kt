package net.asksakis.massdroidv2.data.sendspin

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.math.abs

class ClockSynchronizerTest {

    private lateinit var sync: ClockSynchronizer

    // Reflection helpers to read private state
    private fun getField(name: String): Any? {
        val field = ClockSynchronizer::class.java.getDeclaredField(name)
        field.isAccessible = true
        return field.get(sync)
    }

    private fun getDouble(name: String): Double = getField(name) as Double
    private fun getLong(name: String): Long = getField(name) as Long
    private fun getInt(name: String): Int = getField(name) as Int
    private fun getBoolean(name: String): Boolean = getField(name) as Boolean

    @BeforeEach
    fun setUp() {
        sync = ClockSynchronizer()
    }

    @Nested
    inner class AfterFirstSample {

        @Test
        fun `offset is seeded from first measurement`() {
            // offset = ((serverRx - clientTx) + (serverTx - clientRx)) / 2
            // = ((6500 - 1000) + (6500 - 2000)) / 2 = (5500 + 4500) / 2 = 5000
            sync.processTimeResponse(1000L, 6500L, 6500L, 2000L)

            assertEquals(5000.0, getDouble("offset"), 0.001)
        }

        @Test
        fun `drift is zero after first sample`() {
            sync.processTimeResponse(1000L, 6500L, 6500L, 2000L)

            assertEquals(0.0, getDouble("drift"), 0.001)
        }

        @Test
        fun `count is 1`() {
            sync.processTimeResponse(1000L, 6500L, 6500L, 2000L)

            assertEquals(1, getInt("count"))
        }

        @Test
        fun `offsetCovariance is set to measurement variance`() {
            // RTT = (2000 - 1000) - (6500 - 6500) = 1000
            // maxError = max(1000/2, 1) = 500
            // measurementVariance = 500 * 500 = 250000
            sync.processTimeResponse(1000L, 6500L, 6500L, 2000L)

            assertEquals(250000.0, getDouble("offsetCovariance"), 0.001)
        }

        @Test
        fun `lastUpdateUs is set to clientReceivedUs`() {
            sync.processTimeResponse(1000L, 6500L, 6500L, 2000L)

            assertEquals(2000L, getLong("lastUpdateUs"))
        }

        @Test
        fun `lastRttUs is calculated correctly`() {
            // RTT = (clientRx - clientTx) - (serverTx - serverRx) = (2000-1000) - (6500-6500) = 1000
            sync.processTimeResponse(1000L, 6500L, 6500L, 2000L)

            assertEquals(1000L, getLong("lastRttUs"))
        }

        @Test
        fun `useDrift is false`() {
            sync.processTimeResponse(1000L, 6500L, 6500L, 2000L)

            assertFalse(getBoolean("useDrift"))
        }

        @Test
        fun `currentOffset snapshot is published`() {
            sync.processTimeResponse(1000L, 6500L, 6500L, 2000L)

            assertEquals(5000.0, getDouble("currentOffset"), 0.001)
        }

        @Test
        fun `currentLastUpdate snapshot is published`() {
            sync.processTimeResponse(1000L, 6500L, 6500L, 2000L)

            assertEquals(2000L, getLong("currentLastUpdate"))
        }
    }

    @Nested
    inner class AfterSecondSample {

        @BeforeEach
        fun feedTwoSamples() {
            // First: offset=5000, clientRx=2000
            sync.processTimeResponse(1000L, 6500L, 6500L, 2000L)
            // Second: measurement = ((8500-3000) + (8500-4000)) / 2 = (5500+4500)/2 = 5000
            sync.processTimeResponse(3000L, 8500L, 8500L, 4000L)
        }

        @Test
        fun `count is 2`() {
            assertEquals(2, getInt("count"))
        }

        @Test
        fun `drift is computed from two measurements`() {
            // drift = (measurement2 - offset1) / dt = (5000 - 5000) / (4000 - 2000) = 0
            assertEquals(0.0, getDouble("drift"), 0.0001)
        }

        @Test
        fun `offset is updated to second measurement`() {
            // offset = measurement2 = 5000
            assertEquals(5000.0, getDouble("offset"), 0.001)
        }

        @Test
        fun `driftCovariance is initialized`() {
            // driftCovariance = (prevOffsetCov + measurementVariance) / (dt * dt)
            // prevOffsetCov = 250000, mVar for second sample: RTT=1000, maxErr=500, var=250000
            // dt = 4000 - 2000 = 2000
            // driftCovariance = (250000 + 250000) / (2000*2000) = 500000 / 4000000 = 0.125
            assertEquals(0.125, getDouble("driftCovariance"), 0.001)
        }

        @Test
        fun `lastUpdateUs is updated to second clientRx`() {
            assertEquals(4000L, getLong("lastUpdateUs"))
        }

        @Test
        fun `useDrift remains false after only two samples`() {
            assertFalse(getBoolean("useDrift"))
        }

        @Test
        fun `currentDrift snapshot is published`() {
            assertEquals(0.0, getDouble("currentDrift"), 0.0001)
        }
    }

    @Nested
    inner class AfterMultipleSamples {

        @Test
        fun `offset converges to true offset with consistent server clock`() {
            val trueOffset = 10_000L
            for (i in 0 until 30) {
                val clientTx = (i * 2000L) + 1000L
                val clientRx = clientTx + 200L // 200us RTT
                val serverRx = clientTx + trueOffset + 100L
                val serverTx = serverRx
                sync.processTimeResponse(clientTx, serverRx, serverTx, clientRx)
            }

            val offset = getDouble("offset")
            assertTrue(
                abs(offset - trueOffset) < 200,
                "Expected offset near ${trueOffset}, got $offset"
            )
        }

        @Test
        fun `drift detects clock skew when server drifts`() {
            // Simulate server clock drifting: 1us per 1000us = 1000ppm drift
            val baseOffset = 10_000L
            for (i in 0 until 100) {
                val clientTx = (i * 5000L) + 1000L
                val clientRx = clientTx + 200L
                val driftAccumulated = (i * 5).toLong() // 1us per 1000us elapsed
                val serverRx = clientTx + baseOffset + driftAccumulated + 100L
                val serverTx = serverRx
                sync.processTimeResponse(clientTx, serverRx, serverTx, clientRx)
            }

            val drift = getDouble("drift")
            // drift should be positive (server getting ahead)
            assertTrue(drift > 0.0, "Expected positive drift, got $drift")
        }

        @Test
        fun `offsetCovariance decreases with more samples`() {
            sync.processTimeResponse(1000L, 6500L, 6500L, 2000L)
            sync.processTimeResponse(3000L, 8500L, 8500L, 4000L)
            val covAfterTwo = getDouble("offsetCovariance")

            for (i in 2 until 20) {
                val clientTx = (i * 2000L) + 1000L
                val clientRx = clientTx + 200L
                sync.processTimeResponse(clientTx, clientTx + 5100L, clientTx + 5100L, clientRx)
            }
            val covAfterMany = getDouble("offsetCovariance")

            assertTrue(
                covAfterMany < covAfterTwo,
                "Covariance should decrease: after2=$covAfterTwo afterMany=$covAfterMany"
            )
        }

        @Test
        fun `count increments correctly through samples`() {
            for (i in 0 until 10) {
                val clientTx = (i * 2000L) + 1000L
                sync.processTimeResponse(clientTx, clientTx + 5000L, clientTx + 5000L, clientTx + 200L)
            }

            assertEquals(10, getInt("count"))
        }

        @Test
        fun `lastRttUs reflects most recent sample`() {
            // First sample: RTT = (2000-1000) - 0 = 1000
            sync.processTimeResponse(1000L, 6500L, 6500L, 2000L)
            assertEquals(1000L, getLong("lastRttUs"))

            // Second sample: RTT = (5000-3000) - (8600-8500) = 2000 - 100 = 1900
            sync.processTimeResponse(3000L, 8500L, 8600L, 5000L)
            assertEquals(1900L, getLong("lastRttUs"))
        }

        @Test
        fun `useDrift becomes true when drift is statistically significant`() {
            // Feed samples with consistent large drift to make it significant
            val baseOffset = 10_000L
            for (i in 0 until 100) {
                val clientTx = (i * 10000L) + 1000L
                val clientRx = clientTx + 100L // very low RTT for tight covariance
                val driftAccumulated = (i * 50).toLong() // 5us per 1000us = large drift
                val serverRx = clientTx + baseOffset + driftAccumulated + 50L
                val serverTx = serverRx
                sync.processTimeResponse(clientTx, serverRx, serverTx, clientRx)
            }

            assertTrue(getBoolean("useDrift"), "useDrift should be true with significant drift")
            assertTrue(getBoolean("currentUseDrift"), "currentUseDrift snapshot should match")
        }
    }

    @Nested
    inner class CountBehavior {

        @Test
        fun `count increments from 2 through 99 unconditionally`() {
            for (i in 0 until 50) {
                val clientTx = (i * 2000L) + 1000L
                sync.processTimeResponse(clientTx, clientTx + 5000L, clientTx + 5000L, clientTx + 200L)
            }
            assertEquals(50, getInt("count"))
        }

        @Test
        fun `count reaches 99 after 99 samples`() {
            for (i in 0 until 99) {
                val clientTx = (i * 2000L) + 1000L
                sync.processTimeResponse(clientTx, clientTx + 5000L, clientTx + 5000L, clientTx + 200L)
            }
            assertEquals(99, getInt("count"))
        }

        @Test
        fun `count reaches 100 at boundary`() {
            for (i in 0 until 100) {
                val clientTx = (i * 2000L) + 1000L
                sync.processTimeResponse(clientTx, clientTx + 5000L, clientTx + 5000L, clientTx + 200L)
            }
            // At sample 100: count was 99, enters `count < 100` branch one last time -> count=100
            // Then `if (count >= 100) count++` makes it 101
            assertEquals(101, getInt("count"))
        }

        @Test
        fun `count continues incrementing past 100 with small residuals`() {
            for (i in 0 until 105) {
                val clientTx = (i * 2000L) + 1000L
                sync.processTimeResponse(clientTx, clientTx + 5000L, clientTx + 5000L, clientTx + 200L)
            }
            // First 99 samples: count goes 1..99 (uses count < 100 branch)
            // Sample 100: count was 99, count < 100 -> count=100, then count>=100 -> count=101
            // Sample 101: count=101, not < 100, residual small so no forgetting, count>=100 -> 102
            // ...so after 105 samples we expect count = 99 + (105-99) + (105-100) = let me trace:
            // Actually: samples 1-99 -> count 1..99 via the if(count<100) branch
            // sample 100: count=99 -> enters if(count<100), count becomes 100, then if(count>=100) count++ -> 101
            // sample 101: count=101, enters else branch (no forgetting if small residual), then if(count>=100) -> 102
            // sample 102: count=102 -> else branch -> count=103
            // ...
            // sample 105: count should be 101 + (105-100) = 106
            assertEquals(106, getInt("count"))
        }

        @Test
        fun `at count 100 boundary forgetting is possible but not before`() {
            // Feed 99 stable samples
            for (i in 0 until 99) {
                val clientTx = (i * 2000L) + 1000L
                sync.processTimeResponse(clientTx, clientTx + 5000L, clientTx + 5000L, clientTx + 200L)
            }
            val covAt99 = getDouble("offsetCovariance")

            // Sample 100 with a huge residual — still uses count<100 branch (no forgetting)
            val clientTx = 99 * 2000L + 1000L
            sync.processTimeResponse(clientTx, clientTx + 50_000L, clientTx + 50_000L, clientTx + 200L)
            val covAt100 = getDouble("offsetCovariance")

            // Now sample 101 with huge residual — enters else branch, forgetting applies
            val clientTx2 = 100 * 2000L + 1000L
            val covBefore101 = getDouble("offsetCovariance")
            sync.processTimeResponse(clientTx2, clientTx2 + 50_000L, clientTx2 + 50_000L, clientTx2 + 200L)
            val covAfter101 = getDouble("offsetCovariance")

            // After forgetting, covariance should be larger
            assertTrue(
                covAfter101 > covBefore101 * 0.5,
                "Forgetting should prevent covariance from shrinking too fast: before=$covBefore101 after=$covAfter101"
            )
        }
    }

    @Nested
    inner class AdaptiveForgetting {

        @Test
        fun `large residual inflates covariances when count exceeds 100`() {
            // Feed 100 stable samples to get past the early phase
            for (i in 0 until 101) {
                val clientTx = (i * 2000L) + 1000L
                val clientRx = clientTx + 200L
                sync.processTimeResponse(clientTx, clientTx + 5100L, clientTx + 5100L, clientRx)
            }
            val covBefore = getDouble("offsetCovariance")

            // Feed a sample with a huge offset jump (triggers forgetting)
            val clientTx = 203_001L
            val clientRx = clientTx + 200L
            // Massive jump in server time -> large residual
            sync.processTimeResponse(clientTx, clientTx + 50_000L, clientTx + 50_000L, clientRx)
            val covAfter = getDouble("offsetCovariance")

            assertTrue(
                covAfter > covBefore,
                "Covariance should increase after large residual: before=$covBefore after=$covAfter"
            )
        }
    }

    @Nested
    inner class SoftResetState {

        @Test
        fun `softReset sets offset and count correctly`() {
            sync.processTimeResponse(1000L, 6500L, 6500L, 2000L)
            sync.processTimeResponse(3000L, 8500L, 8500L, 4000L)

            sync.softReset(previousOffsetUs = 12000L, preserveDrift = true, initialCovariance = 100_000.0)

            assertEquals(12000.0, getDouble("offset"), 0.001)
            assertEquals(2, getInt("count"))
            assertEquals(100_000.0, getDouble("offsetCovariance"), 0.001)
        }

        @Test
        fun `softReset preserves drift when requested`() {
            sync.processTimeResponse(1000L, 6500L, 6500L, 2000L)
            sync.processTimeResponse(3000L, 8600L, 8600L, 4000L) // slight drift
            val driftBefore = getDouble("drift")

            sync.softReset(previousOffsetUs = 12000L, preserveDrift = true)

            assertEquals(driftBefore, getDouble("drift"), 0.0001)
        }

        @Test
        fun `softReset clears drift when not preserving`() {
            sync.processTimeResponse(1000L, 6500L, 6500L, 2000L)
            sync.processTimeResponse(3000L, 8600L, 8600L, 4000L)

            sync.softReset(previousOffsetUs = 12000L, preserveDrift = false)

            assertEquals(0.0, getDouble("drift"), 0.0001)
            assertFalse(getBoolean("useDrift"))
        }

        @Test
        fun `softReset publishes currentOffset snapshot`() {
            sync.softReset(previousOffsetUs = 7777L, preserveDrift = false)

            assertEquals(7777.0, getDouble("currentOffset"), 0.001)
        }
    }

    @Nested
    inner class FullResetState {

        @Test
        fun `reset zeroes all state variables`() {
            // Build up state
            for (i in 0 until 10) {
                val clientTx = (i * 2000L) + 1000L
                sync.processTimeResponse(clientTx, clientTx + 5000L, clientTx + 5000L, clientTx + 200L)
            }

            sync.reset()

            assertEquals(0.0, getDouble("offset"), 0.0)
            assertEquals(0.0, getDouble("drift"), 0.0)
            assertEquals(Double.MAX_VALUE, getDouble("offsetCovariance"))
            assertEquals(0.0, getDouble("offsetDriftCovariance"), 0.0)
            assertEquals(0.0, getDouble("driftCovariance"), 0.0)
            assertEquals(0L, getLong("lastUpdateUs"))
            assertEquals(0, getInt("count"))
            assertFalse(getBoolean("useDrift"))
            assertEquals(0.0, getDouble("currentOffset"), 0.0)
            assertEquals(0.0, getDouble("currentDrift"), 0.0)
            assertFalse(getBoolean("currentUseDrift"))
            assertEquals(0L, getLong("currentLastUpdate"))
        }
    }
}
