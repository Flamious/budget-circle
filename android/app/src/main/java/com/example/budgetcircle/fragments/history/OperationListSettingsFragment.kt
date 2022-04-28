package com.example.budgetcircle.fragments.history

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.FragmentOperationListSettingsBinding
import com.example.budgetcircle.dialogs.Dialogs
import com.example.budgetcircle.settings.Settings
import com.example.budgetcircle.viewmodel.BudgetCircleData


class OperationListSettingsFragment : Fragment() {
    lateinit var binding: FragmentOperationListSettingsBinding
    private val budgetCircleData: BudgetCircleData by activityViewModels()
    private var previousDate: Int = 0
    private var previousDateString: String = ""
    private var previousOrder = ""
    private var previousBudgetType = 0
    private var previousBudgetTypeString = ""
    private var previousType = 0
    private var previousTypeString = ""
    private var previousOperationTypeString = ""

    private val appear: Animation by lazy {
        AnimationUtils.loadAnimation(
            this.requireContext(),
            R.anim.appear_short_anim
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOperationListSettingsBinding.inflate(inflater)
        setValues()
        setButtons()
        setObservation()
        setTheme()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        appear()
    }

    //region Setting
    private fun setValues() {
        previousDate = budgetCircleData.operationListDate.value!!
        previousDateString = budgetCircleData.operationListDateString.value!!
        previousOrder = budgetCircleData.operationListStartWith.value!!
        previousBudgetType = budgetCircleData.operationListChosenBudgetType.value!!
        previousBudgetTypeString = budgetCircleData.operationListChosenBudgetTypeString.value!!
        previousType = budgetCircleData.operationListChosenType.value!!
        previousTypeString = budgetCircleData.operationListChosenTypeString.value!!
        previousOperationTypeString = budgetCircleData.operationType.value!!
    }

    private fun setButtons() {
        val effectColor: Int = if (Settings.isDay()) {
            R.style.orangeEdgeEffect
        } else {
            R.style.darkEdgeEffect
        }

        binding.operationListSettingsFragmentSelectPeriodButton.setOnClickListener {
            Dialogs().chooseOne(
                this.requireContext(),
                resources.getString(R.string.choosingPeriod),
                resources.getStringArray(R.array.periodsString),
                resources.getIntArray(R.array.periodsInt).toTypedArray(),
                budgetCircleData.operationListDateString,
                budgetCircleData.operationListDate,
                effectColor
            )
        }

        binding.operationListSettingsFragmentSelectOperationTypeButton.setOnClickListener {
            Dialogs().chooseOne(
                this.requireContext(),
                resources.getString(R.string.operations),
                resources.getStringArray(R.array.operationTypes),
                budgetCircleData.operationType,
                effectColor
            )
        }

        binding.operationListSettingsFragmentSelectOrderButton.setOnClickListener {
            Dialogs().chooseOne(
                this.requireContext(),
                resources.getString(R.string.start_with),
                resources.getStringArray(R.array.startWith),
                budgetCircleData.operationListStartWith,
                effectColor
            )
        }

        binding.operationListSettingsFragmentSelectBudgetTypeButton.setOnClickListener {
            val types =
                Array(budgetCircleData.budgetTypes.value!!.size + 1) { index ->
                    if (index > 0) budgetCircleData.budgetTypes.value!![index - 1].title else resources.getString(
                        R.string.all
                    )
                }
            val typesId =
                Array(budgetCircleData.budgetTypes.value!!.size + 1) { index ->
                    if (index > 0) budgetCircleData.budgetTypes.value!![index - 1].id else 0
                }

            Dialogs().chooseOne(
                this.requireContext(),
                resources.getString(R.string.budgetTypes),
                types,
                typesId,
                budgetCircleData.operationListChosenBudgetTypeString,
                budgetCircleData.operationListChosenBudgetType,
                effectColor
            )
        }

        binding.operationListSettingsFragmentSelectTypeButton.setOnClickListener {
            var types: Array<String> = arrayOf()
            when (budgetCircleData.operationType.value!!) {
                resources.getString(R.string.exchange_type) -> {
                    types = Array((budgetCircleData.budgetTypes.value!!.size + 1)) { index ->
                        if (index == 0) resources.getString(R.string.all)
                        else budgetCircleData.budgetTypes.value!![index - 1].title
                    }

                }
                resources.getString(R.string.exp_type) -> {
                    types = Array((budgetCircleData.expenseTypes.value!!.size + 1)) { index ->
                        if (index == 0) resources.getString(R.string.all)
                        else budgetCircleData.expenseTypes.value!![index - 1].title
                    }

                }
                resources.getString(R.string.earn_type) ->  {
                    types = Array((budgetCircleData.earningTypes.value!!.size + 1)) { index ->
                        if (index == 0) resources.getString(R.string.all)
                        else budgetCircleData.earningTypes.value!![index - 1].title
                    }

                }
            }
            var typesId: Array<Int> = arrayOf()
            when (budgetCircleData.operationType.value!!) {
                resources.getString(R.string.exchange_type) -> {
                    typesId = Array((budgetCircleData.budgetTypes.value!!.size + 1)) { index ->
                        if (index == 0) 0
                        else budgetCircleData.budgetTypes.value!![index - 1].id
                    }

                }
                resources.getString(R.string.exp_type) -> {
                    typesId = Array((budgetCircleData.expenseTypes.value!!.size + 1)) { index ->
                        if (index == 0) 0
                        else budgetCircleData.expenseTypes.value!![index - 1].id
                    }

                }
                resources.getString(R.string.earn_type) ->  {
                    typesId = Array((budgetCircleData.earningTypes.value!!.size + 1)) { index ->
                        if (index == 0) 0
                        else budgetCircleData.earningTypes.value!![index - 1].id
                    }

                }
            }

            Dialogs().chooseOne(
                this.requireContext(),
                budgetCircleData.operationType.value!!,
                types,
                typesId,
                budgetCircleData.operationListChosenTypeString,
                budgetCircleData.operationListChosenType,
                effectColor
            )
        }

        binding.operationListSettingsFragmentFilterButton.setOnClickListener {
            apply()
        }

        binding.operationListSettingsFragmentBackButton.setOnClickListener {
            cancel()
        }
    }

    private fun setTheme() {
        val textPrimary: Int
        val textSecondary: Int
        val backgroundColor: Int
        val mainColor: Int

        binding.apply {
            if (Settings.isNight()) {
                textPrimary = ContextCompat.getColor(
                    this@OperationListSettingsFragment.requireContext(),
                    R.color.light_grey
                )
                textSecondary = ContextCompat.getColor(
                    this@OperationListSettingsFragment.requireContext(),
                    R.color.grey
                )
                backgroundColor = ContextCompat.getColor(
                    this@OperationListSettingsFragment.requireContext(),
                    R.color.dark_grey
                )
                mainColor = ContextCompat.getColor(
                    this@OperationListSettingsFragment.requireContext(),
                    R.color.darker_grey
                )

                operationListSettingsFragmentBackButton.backgroundTintList =
                    ColorStateList.valueOf(mainColor)
                operationListSettingsFragmentFilterButton.backgroundTintList =
                    ColorStateList.valueOf(mainColor)
                operationListSettingsFragmentHeaderLayout.setBackgroundColor(mainColor)
                operationListSettingsFragmentLayout.backgroundTintList = ColorStateList.valueOf(backgroundColor)


                operationListSettingsFragmentPeriodTitle.setTextColor(textSecondary)
                operationListSettingsFragmentBudgetTypeTitle.setTextColor(textSecondary)
                operationListSettingsFragmentOrderTitle.setTextColor(textSecondary)
                operationListSettingsFragmentOperationTypeTitle.setTextColor(textSecondary)
                operationListSettingsFragmentTypeTitle.setTextColor(textSecondary)

                operationListSettingsFragmentSelectPeriodButton.setTextColor(textPrimary)
                operationListSettingsFragmentSelectBudgetTypeButton.setTextColor(textPrimary)
                operationListSettingsFragmentSelectOrderButton.setTextColor(textPrimary)
                operationListSettingsFragmentSelectOperationTypeButton.setTextColor(textPrimary)
                operationListSettingsFragmentSelectTypeButton.setTextColor(textPrimary)
            }
        }
    }

    private fun setObservation() {
        budgetCircleData.operationListDateString.observe(this.viewLifecycleOwner, {
            binding.operationListSettingsFragmentSelectPeriodButton.text = it
        })

        budgetCircleData.operationType.observe(this.viewLifecycleOwner, {
            if (it == resources.getString(R.string.all)) {
                binding.operationListSettingsFragmentSelectTypeButton.isClickable = false
                binding.operationListSettingsFragmentSelectTypeButton.setTextColor(
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            this.requireContext(),
                            R.color.grey
                        )
                    )
                )
            } else {
                binding.operationListSettingsFragmentSelectTypeButton.isClickable = true
                binding.operationListSettingsFragmentSelectTypeButton.setTextColor(
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            this.requireContext(),
                            if(Settings.isDay()) R.color.text_primary else R.color.light_grey
                        )
                    )
                )
            }

