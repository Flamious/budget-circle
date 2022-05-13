namespace BudgetCircleApi.DAL.Repositiories
{
    using BudgetCircleApi.DAL.Entities;
    using BudgetCircleApi.DAL.Interfaces;
    using Microsoft.EntityFrameworkCore;
    using System.Collections.Generic;
    using System.Threading.Tasks;

    public class ExpenseTypeRepositiory : IRepository<ExpenseType>
    {
        private readonly BudgetCircleContext _context;

        public ExpenseTypeRepositiory(BudgetCircleContext context)
        {
            _context = context;
        }

        public async Task Create(ExpenseType item)
        {
            await _context.ExpenseType.AddAsync(item);
        }

        public void Delete(int id)
        {
            var item = _context.ExpenseType.Find(id);
            if (item != null)
            {
                _context.Remove(item);
            }
        }

        public IEnumerable<ExpenseType> GetAll()
        {
            return _context.ExpenseType;
        }

        public async Task<ExpenseType> Get(int id)
        {
            return await _context.ExpenseType.FindAsync(id);
        }

        public async Task Update(ExpenseType item)
        {
            var entity = await _context.ExpenseType.FindAsync(item.Id);
            if (entity == null)
            {
                return;
            }

            _context.Entry(entity).CurrentValues.SetValues(item);
        }
    }
}
