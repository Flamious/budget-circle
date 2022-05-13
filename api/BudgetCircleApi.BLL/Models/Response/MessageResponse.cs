namespace BudgetCircleApi.BLL.Models.Response
{
    public class MessageResponse
    {
        public MessageResponse(string message)
        {
            Message = message;
        }

        public string Message { get; protected set; }
    }
}
