namespace BudgetCircleApi.DAL.Entities
{
    using System;
    using System.Collections.Generic;
    using Microsoft.AspNetCore.Identity;

    public class User : IdentityUser
    {
        public User()
        {
            Operations = new HashSet<Operation>();
            BudgetTypes = new HashSet<BudgetType>();
        }

        public virtual ICollection<Operation> Operations { get; set; }

        public virtual ICollection<BudgetType> BudgetTypes { get; set; }
    }
}
