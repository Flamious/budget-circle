namespace BudgetCircleApi.BLL.Models.Request
{
    using System.ComponentModel.DataAnnotations;

    public class NewPasswordModel
    {
        [Required(ErrorMessage = "New password is required")]
        public string NewPassword { get; set; }

        [Required(ErrorMessage = "Password is required")]
        public string Password { get; set; }

        [Required(ErrorMessage = "Confirmation password is required")]
        public string ConfirmationPassword { get; set; }
    }
}