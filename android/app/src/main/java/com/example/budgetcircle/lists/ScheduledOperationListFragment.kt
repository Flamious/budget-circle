package com.example.budgetcircle.lists

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EdgeEffect
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.FragmentScheduledOperationListBinding
import com.example.budgetcircle.dialogs.Dialogs
import com.example.budgetcircle.forms.OperationFormActivity
import com.example.budgetcircle.settings.Settings
import com.example.budgetcircle.viewmodel.BudgetCircleData
import com.example.budgetcircle.viewmodel.items.HistoryItem
import com.example.budgetcircle.viewmodel.items.ScheduledOperationAdapter
import com.example.budgetcircle.viewmodel.models.Operation
import com.example.budgetcircle.viewmodel.models.ScheduledOperation

class ScheduledOperationListFragment(val isExpense: Boolean) : Fragment() {
    lateinit var binding: FragmentScheduledOperationListBinding
    private lateinit var adapter: ScheduledOperationAdapter

    private var launcher: ActivityResultLauncher<Intent>? = null
    private val budgetCircleData: BudgetCircleData by activityViewModels()

    private var chosenItem: ScheduledOperation? = null

    private val appear: Animation by lazy {
        AnimationUtils.loadAnimation(
            this.context,
            R.anim.appear_short_anim
        )
    }

