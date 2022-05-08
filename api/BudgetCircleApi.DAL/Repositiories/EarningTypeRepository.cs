namespace BudgetCircleApi.DAL.Repositiories
{
    using BudgetCircleApi.DAL.Entities;
    using BudgetCircleApi.DAL.Interfaces;
    using Microsoft.EntityFrameworkCore;
    using System.Collections.Generic;
    using System.Threading.Tasks;

    public class EarningTypeRepositiory : IRepository<EarningType>
    {
        private readonly BudgetCircleContext _context;

        public EarningTypeRepositiory(BudgetCircleContext context)
        {
            _context = context;
        }

        public async Task Create(EarningType item)
        {
            await _context.EarningType.AddAsync(item);
        }

        public void Delete(int id)
        {
            var item = _context.EarningType.Find(id);
            if (item != null)
            {
                _context.Remove(item);
            }
        }

        public IEnumerable<EarningType> GetAll()
        {
            return _context.EarningType;
        }

        public async Task<EarningType> Get(int id)
        {
            return await _context.EarningType.FindAsync(id);
        }

        public async Task Update(EarningType item)
        {
            var entity = await _context.EarningType.FindAsync(item.Id);
            if (entity == null)
            {
                return;
            }

            _context.Entry(entity).CurrentValues.SetValues(item);
        }
    }
}
