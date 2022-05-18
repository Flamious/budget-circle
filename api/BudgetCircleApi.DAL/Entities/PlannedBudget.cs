namespace BudgetCircleApi.DAL.Entities
{
    using System.ComponentModel.DataAnnotations.Schema;

    public class PlannedBudget
    {
        public int Id { get; set; }

        public int Month { get; set; }

        public int Year { get; set; }

        [Column(TypeName = "decimal(18,2)")]
        public double PlannedEarnings { get; set; }

        [Column(TypeName = "decimal(18,2)")]
        public double PlannedExpenses { get; set; }

        public string UserId { get; set; }

        public virtual User User { get; set; }
    }
}
