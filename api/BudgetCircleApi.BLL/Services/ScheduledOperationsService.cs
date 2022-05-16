namespace BudgetCircleApi.BLL.Services
{
    using BudgetCircleApi.BLL.Interfaces;
    using BudgetCircleApi.BLL.Models.Request;
    using BudgetCircleApi.BLL.Models.Response;
    using BudgetCircleApi.BLL.Models.ShortEntity;
    using BudgetCircleApi.DAL.Entities;
    using BudgetCircleApi.DAL.Interfaces;
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Threading.Tasks;

    class ScheduledOperationsService : IScheduledOperationsService
    {
        private readonly IDbRepository _context;
        private readonly IOperationsService _operationsService;
        private readonly IBudgetTypesService _budgetTypesService;

        public ScheduledOperationsService(
            IDbRepository repository,
            IOperationsService operationsService,
            IBudgetTypesService budgetTypesService
            )
        {
            _context = repository;
            _operationsService = operationsService;
            _budgetTypesService = budgetTypesService;
        }

        public async Task<MessageResponse> AddScheduledOperation(string userId, ScheduledOperationModel model)
        {

            try
            {
                ScheduledOperation scheduledPperation = new ScheduledOperation()
                {
                    Title = model.Title,
                    BudgetTypeId = model.BudgetTypeId,
                    Commentary = model.Commentary ?? string.Empty,
                    Sum = model.Sum,
                    IsExpense = model.IsExpense,
                    TypeId = model.TypeId,
                    UserId = userId
                };

                await _context.ScheduledOperations.Create(scheduledPperation);
                await _context.Save();

                await _operationsService.AddOperation(userId, new OperationModel()
                {
                    Title = model.Title,
                    Sum = model.Sum,
                    BudgetTypeId = model.BudgetTypeId,
                    Commentary = model.Commentary,
                    IsExpense = model.IsExpense,
                    IsScheduled = true,
                    TypeId = model.TypeId
                });

                return new MessageResponse("Scheduled operation was added");
            }
            catch (Exception e)
            {
                return new ErrorResponse(e.Message);
            }
        }

        public async Task ActivateScheduledOperations()
        {
            var scheduledOperations = _context.ScheduledOperations.GetAll().ToList();

            foreach (var operation in scheduledOperations)
            {
                await _operationsService.AddOperation(operation.UserId, new OperationModel()
                {
                    Title = operation.Title,
                    BudgetTypeId = operation.BudgetTypeId,
                    Sum = operation.Sum,
                    TypeId = operation.TypeId,
                    Commentary = operation.Commentary,
                    IsExpense = operation.IsExpense,
                    IsScheduled = true
                });

                await _budgetTypesService.AddBudgetTypeSum(operation.UserId, operation.BudgetTypeId, operation.IsExpense ? -operation.Sum : operation.Sum);
            }

            await _context.Save();
        }

        public async Task RemoveScheduledOperation(string userId, int id)
        {
            var entity = await _context.ScheduledOperations.Get(id);
            if (entity.UserId != userId)
            {
                return;
            }

            _context.ScheduledOperations.Delete(id);
            await _context.Save();
        }

        public async Task UpdateScheduledOperation(string userId, int id, ScheduledOperationModel model)
        {
            var entity = await _context.ScheduledOperations.Get(id);
            if (entity.UserId != userId)
            {
                return;
            }

            entity.Title = model.Title;
            entity.BudgetTypeId = model.BudgetTypeId;
            entity.Commentary = model.Commentary ?? "";
            entity.Sum = model.Sum;
            entity.IsExpense = model.IsExpense;
            entity.TypeId = model.TypeId;
            entity.UserId = userId;

            await _context.ScheduledOperations.Update(entity);
            await _context.Save();
        }

        public IEnumerable<ScheduledOperationShort> GetScheduledOperations(string userId, bool isExpense)
        {
            IEnumerable<ScheduledOperation> allEntities = new List<ScheduledOperation>();
            allEntities = _context.ScheduledOperations.GetAll();
            List<ScheduledOperationShort> result = allEntities
                .Where(t => t.UserId == userId && t.IsExpense == isExpense)
                .Select(item => new ScheduledOperationShort(item))
                .ToList();

            return result;
        }
    }
}
