namespace BudgetCircleApi.BLL.Models.Request
{
    public class ChartOperationRequest
    {
        public int? BudgetTypeId { get; set; }

        public string Period { get; set; }
    }
}
