namespace BudgetCircleApi.BLL.Models.ShortEntity
{
    using BudgetCircleApi.DAL.Entities;

    public class BudgetTypeShort : TypeShort
    {
        public BudgetTypeShort() { }

        public BudgetTypeShort(BudgetType budgetType)
            : base(budgetType)
        {
            Sum = budgetType.Sum;
            IsDeletable = budgetType.IsDeletable;
        }

        public double Sum { get; set; }

        public bool IsDeletable { get; set; }

    }
}