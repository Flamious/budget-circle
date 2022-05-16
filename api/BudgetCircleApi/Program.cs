namespace BudgetCircleApi
{
    using BudgetCircleApi.Jobs;
    using Microsoft.AspNetCore.Hosting;
    using Microsoft.Extensions.Hosting;
    using Quartz;

    public class Program
    {
        public static void Main(string[] args)
        {
            CreateHostBuilder(args).Build().Run();
        }

        public static IHostBuilder CreateHostBuilder(string[] args) =>
            Host.CreateDefaultBuilder(args)
                .ConfigureWebHostDefaults(webBuilder =>
                {
                    webBuilder.UseStartup<Startup>();
                })
                .ConfigureServices((hostContext, services) =>
                {
                    services.AddQuartz(q =>
                    {
                        q.UseMicrosoftDependencyInjectionJobFactory();

                        var jobKey = new JobKey("BudgetCircleScheduledOperationsJob");

                        q.AddJob<ScheduledOperationsJob>(options =>
                        {
                            options.WithIdentity(jobKey);
                        });

                        q.AddTrigger(options =>
                        {
                            options.ForJob(jobKey);
                            options.WithIdentity("BudgetCircleScheduledOperationsJobTrigger");
                            options.WithSchedule(CronScheduleBuilder.MonthlyOnDayAndHourAndMinute(1, 1, 0));
                        });
                    });

                    services.AddQuartzHostedService(q =>
                    {
                        q.WaitForJobsToComplete = true;
                    });
                });
    }
}
