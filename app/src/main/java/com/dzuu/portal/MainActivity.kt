
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import fi.iki.elonen.NanoHTTPD
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private var webServer: LocalWebServer? = null
    private var isRunning = false

    private lateinit class LocalWebServer(val activity: MainActivity, port: Int) : NanoHTTPD(port) {
        override fun serve(session: IHTTPSession): Response {
            val uri = session.uri
            activity.runOnUiThread {
                activity.appendLog("Target akses: $uri")
            }

            // Ambil HTML dari inputan user secara real-time
            var htmlContent = ""
            activity.runOnUiThread {
                val edt = activity.findViewById<EditText>(R.id.edtHtml)
                htmlContent = edt.text.toString()
            }

            return newFixedLengthResponse(Response.Status.OK, "text/html", htmlContent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnToggle = findViewById<Button>(R.id.btnToggle)
        val txtStatus = findViewById<TextView>(R.id.txtStatus)

        btnToggle.setOnClickListener {
            if (!isRunning) {
                try {
                    // Jalankan server di port 8080 (bisa diakses via http://IP_HP:8080)
                    webServer = LocalWebServer(this, 8080)
                    webServer?.start()
                    isRunning = true
                    btnToggle.text = "STOP SERVER"
                    btnToggle.setBackgroundColor(android.graphics.Color.parseColor("#ef4444"))
                    txtStatus.text = "Status Server: RUNNING (Port 8080)"
                    txtStatus.setTextColor(android.graphics.Color.parseColor("#22c55e"))
                    appendLog("Server berhasil aktif di port 8080!")
                } catch (e: IOException) {
                    appendLog("Gagal start server: ${e.message}")
                }
            } else {
                webServer?.stop()
                isRunning = false
                btnToggle.text = "START SERVER"
                btnToggle.setBackgroundColor(android.graphics.Color.parseColor("#38bdf8"))
                txtStatus.text = "Status Server: OFF"
                txtStatus.setTextColor(android.graphics.Color.parseColor("#ef4444"))
                appendLog("Server dimatikan.")
            }
        }
    }

    private fun appendLog(text: String) {
        val txtLogs = findViewById<TextView>(R.id.txtLogs)
        txtLogs.append("> $text\n")
    }

    override fun onDestroy() {
        super.onDestroy()
        webServer?.stop()
    }
}
