namespace BudgetCircleApi.BLL.Services
{
    using BudgetCircleApi.BLL.Interfaces;
    using BudgetCircleApi.BLL.Models;
    using BudgetCircleApi.BLL.Models.Request;
    using BudgetCircleApi.BLL.Models.Response;
    using BudgetCircleApi.BLL.Models.ShortEntity;
    using BudgetCircleApi.DAL.Entities;
    using BudgetCircleApi.DAL.Interfaces;
    using System;
    using System.Collections.Generic;
    using System.Globalization;
    using System.Linq;
    using System.Threading.Tasks;

    class OperationsServices : IOperationsServices
    {
        private const int EntitiesPerPage = 10;
        private readonly IDbRepository _context;

        public OperationsServices(IDbRepository repository)
        {
            _context = repository;
        }

        public async Task<MessageResponse> AddOperation(string userId, OperationModel model)
        {
            try
            {
                Operation operation = new Operation()
                {
                    Title = model.Title,
                    BudgetTypeId = model.BudgetTypeId,
                    Commentary = model.Commentary ?? string.Empty,
                    Date = DateTime.Now,
                    Sum = model.Sum,
                    IsExpense = model.IsExpense,
                    TypeId = model.TypeId,
                    UserId = userId,
                };
                await _context.Operations.Create(operation);
                await _context.Save();

                return new MessageResponse("Operation was added");
            }
            catch (Exception e)
            {
                return new ErrorResponse(e.Message);
            }
        }

        public List<ChartOperation> GetChartOperations(string userId, ChartOperationRequest request)
        {
            List<ChartOperation> result = new List<ChartOperation>();
            var _request = _context.Operations
                .GetAll()
                .Where(t => t.UserId == userId);

            if(request.BudgetTypeId != null)
            {
                _request = _request.Where(item => item.BudgetTypeId == request.BudgetTypeId);
            }

            var currentDate = DateTime.Now;

            switch (request.Period.ToLower())
            {
                case Requests.Year:
                    _request = _request.Where(item => item.Date.Year == currentDate.Year);
                    for (int month = 1; month <= 12; month++)
                    {
                        result.Add(new ChartOperation()
                        {
                            Title = CultureInfo.GetCultureInfo("en-US").DateTimeFormat.GetMonthName(month),
                            Expenses = _request.Where(item => item.Date.Month == month && item.IsExpense == true).Sum(item => item.Sum),
                            Earnings = _request.Where(item => item.Date.Month == month && item.IsExpense == false).Sum(item => item.Sum),
                            Exchanges = _request.Where(item => item.Date.Month == month && item.IsExpense == null).Sum(item => item.Sum),
                        });
                    }

                    break;
                case Requests.Month:
                    _request = _request.Where(item => item.Date.Year == currentDate.Year && item.Date.Month == currentDate.Month);
                    for (int day = 1; day <= DateTime.DaysInMonth(currentDate.Year, currentDate.Month); day++)
                    {
                        result.Add(new ChartOperation()
                        {
                            Title = day.ToString("##"),
                            Expenses = _request.Where(item => item.Date.Day == day && item.IsExpense == true).Sum(item => item.Sum),
                            Earnings = _request.Where(item => item.Date.Day == day && item.IsExpense == false).Sum(item => item.Sum),
                            Exchanges = _request.Where(item => item.Date.Day == day && item.IsExpense == null).Sum(item => item.Sum),
                        });
                    }

                    break;
                case Requests.Week:
                    var calendar = DateTimeFormatInfo.CurrentInfo.Calendar;
                    var currentWeekStart = currentDate.Date.AddDays(-1 * (int)calendar.GetDayOfWeek(currentDate));
                    _request = _request.Where(item => item.Date.Date.AddDays(-1 * (int)calendar.GetDayOfWeek(item.Date)) == currentWeekStart);
                    
                    for(int dayOfWeek = 1; dayOfWeek <= 7; dayOfWeek++)
                    {
                        result.Add(new ChartOperation()
                        {
                            Title = ((DayOfWeek)(dayOfWeek % 7)).ToString(),
                            Expenses = _request.Where(item => (int)item.Date.DayOfWeek == dayOfWeek && item.IsExpense == true).Sum(item => item.Sum),
                            Earnings = _request.Where(item => (int)item.Date.DayOfWeek == dayOfWeek && item.IsExpense == false).Sum(item => item.Sum),
                            Exchanges = _request.Where(item => (int)item.Date.DayOfWeek == dayOfWeek && item.IsExpense == null).Sum(item => item.Sum),
                        });
                    }

                    break;
                default:
                    for (int year = 2018; year <= DateTime.Now.Year; year++)
                    {
                        result.Add(new ChartOperation()
                        {
                            Title = year.ToString(),
                            Expenses = _request.Where(item => item.Date.Year == year && item.IsExpense == true).Sum(item => item.Sum),
                            Earnings = _request.Where(item => item.Date.Year == year && item.IsExpense == false).Sum(item => item.Sum),
                            Exchanges = _request.Where(item => item.Date.Year == year && item.IsExpense == null).Sum(item => item.Sum),
                        });
                    }

                    break;
            }
            return result;
        }

