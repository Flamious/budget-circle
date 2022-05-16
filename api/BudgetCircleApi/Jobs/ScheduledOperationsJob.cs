namespace BudgetCircleApi.Jobs
{
    using BudgetCircleApi.BLL.Interfaces;
    using Quartz;
    using System.Threading.Tasks;

    [DisallowConcurrentExecution]
    public class ScheduledOperationsJob : IJob
    {
        private readonly IScheduledOperationsService _scheduledOperationsService;

        public ScheduledOperationsJob(IScheduledOperationsService scheduledOperationsService)
        {
            _scheduledOperationsService = scheduledOperationsService;
        }

        public async Task Execute(IJobExecutionContext context)
        {
            await _scheduledOperationsService.ActivateScheduledOperations();
        }
    }
}
