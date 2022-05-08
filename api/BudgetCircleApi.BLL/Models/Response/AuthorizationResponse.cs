namespace BudgetCircleApi.BLL.Models.Response
{
    public class AuthorizationResponse : MessageResponse
    {
        public AuthorizationResponse(string message, string token)
            : base(message)
        {
            Token = token;
        }

        public string Token { get; protected set; }
    }
}
