package com.example.budgetcircle.forms

import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.ActivityOperationFormBinding
import com.example.budgetcircle.dialogs.Dialogs
import com.example.budgetcircle.dialogs.Index
import com.example.budgetcircle.settings.SumInputFilter

class OperationFormActivity : AppCompatActivity() {
    lateinit var binding: ActivityOperationFormBinding
    private var chosenBudgetType: Index = Index(0)
    private var chosenType: Index = Index(0)
    lateinit var budgetTypes: Array<String>
    private lateinit var types: Array<String>
    private var budgetTypesSums: Array<Double> = arrayOf()
    private var isExpense: Boolean = false
    private var isEdit: Boolean = false

    private val appear: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.appear_short_anim
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOperationFormBinding.inflate(layoutInflater)
        setTheme()
        setInitialValues()
        setButtons()
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        appear()
    }

    override fun onBackPressed() {
        exit()
    }

    //region Setting
    private fun setTheme() {
        isExpense = intent.extras?.getBoolean("isExpense", false)!!
        val mainColor = if (isExpense) R.color.red_main else R.color.blue_main
        val secondaryColor = if (isExpense) R.color.red_secondary else R.color.blue_secondary

        if(isExpense) {
            binding.operationFormBudgetTypeTitle.text = resources.getText(R.string.new_expense)
            binding.operationFormAddButton.text = resources.getText(R.string.add_exp)
        } else {
            binding.operationFormBudgetTypeTitle.text = resources.getText(R.string.new_earning)
            binding.operationFormAddButton.text = resources.getText(R.string.add_earn)
        }

        binding.operationFormAddButton.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                this,
                mainColor
            )
        )

        setFieldColor(binding.operationTitleField, mainColor, secondaryColor)
        setFieldColor(binding.operationSumField, mainColor, secondaryColor)
        setFieldColor(binding.operationCommentaryField, mainColor, secondaryColor)
    }

    private fun setFieldColor(textView: TextView, mainColor: Int, secondaryColor: Int) {
        textView.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                this,
                mainColor
            )
        )
        textView.highlightColor =
            ContextCompat.getColor(this, mainColor)
        textView.setLinkTextColor(
            ContextCompat.getColor(
                this,
                secondaryColor
            )
        )
    }

    private fun setButtons() {
        binding.operationFragmentBudgetTypeButton.setOnClickListener {
            Dialogs().chooseOne(
                this,
                resources.getString(R.string.account),
                budgetTypes,
                binding.operationFragmentBudgetTypeButton,
                chosenBudgetType,
                R.style.blueEdgeEffect
            )
        }
        binding.operationFragmentTypeButton.setOnClickListener {
            Dialogs().chooseOne(
                this,
                resources.getString(R.string.kind),
                types,
                binding.operationFragmentTypeButton,
                chosenType,
                R.style.blueEdgeEffect
            )
        }
        binding.operationFormAddButton.setOnClickListener {
            add()
        }
        binding.operationFormBackButton.setOnClickListener {
            exit()
        }
    }

    private fun setEditPage() {
        if(isExpense) {
            binding.operationFormBudgetTypeTitle.text = resources.getText(R.string.edit_exp)
            binding.operationFormAddButton.text = resources.getText(R.string.edit_exp)

        } else {
            binding.operationFormBudgetTypeTitle.text = resources.getText(R.string.edit_earn)
            binding.operationFormAddButton.text = resources.getText(R.string.edit_earn)
        }

        binding.operationSumField.setText(intent.extras?.getDouble("sum")!!.toString())
        binding.operationTitleField.setText(intent.extras?.getString("title")!!)
        binding.operationCommentaryField.setText(intent.extras?.getString("commentary")!!)
        chosenType.value = intent.extras?.getInt("typeIndex")!!
        chosenBudgetType.value = intent.extras?.getInt("budgetTypeIndex")!!
        binding.operationFragmentTypeButton.text = types[chosenType.value]
        binding.operationFragmentBudgetTypeButton.text = budgetTypes[chosenBudgetType.value]
    }

    private fun setInitialValues() {
        isEdit = intent.extras?.getBoolean("isEdit", false)!!
        budgetTypes = intent.extras?.getStringArray("budgetTypes")!!
        types = intent.extras?.getStringArray("types")!!
        if (!isEdit) {
            binding.operationFragmentBudgetTypeButton.text = budgetTypes[0]
            binding.operationFragmentTypeButton.text = types[0]

            if(isExpense) budgetTypesSums =
                (intent.extras?.getSerializable("budgetTypesSums")!! as Array<*>).filterIsInstance<Double>()
                    .toTypedArray()
        } else {
            setEditPage()
        }

        binding.operationSumField.filters = arrayOf<InputFilter>(SumInputFilter())

    }
    //endregion
    //region Methods
    private fun appear() {
        binding.operationFormScrollView.startAnimation(appear)
        binding.operationFormAddButton.startAnimation(appear)
        binding.operationFormBackButton.startAnimation(appear)
        binding.operationFormTitle.startAnimation(appear)
    }

    private fun checkFields(): Boolean {
        val sum = binding.operationSumField.text.toString().toDoubleOrNull()
        var isValid = true
        binding.operationSumField.apply {
            error = null
            when {
                sum == null -> {
                    error = resources.getString(R.string.empty_field)
                    isValid = false
                }
                sum <= 0.0 -> {
                    error = resources.getString(R.string.zero_sum)
                    isValid = false
                }
                else -> {
                    if (!isEdit && isExpense) {
                        if (sum > budgetTypesSums[chosenBudgetType.value]) {
                            error =
                                "${resources.getString(R.string.insufficient_funds)} (${budgetTypesSums[chosenBudgetType.value]})"
                            isValid = false
                        }
                    }
                }
            }
        }

        binding.operationTitleField.apply {
            error = null
            if (text.isNullOrBlank()) {
                error = resources.getString(R.string.empty_field)
                isValid = false
            }
        }

        return isValid
    }

    private fun add() {
        if (checkFields()) {
            val intent = Intent()
            intent.putExtra("sum", binding.operationSumField.text.toString().toDouble())
            intent.putExtra("typeIndex", chosenType.value)
            intent.putExtra("title", binding.operationTitleField.text.toString())
            intent.putExtra("budgetTypeIndex", chosenBudgetType.value)
            intent.putExtra("commentary", binding.operationCommentaryField.text.toString())
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    private fun exit() {
        val intent = Intent()
        setResult(RESULT_CANCELED, intent)
        finish()
    }
    //endregion
}