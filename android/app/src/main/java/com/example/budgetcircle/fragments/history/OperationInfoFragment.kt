package com.example.budgetcircle.fragments.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.FragmentHistoryBinding
import com.example.budgetcircle.databinding.FragmentOperationInfoBinding
import com.example.budgetcircle.fragments.BudgetFragment
import com.example.budgetcircle.viewmodel.BudgetData

class OperationInfoFragment : Fragment() {
    lateinit var binding: FragmentOperationInfoBinding
    private val budgetData: BudgetData by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOperationInfoBinding.inflate(inflater)

        setButtons()
        budgetData.chosenHistoryItem.observe(this.viewLifecycleOwner, {
            it?.let {
                binding.apply {
                    infoOpTitle.text = it.title
                    if (it.isExpense) {
                        sumInfo.text = "-${it.sum}"
                        sumInfo.setTextColor(
                            ContextCompat.getColor(
                                this@OperationInfoFragment.requireContext(),
                                R.color.red_switch_main
                            )
                        )
                    } else {
                        sumInfo.text = "+${it.sum}"
                        sumInfo.setTextColor(
                            ContextCompat.getColor(
                                this@OperationInfoFragment.requireContext(),
                                R.color.green_button
                            )
                        )
                    }
                    accountInfo.text = it.budgetType
                    kindInfo.text = it.type
                    commentInfo.text = it.commentary
                }
            }
        })
        return binding.root
    }

    private fun setButtons() {
        binding.infoBackButton.setOnClickListener {
            exit()
        }
        binding.opDeleteButton.setOnClickListener {
            val isDeleted = budgetData.chosenHistoryItem.value.let {
                budgetData.deleteOperation(it!!)
            }
            if (!isDeleted) {
                Toast.makeText(this.requireContext(), resources.getText(R.string.insufficient_funds), Toast.LENGTH_LONG).show()
            } else {
                budgetData.chosenHistoryItemIndex.let {
                    if (it.value == 0 || it.value == null) it.postValue(null)
                    else it.postValue(it.value!! - 1)
                    Toast.makeText(this.requireContext(), resources.getText(R.string.deleted), Toast.LENGTH_SHORT).show()
                }
                exit()
            }
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

    companion object {
        @JvmStatic
        fun newInstance() = OperationInfoFragment()
    }
}