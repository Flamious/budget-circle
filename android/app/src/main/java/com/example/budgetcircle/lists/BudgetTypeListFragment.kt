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
import com.example.budgetcircle.databinding.FragmentBudgetTypeListBinding
import com.example.budgetcircle.dialogs.Dialogs
import com.example.budgetcircle.forms.BudgetFormActivity
import com.example.budgetcircle.fragments.BudgetFragment
import com.example.budgetcircle.fragments.UserFragment
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
        setTheme()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        appear()
    }

    //region Setting
    private fun setTheme() {
        binding.apply {
            if (BudgetDataApi.mode.value!! == UserFragment.NIGHT) {
                val backgroundColor = ContextCompat.getColor(
                    this@BudgetTypeListFragment.requireContext(),
                    R.color.dark_grey
                )
                val mainColor = ContextCompat.getColor(
                    this@BudgetTypeListFragment.requireContext(),
                    R.color.darker_grey
                )

                budgetTypeListActivityList.edgeEffectFactory =
                    object : RecyclerView.EdgeEffectFactory() {
                        override fun createEdgeEffect(
                            view: RecyclerView,
                            direction: Int
                        ): EdgeEffect {
                            return EdgeEffect(view.context).apply {
                                color = ColorStateList.valueOf(mainColor).defaultColor
                            }
                        }
                    }

                budgetTypeListActivityList.backgroundTintList =
                    ColorStateList.valueOf(backgroundColor)
                budgetTypeListActivityHeaderLayout.backgroundTintList =
                    ColorStateList.valueOf(mainColor)
                budgetTypeListActivityBackButton.backgroundTintList =
                    ColorStateList.valueOf(mainColor)
                budgetTypeListActivityLayout.backgroundTintList =
                    ColorStateList.valueOf(backgroundColor)
                budgetTypeListActivityProgressBar.indeterminateTintList =
                    ColorStateList.valueOf(mainColor)
            }
        }
    }

    private fun setAdapter() {
        val textPrimary: Int
        val textSecondary: Int
        val backgroundColor: Int
        val borderColor: Int
        val buttonColor: Int?

        if (BudgetDataApi.mode.value!! == UserFragment.DAY) {
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

        adapter =
            BudgetTypeAdapter(textPrimary, textSecondary, backgroundColor, borderColor, buttonColor)
        binding.apply {
            budgetTypeListActivityList.layoutManager =
                GridLayoutManager(this@BudgetTypeListFragment.context, 1)
            budgetTypeListActivityList.adapter = adapter
        }
    }

    private fun setButtons() {
        binding.budgetTypeListActivityBackButton.setOnClickListener {
            exit()
        }
        adapter.onEditClick = {
            editBudgetType(it)
        }
        adapter.onDeleteClick = {
            val background: Int
            val buttonColor: Int

            if (BudgetDataApi.mode.value!! == UserFragment.NIGHT) {
                background = R.style.darkEdgeEffect
                buttonColor = ContextCompat.getColor(this.requireContext(), R.color.darker_grey)
            } else {
                background = R.style.greenEdgeEffect
                buttonColor = ContextCompat.getColor(this.requireContext(), R.color.green_main)
            }

            itemUnderDeletion = it
            Dialogs().chooseYesNo(
                this.requireContext(),
                resources.getString(R.string.delete) + " " + it.title,
                resources.getString(R.string.r_u_sure),
                resources.getString(R.string.yes),
                resources.getString(R.string.no),
                buttonColor,
                ::deleteBudgetType,
                background
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
        binding.budgetTypeListActivityList.startAnimation(createList)
    }

    private fun appear() {
        binding.budgetTypeListActivityHeaderLayout.startAnimation(appear)
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
        binding.budgetTypeListActivityProgressBar.visibility = View.VISIBLE
        binding.budgetTypeListActivityList.visibility = View.INVISIBLE
    }

    private fun stopLoading() {
        binding.budgetTypeListActivityProgressBar.visibility = View.GONE
        binding.budgetTypeListActivityList.visibility = View.VISIBLE
    }
    //endregion
}