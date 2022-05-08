namespace BudgetCircleApi.BLL.Interfaces
{
    using BudgetCircleApi.BLL.Models.Request;
    using BudgetCircleApi.BLL.Models.Response;
    using System.Threading.Tasks;

    public interface IAuthorizationService
    {
        Task<MessageResponse> SignUp(SignUpModel model);

        Task<MessageResponse> SignIn(SignInModel model);

        Task<MessageResponse> RefreshToken(string nameIdentifier);

        Task<MessageResponse> ChangePassword(string userId, NewPasswordModel model);
    }
}