            if (it == resources.getString(R.string.exchange_type)) {
                binding.operationListSettingsFragmentTitle.text = resources.getString(R.string.to)
            } else {
                binding.operationListSettingsFragmentTitle.text = resources.getString(R.string.type)
            }

            binding.operationListSettingsFragmentSelectOperationTypeButton.apply {
                if (text.toString() == "" || text.toString() == it) {
                    binding.operationListSettingsFragmentSelectTypeButton.text =
                        budgetCircleData.operationListChosenTypeString.value!!
                } else {
                    budgetCircleData.operationListChosenType.postValue(0)
                    budgetCircleData.operationListChosenTypeString.postValue(resources.getString(R.string.all))
                }
            }

            binding.operationListSettingsFragmentSelectOperationTypeButton.text = it

        })
        budgetCircleData.operationListChosenBudgetTypeString.observe(this.viewLifecycleOwner, {
            binding.operationListSettingsFragmentSelectBudgetTypeButton.text = it
        })
        budgetCircleData.operationListChosenTypeString.observe(this.viewLifecycleOwner, {
            binding.operationListSettingsFragmentSelectTypeButton.text = it
        })
        budgetCircleData.operationListStartWith.observe(this.viewLifecycleOwner, {
            binding.operationListSettingsFragmentSelectOrderButton.text = it
        })
    }
    //endregion

    //region Methods
    private fun appear() {
        binding.operationListSettingsFragmentScrollView.startAnimation(appear)
        binding.operationListSettingsFragmentFilterButton.startAnimation(appear)
        binding.operationListSettingsFragmentHeaderLayout.startAnimation(appear)
    }

    private fun openHistory() {
        activity
            ?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fragmentPanel, HistoryFragment())
            ?.commit()
    }

    private fun apply() {
        budgetCircleData.page.postValue(1)
        openHistory()
    }

    private fun cancel() {
        budgetCircleData.operationListDate.postValue(previousDate)
        budgetCircleData.operationListDateString.postValue(previousDateString)
        budgetCircleData.operationListStartWith.postValue(previousOrder)
        budgetCircleData.operationListChosenBudgetType.postValue(previousBudgetType)
        budgetCircleData.operationListChosenBudgetTypeString.postValue(previousBudgetTypeString)
        budgetCircleData.operationListChosenType.postValue(previousType)
        budgetCircleData.operationListChosenTypeString.postValue(previousTypeString)
        budgetCircleData.operationType.postValue(previousOperationTypeString)
        openHistory()
    }
    //endregion
}