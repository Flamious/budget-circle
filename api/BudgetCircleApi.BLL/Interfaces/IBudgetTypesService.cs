namespace BudgetCircleApi.BLL.Interfaces
{
    using BudgetCircleApi.BLL.Models.Response;
    using BudgetCircleApi.BLL.Models.ShortEntity;
    using System.Collections.Generic;
    using System.Threading.Tasks;

    public interface IBudgetTypesService
    {
        IEnumerable<BudgetTypeShort> GetBudgetTypes(string userId);

        Task<MessageResponse> AddBudgetType(string userId, string Name, double sum);

        Task<MessageResponse> AddDefaultBudgetType(string userId);

        Task UpdateBudgetType(string userId, int id, string name, double sum);

        Task RemoveBudgetType(string userId, int id);

        Task ClearBudgetTypeSums(string userId);
    }
}
