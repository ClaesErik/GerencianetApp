package com.claeserik.gerencianetapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import br.com.gerencianet.gnsdk.config.Config
import br.com.gerencianet.gnsdk.interfaces.IGnListener
import br.com.gerencianet.gnsdk.lib.Endpoints
import br.com.gerencianet.gnsdk.models.*
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity(), IGnListener {

    private lateinit var config: Config;
    private lateinit var creditCard: CreditCard;
    private lateinit var gnClient: Endpoints;
    private lateinit var pay_button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //TODO: TEM Q FAZER AQUI

        config = Config()
        config.accountCode = "20298f5270c938cd428a1efcaf2df200";
        config.isSandbox = false;

        gnClient = Endpoints(config, this);

        pay_button = findViewById<Button>(R.id.pay_button)
        this.pay_button.setOnClickListener {
            receberToken();
        }

    }


    fun receberToken(){

        creditCard = CreditCard()
        creditCard.cvv = "145"
        creditCard.number = "5130629175579380"
        creditCard.expirationMonth = "01"
        creditCard.expirationYear = "2025"
        creditCard.brand = "mastercard"


        runBlocking {
           async { getToken() }.await()
        }
//TODO: ADICIONAR TAL FUNCAO

    }

    suspend fun getToken(){
        gnClient.getPaymentToken(creditCard)
    }

    override fun onInstallmentsFetched(p0: PaymentData?) {
        var insta: List<Installment> = p0!!.installments;
        for(installment in insta) {
            Log.d("MYTAG: ", "Response: " + installment.value.toString())
        }
    }

    override fun onPaymentTokenFetched(p0: PaymentToken?) {
        if (p0 != null) {
            var textView = findViewById<TextView>(R.id.textView_1);
            textView.text = "Payment token: " + p0.hash
            Log.d("MYTAG", p0.hash)
        }else{
            Log.d("MYTAG", "PaymentToken está nulo")
        }
    }

    override fun onError(err: Error?) {
        var errorMsg = "Code: " + err!!.code +
        " Message: " + err!!.message;
        Log.d("ERR", "onError: $errorMsg")
    }

    //    override fun onPaymentDataFetched(p0: PaymentData?) {
//        var insta: List<Installment> = p0!!.installments;
//        for(installment in insta) {
//            Log.d("MYTAG: ", "Response: " + installment.value.toString())
//        }
//    }
//
//    override fun onPaymentTokenFetched(p0: PaymentToken?) {
//        if (p0 != null) {
//            var textView = findViewById<TextView>(R.id.textView_1);
//            textView.text = "Payment token: " + p0.hash
//            Log.d("MYTAG", p0.hash)
//        }else{
//            Log.d("MYTAG", "PaymentToken está nulo")
//        }
//    }
//
//    override fun onError(err: Error?) {
//        var errorMsg = "Code: " + err!!.code +
//        " Message: " + err!!.message;
//        Log.d("ERR", "onError: $errorMsg")
//    }
}