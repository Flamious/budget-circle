namespace BudgetCircleApi.BLL.Interfaces
{
    using System.Threading.Tasks;

    public interface IAdminServices
    {
        Task InitializeTypes();

        Task InitializeRoles();
    }
}
