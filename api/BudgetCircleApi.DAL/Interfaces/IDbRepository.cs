namespace BudgetCircleApi.DAL.Interfaces
{
    using BudgetCircleApi.DAL.Entities;
    using System.Threading.Tasks;

    public interface IDbRepository
    {
        IRepository<BudgetType> BudgetTypes { get; }

        IRepository<EarningType> EarningTypes { get; }

        IRepository<ExpenseType> ExpenseTypes { get; }

        IRepository<Operation> Operations { get; }

        IRepository<ScheduledOperation> ScheduledOperations { get; }

        IRepository<User> Users { get; }

        Task Save();
    }
}
