namespace BudgetCircleApi.DAL.Repositiories
{
    using BudgetCircleApi.DAL.Entities;
    using BudgetCircleApi.DAL.Interfaces;
    using Microsoft.EntityFrameworkCore;
    using System.Collections.Generic;
    using System.Threading.Tasks;

    public class OperationRepositiory : IRepository<Operation>
    {
        private readonly BudgetCircleContext _context;

        public OperationRepositiory(BudgetCircleContext context)
        {
            _context = context;
        }

        public async Task Create(Operation item)
        {
            await _context.Operation.AddAsync(item);
        }

        public void Delete(int id)
        {
            var item = _context.Operation.Find(id);
            if (item != null)
            {
                _context.Remove(item);
            }
        }

        public IEnumerable<Operation> GetAll()
        {
            return _context.Operation;
        }

        public async Task<Operation> Get(int id)
        {
            return await _context.Operation.FindAsync(id);
        }

        public async Task Update(Operation item)
        {
            var entity = await _context.Operation.FindAsync(item.Id);
            if (entity == null)
            {
                return;
            }

            _context.Entry(entity).CurrentValues.SetValues(item);
        }
    }
}
