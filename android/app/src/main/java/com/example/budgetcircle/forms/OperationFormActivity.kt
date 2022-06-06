package com.example.budgetcircle.forms

import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.ActivityOperationFormBinding
import com.example.budgetcircle.dialogs.Dialogs
import com.example.budgetcircle.dialogs.Index
import com.example.budgetcircle.settings.Settings
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

        if(intent.getBooleanExtra("isLocal", false)) {
            binding.operationFormActivityIsScheduledSwitch.visibility = View.GONE
        }

        val textPrimary: Int
        val textSecondary: Int
        val backgroundColor: Int
        val mainColor: Int

        val switchCircleColor: Int
        val switchUncheckedColor: Int
        val switchCheckedColor: Int

        binding.apply {
            if (Settings.isDay()) {
                textPrimary = ContextCompat.getColor(
                    this@OperationFormActivity,
                    R.color.text_primary
                )
                textSecondary = ContextCompat.getColor(
                    this@OperationFormActivity,
                    R.color.text_secondary
                )
                backgroundColor = ContextCompat.getColor(
                    this@OperationFormActivity,
                    R.color.light_grey
                )
                mainColor = ContextCompat.getColor(
                    this@OperationFormActivity,
                    if (isExpense) R.color.red_main else R.color.blue_main
                )
                switchCircleColor = mainColor
                switchUncheckedColor = ContextCompat.getColor(
                    this@OperationFormActivity,
                    R.color.grey
                )
                switchCheckedColor = ContextCompat.getColor(
                    this@OperationFormActivity,
                    if (isExpense) R.color.red_secondary else R.color.blue_secondary
                )
            } else {
                textPrimary = ContextCompat.getColor(
                    this@OperationFormActivity,
                    R.color.light_grey
                )
                textSecondary = ContextCompat.getColor(
                    this@OperationFormActivity,
                    R.color.grey
                )
                backgroundColor = ContextCompat.getColor(
                    this@OperationFormActivity,
                    R.color.dark_grey
                )
                mainColor = ContextCompat.getColor(
                    this@OperationFormActivity,
                    R.color.darker_grey
                )
                switchCircleColor = ContextCompat.getColor(
                    this@OperationFormActivity,
                    R.color.white
                )
                switchUncheckedColor = ContextCompat.getColor(
                    this@OperationFormActivity,
                    R.color.darker_grey
                )
                switchCheckedColor = ContextCompat.getColor(
                    this@OperationFormActivity,
                    R.color.grey
                )
            }
            if (isExpense) {
                binding.operationFormActivityTitle.text = resources.getText(R.string.new_expense)
                binding.operationFormActivityAddButton.text = resources.getText(R.string.add_exp)
            } else {
                binding.operationFormActivityTitle.text = resources.getText(R.string.new_earning)
                binding.operationFormActivityAddButton.text = resources.getText(R.string.add_earn)
            }

            operationFormActivityHeaderLayout.setBackgroundColor(mainColor)
            operationFormActivityAddButton.backgroundTintList =
                ColorStateList.valueOf(mainColor)
            operationFormActivityBackButton.backgroundTintList =
                ColorStateList.valueOf(mainColor)
            operationFormActivityLayout.setBackgroundColor(backgroundColor)
            operationFormActivityBudgetTypeButton.setTextColor(textPrimary)
            operationFormActivityTypeButton.setTextColor(textPrimary)
            operationFormActivityBudgetTypeTitle.setTextColor(textSecondary)
            operationFormActivityKindTitle.setTextColor(textSecondary)

            Settings.setFieldColor(
                mainColor,
                textPrimary,
                textSecondary,
                binding.operationFormActivityTitleField,
                binding.operationFormActivitySumField,
                binding.operationFormActivityCommentaryField
            )

            binding.operationFormActivityIsScheduledSwitch.setTextColor(textPrimary)
            Settings.setSwitchColor(
                switchCircleColor,
                switchUncheckedColor,
                switchCheckedColor,
                binding.operationFormActivityIsScheduledSwitch
            )
        }
    }

    private fun setButtons() {
        val effectColor: Int = if (Settings.isDay()) {
            if (isExpense) R.style.redEdgeEffect else R.style.blueEdgeEffect
        } else {
            R.style.darkEdgeEffect
        }

        binding.operationFormActivityBudgetTypeButton.setOnClickListener {
            Dialogs().chooseOne(
                this,
                resources.getString(R.string.account),
                budgetTypes,
                binding.operationFormActivityBudgetTypeButton,
                chosenBudgetType,
                effectColor
            )
        }
        binding.operationFormActivityTypeButton.setOnClickListener {
            Dialogs().chooseOne(
                this,
                resources.getString(R.string.kind),
                types,
                binding.operationFormActivityTypeButton,
                chosenType,
                effectColor
            )
        }
        binding.operationFormActivityAddButton.setOnClickListener {
            accept()
        }
        binding.operationFormActivityBackButton.setOnClickListener {
            exit()
        }
    }

    private fun setEditPage() {
        binding.operationFormActivityIsScheduledSwitch.visibility = View.GONE

        if (isExpense) {
            binding.operationFormActivityTitle.text = resources.getText(R.string.edit_exp)
            binding.operationFormActivityAddButton.text = resources.getText(R.string.edit_exp)

        } else {
            binding.operationFormActivityTitle.text = resources.getText(R.string.edit_earn)
            binding.operationFormActivityAddButton.text = resources.getText(R.string.edit_earn)
        }

        intent.extras?.getBoolean("isScheduled", false).let {
            if (it != null) {
                binding.operationFormActivityIsScheduledSwitch.isClickable = !it
                binding.operationFormActivityIsScheduledSwitch.isChecked = it
            }
        }
        binding.operationFormActivitySumField.setText(intent.extras?.getDouble("sum")!!.toString())
        binding.operationFormActivitySumField.setText(intent.extras?.getDouble("sum")!!.toString())

        binding.operationFormActivityTitleField.setText(intent.extras?.getString("title")!!)
        binding.operationFormActivityCommentaryField.setText(intent.extras?.getString("commentary")!!)
        chosenType.value = intent.extras?.getInt("typeIndex")!!
        chosenBudgetType.value = intent.extras?.getInt("budgetTypeIndex")!!
        binding.operationFormActivityTypeButton.text = types[chosenType.value]
        binding.operationFormActivityBudgetTypeButton.text = budgetTypes[chosenBudgetType.value]
    }

    private fun setInitialValues() {
        isEdit = intent.extras?.getBoolean("isEdit", false)!!
        budgetTypes = intent.extras?.getStringArray("budgetTypes")!!
        types = intent.extras?.getStringArray("types")!!
        if (!isEdit) {
            binding.operationFormActivityBudgetTypeButton.text = budgetTypes[0]
            binding.operationFormActivityTypeButton.text = types[0]

            if (isExpense) budgetTypesSums =
                (intent.extras?.getSerializable("budgetTypesSums")!! as Array<*>).filterIsInstance<Double>()
                    .toTypedArray()
        } else {
            setEditPage()
        }

        binding.operationFormActivitySumField.filters = arrayOf<InputFilter>(SumInputFilter())

    }
    //endregion

    //region Methods
    private fun appear() {
        binding.operationFormActivityScrollView.startAnimation(appear)
        binding.operationFormActivityAddButton.startAnimation(appear)
        binding.operationFormActivityHeaderLayout.startAnimation(appear)
    }

    private fun checkFields(): Boolean {
        val sum = binding.operationFormActivitySumField.text.toString().toDoubleOrNull()
        var isValid = true
        binding.operationFormActivitySumField.apply {
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
            }
        }

        binding.operationFormActivityTitleField.apply {
            error = null
            if (text.isNullOrBlank()) {
                error = resources.getString(R.string.empty_field)
                isValid = false
            }
        }

        return isValid
    }

    private fun accept() {
        if (checkFields()) {
            val intent = Intent()
            intent.putExtra("sum", binding.operationFormActivitySumField.text.toString().toDouble())
            intent.putExtra("typeIndex", chosenType.value)
            intent.putExtra("title", binding.operationFormActivityTitleField.text.toString())
            intent.putExtra("budgetTypeIndex", chosenBudgetType.value)
            intent.putExtra(
                "commentary",
                binding.operationFormActivityCommentaryField.text.toString()
            )
            intent.putExtra(
                "isScheduled",
                binding.operationFormActivityIsScheduledSwitch.isChecked
            )
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