namespace BudgetCircleApi.DAL.Entities.Interfaces
{
    using System.Collections.Generic;

    public abstract class Type
    {
        protected Type()
        {
            Operations = new HashSet<Operation>();
            ScheduledOperations = new HashSet<ScheduledOperation>();
        }

        public int Id { get; set; }

        public string Title { get; set; }

        public string UserId { get; set; }

        public ICollection<Operation> Operations { get; set; }

        public ICollection<ScheduledOperation> ScheduledOperations { get; set; }
    }
}
