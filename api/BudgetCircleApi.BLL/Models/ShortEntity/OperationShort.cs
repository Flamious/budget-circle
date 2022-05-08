namespace BudgetCircleApi.BLL.Models.ShortEntity
{
    using BudgetCircleApi.DAL.Entities;

    public class OperationShort
    {
        public OperationShort() { }

        public OperationShort(Operation operation)
        {
            Id = operation.Id;
            Title = operation.Title;
            Sum = operation.Sum;
            Date = operation.Date.ToString("dd.MM.yyyy");
            TypeId = operation.TypeId;
            BudgetTypeId = operation.BudgetTypeId;
            Commentary = operation.Commentary;
            IsExpense = operation.IsExpense;
        }

        public int Id { get; set; }

        public string Title { get; set; }

        public double Sum { get; set; }

        public string Date { get; set; }

        public int TypeId { get; set; }

        public int BudgetTypeId { get; set; }

        public string Commentary { get; set; }

        public bool? IsExpense { get; set; }
    }

    public class OperationModelSum
    {
        public string Type { get; set; }

        public double Sum { get; set; }
    }
}
