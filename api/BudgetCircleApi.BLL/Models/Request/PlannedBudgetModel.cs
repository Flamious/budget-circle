namespace BudgetCircleApi.BLL.Models.Request
{
    using System.ComponentModel.DataAnnotations;

    public class PlannedBudgetModel
    {
        [Required]
        public int Month { get; set; }

        [Required]
        public int Year { get; set; }

        [Required]
        public double Earnings { get; set; }

        [Required]
        public double Expenses { get; set; }
    }
}
