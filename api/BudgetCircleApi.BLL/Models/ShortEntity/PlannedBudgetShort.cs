namespace BudgetCircleApi.BLL.Models.ShortEntity
{
    using System.Collections.Generic;

    public class PlannedBudgetShort
    {
        public bool IsPlanned { get; set; }

        public int Id { get; set; }

        public double PlannedEarnings { get; set; }

        public double PlannedExpenses { get; set; }

        public double CurrentEarnings { get; set; }

        public double CurrentExpenses { get; set; }

        public double PlannedBudget => PlannedEarnings - PlannedExpenses;

        public double CurrentBudget => CurrentEarnings - CurrentExpenses;

        public List<OperationTypePart> Expenses { get; set; }

        public List<OperationTypePart> Earnings { get; set; }

        public List<OperationTypePart> Accounts { get; set; }

        public class OperationTypePart
        {
            public string Title { get; set; }

            public double Sum { get; set; }

            public double Percentage { get; set; }
        }
    }
}
