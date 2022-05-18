namespace BudgetCircleApi.DAL.Repositiories
{
    using BudgetCircleApi.DAL.Entities;
    using BudgetCircleApi.DAL.Interfaces;
    using System.Collections.Generic;
    using System.Threading.Tasks;

    class PlannedBudgetRepository : IRepository<PlannedBudget>
    {
        private readonly BudgetCircleContext _context;

        public PlannedBudgetRepository(BudgetCircleContext context)
        {
            _context = context;
        }

        public async Task Create(PlannedBudget item)
        {
            await _context.PlannedBudget.AddAsync(item);
        }

        public void Delete(int id)
        {
            var item = _context.PlannedBudget.Find(id);
            if (item != null)
            {
                _context.Remove(item);
            }
        }

        public IEnumerable<PlannedBudget> GetAll()
        {
            return _context.PlannedBudget;
        }

        public async Task<PlannedBudget> Get(int id)
        {
            return await _context.PlannedBudget.FindAsync(id);
        }

        public async Task Update(PlannedBudget item)
        {
            var entity = await _context.PlannedBudget.FindAsync(item.Id);
            if (entity == null)
            {
                return;
            }

            _context.Entry(entity).CurrentValues.SetValues(item);
        }
    }
}
