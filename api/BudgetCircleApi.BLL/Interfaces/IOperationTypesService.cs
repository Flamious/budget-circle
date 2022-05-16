namespace BudgetCircleApi.BLL.Interfaces
{
    using BudgetCircleApi.BLL.Models.Response;
    using BudgetCircleApi.BLL.Models.ShortEntity;
    using System.Collections.Generic;
    using System.Threading.Tasks;

    public interface IOperationTypesService
    {
        Task AddDefaultExpenseTypes();

        Task AddDefaultEarningTypes();

        Task DeleteAllExpenseTypes();

        Task DeleteAllEarningTypes();

        IEnumerable<TypeShort> GetEarningTypes(string userId);

        IEnumerable<TypeShort> GetExpenseTypes(string userId);

        Task<MessageResponse> AddEarningType(string userId, string name);

        Task<MessageResponse> AddExpenseType(string userId, string name);

        Task UpdateEarningType(string userId, int id, string name);

        Task UpdateExpenseType(string userId, int id, string name);

        Task RemoveEarningType(string userId, int id);

        Task RemoveExpenseType(string userId, int id);
    }
}
