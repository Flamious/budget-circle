namespace BudgetCircleApi.DAL.Interfaces
{
    using System.Collections.Generic;
    using System.Threading.Tasks;

    public interface IRepository<T> 
        where T : class
    {
        IEnumerable<T> GetAll();

        Task<T> Get(int id);

        Task Create(T item);

        Task Update(T item);

        void Delete(int id);
    }
}
