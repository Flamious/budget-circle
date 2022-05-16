namespace BudgetCircleApi.DAL.Entities
{
    using System;
    using System.ComponentModel.DataAnnotations.Schema;

    public class ScheduledOperation
    {
        public int Id { get; set; }

        public string Title { get; set; }

        [Column(TypeName = "decimal(18,2)")]
        public double Sum { get; set; }

        public int TypeId { get; set; }

        public int BudgetTypeId { get; set; }

        public string UserId { get; set; }

        public string Commentary { get; set; }

        public bool IsExpense { get; set; }

        public virtual Interfaces.Type Type { get; set; }

        public virtual BudgetType BudgetType { get; set; }

        public virtual User User { get; set; }
    }
}