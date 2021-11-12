package com.example.budgetcircle.lists

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.budgetcircle.R
import com.example.budgetcircle.database.entities.types.BudgetType
import com.example.budgetcircle.databinding.FragmentBudgetTypeListBinding
import com.example.budgetcircle.forms.BudgetFormActivity
import com.example.budgetcircle.fragments.BudgetFragment
import com.example.budgetcircle.viewmodel.BudgetData
import com.example.budgetcircle.viewmodel.items.BudgetTypeAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*

class BudgetTypeListFragment : Fragment() {

    lateinit var binding: FragmentBudgetTypeListBinding
    private val adapter = BudgetTypeAdapter()
    private var launcher: ActivityResultLauncher<Intent>? = null
    private val budgetData: BudgetData by activityViewModels()

    private var lastTypeId: Int = -1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBudgetTypeListBinding.inflate(inflater)
        setButtons()
        init()

        launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val name = result.data?.getStringExtra("newAccountName")!!
                    val sum = result.data?.getDoubleExtra("newAccountBudget", 0.0)!!
                    budgetData.editBudgetType(
                        lastTypeId,
                        BudgetType(
                            name,
                            sum,
                            true
                        )
                    )
                }
            }

        budgetData.budgetTypes.observe(this.viewLifecycleOwner, {
            adapter.setList(it)
        })
        return binding.root
    }

    private fun setButtons() {
        binding.budgetTypesBackButton2.setOnClickListener {
            exit()
        }
        adapter.onEditClick = {
            editBudgetType(it)
        }
        adapter.onDeleteClick = {
            deleteBudgetType(it)
        }
    }

    private fun init() {
        binding.apply {
            budgetTypeList.layoutManager = GridLayoutManager(this@BudgetTypeListFragment.context, 1)
            budgetTypeList.adapter = adapter
        }
    }

    private fun editBudgetType(item: BudgetType) {
        val intent = Intent(activity, BudgetFormActivity::class.java)
        lastTypeId = item.id
        intent.putExtra("edit", "edit")
        intent.putExtra("accountName", item.title)
        intent.putExtra("newAccountBudget", item.sum)
        launcher?.launch(intent)
    }

    private fun deleteBudgetType(item: BudgetType) {
        val dialog = MaterialAlertDialogBuilder(this.requireContext(), R.style.greenButtonsDialog)
            .setTitle(resources.getString(R.string.delete) + " " + item.title)
            .setMessage(resources.getString(R.string.r_u_sure))
            .setPositiveButton(
                resources.getString(R.string.yes)
            ) { dialogInterface, _ ->
                run {
                    budgetData.deleteBudgetType(item.id)
                    dialogInterface.dismiss()
                }
            }
            .setNegativeButton(
                resources.getString(R.string.no)
            ) { dialogInterface, _ -> dialogInterface.dismiss() }
            .show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(this.requireContext(), R.color.green_switch_main))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(this.requireContext(), R.color.green_switch_main))
    }

    private fun exit() {
        activity
            ?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fragmentPanel, BudgetFragment())
            ?.disallowAddToBackStack()
            ?.commit()
    }
}