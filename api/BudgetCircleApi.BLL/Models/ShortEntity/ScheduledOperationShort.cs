namespace BudgetCircleApi.BLL.Models.ShortEntity
{
    using BudgetCircleApi.DAL.Entities;

    public class ScheduledOperationShort
    {
        public ScheduledOperationShort() { }

        public ScheduledOperationShort(ScheduledOperation operation)
        {
            Id = operation.Id;
            Title = operation.Title;
            Sum = operation.Sum;
            TypeId = operation.TypeId;
            BudgetTypeId = operation.BudgetTypeId;
            Commentary = operation.Commentary;
            IsExpense = operation.IsExpense;
        }

        public int Id { get; set; }

        public string Title { get; set; }

        public double Sum { get; set; }

        public int TypeId { get; set; }

        public int BudgetTypeId { get; set; }

        public string Commentary { get; set; }

        public bool IsExpense { get; set; }
    }
}
