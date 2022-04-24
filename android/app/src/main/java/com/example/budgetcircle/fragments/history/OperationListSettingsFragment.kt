package com.example.budgetcircle.fragments.history

import android.content.res.ColorStateList
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.FragmentOperationListSettingsBinding
import com.example.budgetcircle.dialogs.Dialogs
import com.example.budgetcircle.fragments.UserFragment
import com.example.budgetcircle.viewmodel.BudgetDataApi
import com.example.budgetcircle.viewmodel.models.BudgetType


class OperationListSettingsFragment : Fragment() {
    lateinit var binding: FragmentOperationListSettingsBinding
    private val budgetDataApi: BudgetDataApi by activityViewModels()
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
        setButtons()
        setObservation()
        setTheme()

        previousDate = budgetDataApi.operationListDate.value!!
        previousDateString = budgetDataApi.operationListDateString.value!!
        previousOrder = budgetDataApi.operationListStartWith.value!!
        previousBudgetType = budgetDataApi.operationListChosenBudgetType.value!!
        previousBudgetTypeString = budgetDataApi.operationListChosenBudgetTypeString.value!!
        previousType = budgetDataApi.operationListChosenType.value!!
        previousTypeString = budgetDataApi.operationListChosenTypeString.value!!
        previousOperationTypeString = budgetDataApi.operationType.value!!
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        appear()
    }

    //region Setting
    private fun setButtons() {
        val effectColor: Int = if (BudgetDataApi.mode.value!! == UserFragment.DAY) {
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
                budgetDataApi.operationListDateString,
                budgetDataApi.operationListDate,
                effectColor
            )
        }

        binding.operationListSettingsFragmentSelectOperationTypeButton.setOnClickListener {
            Dialogs().chooseOne(
                this.requireContext(),
                resources.getString(R.string.operations),
                resources.getStringArray(R.array.operationTypes),
                budgetDataApi.operationType,
                effectColor
            )
        }

        binding.operationListSettingsFragmentSelectOrderButton.setOnClickListener {
            Dialogs().chooseOne(
                this.requireContext(),
                resources.getString(R.string.start_with),
                resources.getStringArray(R.array.startWith),
                budgetDataApi.operationListStartWith,
                effectColor
            )
        }

        binding.operationListSettingsFragmentSelectBudgetTypeButton.setOnClickListener {
            val types =
                Array(budgetDataApi.budgetTypes.value!!.size + 1) { index ->
                    if (index > 0) budgetDataApi.budgetTypes.value!![index - 1].title else resources.getString(
                        R.string.all
                    )
                }
            val typesId =
                Array(budgetDataApi.budgetTypes.value!!.size + 1) { index ->
                    if (index > 0) budgetDataApi.budgetTypes.value!![index - 1].id else 0
                }

            Dialogs().chooseOne(
                this.requireContext(),
                resources.getString(R.string.budgetTypes),
                types,
                typesId,
                budgetDataApi.operationListChosenBudgetTypeString,
                budgetDataApi.operationListChosenBudgetType,
                effectColor
            )
        }

        binding.operationListSettingsFragmentSelectTypeButton.setOnClickListener {
            var types: Array<String> = arrayOf()
            when (budgetDataApi.operationType.value!!) {
                resources.getString(R.string.exchange_type) -> {
                    types = Array((budgetDataApi.budgetTypes.value!!.size + 1)) { index ->
                        if (index == 0) resources.getString(R.string.all)
                        else budgetDataApi.budgetTypes.value!![index - 1].title
                    }

                }
                resources.getString(R.string.exp_type) -> {
                    types = Array((budgetDataApi.expenseTypes.value!!.size + 1)) { index ->
                        if (index == 0) resources.getString(R.string.all)
                        else budgetDataApi.expenseTypes.value!![index - 1].title
                    }

                }
                resources.getString(R.string.earn_type) ->  {
                    types = Array((budgetDataApi.earningTypes.value!!.size + 1)) { index ->
                        if (index == 0) resources.getString(R.string.all)
                        else budgetDataApi.earningTypes.value!![index - 1].title
                    }

                }
            }
            var typesId: Array<Int> = arrayOf()
            when (budgetDataApi.operationType.value!!) {
                resources.getString(R.string.exchange_type) -> {
                    typesId = Array((budgetDataApi.budgetTypes.value!!.size + 1)) { index ->
                        if (index == 0) 0
                        else budgetDataApi.budgetTypes.value!![index - 1].id
                    }

                }
                resources.getString(R.string.exp_type) -> {
                    typesId = Array((budgetDataApi.expenseTypes.value!!.size + 1)) { index ->
                        if (index == 0) 0
                        else budgetDataApi.expenseTypes.value!![index - 1].id
                    }

                }
                resources.getString(R.string.earn_type) ->  {
                    typesId = Array((budgetDataApi.earningTypes.value!!.size + 1)) { index ->
                        if (index == 0) 0
                        else budgetDataApi.earningTypes.value!![index - 1].id
                    }

                }
            }

            Dialogs().chooseOne(
                this.requireContext(),
                budgetDataApi.operationType.value!!,
                types,
                typesId,
                budgetDataApi.operationListChosenTypeString,
                budgetDataApi.operationListChosenType,
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
            if (BudgetDataApi.mode.value!! == UserFragment.NIGHT) {
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
        budgetDataApi.operationListDateString.observe(this.viewLifecycleOwner, {
            binding.operationListSettingsFragmentSelectPeriodButton.text = it
        })

        budgetDataApi.operationType.observe(this.viewLifecycleOwner, {
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
                            if(BudgetDataApi.mode.value!! == UserFragment.DAY) R.color.text_primary else R.color.light_grey
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
                        budgetDataApi.operationListChosenTypeString.value!!
                } else {
                    budgetDataApi.operationListChosenType.postValue(0)
                    budgetDataApi.operationListChosenTypeString.postValue(resources.getString(R.string.all))
                }
            }

            binding.operationListSettingsFragmentSelectOperationTypeButton.text = it

        })
        budgetDataApi.operationListChosenBudgetTypeString.observe(this.viewLifecycleOwner, {
            binding.operationListSettingsFragmentSelectBudgetTypeButton.text = it
        })
        budgetDataApi.operationListChosenTypeString.observe(this.viewLifecycleOwner, {
            binding.operationListSettingsFragmentSelectTypeButton.text = it
        })
        budgetDataApi.operationListStartWith.observe(this.viewLifecycleOwner, {
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
        budgetDataApi.page.postValue(1)
        openHistory()
    }

    private fun cancel() {
        budgetDataApi.operationListDate.postValue(previousDate)
        budgetDataApi.operationListDateString.postValue(previousDateString)
        budgetDataApi.operationListStartWith.postValue(previousOrder)
        budgetDataApi.operationListChosenBudgetType.postValue(previousBudgetType)
        budgetDataApi.operationListChosenBudgetTypeString.postValue(previousBudgetTypeString)
        budgetDataApi.operationListChosenType.postValue(previousType)
        budgetDataApi.operationListChosenTypeString.postValue(previousTypeString)
        budgetDataApi.operationType.postValue(previousOperationTypeString)
        openHistory()
    }
    //endregion
}