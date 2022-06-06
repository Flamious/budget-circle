namespace BudgetCircleApi.BLL.Interfaces
{
    using BudgetCircleApi.BLL.Models.Request;
    using BudgetCircleApi.BLL.Models.Response;
    using BudgetCircleApi.BLL.Models.ShortEntity;
    using System.Collections.Generic;
    using System.Threading.Tasks;

    public interface IOperationsService
    {
        MessageResponse GetOperations(string userId, OperationRequest request);

        Task<MessageResponse> AddOperation(string userId, OperationModel model);

        Task AddManyOperation(string userId, List<OperationModel> models);

        Task UpdateOperation(string userId, int id, OperationModel model);

        Task RemoveOperation(string userId, int id);

        Task RemoveAllOperations(string userId);

        List<OperationModelSum> GetOperationSum(string userId, OperationRequestSum request);

        List<ChartOperation> GetChartOperations(string userId, ChartOperationRequest request);
    }
}
