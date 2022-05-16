namespace BudgetCircleApi.DAL.Repositiories
{
    using BudgetCircleApi.DAL.Entities;
    using BudgetCircleApi.DAL.Interfaces;
    using System.Collections.Generic;
    using System.Threading.Tasks;

    class ScheduledOperationRepository : IRepository<ScheduledOperation>
    {
        private readonly BudgetCircleContext _context;

        public ScheduledOperationRepository(BudgetCircleContext context)
        {
            _context = context;
        }

        public async Task Create(ScheduledOperation item)
        {
            await _context.ScheduledOperation.AddAsync(item);
        }

        public void Delete(int id)
        {
            var item = _context.ScheduledOperation.Find(id);
            if (item != null)
            {
                _context.Remove(item);
            }
        }

        public IEnumerable<ScheduledOperation> GetAll()
        {
            return _context.ScheduledOperation;
        }

        public async Task<ScheduledOperation> Get(int id)
        {
            return await _context.ScheduledOperation.FindAsync(id);
        }

        public async Task Update(ScheduledOperation item)
        {
            var entity = await _context.ScheduledOperation.FindAsync(item.Id);
            if (entity == null)
            {
                return;
            }

            _context.Entry(entity).CurrentValues.SetValues(item);
        }
    }
}
