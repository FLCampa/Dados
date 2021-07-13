package br.edu.ifsp.scl.ads.pdm.dados

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import br.edu.ifsp.scl.ads.pdm.dados.SettingsActivity.Constantes.NUMERO_DADOS_ATRIBUTO
import br.edu.ifsp.scl.ads.pdm.dados.SettingsActivity.Constantes.NUMERO_FACES_ATRIBUTO
import br.edu.ifsp.scl.ads.pdm.dados.databinding.ActivityMainBinding
import kotlin.random.Random
import kotlin.random.nextInt

class MainActivity : AppCompatActivity() {

    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var geradorRandomico: Random

    private lateinit var settingsActivityLauncher: ActivityResultLauncher<Intent>
    private var numberOfDices: Int = 1
    private var numberOfFaces: Int = 6
    private lateinit var configuracoesSharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        configuracoesSharedPreferences = getSharedPreferences(SettingsActivity.Constantes.CONFIGURACOES_ARQUIVO, MODE_PRIVATE)
        geradorRandomico = Random(System.currentTimeMillis())

        activityMainBinding.jogarDadoBt.setOnClickListener {

            val firstResult: Int = geradorRandomico.nextInt(1..numberOfFaces)
            val msg = "A(s) face(s) sorteada(s) foi(ram) $firstResult".also { activityMainBinding.resultadoTv.text = it }
            val nomePrimeiraImagem = "dice_$firstResult"

            activityMainBinding.resultadoIv.setImageResource(
                resources.getIdentifier(nomePrimeiraImagem, "mipmap", packageName)
            )

            if (numberOfDices == 2) {
                val secondResult: Int = geradorRandomico.nextInt(1..numberOfFaces)
                "$msg e $secondResult".also { activityMainBinding.resultadoTv.text = it }
                val nomeSegundaImagem = "dice_$secondResult"

                activityMainBinding.segundoResultadoIv.setImageResource(
                    resources.getIdentifier(nomeSegundaImagem, "mipmap", packageName)
                )
                if (numberOfFaces <= 6) {
                    activityMainBinding.segundoResultadoIv.visibility = View.VISIBLE
                } else {
                    activityMainBinding.resultadoIv.visibility = View.GONE
                    activityMainBinding.segundoResultadoIv.visibility = View.GONE
                }
            }
            else if (numberOfFaces > 6) {
                activityMainBinding.resultadoIv.visibility = View.GONE
                activityMainBinding.segundoResultadoIv.visibility = View.GONE
            }
            else {
                activityMainBinding.segundoResultadoIv.visibility = View.GONE
            }
        }

        settingsActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                if (result.data != null) {
                    val configuracao: Configuracao? = result.data?.getParcelableExtra<Configuracao>(Intent.EXTRA_USER)
//                    numberOfDices = configuracao!!.numeroDados
//                    numberOfFaces = configuracao!!.numeroFaces
                    numberOfDices = configuracoesSharedPreferences.getInt(NUMERO_DADOS_ATRIBUTO, 1)
                    numberOfFaces = configuracoesSharedPreferences.getInt(NUMERO_FACES_ATRIBUTO, 6)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.settingsMi) {
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            settingsActivityLauncher.launch(settingsIntent)
            return true
        }
        return false
    }
}