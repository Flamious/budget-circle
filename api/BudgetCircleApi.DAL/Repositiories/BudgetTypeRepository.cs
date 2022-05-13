namespace BudgetCircleApi.DAL.Repositiories
{
    using BudgetCircleApi.DAL.Entities;
    using BudgetCircleApi.DAL.Interfaces;
    using Microsoft.EntityFrameworkCore;
    using System.Collections.Generic;
    using System.Threading.Tasks;

    public class BudgetTypeRepositiory : IRepository<BudgetType>
    {
        private readonly BudgetCircleContext _context;

        public BudgetTypeRepositiory(BudgetCircleContext context)
        {
            _context = context;
        }

        public async Task Create(BudgetType item)
        {
            await _context.BudgetType.AddAsync(item);
        }

        public void Delete(int id)
        {
            var item = _context.BudgetType.Find(id);
            if (item != null)
            {
                _context.Remove(item);
            }
        }

        public IEnumerable<BudgetType> GetAll()
        {
            return _context.BudgetType;
        }

        public async Task<BudgetType> Get(int id)
        {
            return await _context.BudgetType.FindAsync(id);
        }

        public async Task Update(BudgetType item)
        {
            var entity = await _context.BudgetType.FindAsync(item.Id);
            if(entity == null)
            {
                return;
            }

            _context.Entry(entity).CurrentValues.SetValues(item);
        }
    }
}
