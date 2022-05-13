namespace BudgetCircleApi.BLL.Services
{
    using BudgetCircleApi.BLL.Interfaces;
    using BudgetCircleApi.BLL.Models.Response;
    using BudgetCircleApi.BLL.Models.ShortEntity;
    using BudgetCircleApi.DAL.Entities;
    using BudgetCircleApi.DAL.Interfaces;
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Threading.Tasks;

    class OperationTypesServices : IOperationTypesServices
    {
        private readonly IDbRepository _context;

        public OperationTypesServices(IDbRepository repository)
        {
            _context = repository;
        }

        public async Task<MessageResponse> AddEarningType(string userId, string name)
        {
            try
            {
                EarningType type = new EarningType() { Title = name, UserId = userId };
                await _context.EarningTypes.Create(type);
                await _context.Save();

                return new MessageResponse("Type was added");
            }
            catch (Exception e)
            {
                return new ErrorResponse(e.Message);
            }
        }

        public async Task<MessageResponse> AddExpenseType(string userId, string name)
        {
            try
            {
                ExpenseType type = new ExpenseType() { Title = name, UserId = userId };
                await _context.ExpenseTypes.Create(type);
                await _context.Save();

                return new MessageResponse("Type was added");
            }
            catch (Exception e)
            {
                return new ErrorResponse(e.Message);
            }
        }

        public async Task AddDefaultEarningTypes()
        {
            await DeleteAllEarningTypes();
            await _context.EarningTypes.Create(new EarningType()
            {
                Title = "Other",
            });
            await _context.Save();
        }

        public async Task AddDefaultExpenseTypes()
        {
            await DeleteAllExpenseTypes();
            await _context.ExpenseTypes.Create(new ExpenseType()
            {
                Title = "Other",
            });
            await _context.Save();
        }

        public async Task DeleteAllEarningTypes()
        {
            var entities = _context.EarningTypes.GetAll().ToList();
            foreach (var e in entities)
            {
                _context.EarningTypes.Delete(e.Id);
            }
            await _context.Save();
        }

        public async Task DeleteAllExpenseTypes()
        {
            var entities = _context.ExpenseTypes.GetAll().ToList();
            foreach (var e in entities)
            {
                _context.ExpenseTypes.Delete(e.Id);
            }
            await _context.Save();
        }

        public IEnumerable<TypeShort> GetEarningTypes(string userId)
        {
            List<EarningType> allEntities = new List<EarningType>();
            allEntities = _context.EarningTypes.GetAll().Where(t => t.UserId == userId || t.UserId == null).ToList();

            List<TypeShort> result = allEntities
                .Select(item => new TypeShort(item))
                .ToList();

            for (int i = 0; i < allEntities.Count(); i++)
            {
                result[i].Sum = _context.Operations.GetAll().Where(e => e.IsExpense == false && e.TypeId == allEntities[i].Id).Sum(e => e.Sum);
            }

            return result;
        }

        public IEnumerable<TypeShort> GetExpenseTypes(string userId)
        {
            List<ExpenseType> allEntities = new List<ExpenseType>();
            allEntities = _context.ExpenseTypes.GetAll().Where(t => t.UserId == userId || t.UserId == null).ToList();

            List<TypeShort> result = allEntities
                .Select(item => new TypeShort(item))
                .ToList();

            for (int i = 0; i < allEntities.Count(); i++)
            {
                result[i].Sum = _context.Operations.GetAll().Where(e => e.IsExpense == true && e.TypeId == allEntities[i].Id).Sum(e => e.Sum);
            }

            return result;
        }

        public async Task RemoveEarningType(string userId, int id)
        {
            var entity = await _context.EarningTypes.Get(id);
            if (entity.UserId != userId)
            {
                return;
            }

            var operations = _context.Operations.GetAll().Where(item => item.UserId == userId && item.TypeId == id && item.IsExpense == false).ToList();
            foreach(var operation in operations)
            {
                _context.Operations.Delete(operation.Id);
            }

            _context.EarningTypes.Delete(id);
            await _context.Save();
        }

        public async Task RemoveExpenseType(string userId, int id)
        {
            var entity = await _context.ExpenseTypes.Get(id);
            if (entity.UserId != userId)
            {
                return;
            }

            var operations = _context.Operations.GetAll().Where(item => item.UserId == userId && item.TypeId == id && item.IsExpense == true).ToList();
            foreach (var operation in operations)
            {
                _context.Operations.Delete(operation.Id);
            }

            _context.ExpenseTypes.Delete(id);
            await _context.Save();
        }

        public async Task UpdateEarningType(string userId, int id, string name)
        {
            var entity = await _context.EarningTypes.Get(id);
            if (entity.UserId != userId)
            {
                return;
            }

            entity.Title = name;

            await _context.EarningTypes.Update(entity);
            await _context.Save();
        }

        public async Task UpdateExpenseType(string userId, int id, string name)
        {
            var entity = await _context.ExpenseTypes.Get(id);
            if (entity.UserId != userId)
            {
                return;
            }

            entity.Title = name;

            await _context.ExpenseTypes.Update(entity);
            await _context.Save();
        }
    }
}
