namespace BudgetCircleApi.BLL.Models.Response
{
    using BudgetCircleApi.BLL.Models.ShortEntity;
    using System.Collections.Generic;

    public class OperationListResponse : MessageResponse
    {
        public OperationListResponse(string message, bool isLastPage, List<OperationShort> operations)
            : base(message)
        {
            IsLastPage = isLastPage;
            Operations = operations;
        }

        public bool IsLastPage { get; protected set; }

        public List<OperationShort> Operations { get; protected set; }
    }
}