    private val createList: Animation by lazy {
        AnimationUtils.loadAnimation(
            this.context,
            android.R.anim.slide_in_left
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScheduledOperationListBinding.inflate(inflater)
        budgetCircleData.getScheduledOperations(isExpense)
        setAdapter()
        setButtons()
        setTheme()
        setObservation()
        setLauncher()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        appear()
    }

    //region Setting
    private fun setAdapter() {
        val textPrimary: Int
        val textSecondary: Int
        val backgroundColor: Int
        val borderColor: Int
        val buttonColor: Int?

        if (Settings.isDay()) {
            textPrimary = ContextCompat.getColor(this.requireContext(), R.color.text_primary)
            textSecondary = ContextCompat.getColor(this.requireContext(), R.color.text_secondary)
            backgroundColor = ContextCompat.getColor(this.requireContext(), R.color.white)
            borderColor = ContextCompat.getColor(this.requireContext(), R.color.light_grey)
            buttonColor = null
        } else {
            textPrimary = ContextCompat.getColor(this.requireContext(), R.color.light_grey)
            textSecondary = ContextCompat.getColor(this.requireContext(), R.color.grey)
            backgroundColor = ContextCompat.getColor(this.requireContext(), R.color.darker_grey)
            borderColor = ContextCompat.getColor(this.requireContext(), R.color.dark_grey)
            buttonColor = ContextCompat.getColor(this.requireContext(), R.color.light_grey)
        }

        adapter = ScheduledOperationAdapter(
            budgetCircleData.budgetTypes.value!!.toTypedArray(),
            budgetCircleData.earningTypes.value!!.toTypedArray(),
            budgetCircleData.expenseTypes.value!!.toTypedArray(),
            textPrimary,
            textSecondary,
            backgroundColor,
            borderColor,
            buttonColor
        )
        binding.apply {
            scheduledOperationListFragmentList.layoutManager =
                GridLayoutManager(this@ScheduledOperationListFragment.context, 1)
            scheduledOperationListFragmentList.adapter = adapter
        }
    }

    private fun setTheme() {
        val backgroundColor: Int
        val mainColor: Int
        val textPrimary: Int

        binding.apply {
            if (Settings.isDay()) {
                backgroundColor = ContextCompat.getColor(
                    this@ScheduledOperationListFragment.requireContext(),
                    R.color.light_grey
                )
                mainColor = ContextCompat.getColor(
                    this@ScheduledOperationListFragment.requireContext(),
                    if (isExpense) R.color.red_main else R.color.blue_main
                )
                textPrimary = ContextCompat.getColor(
                    this@ScheduledOperationListFragment.requireContext(),
                    R.color.text_secondary
                )
            } else {
                backgroundColor = ContextCompat.getColor(
                    this@ScheduledOperationListFragment.requireContext(),
                    R.color.dark_grey
                )
                mainColor = ContextCompat.getColor(
                    this@ScheduledOperationListFragment.requireContext(),
                    R.color.darker_grey
                )
                textPrimary = ContextCompat.getColor(
                    this@ScheduledOperationListFragment.requireContext(),
                    R.color.light_grey
                )
            }

            scheduledOperationListFragmentTitle.text =
                resources.getText(if (isExpense) R.string.expenses_fragment else R.string.earnings_fragment)

            scheduledOperationListFragmentList.edgeEffectFactory =
                object : RecyclerView.EdgeEffectFactory() {
                    override fun createEdgeEffect(view: RecyclerView, direction: Int): EdgeEffect {
                        return EdgeEffect(view.context).apply {
                            color = ColorStateList.valueOf(mainColor).defaultColor
                        }
                    }
                }

            scheduledOperationListFragmentNoEntriesTextView.setTextColor(textPrimary)
            scheduledOperationListFragmentList.backgroundTintList =
                ColorStateList.valueOf(backgroundColor)
            scheduledOperationListFragmentHeaderLayout.setBackgroundColor(mainColor)
            scheduledOperationListFragmentBackButton.backgroundTintList =
                ColorStateList.valueOf(mainColor)
            scheduledOperationListFragmentLayout.setBackgroundColor(backgroundColor)
            scheduledOperationListFragmentProgressBar.indeterminateTintList =
                ColorStateList.valueOf(mainColor)
        }
    }

    private fun setButtons() {
        binding.scheduledOperationListFragmentBackButton.setOnClickListener {
            exit()
        }
        adapter.onEditClick = {
            chosenItem = it
            updateScheduledOperation(it)
        }
        adapter.onDeleteClick = {
            val background: Int
            val buttonColor: Int
            if (isExpense) {
                if (Settings.isDay()) {
                    background = R.style.redEdgeEffect
                    buttonColor = ContextCompat.getColor(this.requireContext(), R.color.red_main)
                } else {
                    background = R.style.darkEdgeEffect
                    buttonColor = ContextCompat.getColor(this.requireContext(), R.color.darker_grey)
                }

            } else {
                if (Settings.isDay()) {
                    background = R.style.blueEdgeEffect
                    buttonColor = ContextCompat.getColor(this.requireContext(), R.color.blue_main)
                } else {
                    background = R.style.darkEdgeEffect
                    buttonColor = ContextCompat.getColor(this.requireContext(), R.color.darker_grey)
                }
            }

            chosenItem = it

            Dialogs().chooseYesNo(
                this.requireContext(),
                resources.getString(R.string.delete) + " " + it.title,
                resources.getString(R.string.r_u_sure),
                resources.getString(R.string.yes),
                resources.getString(R.string.no),
                buttonColor,
                ::deleteScheduledOperation,
                background
            )
        }
    }

    private fun setLauncher() {
        launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    startLoading()
                    val budgetTypeIndex = result.data?.getIntExtra("budgetTypeIndex", 0)!!
                    val operationTypeIndex = result.data?.getIntExtra("typeIndex", 0)!!
                    val operationSum = result.data?.getDoubleExtra("sum", 0.0)!!
                    val operationTitle: String = result.data?.getStringExtra("title")!!
                    val operationCommentary: String = result.data?.getStringExtra("commentary")!!
                    val budgetTypeId = budgetCircleData.budgetTypes.value!![budgetTypeIndex].id
                    val typeId = when (chosenItem!!.isExpense) {
                        true -> budgetCircleData.expenseTypes.value!![operationTypeIndex].id
                        false -> budgetCircleData.earningTypes.value!![operationTypeIndex].id
                    }

                    budgetCircleData.editScheduledOperation(
                        chosenItem!!.id,
                        ScheduledOperation(
                            -1,
                            operationTitle,
                            operationSum,
                            typeId,
                            budgetTypeId,
                            operationCommentary,
                            chosenItem!!.isExpense
                        )
                    )

                    chosenItem = null
                }
            }

    }

    private fun setObservation() {
        budgetCircleData.scheduledOperationList.observe(this.viewLifecycleOwner, {
            if (it != null) {
                stopLoading()
                adapter.setList(it)
                if (it.count() == 0) {
                    binding.scheduledOperationListFragmentNoEntriesTextView.visibility = View.VISIBLE
                } else {
                    binding.scheduledOperationListFragmentNoEntriesTextView.visibility = View.GONE
                }
                createList()
            }
        })
    }

    //endregion
    //region Methods
    private fun createList() {
        binding.scheduledOperationListFragmentList.startAnimation(createList)
    }

    private fun appear() {
        binding.scheduledOperationListFragmentHeaderLayout.startAnimation(appear)
        createList()
    }

    private fun updateScheduledOperation(operation: ScheduledOperation) {
        val intent = Intent(activity, OperationFormActivity::class.java)
        intent.putExtra("isExpense", isExpense)
        intent.putExtra("isScheduled", true)

        if (isExpense) {
            intent.putExtra(
                "types",
                Array(budgetCircleData.expenseTypes.value!!.size) { index -> budgetCircleData.expenseTypes.value!![index].title })
            intent.putExtra(
                "typeIndex",
                budgetCircleData.expenseTypes.value!!.indexOfFirst { it.id == operation.typeId })
        } else {
            intent.putExtra(
                "types",
                Array(budgetCircleData.earningTypes.value!!.size) { index -> budgetCircleData.earningTypes.value!![index].title })
            intent.putExtra(
                "typeIndex",
                budgetCircleData.earningTypes.value!!.indexOfFirst { it.id == operation.typeId })
        }

        intent.putExtra("sum", operation.sum)
        intent.putExtra(
            "budgetTypeIndex",
            budgetCircleData.budgetTypes.value!!.indexOfFirst { it.id == operation.budgetTypeId })
        intent.putExtra("title", operation.title)
        intent.putExtra("commentary", operation.commentary)


        intent.putExtra("isEdit", true)
        intent.putExtra(
            "budgetTypes",
            Array(budgetCircleData.budgetTypes.value!!.size) { index -> budgetCircleData.budgetTypes.value!![index].title })
        launcher?.launch(intent)
    }

    private fun deleteScheduledOperation() {
        chosenItem?.let {
            startLoading()
            budgetCircleData.deleteScheduledOperation(it.id, isExpense)
        }
        chosenItem = null
    }

    private fun exit() {
        activity
            ?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(
                R.id.fragmentPanel,
                OperationTypeListFragment(isExpense)
            )
            ?.disallowAddToBackStack()
            ?.commit()
    }

    private fun startLoading() {
        binding.scheduledOperationListFragmentProgressBar.visibility = View.VISIBLE
        binding.scheduledOperationListFragmentList.visibility = View.INVISIBLE
    }

    private fun stopLoading() {
        binding.scheduledOperationListFragmentProgressBar.visibility = View.GONE
        binding.scheduledOperationListFragmentList.visibility = View.VISIBLE
    }
    //endregion
}