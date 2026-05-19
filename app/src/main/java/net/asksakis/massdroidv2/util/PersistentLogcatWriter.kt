package net.asksakis.massdroidv2.util

import android.content.Context
import android.util.Log
import java.io.File

/**
 * Captures the device's logcat stream to rotating files inside the app's
 * external-files dir. logcat itself does the rotation via the `-f`, `-r`, and
 * `-n` options, so the overhead is a single forked process plus disk I/O.
 *
 * Files land at:
 *   /sdcard/Android/data/<package>/files/logs/app.log[.1..N]
 *
 * Total budget ≈ 10 MB × 10 = 100 MB, which is several hours of debug logs in
 * normal use and survives across logcat ring-buffer flushes. Pull them with:
 *   adb pull /sdcard/Android/data/<package>/files/logs/ ./logs/
 *
 * Lifecycle: started once from MassDroidApp.onCreate(). The forked process
 * dies with the app process (no separate stop path needed; if the app is
 * killed, the child terminates too).
 */
object PersistentLogcatWriter {
    private const val TAG = "PersistentLog"
    private const val MAX_KB_PER_FILE = 10_240
    private const val MAX_FILE_COUNT = 10

    @Volatile private var process: Process? = null

    fun start(context: Context) {
        if (process != null) return
        val logsDir = File(context.getExternalFilesDir(null), "logs")
        if (!logsDir.exists() && !logsDir.mkdirs()) {
            Log.w(TAG, "Could not create logs dir at ${logsDir.absolutePath}")
            return
        }
        val target = File(logsDir, "app.log").absolutePath
        try {
            // -v threadtime: include thread id + level + tag, matches the
            //   default `adb logcat` formatting users are used to seeing.
            // -r / -n: rotate at MAX_KB_PER_FILE per file, keep MAX_FILE_COUNT
            //   files. logcat opens the next slot automatically when the
            //   current one passes the size threshold.
            // -f: write to file path (logcat handles the file rotation
            //   bookkeeping internally — no need for us to babysit it).
            process = Runtime.getRuntime().exec(
                arrayOf(
                    "logcat",
                    "-v", "threadtime",
                    "-f", target,
                    "-r", MAX_KB_PER_FILE.toString(),
                    "-n", MAX_FILE_COUNT.toString(),
                )
            )
            Log.d(TAG, "Persistent logcat writer started at $target ($MAX_KB_PER_FILE KB × $MAX_FILE_COUNT)")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start persistent logcat writer: ${e.message}")
        }
    }
}
