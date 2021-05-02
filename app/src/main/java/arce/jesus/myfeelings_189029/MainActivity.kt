package arce.jesus.myfeelings_189029

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    var jsonFile: JSONFile? = null
    var veryHappy = 0.0f
    var happy = 0.0f
    var neutral = 0.0f
    var sad = 0.0f
    var verySad = 0.0f
    var data: Boolean = false
    var lista = ArrayList<Emociones>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        jsonFile = JSONFile()
        fetchingData()
        if (!data) {
            var emociones = ArrayList<Emociones>()
            val fondo = CustomCircleDrawable(this, emociones)
            graph.background = fondo
            graphVeryHappy.background =
                CustomBarDrawable(
                    this,
                    Emociones("Muy feliz", 0.0f, R.color.mustard, veryHappy.toInt())
                )
            graphVeryHappy.background =
                CustomBarDrawable(
                    this,
                    Emociones("Feliz", 0.0f, R.color.colorAccent, happy.toInt())
                )
            graphVeryHappy.background =
                CustomBarDrawable(
                    this,
                    Emociones("Neutral", 0.0f, R.color.greenie, neutral.toInt())
                )
            graphVeryHappy.background =
                CustomBarDrawable(this, Emociones("Triste", 0.0f, R.color.blue, sad.toInt()))
            graphVeryHappy.background =
                CustomBarDrawable(
                    this,
                    Emociones("Muy triste", 0.0f, R.color.deepblue, verySad.toInt())
                )
        } else {
            actualizarGrafica()
            iconoMayoria()
        }

        btnGuardar.setOnClickListener {
            guardar()
        }

        btn_veryHappy.setOnClickListener {
            veryHappy++
            iconoMayoria()
            actualizarGrafica()
        }

        btn_happy.setOnClickListener {
            happy++
            iconoMayoria()
            actualizarGrafica()
        }
        btn_neutral.setOnClickListener {
            neutral++
            iconoMayoria()
            actualizarGrafica()
        }
        btn_sad.setOnClickListener {
            sad++
            iconoMayoria()
            actualizarGrafica()
        }

        btn_verySad.setOnClickListener {
            verySad++
            iconoMayoria()
            actualizarGrafica()
        }
    }

    fun fetchingData() {
        try {
            var json: String = jsonFile?.getData(this) ?: ""
            if (json != "") {
                this.data = true
                var jsonArray: JSONArray = JSONArray(json)

                this.lista = parseJson(jsonArray)

                lista.forEach { i ->
                    when (i.nombre) {
                        "Muy feliz" -> veryHappy = i.total.toFloat()
                        "Feliz" -> happy = i.total.toFloat()
                        "Neutral" -> neutral = i.total.toFloat()
                        "Triste" -> sad = i.total.toFloat()
                        "Muy triste" -> verySad = i.total.toFloat()
                    }
                }
            } else {
                this.data = false
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun parseJson(jsonArray: JSONArray): ArrayList<Emociones> {
        var lista = ArrayList<Emociones>()

        for (i in 0..jsonArray.length()) {
            try {
                val nombre = jsonArray.getJSONObject(i).getString("nombre")
                val porcentaje = jsonArray.getJSONObject(i).getDouble("porcentaje").toFloat()
                val color = jsonArray.getJSONObject(i).getInt("color")
                val total = jsonArray.getJSONObject(i).getDouble("total").toFloat()
                var emocion = Emociones(nombre, porcentaje, color, total.toInt())
                lista.add(emocion)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return lista
    }

    fun actualizarGrafica() {
        val total = veryHappy + happy + neutral + verySad + sad

        var pVH: Float = (veryHappy * 100 / total)
        var pH: Float = (happy * 100 / total)
        var pN: Float = (neutral * 100 / total)
        var pS: Float = (sad * 100 / total)
        var pVS: Float = (verySad * 100 / total)

        Log.d("porcentajes", "very happy" + pVH)
        Log.d("porcentajes", "happy" + pH)
        Log.d("porcentajes", "neutral" + pN)
        Log.d("porcentajes", "sad" + pS)
        Log.d("porcentajes", "very sad" + pVS)

        lista.clear()
        lista.add(Emociones("Muy feliz", pVH, R.color.mustard, veryHappy.toInt()))
        lista.add(Emociones("Feliz", pH, R.color.colorAccent, happy.toInt()))
        lista.add(Emociones("Neutral", pN, R.color.greenie, neutral.toInt()))
        lista.add(Emociones("Triste", pS, R.color.blue, sad.toInt()))
        lista.add(Emociones("Muy triste", pVS, R.color.deepblue, verySad.toInt()))

        val fondo = CustomCircleDrawable(this, lista)

        graphVeryHappy.background = CustomBarDrawable(this, lista[0])
        graphHappy.background = CustomBarDrawable(this, lista[1])
        graphNeutral.background = CustomBarDrawable(this, lista[2])
        graphTriste.background = CustomBarDrawable(this, lista[3])
        graphVerySad.background = CustomBarDrawable(this, lista[4])

        graph.background = fondo
    }

    fun iconoMayoria() {
        if (veryHappy > happy && veryHappy > neutral && veryHappy > sad && veryHappy > verySad) {
            icon.setImageResource(R.drawable.ic_very_happy)
        } else if (happy > veryHappy && happy > neutral && happy > sad && happy > verySad) {
            icon.setImageResource(R.drawable.ic_happy)
        } else if (neutral > veryHappy && neutral > happy && neutral > sad && neutral > verySad) {
            icon.setImageResource(R.drawable.ic_neutral)
        } else if (sad > veryHappy && sad > neutral && sad > happy && sad > verySad) {
            icon.setImageResource(R.drawable.ic_sad)
        } else if (verySad > veryHappy && verySad > neutral && verySad > sad && verySad > happy) {
            icon.setImageResource(R.drawable.ic_verysad)
        }
    }

    fun guardar() {
        var jsonArray = JSONArray()
        var o: Int = 0
        lista.forEach { i ->
            Log.d("objetos", i.toString())
            var j: JSONObject = JSONObject()
            j.put("nombre", i.nombre)
            j.put("porcentaje", i.porcentaje)
            j.put("color", i.color)
            j.put("total", i.total)

            jsonArray.put(o, j)
            o++
        }

        jsonFile?.saveData(this, jsonArray.toString())

        Toast.makeText(this, "Datos guardados", Toast.LENGTH_SHORT).show()
    }

}