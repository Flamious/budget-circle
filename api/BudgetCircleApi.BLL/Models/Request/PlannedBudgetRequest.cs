namespace BudgetCircleApi.BLL.Models.Request
{
    using System.ComponentModel.DataAnnotations;

    public class PlannedBudgetRequest
    {
        [Required]
        public int Month { get; set; }

        [Required]
        public int Year { get; set; }
    }
}