        public MessageResponse GetOperations(string userId, OperationRequest request)
        {
            var _request = _context.Operations
                .GetAll()
                .Where(t => t.UserId == userId);


            if (request.BudgetTypeId != null)
            {
                _request = _request.Where(x => x.BudgetTypeId == request.BudgetTypeId);
            }

            switch (request.Kind?.ToLower())
            {
                case OperationTypes.Expenses:
                    _request = _request.Where(x => x.IsExpense == true);
                    if (request.TypeId != null)
                    {
                        _request = _request.Where(x => x.TypeId == request.TypeId);
                    }

                    break;
                case OperationTypes.Earnings:
                    _request = _request.Where(x => x.IsExpense == false);
                    if (request.TypeId != null)
                    {
                        _request = _request.Where(x => x.TypeId == request.TypeId);
                    }

                    break;
                case OperationTypes.Exchanges:
                    _request = _request.Where(x => x.IsExpense == null);
                    if (request.TypeId != null)
                    {
                        _request = _request.Where(x => x.TypeId == request.TypeId);
                    }

                    break;
                default:
                    break;
            }

            if (request.Period != null && request.Period > 0)
            {
                _request = _request.Where(x => x.Date >= DateTime.Now.AddDays(-request.Period ?? 0));
            }

            if (request.Order == null || request.Order.ToLower() != Requests.Dec)
            {
                _request = _request.Reverse();
            }

            int entitiesNumber = _request.Count();
            bool isEnd = true;

            if (request.Page != null)
            {
                _request = _request
                    .Skip((request.Page.Value - 1) * EntitiesPerPage)
                    .Take(EntitiesPerPage);

                isEnd = request.Page.Value * EntitiesPerPage >= entitiesNumber;
            }

            List<OperationShort> result = _request
                .Select(item => new OperationShort(item))
                .ToList();

            return new OperationListResponse("Success", isEnd, result);
        }

        public async Task RemoveOperation(string userId, int id)
        {
            var entity = await _context.Operations.Get(id);
            if (entity.UserId != userId)
            {
                return;
            }

            _context.Operations.Delete(id);
            await _context.Save();
        }

        public async Task UpdateOperation(string userId, int id, OperationModel model)
        {
            var entity = await _context.Operations.Get(id);
            if (entity.UserId != userId)
            {
                return;
            }

            entity.Title = model.Title;
            entity.BudgetTypeId = model.BudgetTypeId;
            entity.Commentary = model.Commentary ?? "";
            entity.Sum = model.Sum;
            entity.IsExpense = model.IsExpense;
            entity.TypeId = model.TypeId;
            entity.UserId = userId;

            await _context.Operations.Update(entity);
            await _context.Save();
        }

        public List<OperationModelSum> GetOperationSum(string userId, OperationRequestSum request)
        {
            List<OperationModelSum> sums = new List<OperationModelSum>();
            var operations = (GetOperations(userId, new OperationRequest()
            {
                Period = request.Period <= 0 ? null : request.Period,
                Kind = request.IsExpense ? OperationTypes.Expenses : OperationTypes.Earnings
            }) as OperationListResponse).Operations;

            if (request.IsExpense)
            {
                var types = _context.ExpenseTypes.GetAll().ToList();
                foreach (var type in types)
                {
                    var typeList = operations.Where(x => x.TypeId == type.Id).ToList();
                    double sum = 0;
                    foreach (var operation in typeList)
                    {
                        sum += operation.Sum;
                    }

                    sums.Add(new OperationModelSum()
                    {
                        Type = type.Title,
                        Sum = sum
                    });
                }
            }
            else
            {
                var types = _context.EarningTypes.GetAll().ToList();
                foreach (var type in types)
                {
                    var typeList = operations.Where(x => x.TypeId == type.Id).ToList();
                    double sum = 0;
                    foreach (var operation in typeList)
                    {
                        sum += operation.Sum;
                    }

                    sums.Add(new OperationModelSum()
                    {
                        Type = type.Title,
                        Sum = sum
                    });
                }
            }

            return sums;
        }

        public async Task RemoveAllOperations(string userId)
        {
            var entities = _context.Operations.GetAll().Where(item => item.UserId == userId).ToList();
            foreach (var entity in entities)
            {
                _context.Operations.Delete(entity.Id);
            }

            await _context.Save();
        }
    }
}
