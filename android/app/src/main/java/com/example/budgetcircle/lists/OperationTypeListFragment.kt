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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.FragmentOperationTypeListBinding
import com.example.budgetcircle.dialogs.Dialogs
import com.example.budgetcircle.forms.OperationTypeFormActivity
import com.example.budgetcircle.fragments.OperationFragment
import com.example.budgetcircle.settings.Settings
import com.example.budgetcircle.viewmodel.BudgetCircleData
import com.example.budgetcircle.viewmodel.items.OperationTypeAdapter
import com.example.budgetcircle.viewmodel.models.OperationType

class OperationTypeListFragment(val isExpense: Boolean) : Fragment() {

    lateinit var binding: FragmentOperationTypeListBinding
    private lateinit var adapter: OperationTypeAdapter
    private var launcher: ActivityResultLauncher<Intent>? = null
    private val budgetCircleData: BudgetCircleData by activityViewModels()

    private var itemUnderDeletion: OperationType? = null
    private var lastTypeId: Int = -1

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
        binding = FragmentOperationTypeListBinding.inflate(inflater)
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
            buttonColor = ContextCompat.getColor(this.requireContext(), R.color.light_grey)
            borderColor = ContextCompat.getColor(this.requireContext(), R.color.dark_grey)
        }

        adapter = OperationTypeAdapter(textPrimary, textSecondary, backgroundColor, borderColor, buttonColor)
        binding.apply {
            operationTypeListFragmentList.layoutManager =
                GridLayoutManager(this@OperationTypeListFragment.context, 1)
            operationTypeListFragmentList.adapter = adapter
        }
    }

    private fun setTheme() {
        val backgroundColor: Int
        val mainColor: Int

        binding.apply {
            if (Settings.isDay()) {
                backgroundColor = ContextCompat.getColor(
                    this@OperationTypeListFragment.requireContext(),
                    R.color.light_grey
                )
                mainColor = ContextCompat.getColor(
                    this@OperationTypeListFragment.requireContext(),
                    if (isExpense) R.color.red_main else R.color.blue_main
                )
            } else {
                backgroundColor = ContextCompat.getColor(
                    this@OperationTypeListFragment.requireContext(),
                    R.color.dark_grey
                )
                mainColor = ContextCompat.getColor(
                    this@OperationTypeListFragment.requireContext(),
                    R.color.darker_grey
                )
            }

            operationTypeListFragmentTitle.text =
                resources.getText(if (isExpense) R.string.expenses_fragment else R.string.earnings_fragment)

            operationTypeListFragmentList.edgeEffectFactory = object : RecyclerView.EdgeEffectFactory() {
                override fun createEdgeEffect(view: RecyclerView, direction: Int): EdgeEffect {
                    return EdgeEffect(view.context).apply {
                        color = ColorStateList.valueOf(mainColor).defaultColor
                    }
                }
            }

            operationTypeListFragmentList.backgroundTintList = ColorStateList.valueOf(backgroundColor)
            operationTypeListFragmentHeaderLayout.setBackgroundColor(mainColor)
            operationTypeListFragmentBackButton.backgroundTintList = ColorStateList.valueOf(mainColor)
            operationTypeListFragmentAddButton.backgroundTintList = ColorStateList.valueOf(mainColor)
            operationTypeListFragmentOpenScheduledOperarationsButton.backgroundTintList = ColorStateList.valueOf(mainColor)
            operationTypeListFragmentLayout.backgroundTintList = ColorStateList.valueOf(backgroundColor)
            operationTypeListFragmentProgressBar.indeterminateTintList =
                ColorStateList.valueOf(mainColor)
        }
    }

    private fun setButtons() {
        binding.operationTypeListFragmentOpenScheduledOperarationsButton.setOnClickListener {
            openScheduledOperationsFragment()
        }
        binding.operationTypeListFragmentBackButton.setOnClickListener {
            exit()
        }
        binding.operationTypeListFragmentAddButton.setOnClickListener {
            addOperationType()
        }
        adapter.onEditClick = {
            editOperationType(it)
        }
        adapter.onDeleteClick = {
            val background: Int
            val buttonColor:Int
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

            itemUnderDeletion = it
            Dialogs().chooseYesNo(
                this.requireContext(),
                resources.getString(R.string.delete) + " " + it.title,
                resources.getString(R.string.r_u_sure),
                resources.getString(R.string.yes),
                resources.getString(R.string.no),
                buttonColor,
                ::deleteOperationType,
                background
            )
        }
    }

    private fun setLauncher() {
        launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    startLoading()
                    val isEdit = result.data?.getBooleanExtra("isEdit", false)!!
                    val title = result.data?.getStringExtra("title")!!
                    if (!isEdit) {
                        if (isExpense) budgetCircleData.addExpenseType(title)
                        else budgetCircleData.addEarningType(title)
                    } else {
                        if (isExpense) budgetCircleData.editExpenseType(
                            lastTypeId,
                            title
                        )
                        else budgetCircleData.editEarningType(lastTypeId, title)
                    }
                }
            }

    }

    private fun setObservation() {
        if (isExpense)
            budgetCircleData.expenseTypes.observe(this.viewLifecycleOwner, {
                stopLoading()
                adapter.setList(it)
                createList()
            })
        else
            budgetCircleData.earningTypes.observe(this.viewLifecycleOwner, {
                stopLoading()
                adapter.setList(it)
                createList()
            })
    }

    //endregion
    //region Methods
    private fun createList() {
        binding.operationTypeListFragmentList.startAnimation(createList)
    }

    private fun appear() {
        binding.operationTypeListFragmentHeaderLayout.startAnimation(appear)
        binding.operationTypeListFragmentOpenScheduledOperarationsButton.startAnimation(appear)
        binding.operationTypeListFragmentAddButton.startAnimation(appear)

        createList()
    }

    private fun editOperationType(item: OperationType) {
        val intent = Intent(activity, OperationTypeFormActivity::class.java)
        lastTypeId = item.id
        intent.putExtra("isEdit", true)
        intent.putExtra("isExpense", isExpense)
        intent.putExtra("title", item.title)
        launcher?.launch(intent)
    }

    private fun deleteOperationType() {
        itemUnderDeletion?.let {
            startLoading()
            if (isExpense) budgetCircleData.deleteExpenseType(it.id)
            else budgetCircleData.deleteEarningType(it.id)
        }
        itemUnderDeletion = null
    }

    private fun addOperationType() {
        val intent = Intent(activity, OperationTypeFormActivity::class.java)
        intent.putExtra("isExpense", isExpense)

        launcher?.launch(intent)
    }

    private fun openScheduledOperationsFragment() {
        activity
            ?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(
                R.id.fragmentPanel,
                ScheduledOperationListFragment(isExpense)
            )
            ?.disallowAddToBackStack()
            ?.commit()
    }

    private fun exit() {
        activity
            ?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(
                R.id.fragmentPanel,
                OperationFragment(isExpense)
            )
            ?.disallowAddToBackStack()
            ?.commit()
    }

    private fun startLoading() {
        binding.operationTypeListFragmentProgressBar.visibility = View.VISIBLE
        binding.operationTypeListFragmentList.visibility = View.INVISIBLE
    }

    private fun stopLoading() {
        binding.operationTypeListFragmentProgressBar.visibility = View.GONE
        binding.operationTypeListFragmentList.visibility = View.VISIBLE
    }
    //endregion
}