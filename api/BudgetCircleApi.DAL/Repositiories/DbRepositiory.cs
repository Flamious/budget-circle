namespace BudgetCircleApi.DAL.Repositiories
{
    using BudgetCircleApi.DAL.Entities;
    using BudgetCircleApi.DAL.Interfaces;
    using System.Threading.Tasks;

    public class DbRepositiory : IDbRepository
    {
        private readonly BudgetCircleContext _context;

        public DbRepositiory(BudgetCircleContext context)
        {
            _context = context;
        }

        private IRepository<BudgetType> budgetType;
        private IRepository<EarningType> earningType;
        private IRepository<ExpenseType> expenseType;
        private IRepository<Operation> operation;
        private IRepository<ScheduledOperation> scheduledOperation;
        private IRepository<PlannedBudget> plannedBudgets;
        private IRepository<User> user;

        public IRepository<BudgetType> BudgetTypes
        {
            get
            {
                if(budgetType == null)
                {
                    budgetType = new BudgetTypeRepositiory(_context);
                }

                return budgetType;
            }
        }

        public IRepository<EarningType> EarningTypes
        {
            get
            {
                if (earningType == null)
                {
                    earningType = new EarningTypeRepositiory(_context);
                }

                return earningType;
            }
        }

        public IRepository<ExpenseType> ExpenseTypes
        {
            get
            {
                if (expenseType == null)
                {
                    expenseType = new ExpenseTypeRepositiory(_context);
                }

                return expenseType;
            }
        }

        public IRepository<ScheduledOperation> ScheduledOperations
        {
            get
            {
                if (scheduledOperation == null)
                {
                    scheduledOperation = new ScheduledOperationRepository(_context);
                }

                return scheduledOperation;
            }
        }

        public IRepository<Operation> Operations
        {
            get
            {
                if (operation == null)
                {
                    operation = new OperationRepositiory(_context);
                }

                return operation;
            }
        }

        public IRepository<PlannedBudget> PlannedBudgets
        {
            get
            {
                if (plannedBudgets == null)
                {
                    plannedBudgets = new PlannedBudgetRepository(_context);
                }

                return plannedBudgets;
            }
        }

        public IRepository<User> Users
        {
            get
            {
                if (user == null)
                {
                    user = new UserRepositiory(_context);
                }

                return user;
            }
        }

        public async Task Save()
        {
            await _context.SaveChangesAsync();
        }
    }
}
