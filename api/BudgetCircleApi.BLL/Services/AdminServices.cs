namespace BudgetCircleApi.BLL.Services
{
    using BudgetCircleApi.BLL.Interfaces;
    using BudgetCircleApi.BLL.Models;
    using Microsoft.AspNetCore.Identity;
    using System.Threading.Tasks;

    class AdminServices : IAdminServices
    {
        private readonly IOperationTypesServices _operationTypesServices;
        private readonly RoleManager<IdentityRole> _roleManager;

        public AdminServices(
            IOperationTypesServices operationTypesServices, 
            RoleManager<IdentityRole> roleManager)
        {
            _operationTypesServices = operationTypesServices;
            _roleManager = roleManager;
        }

        public async Task InitializeTypes()
        {
            await _operationTypesServices.AddDefaultEarningTypes();
            await _operationTypesServices.AddDefaultExpenseTypes();
        }

        public async Task InitializeRoles()
        {
            if (!await _roleManager.RoleExistsAsync(UserRoles.User))
            {
                await _roleManager.CreateAsync(new IdentityRole(UserRoles.User));
            }

            if (!await _roleManager.RoleExistsAsync(UserRoles.Admin))
            {
                await _roleManager.CreateAsync(new IdentityRole(UserRoles.Admin));
            }
        }
    }
}
