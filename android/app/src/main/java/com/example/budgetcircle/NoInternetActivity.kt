package com.example.budgetcircle

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.budgetcircle.database.entities.Operation
import com.example.budgetcircle.databinding.ActivityNoInternetBinding
import com.example.budgetcircle.dialogs.Dialogs
import com.example.budgetcircle.forms.BudgetExchangeActivity
import com.example.budgetcircle.forms.OperationFormActivity
import com.example.budgetcircle.settings.Settings
import com.example.budgetcircle.viewmodel.LocalData
import com.example.budgetcircle.viewmodel.items.LocalOperationAdapter

class NoInternetActivity : AppCompatActivity() {
    lateinit var binding: ActivityNoInternetBinding
    private val localData: LocalData by viewModels()
    private var launcher: ActivityResultLauncher<Intent>? = null
    private var isExpense: Boolean? = null
    private var lastId: Int = -1
    private var isUpdating: Boolean = false
    private lateinit var adapter: LocalOperationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoInternetBinding.inflate(layoutInflater)
        Settings.login = intent.extras?.getString("login")!!
        setContentView(binding.root)
        setObservation()
        setLauncher()
        setButtons()
        setTheme()
    }

    override fun onBackPressed() {

    }

    //region Settings
    private fun setAdapter() {
        if (localData.budgetTypes.value.isNullOrEmpty()) return
        if (localData.earningTypes.value.isNullOrEmpty()) return
        if (localData.expenseTypes.value.isNullOrEmpty()) return

        val textPrimary: Int
        val textSecondary: Int
        val backgroundColor: Int
        val borderColor: Int
        val buttonColor: Int?
        val earningColor: Int
        val expenseColor: Int
        val exchangeColor: Int


        if (Settings.isDay()) {
            textPrimary = ContextCompat.getColor(this, R.color.text_primary)
            textSecondary = ContextCompat.getColor(this, R.color.text_secondary)
            backgroundColor = ContextCompat.getColor(this, R.color.white)
            borderColor = ContextCompat.getColor(this, R.color.light_grey)
            buttonColor = null
            earningColor = ContextCompat.getColor(this, R.color.blue_main)
            expenseColor = ContextCompat.getColor(this, R.color.red_main)
            exchangeColor = ContextCompat.getColor(this, R.color.green_main)
        } else {
            textPrimary = ContextCompat.getColor(this, R.color.light_grey)
            textSecondary = ContextCompat.getColor(this, R.color.grey)
            backgroundColor = ContextCompat.getColor(this, R.color.darker_grey)
            borderColor = ContextCompat.getColor(this, R.color.dark_grey)
            buttonColor = ContextCompat.getColor(this, R.color.light_grey)
            earningColor = ContextCompat.getColor(this, R.color.blue_secondary)
            expenseColor = ContextCompat.getColor(this, R.color.red_secondary)
            exchangeColor = ContextCompat.getColor(this, R.color.green_secondary)
        }

        adapter = LocalOperationAdapter(
            localData.budgetTypes.value!!.toTypedArray(),
            localData.earningTypes.value!!.toTypedArray(),
            localData.expenseTypes.value!!.toTypedArray(),
            textPrimary,
            textSecondary,
            backgroundColor,
            borderColor,
            buttonColor,
            earningColor,
            expenseColor,
            exchangeColor
        )

        binding.apply {
            noInternetActivityList.layoutManager = GridLayoutManager(this@NoInternetActivity, 1)
            noInternetActivityList.adapter = adapter
        }

        localData.operations.observe(this) {
            if (it != null) {
                adapter.setList(it.reversed())
                if (it.isEmpty()) {
                    binding.noInternetActivityNoEntriesTextView.visibility = View.VISIBLE
                } else {
                    binding.noInternetActivityNoEntriesTextView.visibility = View.GONE
                }
            }
        }

        adapter.onEditClick = {
            updateOperation(it)
        }

        adapter.onDeleteClick = {
            val dialogBackground: Int
            val dialogButtonColor: Int
            if (Settings.isDay()) {
                dialogBackground = R.style.greenEdgeEffect
                dialogButtonColor = ContextCompat.getColor(this, R.color.green_main)
            } else {
                dialogBackground = R.style.darkEdgeEffect
                dialogButtonColor = ContextCompat.getColor(this, R.color.darker_grey)
            }

            lastId = it.id

            Dialogs().chooseYesNo(
                this,
                resources.getString(R.string.delete) + " " + it.title,
                resources.getString(R.string.r_u_sure),
                resources.getString(R.string.yes),
                resources.getString(R.string.no),
                dialogButtonColor,
                ::deleteOperation,
                dialogBackground
            )
        }
    }

    private fun setObservation() {
        localData.budgetTypes.observe(this) {
            setAdapter()
            if(it.size < 2) {
                binding.noInternetActivityAddExchangeButton.visibility = View.GONE
            } else {
                binding.noInternetActivityAddExchangeButton.visibility = View.VISIBLE
            }
        }
        localData.earningTypes.observe(this) {
            setAdapter()
        }
        localData.expenseTypes.observe(this) {
            setAdapter()
        }
    }

    private fun setButtons() {
        binding.noInternetActivityAddEarningButton.setOnClickListener {
            addEarning()
        }
        binding.noInternetActivityAddExpenseButton.setOnClickListener {
            addExpense()
        }
        binding.noInternetActivityAddExchangeButton.setOnClickListener {
            addExchange()
        }
    }

    private fun setTheme() {
        val title = "${resources.getString(R.string.offline)} (${Settings.login})"
        binding.noInternetActivityOfflineTitle.text = title

        if (Settings.isNight()) {
            binding.apply {
                val textPrimary = ContextCompat.getColor(
                    this@NoInternetActivity,
                    R.color.light_grey
                )
                val backgroundColor = ContextCompat.getColor(
                    this@NoInternetActivity,
                    R.color.dark_grey
                )
                val mainColor = ContextCompat.getColor(
                    this@NoInternetActivity,
                    R.color.darker_grey
                )

                noInternetActivityNoEntriesTextView.setTextColor(textPrimary)
                noInternetActivityHeaderLayout.backgroundTintList =
                    ColorStateList.valueOf(mainColor)
                noInternetActivityAddExchangeButton.backgroundTintList =
                    ColorStateList.valueOf(mainColor)
                noInternetActivityAddExchangeButton.imageTintList =
                    ColorStateList.valueOf(textPrimary)
                noInternetActivityAddExpenseButton.backgroundTintList =
                    ColorStateList.valueOf(mainColor)
                noInternetActivityAddExpenseButton.imageTintList =
                    ColorStateList.valueOf(textPrimary)
                noInternetActivityAddEarningButton.backgroundTintList =
                    ColorStateList.valueOf(mainColor)
                noInternetActivityAddEarningButton.imageTintList =
                    ColorStateList.valueOf(textPrimary)
                noInternetActivityButtonsLayout.setBackgroundColor(mainColor)
                noInternetActivityList.setBackgroundColor(backgroundColor)
                noInternetActivityLayout.setBackgroundColor(backgroundColor)
            }
        }
    }

    private fun setLauncher() {
        launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val title: String
                val sum: Double
                val typeId: Int
                val budgetTypeId: Int
                val commentary: String

                if (result.resultCode == Activity.RESULT_OK) {
                    if (isExpense == null) {
                        val from: Int
                        val to: Int

                        if (isUpdating) {
                            from = result.data?.getIntExtra("budgetTypeIndex", 0)!!
                            to = result.data?.getIntExtra("typeIndex", 0)!!
                        } else {

                            from = result.data?.getIntExtra("fromIndex", 0)!!
                            to = result.data?.getIntExtra("toIndex", 0)!!
                        }

                        title = resources.getString(R.string.exchange)
                        sum = result.data?.getDoubleExtra("sum", 0.0)!!
                        commentary = ""

                        typeId = localData.budgetTypes.value!![to].id
                        budgetTypeId = localData.budgetTypes.value!![from].id
                    } else {
                        val budgetTypeIndex = result.data?.getIntExtra("budgetTypeIndex", 0)!!
                        val typeIndex = result.data?.getIntExtra("typeIndex", 0)!!

                        title = result.data?.getStringExtra("title")!!
                        sum = result.data?.getDoubleExtra("sum", 0.0)!!
                        commentary = result.data?.getStringExtra("commentary")!!

                        typeId =
                            if (isExpense == true) localData.expenseTypes.value!![typeIndex].id else localData.earningTypes.value!![typeIndex].id
                        budgetTypeId = localData.budgetTypes.value!![budgetTypeIndex].id
                    }

                    if (isUpdating) {
                        localData.editOperation(
                            lastId,
                            Operation(
                                title,
                                sum,
                                typeId,
                                budgetTypeId,
                                commentary,
                                isExpense,
                                Settings.login
                            )
                        )
                        isUpdating = false
                    } else {
                        localData.addOperation(
                            Operation(
                                title,
                                sum,
                                typeId,
                                budgetTypeId,
                                commentary,
                                isExpense,
                                Settings.login
                            )
                        )
                    }
                }
            }
    }
    //endregion

    //region Methods
    private fun addEarning() {
        isUpdating = false
        isExpense = false
        val intent = Intent(this, OperationFormActivity::class.java)
        intent.putExtra("isExpense", isExpense)
        intent.putExtra(
            "budgetTypes",
            Array(localData.budgetTypes.value!!.size) { index -> localData.budgetTypes.value!![index].title })

        intent.putExtra(
            "types",
            Array(localData.earningTypes.value!!.size) { index -> localData.earningTypes.value!![index].title })
        intent.putExtra("isLocal", true)

        launcher?.launch(intent)
    }

    private fun addExpense() {
        isUpdating = false
        isExpense = true
        val intent = Intent(this, OperationFormActivity::class.java)
        intent.putExtra("isExpense", isExpense)
        intent.putExtra(
            "budgetTypes",
            Array(localData.budgetTypes.value!!.size) { index -> localData.budgetTypes.value!![index].title })

        intent.putExtra(
            "types",
            Array(localData.expenseTypes.value!!.size) { index -> localData.expenseTypes.value!![index].title })
        intent.putExtra(
            "budgetTypesSums",
            Array(localData.budgetTypes.value!!.size) { index -> localData.budgetTypes.value!![index].sum })
        intent.putExtra("isLocal", true)

        launcher?.launch(intent)
    }

    private fun addExchange() {
        isUpdating = false
        isExpense = null
        val intent = Intent(this, BudgetExchangeActivity::class.java)
        intent.putExtra(
            "budgetTypes",
            Array(localData.budgetTypes.value!!.size) { index -> localData.budgetTypes.value!![index].title })
        intent.putExtra(
            "budgetTypesSums",
            Array(localData.budgetTypes.value!!.size) { index -> localData.budgetTypes.value!![index].sum })
        launcher?.launch(intent)
    }

    private fun updateOperation(operation: Operation) {
        lastId = operation.id
        isUpdating = true

        val intent: Intent
        if (operation.isExpense == null) {
            intent = Intent(this, BudgetExchangeActivity::class.java)
            intent.putExtra("exchangeSum", operation.sum)
            intent.putExtra(
                "fromIndex",
                localData.budgetTypes.value!!.indexOfFirst { it.id == operation.budgetTypeId })
            intent.putExtra(
                "toIndex",
                localData.budgetTypes.value!!.indexOfFirst { it.id == operation.typeId })
        } else {
            intent = Intent(this, OperationFormActivity::class.java)
            intent.putExtra("isExpense", operation.isExpense)

            if (operation.isExpense == true) {
                intent.putExtra(
                    "types",
                    Array(localData.expenseTypes.value!!.size) { index -> localData.expenseTypes.value!![index].title })
                intent.putExtra(
                    "typeIndex",
                    localData.expenseTypes.value!!.indexOfFirst { it.id == operation.typeId })
            } else {
                intent.putExtra(
                    "types",
                    Array(localData.earningTypes.value!!.size) { index -> localData.earningTypes.value!![index].title })
                intent.putExtra(
                    "typeIndex",
                    localData.earningTypes.value!!.indexOfFirst { it.id == operation.typeId })
            }

            intent.putExtra("sum", operation.sum)
            intent.putExtra(
                "budgetTypeIndex",
                localData.budgetTypes.value!!.indexOfFirst { it.id == operation.budgetTypeId })
            intent.putExtra("title", operation.title)
            intent.putExtra("commentary", operation.commentary)
            intent.putExtra("isLocal", true)
        }

        intent.putExtra("isEdit", true)
        intent.putExtra(
            "budgetTypes",
            Array(localData.budgetTypes.value!!.size) { index -> localData.budgetTypes.value!![index].title })
        launcher?.launch(intent)
    }

    private fun deleteOperation() {
        localData.deleteOperation(lastId)
    }
    //endregion
}