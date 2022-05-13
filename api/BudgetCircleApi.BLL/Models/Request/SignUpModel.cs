namespace BudgetCircleApi.BLL.Models.Request
{
    using System.ComponentModel.DataAnnotations;

    public class SignUpModel : SignInModel
    {
        [Required(ErrorMessage = "Confirmation password is required")]
        public string ConfirmationPassword { get; set; }
    }
}
