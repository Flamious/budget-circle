package com.example.budgetcircle.fragments.history

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import com.example.budgetcircle.R
import com.example.budgetcircle.database.entities.main.Operation
import com.example.budgetcircle.databinding.FragmentOperationInfoBinding
import com.example.budgetcircle.dialogs.Dialogs
import com.example.budgetcircle.forms.EarningsFormActivity
import com.example.budgetcircle.forms.ExpensesFormActivity
import com.example.budgetcircle.viewmodel.BudgetData
import com.example.budgetcircle.viewmodel.items.HistoryItem
import java.util.*

class OperationInfoFragment : Fragment() {
    lateinit var binding: FragmentOperationInfoBinding
    private var launcher: ActivityResultLauncher<Intent>? = null
    private val budgetData: BudgetData by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOperationInfoBinding.inflate(inflater)
        setButtons()
        setObservation()
        setLauncher()
        return binding.root
    }

    //region Setting
    private fun setButtons() {
        binding.infoBackButton.setOnClickListener {
            exit()
        }
        binding.opEditButton.setOnClickListener {
            updateOperation()
        }
        binding.opDeleteButton.setOnClickListener {
            Dialogs().chooseYesNo(
                this.requireContext(),
                resources.getString(R.string.delete),
                resources.getString(R.string.r_u_sure),
                resources.getString(R.string.yes),
                resources.getString(R.string.no),
                R.color.orange_main,
                ::deleteOperation
            )
        }
    }

    private fun setFieldsValues(item: HistoryItem?) {
        item?.let {
            binding.apply {
                infoOpTitle.text = it.title
                sumInfo.text = if (it.isExpense) "-${it.sum}" else "+${it.sum}"
                sumInfo.setTextColor(it.color)
                accountInfo.text =
                    budgetData.budgetTypes.value!!.first { type -> type.id == it.budgetTypeId }.title
                kindInfo.text =
                    if (it.isExpense) budgetData.expenseTypes.first { type -> type.id == it.typeId }.title
                    else budgetData.earningTypes.first { type -> type.id == it.typeId }.title
                commentInfo.text = it.commentary
            }
        }
    }

    private fun setLauncher() {
        launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val budgetTypeIndex = result.data?.getIntExtra("budgetTypeIndex", 0)!!
                    val operationTypeIndex = result.data?.getIntExtra("typeIndex", 0)!!
                    val operationTitle = result.data?.getStringExtra("title")!!
                    val operationCommentary = result.data?.getStringExtra("commentary")!!
                    val operationSum = result.data?.getDoubleExtra("sum", 0.0)!!
                    val typeId =
                        if (budgetData.chosenHistoryItem.value!!.isExpense) budgetData.expenseTypes[operationTypeIndex].id
                        else budgetData.earningTypes[operationTypeIndex].id
                    val budgetTypeId = budgetData.budgetTypes.value!![budgetTypeIndex].id
                    val isEdited = budgetData.editOperation(
                        budgetData.chosenHistoryItem.value!!, Operation(
                            operationTitle,
                            operationSum,
                            Date(),
                            typeId,
                            budgetTypeId,
                            operationCommentary,
                            isRepetitive = false,
                            isExpense = false
                        )
                    )
                    if (!isEdited) {
                        Toast.makeText(
                            this.requireContext(),
                            resources.getText(R.string.insufficient_funds),
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        budgetData.chosenHistoryItem.apply {
                            postValue(
                                HistoryItem(
                                    this.value!!.id,
                                    operationTitle,
                                    operationSum,
                                    this.value!!.date,
                                    typeId,
                                    budgetTypeId,
                                    operationCommentary,
                                    this.value!!.isRepetitive,
                                    this.value!!.isExpense,
                                    this.value!!.color,
                                )
                            )
                        }
                    }
                }
            }

    }

    private fun setObservation() {
        budgetData.chosenHistoryItem.observe(this.viewLifecycleOwner, {
            setFieldsValues(it)
        })
    }
    //endregion
    //region Methods
    private fun updateOperation() {
        if (budgetData.chosenHistoryItem.value!!.isExpense) {
            val intent = Intent(activity, ExpensesFormActivity::class.java)
            intent.putExtra("isEdit", true)
            intent.putExtra(
                "budgetTypes",
                Array(budgetData.budgetTypes.value!!.size) { index -> budgetData.budgetTypes.value!![index].title })
            intent.putExtra(
                "expenseTypes",
                Array(budgetData.expenseTypes.size) { index -> budgetData.expenseTypes[index].title })

            intent.putExtra("sum", budgetData.chosenHistoryItem.value!!.sum)
            intent.putExtra(
                "budgetTypeIndex",
                budgetData.budgetTypes.value!!.indexOfFirst { it.id == budgetData.chosenHistoryItem.value!!.budgetTypeId })
            intent.putExtra("isRep", budgetData.chosenHistoryItem.value!!.isRepetitive)
            intent.putExtra("title", budgetData.chosenHistoryItem.value!!.title)
            intent.putExtra(
                "typeIndex",
                budgetData.expenseTypes.indexOfFirst { it.id == budgetData.chosenHistoryItem.value!!.typeId })
            intent.putExtra("commentary", budgetData.chosenHistoryItem.value!!.commentary)
            launcher?.launch(intent)
        } else {
            val intent = Intent(activity, EarningsFormActivity::class.java)
            intent.putExtra("isEdit", true)
            intent.putExtra(
                "budgetTypes",
                Array(budgetData.budgetTypes.value!!.size) { index -> budgetData.budgetTypes.value!![index].title })
            intent.putExtra(
                "earningTypes",
                Array(budgetData.earningTypes.size) { index -> budgetData.earningTypes[index].title })

            intent.putExtra("sum", budgetData.chosenHistoryItem.value!!.sum)
            intent.putExtra(
                "budgetTypeIndex",
                budgetData.budgetTypes.value!!.indexOfFirst { it.id == budgetData.chosenHistoryItem.value!!.budgetTypeId })
            intent.putExtra("isRep", budgetData.chosenHistoryItem.value!!.isRepetitive)
            intent.putExtra("title", budgetData.chosenHistoryItem.value!!.title)
            intent.putExtra(
                "typeIndex",
                budgetData.earningTypes.indexOfFirst { it.id == budgetData.chosenHistoryItem.value!!.typeId })
            intent.putExtra("commentary", budgetData.chosenHistoryItem.value!!.commentary)
            launcher?.launch(intent)

        }
    }

    private fun deleteOperation() {
        val isDeleted = budgetData.chosenHistoryItem.value.let {
            budgetData.deleteOperation(it!!)
        }
        if (!isDeleted) {
            Toast.makeText(
                this.requireContext(),
                resources.getText(R.string.insufficient_funds),
                Toast.LENGTH_LONG
            ).show()
        } else {
            budgetData.chosenHistoryItemIndex.let {
                if (it.value == 0 || it.value == null) it.postValue(null)
                else it.postValue(it.value!! - 1)
                Toast.makeText(
                    this.requireContext(),
                    resources.getText(R.string.deleted),
                    Toast.LENGTH_SHORT
                ).show()
            }
            exit()
        }
    }

    private fun exit() {
        budgetData.chosenHistoryItem.postValue(null)
        activity
            ?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fragmentPanel, HistoryFragment())
            ?.disallowAddToBackStack()
            ?.commit()
    }
    //endregion
}