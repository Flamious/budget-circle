package com.example.budgetcircle.fragments.history

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.FragmentOperationInfoBinding
import com.example.budgetcircle.dialogs.Dialogs
import com.example.budgetcircle.forms.BudgetExchangeActivity
import com.example.budgetcircle.forms.OperationFormActivity
import com.example.budgetcircle.viewmodel.BudgetDataApi
import com.example.budgetcircle.viewmodel.items.HistoryItem
import com.example.budgetcircle.viewmodel.models.Operation

class OperationInfoFragment : Fragment() {
    lateinit var binding: FragmentOperationInfoBinding
    private var launcher: ActivityResultLauncher<Intent>? = null
    private val budgetDataApi: BudgetDataApi by activityViewModels()

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
        binding = FragmentOperationInfoBinding.inflate(inflater)
        setButtons()
        setObservation()
        setLauncher()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        appear()
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
                sumInfo.text = when (it.isExpense) {
                    true -> "-${it.sum}"
                    false -> "+${it.sum}"
                    else -> "${it.sum}"
                }
                sumInfo.setTextColor(it.color)
                accountInfo.text =
                    budgetDataApi.budgetTypes.value!!.first { type -> type.id == it.budgetTypeId }.title
                kindInfo.text =
                    when (it.isExpense) {
                        true -> budgetDataApi.expenseTypes.value!!.first { type -> type.id == it.typeId }.title
                        false -> budgetDataApi.earningTypes.value!!.first { type -> type.id == it.typeId }.title
                        null -> {
                            fromTitle.text = resources.getString(R.string.from)
                            kindOrToTitle.text = resources.getString(R.string.to)
                            commentInfo.visibility = View.GONE
                            commentTitle.visibility = View.GONE
                            budgetDataApi.budgetTypes.value!!.first { type -> type.id == it.typeId }.title
                        }
                    }
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
                    val operationTitle: String
                    val operationCommentary: String
                    val operationSum = result.data?.getDoubleExtra("sum", 0.0)!!
                    val typeId = when (budgetDataApi.chosenHistoryItem.value!!.isExpense) {
                        true -> budgetDataApi.expenseTypes.value!![operationTypeIndex].id
                        false -> budgetDataApi.earningTypes.value!![operationTypeIndex].id
                        else -> budgetDataApi.budgetTypes.value!![operationTypeIndex].id
                    }
                    if (budgetDataApi.chosenHistoryItem.value!!.isExpense == null) {
                        operationTitle = budgetDataApi.chosenHistoryItem.value!!.title
                        operationCommentary = budgetDataApi.chosenHistoryItem.value!!.commentary
                    } else {
                        operationTitle = result.data?.getStringExtra("title")!!
                        operationCommentary = result.data?.getStringExtra("commentary")!!
                    }
                    val budgetTypeId = budgetDataApi.budgetTypes.value!![budgetTypeIndex].id
                    val isEdited = budgetDataApi.editOperation(
                        budgetDataApi.chosenHistoryItem.value!!, Operation(
                            -1,
                            operationTitle,
                            operationSum,
                            "",
                            typeId,
                            budgetTypeId,
                            operationCommentary,
                            budgetDataApi.chosenHistoryItem.value!!.isExpense
                        )
                    )
                    if (!isEdited) {
                        Toast.makeText(
                            this.requireContext(),
                            resources.getText(R.string.insufficient_funds),
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        budgetDataApi.chosenHistoryItem.apply {
                            postValue(
                                HistoryItem(
                                    this.value!!.id,
                                    operationTitle,
                                    operationSum,
                                    this.value!!.date,
                                    typeId,
                                    budgetTypeId,
                                    operationCommentary,
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
        budgetDataApi.chosenHistoryItem.observe(this.viewLifecycleOwner, {
            setFieldsValues(it)
        })
    }

    //endregion
    //region Methods
    private fun appear() {
        binding.scrollView2.startAnimation(appear)
        binding.infoBackButton.startAnimation(appear)
        binding.opDeleteButton.startAnimation(appear)
        binding.opEditButton.startAnimation(appear)
    }

    private fun updateOperation() {
        val isExpense = budgetDataApi.chosenHistoryItem.value!!.isExpense
        var intent: Intent
        if (isExpense == null) {
            intent = Intent(activity, BudgetExchangeActivity::class.java)
            intent.putExtra("exchangeSum", budgetDataApi.chosenHistoryItem.value!!.sum)
            intent.putExtra(
                "fromIndex",
                budgetDataApi.budgetTypes.value!!.indexOfFirst { it.id == budgetDataApi.chosenHistoryItem.value!!.budgetTypeId })
            intent.putExtra(
                "toIndex",
                budgetDataApi.budgetTypes.value!!.indexOfFirst { it.id == budgetDataApi.chosenHistoryItem.value!!.typeId })
        } else {
            intent = Intent(activity, OperationFormActivity::class.java)
            intent.putExtra("isExpense", isExpense)

            if (isExpense) {
                intent.putExtra(
                    "types",
                    Array(budgetDataApi.expenseTypes.value!!.size) { index -> budgetDataApi.expenseTypes.value!![index].title })
                intent.putExtra(
                    "typeIndex",
                    budgetDataApi.expenseTypes.value!!.indexOfFirst { it.id == budgetDataApi.chosenHistoryItem.value!!.typeId })
            } else {
                intent.putExtra(
                    "types",
                    Array(budgetDataApi.earningTypes.value!!.size) { index -> budgetDataApi.earningTypes.value!![index].title })
                intent.putExtra(
                    "typeIndex",
                    budgetDataApi.earningTypes.value!!.indexOfFirst { it.id == budgetDataApi.chosenHistoryItem.value!!.typeId })
            }

            intent.putExtra("sum", budgetDataApi.chosenHistoryItem.value!!.sum)
            intent.putExtra(
                "budgetTypeIndex",
                budgetDataApi.budgetTypes.value!!.indexOfFirst { it.id == budgetDataApi.chosenHistoryItem.value!!.budgetTypeId })
            intent.putExtra("title", budgetDataApi.chosenHistoryItem.value!!.title)
            intent.putExtra("commentary", budgetDataApi.chosenHistoryItem.value!!.commentary)
        }

        intent.putExtra("isEdit", true)
        intent.putExtra(
            "budgetTypes",
            Array(budgetDataApi.budgetTypes.value!!.size) { index -> budgetDataApi.budgetTypes.value!![index].title })
        launcher?.launch(intent)
    }

    private fun deleteOperation() {
        val isDeleted = budgetDataApi.chosenHistoryItem.value.let {
            budgetDataApi.deleteOperation(it!!)
        }
        if (!isDeleted) {
            Toast.makeText(
                this.requireContext(),
                resources.getText(R.string.insufficient_funds),
                Toast.LENGTH_LONG
            ).show()
        } else {
            budgetDataApi.chosenHistoryItemIndex.let {
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
        budgetDataApi.chosenHistoryItem.postValue(null)
        activity
            ?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fragmentPanel, HistoryFragment())
            ?.disallowAddToBackStack()
            ?.commit()
    }
    //endregion
}