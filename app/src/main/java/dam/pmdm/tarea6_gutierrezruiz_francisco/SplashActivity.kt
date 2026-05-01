package dam.pmdm.tarea6_gutierrezruiz_francisco

import android.content.Intent
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val textView = findViewById<TextView>(R.id.textoBienvenida)

        textView.viewTreeObserver.addOnGlobalLayoutListener {
            val anchoTexto = textView.measuredWidth.toFloat()

            if (anchoTexto > 0) {
                val shader = LinearGradient(
                    0f, 0f, anchoTexto, 0f,
                    intArrayOf(
                        ContextCompat.getColor(this, R.color.inicioLetras),
                        ContextCompat.getColor(this, R.color.finLetras),
                    ),
                    null,
                    Shader.TileMode.CLAMP
                )

                textView.paint.shader = shader
            }
        }

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2000)
    }
}