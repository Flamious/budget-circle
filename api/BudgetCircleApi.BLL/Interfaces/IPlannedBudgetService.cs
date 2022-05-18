namespace BudgetCircleApi.BLL.Interfaces
{
    using BudgetCircleApi.BLL.Models.Request;
    using BudgetCircleApi.BLL.Models.Response;
    using BudgetCircleApi.BLL.Models.ShortEntity;
    using System.Threading.Tasks;

    public interface IPlannedBudgetService
    {
        Task<PlannedBudgetShort> GetPlannedBudget(string userId, PlannedBudgetRequest request);

        Task<MessageResponse> AddPlannedBudget(string userId, PlannedBudgetModel model);

        Task UpdatePlannedBudget(string userId, int id, PlannedBudgetModel model);

        Task RemovePlannedBudget(string userId, int id);
    }
}
