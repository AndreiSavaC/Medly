package com.proiectpdm.plugins

import com.proiectpdm.model.*
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabases() {
    val driverClass=environment.config.property("storage.driverClassName").getString()
    val jdbcUrl=environment.config.property("storage.jdbcURL").getString()
    val db=Database.connect(provideDataSource(jdbcUrl,driverClass))

    transaction(db){

        SchemaUtils.create(Doctors,Pacients,Appointments,Categories,Symptoms)

        val categoriesWithSymptoms = mapOf(
            "Probleme Respiratorii" to listOf(
                "Tuse", "Dificultăți de respirație", "Respirație șuierătoare",
                "Durere în piept la respirație", "Secreții nazale abundente",
                "Congestie nazală", "Strănut frecvent", "Voce răgușită",
                "Lipsă de aer la efort minim", "Episod de sufocare"
            ),
            "Probleme Gastro-Intestinale" to listOf(
                "Dureri abdominale", "Greata", "Vărsături", "Diaree",
                "Constipație", "Balonare", "Arsuri la stomac (reflux gastric)",
                "Senzație de greutate după masă", "Pierderea apetitului",
                "Sânge în scaun", "Senzație de greață fără motiv clar",
                "Dificultăți la înghițire"
            ),
            "Probleme Cardiovasculare" to listOf(
                "Dureri în piept", "Palpitații", "Amețeală", "Pierderea cunoștinței",
                "Respirație dificilă în poziție culcată", "Umflarea picioarelor sau a gleznelor",
                "Oboseală extremă la eforturi mici", "Puls neregulat sau slab"
            ),
            "Probleme Musculo-Scheletale" to listOf(
                "Durere de spate", "Dureri articulare", "Dureri musculare",
                "Umflături articulare", "Limitare în mișcare", "Rigiditate musculară",
                "Slăbiciune musculară", "Senzație de crampe musculare", "Durere după efort fizic"
            ),
            "Probleme Neurologice" to listOf(
                "Cefalee (durere de cap)", "Amețeli", "Pierderi de echilibru",
                "Amorțeală sau furnicături", "Tremor", "Convulsii",
                "Slăbiciune bruscă într-o parte a corpului", "Tulburări de vorbire",
                "Pierderi temporare de memorie", "Probleme de concentrare", "Senzație de confuzie"
            ),
            "Probleme Dermatologice" to listOf(
                "Erupții cutanate", "Mâncărimi", "Roșeață", "Inflamație",
                "Leziuni deschise sau ulcere", "Piele uscată sau descuamată",
                "Căderea părului", "Pete pe piele (albe, maronii, roșii)",
                "Acnee severă", "Schimbări ale unghiilor (culoare sau structură)"
            ),
            "Probleme Urinare" to listOf(
                "Durere la urinare", "Senzație de arsură", "Urină cu sânge",
                "Nevoia frecventă de a urina", "Dificultăți în inițierea urinării",
                "Jet de urină slab sau intermitent", "Senzație de golire incompletă a vezicii",
                "Urină tulbure sau cu miros neplăcut"
            ),
            "Probleme Oftalmologice" to listOf(
                "Durere oculară", "Roșeață oculară", "Senzație de corp străin",
                "Vedere încețoșată", "Lăcrimare excesivă", "Sensibilitate la lumină",
                "Pierderea bruscă a vederii", "Mâncărime la nivelul ochilor"
            ),
            "Probleme ORL (Otorinolaringologice)" to listOf(
                "Durere în gât", "Dificultăți de înghițire", "Urechi înfundate",
                "Senzație de presiune în sinusuri", "Pierderea auzului",
                "Senzație de zgomot în urechi (tinitus)", "Ganglioni limfatici umflați"
            ),
            "Probleme Generale" to listOf(
                "Febră", "Oboseală extremă", "Pierdere bruscă în greutate",
                "Frisoane", "Transpirații nocturne", "Lipsa poftei de mâncare",
                "Slăbiciune generalizată", "Dureri musculare difuze",
                "Stare generală de rău (indispoziție)"
            ),
            "Probleme Emoționale și Psihologice" to listOf(
                "Anxietate", "Depresie", "Insomnie", "Stări de panică",
                "Gânduri negative", "Iritabilitate crescută",
                "Dificultăți de socializare", "Oboseală mentală"
            ),
            "Probleme Endocrine și Hormonale" to listOf(
                "Senzație de sete excesivă", "Urinare frecventă", "Creștere rapidă în greutate",
                "Senzație de frig constant", "Pierdere de păr", "Transpirație excesivă",
                "Ciclu menstrual neregulat", "Probleme legate de libido"
            )
        )

        categoriesWithSymptoms.forEach { (categoryName, symptoms) ->
            val categoryId = Categories.insert  {
                it[name] = categoryName
            } get Categories.id

            symptoms.forEach { symptomName ->
                Symptoms.insert {
                    it[name] = symptomName
                    it[Symptoms.categoryId] = categoryId
                }
            }
        }
    }
}


private fun provideDataSource(url:String,driverClass:String):HikariDataSource{
    val hikariConfig= HikariConfig().apply {
        driverClassName=driverClass
        jdbcUrl=url
        maximumPoolSize=3
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    }
    return HikariDataSource(hikariConfig)
}

suspend fun <T> dbQuery(block:suspend ()->T):T{
    return newSuspendedTransaction(Dispatchers.IO) { block() }
}