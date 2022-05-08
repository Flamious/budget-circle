namespace BudgetCircleApi.BLL.Models.ShortEntity
{
    using BudgetCircleApi.DAL.Entities.Interfaces;

    public class TypeShort
    {
        public TypeShort() { }

        public TypeShort(Type type)
        {
            Id = type.Id;
            Title = type.Title;
            IsDeletable = type.UserId != null;
        }

        public int Id { get; set; }

        public string Title { get; set; }

        public double Sum { get; set; }

        public bool IsDeletable { get; set; }
    }
}