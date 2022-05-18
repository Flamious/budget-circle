namespace BudgetCircleApi.BLL.Injections
{
    using BudgetCircleApi.BLL.Interfaces;
    using BudgetCircleApi.BLL.Services;
    using Microsoft.Extensions.Configuration;
    using Microsoft.Extensions.DependencyInjection;

    public static class BLLServices
    {
        public static IServiceCollection AddBLLServices(this IServiceCollection services, IConfiguration _)
        {
            services.AddScoped(typeof(IJWTGenerator), typeof(JWTGenerator));
            services.AddScoped(typeof(IAuthorizationService), typeof(AuthorizationService));
            services.AddScoped(typeof(IAdminServices), typeof(AdminServices));
            services.AddScoped(typeof(IOperationTypesService), typeof(OperationTypesService));
            services.AddScoped(typeof(IBudgetTypesService), typeof(BudgetTypesService));
            services.AddScoped(typeof(IOperationsService), typeof(OperationsService));
            services.AddScoped(typeof(IScheduledOperationsService), typeof(ScheduledOperationsService));
            services.AddScoped(typeof(IPlannedBudgetService), typeof(PlannedBudgetService));

            return services;
        }
    }
}
