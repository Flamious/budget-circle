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

    class BudgetTypesService : IBudgetTypesService
    {
        private readonly IDbRepository _context;

        public BudgetTypesService(IDbRepository repository)
        {
            _context = repository;
        }

        public async Task<MessageResponse> AddDefaultBudgetType(string userId)
        {
            try
            {
                BudgetType type = new BudgetType()
                {
                    Title = "Cash",
                    UserId = userId,
                    Sum = 0,
                    IsDeletable = false
                };
                await _context.BudgetTypes.Create(type);
                await _context.Save();

                return new MessageResponse("Type was added");
            }
            catch (Exception e)
            {
                return new ErrorResponse(e.Message);
            }
        }

        public async Task<MessageResponse> AddBudgetType(string userId, string name, double sum)
        {
            try
            {
                BudgetType type = new BudgetType()
                {
                    Title = name,
                    UserId = userId,
                    Sum = sum,
                    IsDeletable = true
                };
                await _context.BudgetTypes.Create(type);
                await _context.Save();

                return new MessageResponse("Type was added");
            }
            catch (Exception e)
            {
                return new ErrorResponse(e.Message);
            }
        }

        public IEnumerable<BudgetTypeShort> GetBudgetTypes(string userId)
        {
            IEnumerable<BudgetType> allEntities = new List<BudgetType>();
            allEntities = _context.BudgetTypes.GetAll();
            List<BudgetTypeShort> result = allEntities
                .Where(t => t.UserId == userId)
                .Select(item => new BudgetTypeShort(item))
                .ToList();

            return result;
        }

        public async Task RemoveBudgetType(string userId, int id)
        {
            var entity = await _context.BudgetTypes.Get(id);
            if (entity.UserId != userId)
            {
                return;
            }

            var operations = _context.Operations.GetAll().Where(x => (x.BudgetTypeId == id) || (x.IsExpense == null && x.TypeId == id)).Select(x => x.Id).ToList();

            foreach(int opId in operations)
            {
                _context.Operations.Delete(opId);
            }

            _context.BudgetTypes.Delete(id);
            await _context.Save();
        }

        public async Task AddBudgetTypeSum(string userId, int id, double sum)
        {
            var entity = await _context.BudgetTypes.Get(id);
            if (entity.UserId != userId)
            {
                return;
            }

            entity.Sum += sum;

            await _context.BudgetTypes.Update(entity);
            await _context.Save();
        }

        public async Task UpdateBudgetType(string userId, int id, string name, double sum)
        {
            var entity = await _context.BudgetTypes.Get(id);
            if (entity.UserId != userId)
            {
                return;
            }

            entity.Title = name;
            entity.Sum = sum;

            await _context.BudgetTypes.Update(entity);
            await _context.Save();
        }

        public async Task ClearBudgetTypeSums(string userId)
        {
            var entities = _context.BudgetTypes.GetAll().Where(item => item.UserId == userId).ToList();
            foreach (var entity in entities)
            {
                entity.Sum = 0;
                await _context.BudgetTypes.Update(entity);
            }

            await _context.Save();
        }
    }
}
