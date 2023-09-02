package wladimir.andrades.calculadora

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private var total: Double = 0.0
    private var operation: String = ""
    private var operationEq: String = ""
    private var valueEq: String = ""
    private var validateTotal: Boolean = false
    private var definedOperation: Boolean = false
    private var divideByZero: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    //Get and edit properties
    private fun setTextHistory(text: String){
        val txtHistory: TextView = findViewById(R.id.txtHistory)
        txtHistory.text = text
    }

    private fun setTextValue(text: String){
        val txtValue: TextView = findViewById(R.id.txtValue)
        txtValue.text = text
    }

    private fun getTextHistory(): String{
        val txtHistory: TextView = findViewById(R.id.txtHistory)
        return txtHistory.text.toString()
    }

    private fun getTextValue(): String{
        val txtValue: TextView = findViewById(R.id.txtValue)
        return txtValue.text.toString()
    }

    private fun desOrActButtons(action: Boolean){
        val btnCE: Button = findViewById(R.id.btnCE)
        val btnDel: Button = findViewById(R.id.btnDel)
        val btnEq: Button = findViewById(R.id.btnEq)
        val btnSum: Button = findViewById(R.id.btnSum)
        val btnRes: Button = findViewById(R.id.btnRes)
        val btnMul: Button = findViewById(R.id.btnMul)
        val btnDiv: Button = findViewById(R.id.btnDiv)

        btnCE.isEnabled = action
        btnDel.isEnabled = action
        btnEq.isEnabled = action
        btnSum.isEnabled = action
        btnRes.isEnabled = action
        btnMul.isEnabled = action
        btnDiv.isEnabled = action
    }

    private fun resetCalculator(){
        this.total = 0.0
        this.operation = ""
        this.validateTotal = false

        setTextHistory("")
        setTextValue("0")
    }

    private fun focusRightScroll(){
        val hsvTxtHistory: HorizontalScrollView = findViewById(R.id.hsvTxtHistory)
        val hsvTxtValue: HorizontalScrollView = findViewById(R.id.hsvTxtValue)

        hsvTxtHistory.post {
            hsvTxtHistory.fullScroll(HorizontalScrollView.FOCUS_RIGHT)
        }

        hsvTxtValue.post {
            hsvTxtValue.fullScroll(HorizontalScrollView.FOCUS_RIGHT)
        }
    }

    //Validation operation
    private fun validateButtonPressAfterEq(){
        if (this.validateTotal) {
            when (this.operation){
                "+", "-", "*", "/" -> {
                    setTextHistory("")
                    this.total = getTextValue().toDouble()
                    this.validateTotal = false
                }
                else -> resetCalculator()
            }
        }
    }

    private fun validateTextOfTextValue(digit: String){
        validateButtonPressAfterEq()

        when(getTextValue()){
            "0", "Error" -> {
                setTextValue(digit)
                desOrActButtons(true)
            }
            else ->
                if (!this.definedOperation) setTextValue(digit)
                else setTextValue(getTextValue() + digit)
        }

        this.definedOperation = true
    }

    private fun validateButtonPress(){
        validateButtonPressAfterEq()

        if (getTextHistory().isNotBlank()){
            if (this.definedOperation) {
                setTextHistory(getTextHistory() + getTextValue() + this.operation)
            }
            else{
                when(getTextHistory().last()){
                    '0','1','2','3','4','5','6','7','8','9' -> {
                        setTextHistory(getTextHistory() + getTextValue() + this.operation)
                    }
                    '+', '-', '*', '/' -> setTextHistory(getTextHistory().dropLast(1) + this.operation)
                }
            }
        }
        else {
            setTextHistory(getTextValue() + this.operation)
            this.total = getTextValue().toDouble()
        }

        validateDecimal()
        this.definedOperation = false

        focusRightScroll()
    }

    private fun validateDecimal(){
        setTextValue(
            if (this.total % 1 == .0) String.format("%.0f", this.total)
            else this.total.toString()
        )
    }

    private fun validatePerformOperation() {
        if (operation.isNotBlank() && definedOperation) validateOperation(getTextValue())
        else if (operation == "/" && definedOperation) validateOperation(getTextValue())
    }

    private fun validateOperation(value: String){
        when(this.operation){
            "+" -> this.total += value.toDouble()
            "-" -> this.total -= value.toDouble()
            "*" -> this.total *= value.toDouble()
            "/" -> validateDivideByZero(value)
        }
    }

    private fun validateDivideByZero(value: String){
        if (value == "0"){
            desOrActButtons(false)
            setTextHistory("")

            this.divideByZero = true
            this.total = 0.0
            this.operation = ""
            this.validateTotal = false
        }
        else this.total /= value.toDouble()
    }

    //Operations buttons
    fun pressSum(@Suppress("UNUSED_PARAMETER") view: View){
        validatePerformOperation()

        this.operation = "+"
        validateButtonPress()
    }

    fun pressRes(@Suppress("UNUSED_PARAMETER") view: View){
        validatePerformOperation()

        this.operation = "-"
        validateButtonPress()
    }

    fun pressMul(@Suppress("UNUSED_PARAMETER") view: View) {
        validatePerformOperation()

        this.operation = "*"
        validateButtonPress()
    }

    fun pressDiv(@Suppress("UNUSED_PARAMETER") view: View){
        validatePerformOperation()

        if (divideByZero) setTextValue("Error")
        else{
            this.operation = "/"
            validateButtonPress()
        }

        this.divideByZero = false
    }

    fun pressEqual(@Suppress("UNUSED_PARAMETER") view: View){
        if (total == 0.0) total += getTextValue().toDouble()

        if (!validateTotal){
            setTextHistory(getTextHistory() + getTextValue() + "=")
            this.valueEq = getTextValue()
            validateOperation(getTextValue())

            if (divideByZero) setTextValue("Error")
            else validateDecimal()

            this.divideByZero = false
            this.validateTotal = true

            this.operationEq = this.operation
            this.operation = ""
        }
        else {
            if (this.operationEq != ""){
                this.operation = this.operationEq

                setTextHistory((if (this.total % 1 == .0) String.format("%.0f", this.total)
                else this.total.toString()) + this.operation + valueEq + "=")

                validateOperation(this.valueEq)
                validateDecimal()
                focusRightScroll()
            }
        }
    }

    //Special buttons
    fun pressDel(@Suppress("UNUSED_PARAMETER") view: View){
        validateButtonPressAfterEq()

        if (getTextValue().length == 1) setTextValue("0")
        else setTextValue(getTextValue().dropLast(1))
    }

    fun pressC(@Suppress("UNUSED_PARAMETER") view: View){
        resetCalculator()
    }

    fun pressCE(@Suppress("UNUSED_PARAMETER") view: View){
        validateButtonPressAfterEq()

        setTextValue("0")
    }

    //Press digit
    fun pressDigit1(@Suppress("UNUSED_PARAMETER") view: View){
        validateTextOfTextValue("1")
    }

    fun pressDigit2(@Suppress("UNUSED_PARAMETER") view: View){
        validateTextOfTextValue("2")
    }

    fun pressDigit3(@Suppress("UNUSED_PARAMETER") view: View){
        validateTextOfTextValue("3")
    }

    fun pressDigit4(@Suppress("UNUSED_PARAMETER") view: View){
        validateTextOfTextValue("4")
    }

    fun pressDigit5(@Suppress("UNUSED_PARAMETER") view: View){
        validateTextOfTextValue("5")
    }

    fun pressDigit6(@Suppress("UNUSED_PARAMETER") view: View){
        validateTextOfTextValue("6")
    }

    fun pressDigit7(@Suppress("UNUSED_PARAMETER") view: View){
        validateTextOfTextValue("7")
    }

    fun pressDigit8(@Suppress("UNUSED_PARAMETER") view: View){
        validateTextOfTextValue("8")
    }

    fun pressDigit9(@Suppress("UNUSED_PARAMETER") view: View){
        validateTextOfTextValue("9")
    }

    fun pressDigit0(@Suppress("UNUSED_PARAMETER") view: View){
        validateTextOfTextValue("0")
    }
}