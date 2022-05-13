namespace BudgetCircleApi.BLL.Interfaces
{
    using BudgetCircleApi.DAL.Entities;
    using System.Threading.Tasks;

    public interface IJWTGenerator
    {
        Task<string> CreateToken(User user);
    }
}
