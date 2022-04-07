package com.example.budgetcircle.fragments.history

import android.content.res.ColorStateList
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
        binding.selectPeriod.setOnClickListener {
            Dialogs().chooseOne(
                this.requireContext(),
                resources.getString(R.string.choosingPeriod),
                resources.getStringArray(R.array.periodsString),
                resources.getIntArray(R.array.periodsInt).toTypedArray(),
                budgetDataApi.operationListDateString,
                budgetDataApi.operationListDate
            )
        }

        binding.selectOperationType.setOnClickListener {
            Dialogs().chooseOne(
                this.requireContext(),
                resources.getString(R.string.operations),
                resources.getStringArray(R.array.operationTypes),
                budgetDataApi.operationType
            )
        }

        binding.selectOrder.setOnClickListener {
            Dialogs().chooseOne(
                this.requireContext(),
                resources.getString(R.string.start_with),
                resources.getStringArray(R.array.startWith),
                budgetDataApi.operationListStartWith
            )
        }

        binding.selectBudgetType.setOnClickListener {
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
                budgetDataApi.operationListChosenBudgetType
            )
        }

        binding.selectType.setOnClickListener {
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
                budgetDataApi.operationListChosenType
            )
        }

        binding.filterButton.setOnClickListener {
            apply()
        }

        binding.stopFilterButton.setOnClickListener {
            cancel()
        }
    }

    private fun setObservation() {
        budgetDataApi.operationListDateString.observe(this.viewLifecycleOwner, {
            binding.selectPeriod.text = it
        })

        budgetDataApi.operationType.observe(this.viewLifecycleOwner, {
            if (it == resources.getString(R.string.all)) {
                binding.selectType.isClickable = false
                binding.selectType.setTextColor(
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            this.requireContext(),
                            R.color.grey
                        )
                    )
                )
            } else {
                binding.selectType.isClickable = true
                binding.selectType.setTextColor(
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            this.requireContext(),
                            R.color.text_primary
                        )
                    )
                )
            }

            if (it == resources.getString(R.string.exchange_type)) {
                binding.opListTypeTitle.text = resources.getString(R.string.to)
            } else {
                binding.opListTypeTitle.text = resources.getString(R.string.type)
            }

            binding.selectOperationType.apply {
                if (text.toString() == "" || text.toString() == it) {
                    binding.selectType.text =
                        budgetDataApi.operationListChosenTypeString.value!!
                } else {
                    budgetDataApi.operationListChosenType.postValue(0)
                    budgetDataApi.operationListChosenTypeString.postValue(resources.getString(R.string.all))
                }
            }

            binding.selectOperationType.text = it

        })
        budgetDataApi.operationListChosenBudgetTypeString.observe(this.viewLifecycleOwner, {
            binding.selectBudgetType.text = it
        })
        budgetDataApi.operationListChosenTypeString.observe(this.viewLifecycleOwner, {
            binding.selectType.text = it
        })
        budgetDataApi.operationListStartWith.observe(this.viewLifecycleOwner, {
            binding.selectOrder.text = it
        })
    }
    //endregion

    //region Methods
    private fun appear() {
        binding.operationListSettingsScrollView.startAnimation(appear)
        binding.linearLayout3.startAnimation(appear)
        binding.operationSettingsTitle.startAnimation(appear)
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