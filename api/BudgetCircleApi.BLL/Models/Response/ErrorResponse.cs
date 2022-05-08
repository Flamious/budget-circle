namespace BudgetCircleApi.BLL.Models.Response
{
    using System.Collections.Generic;

    public class ErrorResponse : MessageResponse
    {
        public ErrorResponse(string error)
            : base("Error occurred during the process")
        {
            Error = error;
        }

        public string Error { get; protected set; }
    }
}
