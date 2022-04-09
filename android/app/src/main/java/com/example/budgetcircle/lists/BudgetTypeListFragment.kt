package com.example.budgetcircle.lists

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.FragmentBudgetTypeListBinding
import com.example.budgetcircle.dialogs.Dialogs
import com.example.budgetcircle.forms.BudgetFormActivity
import com.example.budgetcircle.fragments.BudgetFragment
import com.example.budgetcircle.viewmodel.BudgetDataApi
import com.example.budgetcircle.viewmodel.items.BudgetTypeAdapter
import com.example.budgetcircle.viewmodel.models.BudgetType
import java.util.*

class BudgetTypeListFragment : Fragment() {

    lateinit var binding: FragmentBudgetTypeListBinding
    private lateinit var adapter: BudgetTypeAdapter
    private var launcher: ActivityResultLauncher<Intent>? = null
    private val budgetDataApi: BudgetDataApi by activityViewModels()
    private var itemUnderDeletion: BudgetType? = null
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
        binding = FragmentBudgetTypeListBinding.inflate(inflater)
        setAdapter()
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
    private fun setAdapter() {
        adapter = BudgetTypeAdapter()
        binding.apply {
            budgetTypeList.layoutManager = GridLayoutManager(this@BudgetTypeListFragment.context, 1)
            budgetTypeList.adapter = adapter
        }
    }

    private fun setButtons() {
        binding.budgetTypesBackButton.setOnClickListener {
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
                    startLoading()
                    val name = result.data?.getStringExtra("newAccountName")!!
                    val sum = result.data?.getDoubleExtra("newAccountBudget", 0.0)!!
                    budgetDataApi.editBudgetType(
                        lastTypeId,
                        BudgetType(
                            -1,
                            name,
                            sum,
                            true
                        )
                    )
                }
            }

    }

    private fun setObservation() {
        budgetDataApi.budgetTypes.observe(this.viewLifecycleOwner, {
            stopLoading()
            adapter.setList(it)
            createList()
        })
    }

    //endregion
    //region Methods
    private fun createList() {
        binding.budgetTypeList.startAnimation(createList)
    }

    private fun appear() {
        binding.budgetTypeListHeaderLayout.startAnimation(appear)
        createList()
    }

    private fun editBudgetType(item: BudgetType) {
        val intent = Intent(activity, BudgetFormActivity::class.java)
        lastTypeId = item.id
        intent.putExtra("edit", "edit")
        intent.putExtra("accountName", item.title)
        intent.putExtra("newAccountBudget", item.sum)
        launcher?.launch(intent)
    }

    private fun deleteBudgetType() {
        itemUnderDeletion?.let {
            startLoading()
            budgetDataApi.deleteBudgetType(it.id)
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

    private fun startLoading() {
        binding.progressBar2.visibility = View.VISIBLE
        binding.budgetTypeList.visibility = View.INVISIBLE
    }

    private fun stopLoading() {
        binding.progressBar2.visibility = View.GONE
        binding.budgetTypeList.visibility = View.VISIBLE
    }
    //endregion
}