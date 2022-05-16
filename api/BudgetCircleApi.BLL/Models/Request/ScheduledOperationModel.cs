namespace BudgetCircleApi.BLL.Models.Request
{
    using System.ComponentModel.DataAnnotations;

    public class ScheduledOperationModel
    {
        [Required(ErrorMessage = "Title is required")]
        public string Title { get; set; }

        [Required(ErrorMessage = "Sum is required")]
        public double Sum { get; set; }

        [Required(ErrorMessage = "Type Id is required")]
        public int TypeId { get; set; }

        [Required(ErrorMessage = "Budget Type Id is required")]
        public int BudgetTypeId { get; set; }

        public string Commentary { get; set; }

        [Required(ErrorMessage = "Budget Operation Kind is required")]
        public bool IsExpense { get; set; } = false;
    }
}
