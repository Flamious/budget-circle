namespace BudgetCircleApi.DAL.Entities
{
    using System.ComponentModel.DataAnnotations.Schema;
    using BudgetCircleApi.DAL.Entities.Interfaces;

    public class BudgetType : Type
    {
        public BudgetType()
            : base()
        {
        }

        [Column(TypeName = "decimal(18,2)")]
        public double Sum { get; set; }

        public bool IsDeletable { get; set; }

        public virtual User User { get; set; }
    }
}
