package com.example.budgetcircle.fragments.history

import android.opengl.Visibility
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.FragmentOperationListSettingsBinding
import com.example.budgetcircle.dialogs.Dialogs
import com.example.budgetcircle.viewmodel.BudgetDataApi


class OperationListSettingsFragment : Fragment() {
    lateinit var binding: FragmentOperationListSettingsBinding
    private val budgetDataApi: BudgetDataApi by activityViewModels()
    private var previousDate: Int = 0
    private var previousDateString: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOperationListSettingsBinding.inflate(inflater)
        setButtons()
        setObservation()

        previousDate = budgetDataApi.operationListDate.value!!
        previousDateString = budgetDataApi.operationListDateString.value!!
        return binding.root
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
            val types =
                Array(budgetDataApi.budgetTypes.value!!.size + 1) { index ->
                    if (index > 0)
                        when (budgetDataApi.operationType.value!!) {
                            resources.getString(R.string.exchange_type) -> budgetDataApi.budgetTypes.value!![index - 1].title
                            resources.getString(R.string.exp_type) -> budgetDataApi.expenseTypes.value!![index - 1].title
                            resources.getString(R.string.earn_type) -> budgetDataApi.earningTypes.value!![index - 1].title
                            else -> resources.getString(R.string.exchange_type)
                        } else resources.getString(
                        R.string.all
                    )
                }
            val typesId =
                Array(budgetDataApi.budgetTypes.value!!.size + 1) { index ->
                    if (index > 0)
                        when (budgetDataApi.operationType.value!!) {
                            resources.getString(R.string.exchange_type) -> budgetDataApi.budgetTypes.value!![index - 1].id
                            resources.getString(R.string.exp_type) -> budgetDataApi.expenseTypes.value!![index - 1].id
                            resources.getString(R.string.earn_type) -> budgetDataApi.earningTypes.value!![index - 1].id
                            else -> 0
                        } else 0

                }

            Dialogs().chooseOne(
                this.requireContext(),
                resources.getString(R.string.budgetTypes),
                types,
                typesId,
                budgetDataApi.operationListChosenTypeString,
                budgetDataApi.operationListChosenType
            )
        }

        binding.filterButton.setOnClickListener {
            budgetDataApi.page.postValue(1)
            openHistory()
        }

        binding.stopFilterButton.setOnClickListener {
            budgetDataApi.operationListDate.postValue(previousDate)
            budgetDataApi.operationListDateString.postValue(previousDateString)
            openHistory()
        }
    }

    private fun setObservation() {
        budgetDataApi.operationListDateString.observe(this.viewLifecycleOwner, {
            binding.selectPeriod.text = it
        })
        budgetDataApi.operationType.observe(this.viewLifecycleOwner, {
            binding.typeLayout.visibility =
                if (it == resources.getString(R.string.all)) View.GONE else View.VISIBLE
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
    private fun openHistory() {
        activity
            ?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fragmentPanel, HistoryFragment())
            ?.commit()
    }
    //endregion
}