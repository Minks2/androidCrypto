package minks2.com.github.monitorcrypto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import minks2.com.github.monitorcrypto.ui.theme.MonitorCryptoTheme
import androidx.appcompat.widget.Toolbar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //trasformando objeto xml em kotlin, toolbarMain é o nome que eu dou pra variavel, ela herda de Toolbar,
        //vira um objeto, ai atribui a um arquivo xml
        val toolbarMain:Toolbar = findViewById(R.id.toolbar_main)
        configureToolbar(toolbarMain)
    }
         private fun configureToolbar (toolbar: Toolbar){
             setSupportActionBar(toolbar)
             toolbar.setTitleTextColor(getColor(R.color.white))
             supportActionBar?.setTitle(getText(R.string.app_title))
             supportActionBar?.setBackgroundDrawable(getDrawable(R.color.primary))
         }


}