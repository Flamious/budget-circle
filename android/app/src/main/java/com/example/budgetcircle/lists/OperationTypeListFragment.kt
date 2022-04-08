package com.example.budgetcircle.lists

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.budgetcircle.viewmodel.BudgetDataApi
import com.example.budgetcircle.viewmodel.items.OperationTypeAdapter
import com.example.budgetcircle.viewmodel.models.OperationType

class OperationTypeListFragment(val isExpense: Boolean) : Fragment() {

    lateinit var binding: FragmentOperationTypeListBinding
    private lateinit var adapter: OperationTypeAdapter
    private var launcher: ActivityResultLauncher<Intent>? = null
    private val budgetDataApi: BudgetDataApi by activityViewModels()

    private var itemUnderDeletion: OperationType? = null
    private var lastTypeId: Int = -1

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

    //region Setting
    private fun setAdapter() {
        adapter = OperationTypeAdapter()
        binding.apply {
            operationTypeList.layoutManager =
                GridLayoutManager(this@OperationTypeListFragment.context, 1)
            operationTypeList.adapter = adapter
        }
    }

    private fun setTheme() {
        val mainColor = if (isExpense) R.color.red_main else R.color.blue_main

        binding.operationTypeList.edgeEffectFactory = object : RecyclerView.EdgeEffectFactory() {
            override fun createEdgeEffect(view: RecyclerView, direction: Int): EdgeEffect {
                return EdgeEffect(view.context).apply {
                    color = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            this@OperationTypeListFragment.requireContext(),
                            mainColor
                        )
                    ).defaultColor
                }
            }
        }

        binding.addOpTypeButton.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                this.requireContext(),
                mainColor
            )
        )

        binding.operationTypeListTitle.text =
            resources.getText(if (isExpense) R.string.expense_types_fragment else R.string.earning_types_fragment)
    }

    private fun setButtons() {
        binding.operationTypesBackButton.setOnClickListener {
            exit()
        }
        binding.addOpTypeButton.setOnClickListener {
            addOperationType()
        }
        adapter.onEditClick = {
            editOperationType(it)
        }
        adapter.onDeleteClick = {
            itemUnderDeletion = it
            Dialogs().chooseYesNo(
                this.requireContext(),
                resources.getString(R.string.delete) + " " + it.title,
                resources.getString(R.string.r_u_sure),
                resources.getString(R.string.yes),
                resources.getString(R.string.no),
                if (isExpense) R.color.red_main else R.color.blue_main,
                ::deleteOperationType
            )
        }
    }

    private fun setLauncher() {
        launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val isEdit = result.data?.getBooleanExtra("isEdit", false)!!
                    val title = result.data?.getStringExtra("title")!!
                    if (!isEdit) {
                        if (isExpense) budgetDataApi.addExpenseType(title)
                        else budgetDataApi.addEarningType(title)
                    } else {
                        if (isExpense) budgetDataApi.editExpenseType(
                            lastTypeId,
                            title
                        )
                        else budgetDataApi.editEarningType(lastTypeId, title)
                    }
                }
            }

    }

    private fun setObservation() {
        if (isExpense)
            budgetDataApi.expenseTypes.observe(this.viewLifecycleOwner, {
                adapter.setList(it)
            })
        else
            budgetDataApi.earningTypes.observe(this.viewLifecycleOwner, {
                adapter.setList(it)
            })
    }

    //endregion
    //region Methods
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
            if (isExpense) budgetDataApi.deleteExpenseType(it.id)
            else budgetDataApi.deleteEarningType(it.id)
        }
        itemUnderDeletion = null
    }

    private fun addOperationType() {
        val intent = Intent(activity, OperationTypeFormActivity::class.java)
        intent.putExtra("isExpense", isExpense)

        launcher?.launch(intent)
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
    //endregion
}