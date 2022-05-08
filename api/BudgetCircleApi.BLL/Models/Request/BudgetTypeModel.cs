namespace BudgetCircleApi.BLL.Models.Request
{
    using System.ComponentModel.DataAnnotations;

    public class BudgetTypeModel
    {
        [Required(ErrorMessage = "Title is required")]
        public string Title { get; set; }

        [Required(ErrorMessage = "Sum is required")]
        public double Sum { get; set; }
    }
}
