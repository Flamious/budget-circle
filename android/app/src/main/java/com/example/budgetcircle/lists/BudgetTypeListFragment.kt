package com.example.budgetcircle.lists

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.budgetcircle.MainActivity
import com.example.budgetcircle.R
import com.example.budgetcircle.database.entities.types.BudgetType
import com.example.budgetcircle.databinding.FragmentBudgetTypeListBinding
import com.example.budgetcircle.dialogs.Dialogs
import com.example.budgetcircle.forms.BudgetFormActivity
import com.example.budgetcircle.fragments.BudgetFragment
import com.example.budgetcircle.viewmodel.BudgetData
import com.example.budgetcircle.viewmodel.items.BudgetTypeAdapter
import java.util.*

class BudgetTypeListFragment : Fragment() {

    lateinit var binding: FragmentBudgetTypeListBinding
    private lateinit var adapter: BudgetTypeAdapter
    private var launcher: ActivityResultLauncher<Intent>? = null
    private val budgetData: BudgetData by activityViewModels()
    private var itemUnderDeletion: BudgetType? = null
    private var lastTypeId: Int = -1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBudgetTypeListBinding.inflate(inflater)
        setAdapter()
        setButtons()
        setObservation()
        setLauncher()
        return binding.root
    }

    //region Setting
    private fun setAdapter() {
        adapter = BudgetTypeAdapter()
        binding.apply {
            budgetTypeList.layoutManager = GridLayoutManager(this@BudgetTypeListFragment.context, 1)
            budgetTypeList.adapter = adapter
        }
    }

    private fun setButtons() {
        binding.budgetTypesBackButton2.setOnClickListener {
            exit()
        }
        adapter.onEditClick = {
            editBudgetType(it)
        }
        adapter.onDeleteClick = {
            itemUnderDeletion = it
            Dialogs().chooseYesNo(
                this.requireContext(),
                resources.getString(R.string.delete) + " " + it.title,
                resources.getString(R.string.r_u_sure),
                resources.getString(R.string.yes),
                resources.getString(R.string.no),
                R.color.green_main,
                ::deleteBudgetType
            )
        }
    }

    private fun setLauncher() {
        launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val name = result.data?.getStringExtra("newAccountName")!!
                    val sum = result.data?.getDoubleExtra("newAccountBudget", 0.0)!!
                    budgetData.editBudgetType(
                        lastTypeId,
                        BudgetType(
                            name,
                            name,
                            sum,
                            true
                        )
                    )
                }
            }

    }

    private fun setObservation() {
        budgetData.budgetTypes.observe(this.viewLifecycleOwner, {
            adapter.setList(it)
        })
    }

    //endregion
    //region Methods
    private fun editBudgetType(item: BudgetType) {
        val intent = Intent(activity, BudgetFormActivity::class.java)
        lastTypeId = item.id
        intent.putExtra("edit", "edit")
        if (MainActivity.isRu())
            intent.putExtra("accountName", item.titleRu)
        else
            intent.putExtra("accountName", item.title)
        intent.putExtra("newAccountBudget", item.sum)
        launcher?.launch(intent)
    }

    private fun deleteBudgetType() {
        itemUnderDeletion?.let {
            budgetData.deleteBudgetType(it.id)
        }
        itemUnderDeletion = null
    }

    private fun exit() {
        activity
            ?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fragmentPanel, BudgetFragment())
            ?.disallowAddToBackStack()
            ?.commit()
    }
    //endregion
}