namespace BudgetCircleApi.BLL.Services
{
    using BudgetCircleApi.BLL.Interfaces;
    using BudgetCircleApi.BLL.Models.Request;
    using BudgetCircleApi.BLL.Models.Response;
    using BudgetCircleApi.BLL.Models.ShortEntity;
    using BudgetCircleApi.DAL.Entities;
    using BudgetCircleApi.DAL.Interfaces;
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Threading.Tasks;
    using static BudgetCircleApi.BLL.Models.ShortEntity.PlannedBudgetShort;

    class PlannedBudgetService : IPlannedBudgetService
    {
        private readonly IDbRepository _context;

        public PlannedBudgetService(IDbRepository repository)
        {
            _context = repository;
        }

        public async Task<MessageResponse> AddPlannedBudget(string userId, PlannedBudgetModel model)
        {
            try
            {
                PlannedBudget budget = new PlannedBudget()
                {
                    Month = model.Month,
                    Year = model.Year,
                    PlannedEarnings = model.Earnings,
                    PlannedExpenses = model.Expenses,
                    UserId = userId
                };

                await _context.PlannedBudgets.Create(budget);
                await _context.Save();

                return new MessageResponse("Budget was added");
            }
            catch (Exception e)
            {
                return new ErrorResponse(e.Message);
            }
        }

        public async Task<PlannedBudgetShort> GetPlannedBudget(string userId, PlannedBudgetRequest request)
        {
            var plannedBudget = _context.PlannedBudgets.GetAll().FirstOrDefault(b => b.Month == request.Month && b.Year == request.Year && b.UserId == userId);
            if (plannedBudget == null) return new PlannedBudgetShort() { IsPlanned = false };

            var operations = _context.Operations.GetAll().Where(op => op.Date.Year == request.Year && op.Date.Month == request.Month && op.UserId == userId && op.IsExpense != null).ToList();
            var earningsTypes = _context.EarningTypes.GetAll().Where(t => t.UserId == userId || t.UserId == null).ToList();
            var expenseTypes = _context.ExpenseTypes.GetAll().Where(t => t.UserId == userId || t.UserId == null).ToList();
            var budgetTypes = _context.BudgetTypes.GetAll().Where(t => t.UserId == userId).ToList();

            var earnings = new List<OperationTypePart>();
            foreach (var type in earningsTypes)
            {
                OperationTypePart typePart = new OperationTypePart()
                {
                    Title = type.Title,
                    Sum = operations.Where(op => op.TypeId == type.Id && op.IsExpense == false).Sum(op => op.Sum)
                };
                typePart.Percentage = typePart.Sum / plannedBudget.PlannedEarnings * 100;
                earnings.Add(typePart);
            }

            var expenses = new List<OperationTypePart>();
            foreach (var type in expenseTypes)
            {
                OperationTypePart typePart = new OperationTypePart()
                {
                    Title = type.Title,
                    Sum = operations.Where(op => op.TypeId == type.Id && op.IsExpense == true).Sum(op => op.Sum)
                };
                typePart.Percentage = typePart.Sum / plannedBudget.PlannedExpenses * 100;
                expenses.Add(typePart);
            }

            var accounts = new List<OperationTypePart>();
            foreach (var type in budgetTypes)
            {
                var positiveSum = operations.Where(op => op.BudgetTypeId == type.Id && op.IsExpense == false).Sum(op => op.Sum);
                var negativeSum = operations.Where(op => op.BudgetTypeId == type.Id && op.IsExpense == true).Sum(op => op.Sum);
                var sum = positiveSum - negativeSum;
                var percentage = sum / (plannedBudget.PlannedEarnings - plannedBudget.PlannedExpenses) * 100;
                if(percentage < 0)
                {
                    percentage *= -1;
                }

                OperationTypePart typePart = new OperationTypePart()
                {
                    Title = type.Title,
                    Sum = sum,
                    Percentage = percentage
                };
                accounts.Add(typePart);
            }

            return new PlannedBudgetShort()
            {
                Id = plannedBudget.Id,
                PlannedEarnings = plannedBudget.PlannedEarnings,
                PlannedExpenses = plannedBudget.PlannedExpenses,
                CurrentEarnings = operations.Where(op => op.IsExpense == false).Sum(op => op.Sum),
                CurrentExpenses = operations.Where(op => op.IsExpense == true).Sum(op => op.Sum),
                Earnings = earnings.OrderByDescending(op => op.Sum).ToList(),
                Expenses = expenses.OrderByDescending(op => op.Sum).ToList(),
                Accounts = accounts.OrderByDescending(op => op.Sum).ToList(),
                IsPlanned = true
            };
        }

        public async Task RemovePlannedBudget(string userId, int id)
        {
            var entity = await _context.PlannedBudgets.Get(id);
            if (entity.UserId != userId)
            {
                return;
            }

            _context.PlannedBudgets.Delete(id);
            await _context.Save();
        }

        public async Task UpdatePlannedBudget(string userId, int id, PlannedBudgetModel model)
        {
            var entity = await _context.PlannedBudgets.Get(id);
            if (entity.UserId != userId)
            {
                return;
            }

            entity.Month = model.Month;
            entity.Year = model.Year;
            entity.PlannedExpenses = model.Expenses;
            entity.PlannedEarnings = model.Earnings;
            entity.UserId = userId;

            await _context.PlannedBudgets.Update(entity);
            await _context.Save();
        }
    }
}
