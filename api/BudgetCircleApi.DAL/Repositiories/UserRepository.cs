namespace BudgetCircleApi.DAL.Repositiories
{
    using BudgetCircleApi.DAL.Entities;
    using BudgetCircleApi.DAL.Interfaces;
    using Microsoft.EntityFrameworkCore;
    using System.Collections.Generic;
    using System.Threading.Tasks;

    public class UserRepositiory : IRepository<User>
    {
        private readonly BudgetCircleContext _context;

        public UserRepositiory(BudgetCircleContext context)
        {
            _context = context;
        }

        public async Task Create(User item)
        {
            await _context.User.AddAsync(item);
        }

        public void Delete(int id)
        {
            var item = _context.User.Find(id);
            if (item != null)
            {
                _context.Remove(item);
            }
        }

        public IEnumerable<User> GetAll()
        {
            return _context.User;
        }

        public async Task<User> Get(int id)
        {
            return await _context.User.FindAsync(id);
        }

        public async Task Update(User item)
        {
            var entity = await _context.User.FindAsync(item.Id);
            if (entity == null)
            {
                return;
            }

            _context.Entry(entity).CurrentValues.SetValues(item);
        }
    }
}
