package com.example.androidapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.androidapp.models.Category
import com.example.androidapp.models.Symptom


class SymptomsActivity : AppCompatActivity() {

    private lateinit var confirmButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_symptoms)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        }

        val selectedDate = intent.getStringExtra("selectedDate")
        val selectedHour = intent.getStringExtra("selectedHour")

        val categories = listOf(
            Category("Probleme Respiratorii", listOf(
                Symptom("Tuse"),
                Symptom("Dificultăți de respirație"),
                Symptom("Respirație șuierătoare"),
                Symptom("Durere în piept la respirație"),
                Symptom("Secreții nazale abundente"),
                Symptom("Congestie nazală"),
                Symptom("Strănut frecvent"),
                Symptom("Voce răgușită"),
                Symptom("Lipsă de aer la efort minim"),
                Symptom("Episod de sufocare")
            )),
            Category("Probleme Gastro-Intestinale", listOf(
                Symptom("Dureri abdominale"),
                Symptom("Greață"),
                Symptom("Vărsături"),
                Symptom("Diaree"),
                Symptom("Constipație"),
                Symptom("Balonare"),
                Symptom("Arsuri la stomac (reflux gastric)"),
                Symptom("Senzație de greutate după masă"),
                Symptom("Pierderea apetitului"),
                Symptom("Sânge în scaun"),
                Symptom("Senzație de greață fără motiv clar"),
                Symptom("Dificultăți la înghițire")
            )),
            Category("Probleme Cardiovasculare", listOf(
                Symptom("Dureri în piept"),
                Symptom("Palpitații"),
                Symptom("Amețeală"),
                Symptom("Pierderea cunoștinței"),
                Symptom("Respirație dificilă în poziție culcată"),
                Symptom("Umflarea picioarelor sau a gleznelor"),
                Symptom("Oboseală extremă la eforturi mici"),
                Symptom("Puls neregulat sau slab")
            )),
            Category("Probleme Musculo-Scheletale", listOf(
                Symptom("Durere de spate"),
                Symptom("Dureri articulare"),
                Symptom("Dureri musculare"),
                Symptom("Umflături articulare"),
                Symptom("Limitare în mișcare"),
                Symptom("Rigiditate musculară"),
                Symptom("Slăbiciune musculară"),
                Symptom("Senzație de crampe musculare"),
                Symptom("Durere după efort fizic")
            )),
            Category("Probleme Neurologice", listOf(
                Symptom("Cefalee (durere de cap)"),
                Symptom("Amețeli"),
                Symptom("Pierderi de echilibru"),
                Symptom("Amorțeală sau furnicături"),
                Symptom("Tremor"),
                Symptom("Convulsii"),
                Symptom("Slăbiciune bruscă într-o parte a corpului"),
                Symptom("Tulburări de vorbire"),
                Symptom("Pierderi temporare de memorie"),
                Symptom("Probleme de concentrare"),
                Symptom("Senzație de confuzie")
            )),
            Category("Probleme Dermatologice", listOf(
                Symptom("Erupții cutanate"),
                Symptom("Mâncărimi"),
                Symptom("Roșeață"),
                Symptom("Inflamație"),
                Symptom("Leziuni deschise sau ulcere"),
                Symptom("Piele uscată sau descuamată"),
                Symptom("Căderea părului"),
                Symptom("Pete pe piele (albe, maronii, roșii)"),
                Symptom("Acnee severă"),
                Symptom("Schimbări ale unghiilor (culoare sau structură)")
            )),
            Category("Probleme Urinare", listOf(
                Symptom("Durere la urinare"),
                Symptom("Senzație de arsură"),
                Symptom("Urină cu sânge"),
                Symptom("Nevoia frecventă de a urina"),
                Symptom("Dificultăți în inițierea urinării"),
                Symptom("Jet de urină slab sau intermitent"),
                Symptom("Senzație de golire incompletă a vezicii"),
                Symptom("Urină tulbure sau cu miros neplăcut")
            )),
            Category("Probleme Oftalmologice", listOf(
                Symptom("Durere oculară"),
                Symptom("Roșeață oculară"),
                Symptom("Senzație de corp străin"),
                Symptom("Vedere încețoșată"),
                Symptom("Lăcrimare excesivă"),
                Symptom("Sensibilitate la lumină"),
                Symptom("Pierderea bruscă a vederii"),
                Symptom("Mâncărime la nivelul ochilor")
            )),
            Category("Probleme ORL (Otorinolaringologice)", listOf(
                Symptom("Durere în gât"),
                Symptom("Dificultăți de înghițire"),
                Symptom("Urechi înfundate"),
                Symptom("Senzație de presiune în sinusuri"),
                Symptom("Pierderea auzului"),
                Symptom("Senzație de zgomot în urechi (tinitus)"),
                Symptom("Ganglioni limfatici umflați")
            )),
            Category("Probleme Generale", listOf(
                Symptom("Febră"),
                Symptom("Oboseală extremă"),
                Symptom("Pierdere bruscă în greutate"),
                Symptom("Frisoane"),
                Symptom("Transpirații nocturne"),
                Symptom("Lipsa poftei de mâncare"),
                Symptom("Slăbiciune generalizată"),
                Symptom("Dureri musculare difuze"),
                Symptom("Stare generală de rău (indispoziție)")
            )),
            Category("Probleme Emoționale și Psihologice", listOf(
                Symptom("Anxietate"),
                Symptom("Depresie"),
                Symptom("Insomnie"),
                Symptom("Stări de panică"),
                Symptom("Gânduri negative"),
                Symptom("Iritabilitate crescută"),
                Symptom("Dificultăți de socializare"),
                Symptom("Oboseală mentală")
            )),
            Category("Probleme Endocrine și Hormonale", listOf(
                Symptom("Senzație de sete excesivă"),
                Symptom("Urinare frecventă"),
                Symptom("Creștere rapidă în greutate"),
                Symptom("Senzație de frig constant"),
                Symptom("Pierdere de păr"),
                Symptom("Transpirație excesivă"),
                Symptom("Ciclu menstrual neregulat"),
                Symptom("Probleme legate de libido")
            ))
        )
        confirmButton = findViewById(R.id.confirmButton)

        val layout = findViewById<LinearLayout>(R.id.categoriesLayout)

        val inflater = LayoutInflater.from(this)

        val colorStateList = resources.getColorStateList(R.color.checkbox_color, theme)

        var isAnySymptomSelected = false

        categories.forEach { category ->
            val categoryView = inflater.inflate(R.layout.item_category, layout, false)

            val categoryTitle = categoryView.findViewById<TextView>(R.id.categoryTitle)
            categoryTitle.text = category.title

            val symptomLayout = categoryView.findViewById<LinearLayout>(R.id.symptomLayout)
            category.symptoms.forEach { symptom ->
                val symptomCheckBox = CheckBox(this)
                symptomCheckBox.text = symptom.name
                symptomCheckBox.buttonTintList = colorStateList
                symptomCheckBox.setOnCheckedChangeListener { _, isChecked ->
                    symptom.isSelected = isChecked
                    isAnySymptomSelected = categories.any { category ->
                        category.symptoms.any { it.isSelected }
                    }
                    updateConfirmButtonState(isAnySymptomSelected)
                }
                symptomLayout.addView(symptomCheckBox)
            }

            layout.addView(categoryView)
        }

        findViewById<Button>(R.id.confirmButton).setOnClickListener {
            val selectedSymptoms = mutableListOf<String>()
            val selectedCategories = mutableListOf<String>()

            categories.forEach { category ->
                val selectedInCategory = mutableListOf<String>()
                category.symptoms.forEach { symptom ->
                    if (symptom.isSelected) {
                        selectedInCategory.add(symptom.name)
                    }
                }
                if (selectedInCategory.isNotEmpty()) {
                    selectedCategories.add(category.title)
                    selectedSymptoms.add("${category.title}: ${selectedInCategory.joinToString(", ")}")
                }
            }

            val intent = Intent(this, ConfirmAppointmentActivity::class.java)
            intent.putStringArrayListExtra("selectedSymptoms", ArrayList(selectedSymptoms))
            intent.putExtra("selectedDate", selectedDate)
            intent.putExtra("selectedHour", selectedHour)
            startActivity(intent)
        }
    }

    private fun updateConfirmButtonState(isAnySymptomSelected: Boolean) {
        if (isAnySymptomSelected) {
            confirmButton.isEnabled = true
            confirmButton.backgroundTintList = resources.getColorStateList(R.color.primaryColor, theme)
        } else {
            confirmButton.isEnabled = false
            confirmButton.backgroundTintList = resources.getColorStateList(R.color.gray_200, theme)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}