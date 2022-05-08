using System.ComponentModel.DataAnnotations;

namespace BudgetCircleApi.BLL.Models.Request
{
    public class OperationRequest
    {
        public string Order { get; set; }

        public string Kind { get; set; }

        public int? BudgetTypeId { get; set; }

        public int? TypeId { get; set; }

        public int? Page { get; set; }

        public double? Period { get; set; }
    }

    public class OperationRequestSum
    {
        [Required]
        public bool IsExpense { get; set; }

        [Required]
        public double? Period { get; set; }
    }
}
