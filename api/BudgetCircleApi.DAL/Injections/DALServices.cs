namespace BudgetCircleApi.DAL.Injections
{
    using BudgetCircleApi.DAL.Entities;
    using BudgetCircleApi.DAL.Interfaces;
    using BudgetCircleApi.DAL.Repositiories;
    using Microsoft.AspNetCore.Identity;
    using Microsoft.EntityFrameworkCore;
    using Microsoft.Extensions.Configuration;
    using Microsoft.Extensions.DependencyInjection;

    public static class DALServices
    {
        public static IServiceCollection AddDALServices(this IServiceCollection services, IConfiguration configuration)
        {
            services.AddScoped(typeof(DbContext), typeof(BudgetCircleContext));
            services.AddIdentity<User, IdentityRole>()
                .AddEntityFrameworkStores<BudgetCircleContext>()
                .AddDefaultTokenProviders();
            services.AddScoped(typeof(IRepository<User>), typeof(UserRepositiory));
            services.AddScoped(typeof(IRepository<Operation>), typeof(OperationRepositiory));
            services.AddScoped(typeof(IRepository<BudgetType>), typeof(BudgetTypeRepositiory));
            services.AddScoped(typeof(IRepository<EarningType>), typeof(EarningTypeRepositiory));
            services.AddScoped(typeof(IRepository<ExpenseType>), typeof(ExpenseTypeRepositiory));
            services.AddScoped(typeof(IRepository<ScheduledOperation>), typeof(ScheduledOperationRepository));
            services.AddScoped(typeof(IDbRepository), typeof(DbRepositiory));
            services.AddDbContext<BudgetCircleContext>(options => options.UseSqlServer(configuration.GetConnectionString("DefaultConnection")));
            
            return services;
        }
    }
}
