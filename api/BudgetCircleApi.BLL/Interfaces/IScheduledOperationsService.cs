using BudgetCircleApi.BLL.Models.Request;
using BudgetCircleApi.BLL.Models.Response;
using BudgetCircleApi.BLL.Models.ShortEntity;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace BudgetCircleApi.BLL.Interfaces
{
    public interface IScheduledOperationsService
    {
        IEnumerable<ScheduledOperationShort> GetScheduledOperations(string userId, bool isExpense);

        Task<MessageResponse> AddScheduledOperation(string userId, ScheduledOperationModel model);

        Task UpdateScheduledOperation(string userId, int id, ScheduledOperationModel model);

        Task RemoveScheduledOperation(string userId, int id);

        Task ActivateScheduledOperations();
    }
}
