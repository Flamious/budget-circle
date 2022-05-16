package com.example.budgetcircle.fragments.history

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.FragmentOperationInfoBinding
import com.example.budgetcircle.dialogs.Dialogs
import com.example.budgetcircle.forms.BudgetExchangeActivity
import com.example.budgetcircle.forms.OperationFormActivity
import com.example.budgetcircle.settings.Settings
import com.example.budgetcircle.viewmodel.BudgetCircleData
import com.example.budgetcircle.viewmodel.items.HistoryItem
import com.example.budgetcircle.viewmodel.models.Operation

class OperationInfoFragment : Fragment() {
    lateinit var binding: FragmentOperationInfoBinding
    private var launcher: ActivityResultLauncher<Intent>? = null
    private val budgetCircleData: BudgetCircleData by activityViewModels()

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
        setTheme()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        appear()
    }

    //region Setting
    private fun setButtons() {
        binding.operationInfoFragmentBackButton.setOnClickListener {
            exit()
        }
        binding.operationInfoFragmentEditButton.setOnClickListener {
            updateOperation()
        }
        binding.operationInfoFragmentDeleteButton.setOnClickListener {
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


    private fun setTheme() {
        val textPrimary: Int
        val textSecondary: Int
        val backgroundColor: Int
        val mainColor: Int

        binding.apply {
            if (Settings.isNight()) {
                textPrimary = ContextCompat.getColor(
                    this@OperationInfoFragment.requireContext(),
                    R.color.light_grey
                )
                textSecondary = ContextCompat.getColor(
                    this@OperationInfoFragment.requireContext(),
                    R.color.grey
                )
                backgroundColor = ContextCompat.getColor(
                    this@OperationInfoFragment.requireContext(),
                    R.color.dark_grey
                )
                mainColor = ContextCompat.getColor(
                    this@OperationInfoFragment.requireContext(),
                    R.color.darker_grey
                )

                operationInfoFragmentBackButton.backgroundTintList =
                    ColorStateList.valueOf(mainColor)
                operationInfoFragmentEditButton.backgroundTintList =
                    ColorStateList.valueOf(mainColor)
                operationInfoFragmentDeleteButton.backgroundTintList =
                    ColorStateList.valueOf(mainColor)
                operationInfoFragmentHeaderLayout.setBackgroundColor(mainColor)
                operationInfoFragmentLayout.backgroundTintList = ColorStateList.valueOf(backgroundColor)

                operationInfoFragmentTitleTitle.setTextColor(textSecondary)
                operationInfoFragmentSumTitle.setTextColor(textSecondary)
                operationInfoFragmentAccountTitle.setTextColor(textSecondary)
                operationInfoFragmentKindTitle.setTextColor(textSecondary)
                operationInfoFragmentCommentaryTitle.setTextColor(textSecondary)

                operationInfoFragmentTitle.setTextColor(textPrimary)
                operationInfoFragmentAccount.setTextColor(textPrimary)
                operationInfoFragmentKind.setTextColor(textPrimary)
                operationInfoFragmentCommentary.setTextColor(textPrimary)
            }
        }
    }

    private fun setFieldsValues(item: HistoryItem?) {
        item?.let {
            binding.apply {
                operationInfoFragmentTitle.text = it.title
                operationInfoFragmentSum.text = when (it.isExpense) {
                    true -> "-${it.sum}"
                    false -> "+${it.sum}"
                    else -> "${it.sum}"
                }
                operationInfoFragmentSum.setTextColor(it.color)
                operationInfoFragmentAccount.text =
                    budgetCircleData.budgetTypes.value!!.first { type -> type.id == it.budgetTypeId }.title
                operationInfoFragmentKind.text =
                    when (it.isExpense) {
                        true -> budgetCircleData.expenseTypes.value!!.first { type -> type.id == it.typeId }.title
                        false -> budgetCircleData.earningTypes.value!!.first { type -> type.id == it.typeId }.title
                        null -> {
                            operationInfoFragmentAccountTitle.text = resources.getString(R.string.from)
                            operationInfoFragmentKindTitle.text = resources.getString(R.string.to)
                            operationInfoFragmentCommentaryLayout.visibility = View.GONE
                            operationInfoFragmentCommentaryTitle.visibility = View.GONE
                            budgetCircleData.budgetTypes.value!!.first { type -> type.id == it.typeId }.title
                        }
                    }
                operationInfoFragmentCommentary.text = it.commentary
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
                    val typeId = when (budgetCircleData.chosenHistoryItem.value!!.isExpense) {
                        true -> budgetCircleData.expenseTypes.value!![operationTypeIndex].id
                        false -> budgetCircleData.earningTypes.value!![operationTypeIndex].id
                        else -> budgetCircleData.budgetTypes.value!![operationTypeIndex].id
                    }
                    if (budgetCircleData.chosenHistoryItem.value!!.isExpense == null) {
                        operationTitle = budgetCircleData.chosenHistoryItem.value!!.title
                        operationCommentary = budgetCircleData.chosenHistoryItem.value!!.commentary
                    } else {
                        operationTitle = result.data?.getStringExtra("title")!!
                        operationCommentary = result.data?.getStringExtra("commentary")!!
                    }
                    val budgetTypeId = budgetCircleData.budgetTypes.value!![budgetTypeIndex].id
                    val isEdited = budgetCircleData.editOperation(
                        budgetCircleData.chosenHistoryItem.value!!, Operation(
                            -1,
                            operationTitle,
                            operationSum,
                            "",
                            typeId,
                            budgetTypeId,
                            operationCommentary,
                            budgetCircleData.chosenHistoryItem.value!!.isExpense
                        )
                    )
                    if (!isEdited) {
                        Toast.makeText(
                            this.requireContext(),
                            resources.getText(R.string.insufficient_funds),
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        budgetCircleData.chosenHistoryItem.apply {
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
                                    this.value!!.isScheduled,
                                    this.value!!.color,
                                )
                            )
                        }
                    }
                }
            }

    }

    private fun setObservation() {
        budgetCircleData.chosenHistoryItem.observe(this.viewLifecycleOwner, {
            setFieldsValues(it)
        })
    }

    //endregion
    //region Methods
    private fun appear() {
        binding.operationInfoFragmentScrollView.startAnimation(appear)
        binding.operationInfoFragmentHeaderLayout.startAnimation(appear)
    }

    private fun updateOperation() {
        val isExpense = budgetCircleData.chosenHistoryItem.value!!.isExpense
        val intent: Intent
        if (isExpense == null) {
            intent = Intent(activity, BudgetExchangeActivity::class.java)
            intent.putExtra("exchangeSum", budgetCircleData.chosenHistoryItem.value!!.sum)
            intent.putExtra(
                "fromIndex",
                budgetCircleData.budgetTypes.value!!.indexOfFirst { it.id == budgetCircleData.chosenHistoryItem.value!!.budgetTypeId })
            intent.putExtra(
                "toIndex",
                budgetCircleData.budgetTypes.value!!.indexOfFirst { it.id == budgetCircleData.chosenHistoryItem.value!!.typeId })
        } else {
            intent = Intent(activity, OperationFormActivity::class.java)
            intent.putExtra("isExpense", isExpense)

            if (isExpense) {
                intent.putExtra(
                    "types",
                    Array(budgetCircleData.expenseTypes.value!!.size) { index -> budgetCircleData.expenseTypes.value!![index].title })
                intent.putExtra(
                    "typeIndex",
                    budgetCircleData.expenseTypes.value!!.indexOfFirst { it.id == budgetCircleData.chosenHistoryItem.value!!.typeId })
            } else {
                intent.putExtra(
                    "types",
                    Array(budgetCircleData.earningTypes.value!!.size) { index -> budgetCircleData.earningTypes.value!![index].title })
                intent.putExtra(
                    "typeIndex",
                    budgetCircleData.earningTypes.value!!.indexOfFirst { it.id == budgetCircleData.chosenHistoryItem.value!!.typeId })
            }

            intent.putExtra("sum", budgetCircleData.chosenHistoryItem.value!!.sum)
            intent.putExtra(
                "budgetTypeIndex",
                budgetCircleData.budgetTypes.value!!.indexOfFirst { it.id == budgetCircleData.chosenHistoryItem.value!!.budgetTypeId })
            intent.putExtra("title", budgetCircleData.chosenHistoryItem.value!!.title)
            intent.putExtra("commentary", budgetCircleData.chosenHistoryItem.value!!.commentary)
        }

        intent.putExtra("isEdit", true)
        intent.putExtra(
            "budgetTypes",
            Array(budgetCircleData.budgetTypes.value!!.size) { index -> budgetCircleData.budgetTypes.value!![index].title })
        launcher?.launch(intent)
    }

    private fun deleteOperation() {
        val isDeleted = budgetCircleData.chosenHistoryItem.value.let {
            budgetCircleData.deleteOperation(it!!)
        }
        if (!isDeleted) {
            Toast.makeText(
                this.requireContext(),
                resources.getText(R.string.insufficient_funds),
                Toast.LENGTH_LONG
            ).show()
        } else {
            budgetCircleData.chosenHistoryItemIndex.let {
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
        budgetCircleData.chosenHistoryItem.postValue(null)
        activity
            ?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fragmentPanel, HistoryFragment())
            ?.disallowAddToBackStack()
            ?.commit()
    }
    //endregion
}