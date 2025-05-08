package minks2.com.github.monitorcrypto

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import minks2.com.github.monitorcrypto.service.MercadoBitcoinServiceFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private val priceHistory = mutableListOf<Float>() // Lista para armazenar o histórico de preços
    private lateinit var priceChart: LineChart // Referência ao gráfico

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configura a toolbar
        val toolbarMain: Toolbar = findViewById(R.id.toolbar_main)
        configureToolbar(toolbarMain)

        // Inicializao gráfico
        priceChart = findViewById(R.id.price_chart)
        setupChart()

        // Configura o botão Refresh
        val btnRefresh: Button = findViewById(R.id.btn_refresh)
        btnRefresh.setOnClickListener {
            makeRestCall()
        }
    }

    private fun configureToolbar(toolbar: Toolbar) {
        supportActionBar?.setTitle(getText(R.string.app_title))
        supportActionBar?.setBackgroundDrawable(getDrawable(R.color.primary))
    }

    private fun setupChart() {
        // Configurações gráfico
        priceChart.description.isEnabled = false
        priceChart.setTouchEnabled(true)
        priceChart.isDragEnabled = true
        priceChart.setScaleEnabled(true)
        priceChart.setPinchZoom(true)

        // Configura eixo X - número de entradas
        priceChart.xAxis.setDrawGridLines(false)
        priceChart.xAxis.setDrawLabels(true)

        // Configura eixo Y
        priceChart.axisLeft.setDrawGridLines(false)
        priceChart.axisRight.isEnabled = false

        // Inicializa o gráfico com dados vazios
        val lineData = LineData()
        priceChart.data = lineData
        priceChart.invalidate()
    }

    private fun updateChart(newPrice: Float) {
        priceHistory.add(newPrice)

        // Limita o número de pontos no gráfico para evitar sobrecarga
        if (priceHistory.size > 50) {
            priceHistory.removeAt(0)
        }

        // Criar entradas para o gráfico
        val entries = priceHistory.mapIndexed { index, price ->
            Entry(index.toFloat(), price)
        }

        // Criar o conjunto de dados
        val dataSet = LineDataSet(entries, "Preço do Bitcoin (BRL)")
        dataSet.color = android.graphics.Color.BLUE
        dataSet.setDrawCircles(true)
        dataSet.setDrawValues(false)

        // Atualiza dados do gráfico
        val lineData = LineData(dataSet)
        priceChart.data = lineData
        priceChart.invalidate() // Redesenhar o gráfico
    }

    private fun makeRestCall() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val service = MercadoBitcoinServiceFactory().create()
                val response = service.getTicker()

                if (response.isSuccessful) {
                    val tickerResponse = response.body()

                    // Atualiza os componentes TextView
                    val lblValue: TextView = findViewById(R.id.lbl_value)
                    val lblDate: TextView = findViewById(R.id.lbl_date)

                    val lastValue = tickerResponse?.ticker?.last?.toDoubleOrNull()
                    if (lastValue != null) {
                        val numberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
                        lblValue.text = numberFormat.format(lastValue)

                        // Adiciona o preço ao gráfico
                        updateChart(lastValue.toFloat())
                    }

                    val date = tickerResponse?.ticker?.date?.let { Date(it * 1000L) }
                    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                    lblDate.text = sdf.format(date)

                } else {
                    val errorMessage = when (response.code()) {
                        400 -> "Bad Request"
                        401 -> "Unauthorized"
                        403 -> "Forbidden"
                        404 -> "Not Found"
                        else -> "Unknown error"
                    }
                    Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Falha na chamada: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}